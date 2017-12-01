package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

    /**
     * The Class BackupFile.
     */
    private class BackupFile {
        
        /** The file. */
        private final File file;
        
        /** The last modified. */
        private final long lastModified;
        
        /** The calendar. */
        private final Calendar calendar;

        /**
         * Instantiates a new backup file.
         *
         * @param file the file
         */
        public BackupFile(File file) {
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
    private Map<Calendar, BackupFile> backupFiles = new TreeMap<Calendar, BackupFile>();
    // To use 256 bit keys, you need the "unlimited strength" encryption policy
    /** The number of bits. */
    // files from Sun.
    private int numberOfBits = 128;

    /**
     * Save backups.
     *
     * @param inDir the in dir
     * @param outDir the out dir
     * @param password the password
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void saveBackups(File inDir, File outDir, String password) throws IOException {
        File[] files = inDir.listFiles();
        for (File file : files) {
            BackupFile backupFile = new BackupFile(file);
            Calendar calendar = backupFile.getCalendar();
            BackupFile currentBackupFile = backupFiles.get(calendar);
            if (currentBackupFile == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New file for cal=" + calendar.getTime());
                    LOGGER.debug("    > " + backupFile.getFile());
                }
                backupFiles.put(calendar, backupFile);
            } else {
                if (backupFile.getLastModified() > currentBackupFile.getLastModified()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Newer file for cal=" + calendar.getTime());
                        LOGGER.debug("    < " + currentBackupFile.getFile());
                        LOGGER.debug("    > " + backupFile.getFile());
                    }
                    backupFiles.put(calendar, backupFile);
                }
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        for (BackupFile backupFile : backupFiles.values()) {
            String dateString = formatter.format(backupFile.getCalendar().getTime());
            LOGGER.info("###");
            LOGGER.info("> date=" + dateString);
            File dir = new File(outDir, dateString);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            copyFileToDir(backupFile.getFile(), dir, password);
        }
    }

    /**
     * Copy file to dir.
     *
     * @param fromFile the from file
     * @param toDir the to dir
     * @param password the password
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void copyFileToDir(File fromFile, File toDir, String password) throws IOException {
        File latestFile = getLatestFile(toDir);
        if (latestFile != null) {
            BackupFile currentBackupFile = new BackupFile(latestFile);
            BackupFile backupFile = new BackupFile(fromFile);
            if (backupFile.getLastModified() <= currentBackupFile.getLastModified()) {
                LOGGER.info("Already has latest backup file for toDir=" + toDir);
                LOGGER.info("  currentBackupFile=" + currentBackupFile.getFile());
                LOGGER.info("  backupFile=" + backupFile.getFile());
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
     * @param fromFile the from file
     * @param toFile the to file
     * @param password the password
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void copyFile(File fromFile, File toFile, String password) throws IOException {
        if (password == null) {
            copyFile(fromFile, toFile);
        } else {
            try {
                SecretKey key = computeKey(password);
                if (key != null) {
                    EncryptionHelper helper = new EncryptionHelper();
                    int encryptMode = Cipher.ENCRYPT_MODE;
                    Cipher cipher = helper.getCipher();
                    cipher.init(encryptMode, key, helper.getSalt());
                    File encryptedToFile = new File(toFile.getAbsoluteFile().getParentFile(), toFile.getName() + "_encrypted");
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
    }

    /**
     * Compute key.
     *
     * @param password the password
     * @return the secret key
     * @throws InvalidKeySpecException the invalid key spec exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    private SecretKey computeKey(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return computeKey(password, EncryptionHelper.DEFAULT_SALT_BYTES);
    }

    /**
     * Compute key.
     *
     * @param password the password
     * @param salt the salt
     * @return the secret key
     * @throws InvalidKeySpecException the invalid key spec exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    private SecretKey computeKey(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
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
     * @param toDir the to dir
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
     * @param fromFile the from file
     * @param toFile the to file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void copyFile(File fromFile, File toFile) throws IOException {
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

}
