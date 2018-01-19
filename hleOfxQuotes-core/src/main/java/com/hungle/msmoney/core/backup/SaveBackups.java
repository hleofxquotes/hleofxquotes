package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    /**
     * The Class BackupFile.
     */
    private class DailyFile {

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
    private Map<Calendar, DailyFile> dailyFiles = new TreeMap<Calendar, DailyFile>();

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
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void saveBackups(File fromDir, File toDir, String password) throws IOException {
        try {
            if (saveBackupsListener != null) {
                saveBackupsListener.notifyStartBackup();
            }
            updateDailyFiles(fromDir);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            for (DailyFile dailyFile : dailyFiles.values()) {
                String dateString = formatter.format(dailyFile.getCalendar().getTime());
                LOGGER.info("###");
                LOGGER.info("> date=" + dateString);
                File dir = new File(toDir, dateString);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                try {
                    if (saveBackupsListener != null) {
                        saveBackupsListener.notifyStartCopyFile(dailyFile.getFile(), dailyFiles.size());
                    }
                    copyFileToDir(dailyFile.getFile(), dir, password);
                } finally {
                    if (saveBackupsListener != null) {
                        saveBackupsListener.notifyDoneCopyFile(dailyFile.getFile(), dailyFiles.size());
                    }
                }
            }
        } finally {
            if (saveBackupsListener != null) {
                saveBackupsListener.notifyDoneBackup();
            }
        }
    }

    private void updateDailyFiles(File fromDir) {
        File[] files = fromDir.listFiles();
        dailyFiles.clear();
        for (File file : files) {
            DailyFile dailyFile = new DailyFile(file);
            Calendar key = dailyFile.getCalendar();
            DailyFile currentDailyFile = dailyFiles.get(key);
            if (currentDailyFile == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New file for cal=" + key.getTime());
                    LOGGER.debug("    > " + dailyFile.getFile());
                }
                dailyFiles.put(key, dailyFile);
            } else {
                if (dailyFile.getLastModified() > currentDailyFile.getLastModified()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Newer file for cal=" + key.getTime());
                        LOGGER.debug("    < " + currentDailyFile.getFile());
                        LOGGER.debug("    > " + dailyFile.getFile());
                    }
                    dailyFiles.put(key, dailyFile);
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
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void copyFileToDir(File fromFile, File toDir, String password) throws IOException {
        File latestFile = getLatestFile(toDir);
        if (latestFile != null) {
            DailyFile currentDailyFile = new DailyFile(latestFile);
            DailyFile dailyFile = new DailyFile(fromFile);
            if (dailyFile.getLastModified() <= currentDailyFile.getLastModified()) {
                LOGGER.info("Already has latest backup file for toDir=" + toDir);
                LOGGER.info("  currentBackupFile=" + currentDailyFile.getFile());
                LOGGER.info("  backupFile=" + dailyFile.getFile());
                return;
            }
        }

        File toFile = new File(toDir, fromFile.getName());
        StopWatch stopWatch = new StopWatch();

        try {
            LOGGER.info("> Copying");
            LOGGER.info("  fromFile=" + fromFile);
            LOGGER.info("  toFile=" + toFile);
            copyFile(fromFile, toFile, password);
        } finally {
            long delta = stopWatch.click();
            LOGGER.info("< DONE, delta=" + delta);
        }
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

    public void copyFile(File fromFile, File toFile) throws IOException {
        copyFileUsingStandardCopyOption(fromFile, toFile);
    }

    private void copyFileUsingStandardCopyOption(File fromFile, File toFile) throws IOException {
        Path source = fromFile.toPath();
        Path target = toFile.toPath();
        CopyOption[] options = new CopyOption[] { 
                StandardCopyOption.REPLACE_EXISTING,
//                StandardCopyOption.COPY_ATTRIBUTES, 
//                StandardCopyOption.ATOMIC_MOVE,
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
     * @param toDir
     *            the to dir
     * @return the latest file
     */
    private File getLatestFile(File toDir) {
        File latestFile = null;

        File[] files = toDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (latestFile == null) {
                    latestFile = file;
                } else {
                    if (file.lastModified() > latestFile.lastModified()) {
                        latestFile = file;
                    }
                }
            }
        }

        return latestFile;
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

    public SaveBackupsListener getSaveBackups() {
        return saveBackupsListener;
    }

    public void setSaveBackupsListener(SaveBackupsListener saveBackupsListener) {
        this.saveBackupsListener = saveBackupsListener;
    }

}
