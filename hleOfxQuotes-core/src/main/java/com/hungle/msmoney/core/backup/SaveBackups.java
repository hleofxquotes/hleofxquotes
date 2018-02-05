package com.hungle.msmoney.core.backup;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.encryption.EncryptionHelper;
import com.hungle.msmoney.core.encryption.EncryptionHelperException;
import com.hungle.msmoney.core.misc.StopWatch;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveBackups.
 */
public class SaveBackups {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SaveBackups.class);

    private SaveBackupsListener saveBackupsListener;

    private SaveBackupsResult result;

    /**
     * The Class BackupFile.
     */
    private static final class DailyFile {

        /** The file. */
        private final File file;

        /** The last modified. */
        private final long lastModified;

        /** The calendar. */
        private final Calendar calendar;

        /**
         * Instantiates a new backup file.
         *
         * @param file
         *            the file
         */
        public DailyFile(File file) {
            this.file = file;
            this.lastModified = file.lastModified();

            this.calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lastModified);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.clear();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }

        /**
         * Gets the file.
         *
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * Gets the last modified.
         *
         * @return the last modified
         */
        public long getLastModified() {
            return lastModified;
        }

        /**
         * Gets the calendar.
         *
         * @return the calendar
         */
        public Calendar getCalendar() {
            return calendar;
        }

    }

    /** The backup files. */
    private Map<Calendar, DailyFile> dailyFilesMap = new TreeMap<Calendar, DailyFile>();

    // To use 256 bit keys, you need the "unlimited strength" encryption policy
    /** The number of bits. */
    // files from Sun.
    private int numberOfBits = 128;

    /**
     * Save backups.
     *
     * @param fromDir
     *            the in dir
     * @param toDir
     *            the out dir
     * @param password
     *            the password
     * @return
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public SaveBackupsResult saveBackups(File fromDir, File toDir, String password) throws IOException {
        result = new SaveBackupsResult();
        StopWatch stopWatch = new StopWatch();
        // result.setStarted(stopWatch.click());

        try {
            if (saveBackupsListener != null) {
                saveBackupsListener.notifyStartBackup();
            }
            updateFilesMap(fromDir, dailyFilesMap);

            SimpleDateFormat dirNameFormatter = new SimpleDateFormat("yyyy/MM/dd");
            for (DailyFile dailyFile : dailyFilesMap.values()) {
                String dirName = dirNameFormatter.format(dailyFile.getCalendar().getTime());
                LOGGER.info("###");
                LOGGER.info("> dirName=" + dirName);
                File dir = new File(toDir, dirName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                boolean copied = false;
                try {
                    if (saveBackupsListener != null) {
                        saveBackupsListener.notifyStartCopyFile(dailyFile.getFile(), dirName, dailyFilesMap.size());
                    }
                    copied = copyFileToDir(dailyFile.getFile(), dir, password);
                    if (copied) {
                        result.incCopiedCount();
                    }
                    result.intCount();
                } finally {
                    if (saveBackupsListener != null) {
                        saveBackupsListener.notifyDoneCopyFile(copied, dailyFile.getFile(), dirName,
                                dailyFilesMap.size());
                    }
                }
            }
        } finally {
            result.setElapsed(stopWatch.click());

            if (saveBackupsListener != null) {
                saveBackupsListener.notifyDoneBackup();
            }
        }
        return result;
    }

    /**
     * 
     * @param fromDir
     * @param dailyFilesMap
     */
    private static final void updateFilesMap(File fromDir, Map<Calendar, DailyFile> dailyFilesMap) {
        File[] files = fromDir.listFiles();
        dailyFilesMap.clear();
        for (File file : files) {
            DailyFile dailyFile = new DailyFile(file);
            Calendar key = dailyFile.getCalendar();
            DailyFile currentDailyFile = dailyFilesMap.get(key);
            if (currentDailyFile == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New file for cal=" + key.getTime());
                    LOGGER.debug("    > " + dailyFile.getFile());
                }
                dailyFilesMap.put(key, dailyFile);
            } else {
                if (dailyFile.getLastModified() > currentDailyFile.getLastModified()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Newer file for cal=" + key.getTime());
                        LOGGER.debug("    < " + currentDailyFile.getFile());
                        LOGGER.debug("    > " + dailyFile.getFile());
                    }
                    dailyFilesMap.put(key, dailyFile);
                }
            }
        }
    }

    /**
     * Copy file to dir.
     *
     * @param fromFile
     *            the from file
     * @param toDir
     *            the to dir
     * @param password
     *            the password
     * @return
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private boolean copyFileToDir(File fromFile, File toDir, String password) throws IOException {
        boolean copied = false;

        File toFile = null;

        File lastModifiedFile = getLastModifiedFile(toDir);
        if (lastModifiedFile != null) {
            DailyFile currentDailyFile = new DailyFile(lastModifiedFile);
            DailyFile dailyFile = new DailyFile(fromFile);
            if (dailyFile.getLastModified() <= currentDailyFile.getLastModified()) {
                LOGGER.info("Already has latest backup file for toDir=" + toDir);
                LOGGER.info("  currentBackupFile=" + currentDailyFile.getFile());
                LOGGER.info("  backupFile=" + dailyFile.getFile());
                // return false;
                toFile = lastModifiedFile;
                copied = false;
            }
        }

        if (toFile == null) {
            toFile = new File(toDir, fromFile.getName());
            StopWatch stopWatch = new StopWatch();

            try {
                LOGGER.info("> Copying");
                LOGGER.info("  fromFile=" + fromFile);
                LOGGER.info("  toFile=" + toFile);
                copyFile(fromFile, toFile, password);
                copied = true;
            } finally {
                long delta = stopWatch.click();
                LOGGER.info("< DONE, delta=" + delta);
            }
        }

        if (toFile != null) {
            generateHashFiles(toFile);
        }

        return copied;
    }

    private void generateHashFiles(File sourceFile) {
        try {
            File hashFile = null;
            String hashName = null;

            hashName = MD5;
            String md5String = new DigestUtils(hashName).digestAsHex(sourceFile);
            hashFile = writeHash(md5String, sourceFile.getName(), toHashFile(sourceFile, ".md5"));
            if (hashFile != null) {
                LOGGER.info("Created hashFile=" + hashFile.getAbsolutePath());
            }

            hashName = SHA_256;
            String sha256String = new DigestUtils(hashName).digestAsHex(sourceFile);
            hashFile = writeHash(sha256String, sourceFile.getName(), toHashFile(sourceFile, ".sha256"));
            if (hashFile != null) {
                LOGGER.info("Created hashFile=" + hashFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.warn(e);
        }

    }

    private File writeHash(String hashString, String sourceFileName, File hashFile) throws IOException {
        if (hashFile.exists()) {
            return null;
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(hashFile));
            writeHash(hashString, sourceFileName, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }

        return hashFile;
    }

    private void writeHash(String hashString, String sourceFileName, PrintWriter writer) {
        writer.println(hashString + " *" + sourceFileName);

    }

    private File toHashFile(File sourceFile, String suffix) {
        sourceFile = sourceFile.getAbsoluteFile();

        File parentDir = sourceFile.getParentFile();
        String name = sourceFile.getName();
        name = name + suffix;

        File hashFile = new File(parentDir, name);
        return hashFile;
    }

    /**
     * Copy file.
     *
     * @param fromFile
     *            the from file
     * @param toFile
     *            the to file
     * @param password
     *            the password
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void copyFile(File fromFile, File toFile, String password) throws IOException {
        if (saveBackupsListener != null) {
            saveBackupsListener.notifyCopyFile(fromFile, toFile, password);
        }
        if (password == null) {
            copyFile(fromFile, toFile);
        } else {
            copyFileWithPassword(fromFile, toFile, password);
        }
    }

    private void copyFile(File fromFile, File toFile) throws IOException {
        copyFileUsingStandardCopyOption(fromFile, toFile);
    }

    private void copyFileUsingStandardCopyOption(File fromFile, File toFile) throws IOException {
        Path source = fromFile.toPath();
        Path target = toFile.toPath();
        CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
                // StandardCopyOption.COPY_ATTRIBUTES,
                // StandardCopyOption.ATOMIC_MOVE,
        };
        Files.copy(source, target, options);
    }

    private void copyFileWithPassword(File fromFile, File toFile, String password) throws IOException {
        try {
            SecretKey key = computeKey(password);
            if (key != null) {
                EncryptionHelper helper = new EncryptionHelper();
                int encryptMode = Cipher.ENCRYPT_MODE;
                Cipher cipher = helper.getCipher();
                cipher.init(encryptMode, key, helper.getSalt());
                File encryptedToFile = new File(toFile.getAbsoluteFile().getParentFile(),
                        toFile.getName() + "_encrypted");
                EncryptionHelper.encryptFile(fromFile, encryptedToFile, cipher);
            }
        } catch (InvalidKeyException e) {
            throw new IOException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        } catch (EncryptionHelperException e) {
            throw new IOException(e);
        } catch (InvalidKeySpecException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    /**
     * Compute key.
     *
     * @param password
     *            the password
     * @return the secret key
     * @throws InvalidKeySpecException
     *             the invalid key spec exception
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     */
    private SecretKey computeKey(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return computeKey(password, EncryptionHelper.DEFAULT_SALT_BYTES);
    }

    /**
     * Compute key.
     *
     * @param password
     *            the password
     * @param salt
     *            the salt
     * @return the secret key
     * @throws InvalidKeySpecException
     *             the invalid key spec exception
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     */
    private SecretKey computeKey(String password, byte[] salt)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        int iterationCount = 2048;
        int keyLength = 128;
        LOGGER.info("keySize=" + keyLength);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    /**
     * Gets the latest file.
     *
     * @param dir
     *            the to dir
     * @return the latest file
     */
    private static final File getLastModifiedFile(File dir) {
        File lastModifiedFile = null;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (lastModifiedFile == null) {
                    lastModifiedFile = file;
                } else {
                    if (file.lastModified() > lastModifiedFile.lastModified()) {
                        lastModifiedFile = file;
                    }
                }
            }
        }

        return lastModifiedFile;
    }

    /**
     * Copy file.
     *
     * @param fromFile
     *            the from file
     * @param toFile
     *            the to file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void copyFileUsingStream(File fromFile, File toFile) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(fromFile).getChannel();
            out = new FileOutputStream(toFile).getChannel();
            in.transferTo(0, in.size(), out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    out = null;
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    in = null;
                }
            }
        }
    }

    public void setSaveBackupsListener(SaveBackupsListener saveBackupsListener) {
        this.saveBackupsListener = saveBackupsListener;
    }

}
