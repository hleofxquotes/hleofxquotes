package com.le.tools.moneyutils.backup;

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

import com.le.tools.moneyutils.encryption.EncryptionHelper;
import com.le.tools.moneyutils.encryption.EncryptionHelperException;
import com.le.tools.moneyutils.ofx.quotes.StopWatch;

public class SaveBackups {
    private static final Logger log = Logger.getLogger(SaveBackups.class);

    private class BackupFile {
        private final File file;
        private final long lastModified;
        private final Calendar calendar;

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

        public File getFile() {
            return file;
        }

        public long getLastModified() {
            return lastModified;
        }

        public Calendar getCalendar() {
            return calendar;
        }

    }

    private Map<Calendar, BackupFile> backupFiles = new TreeMap<Calendar, BackupFile>();
    // To use 256 bit keys, you need the "unlimited strength" encryption policy
    // files from Sun.
    private int numberOfBits = 128;

    public void saveBackups(File inDir, File outDir, String password) throws IOException {
        File[] files = inDir.listFiles();
        for (File file : files) {
            BackupFile backupFile = new BackupFile(file);
            Calendar calendar = backupFile.getCalendar();
            BackupFile currentBackupFile = backupFiles.get(calendar);
            if (currentBackupFile == null) {
                if (log.isDebugEnabled()) {
                    log.debug("New file for cal=" + calendar.getTime());
                    log.debug("    > " + backupFile.getFile());
                }
                backupFiles.put(calendar, backupFile);
            } else {
                if (backupFile.getLastModified() > currentBackupFile.getLastModified()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Newer file for cal=" + calendar.getTime());
                        log.debug("    < " + currentBackupFile.getFile());
                        log.debug("    > " + backupFile.getFile());
                    }
                    backupFiles.put(calendar, backupFile);
                }
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        for (BackupFile backupFile : backupFiles.values()) {
            String dateString = formatter.format(backupFile.getCalendar().getTime());
            log.info("###");
            log.info("> date=" + dateString);
            File dir = new File(outDir, dateString);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            copyFileToDir(backupFile.getFile(), dir, password);
        }
    }

    private void copyFileToDir(File fromFile, File toDir, String password) throws IOException {
        File latestFile = getLatestFile(toDir);
        if (latestFile != null) {
            BackupFile currentBackupFile = new BackupFile(latestFile);
            BackupFile backupFile = new BackupFile(fromFile);
            if (backupFile.getLastModified() <= currentBackupFile.getLastModified()) {
                log.info("Already has latest backup file for toDir=" + toDir);
                log.info("  currentBackupFile=" + currentBackupFile.getFile());
                log.info("  backupFile=" + backupFile.getFile());
                return;
            }
        }

        File toFile = new File(toDir, fromFile.getName());
        StopWatch stopWatch = new StopWatch();

        try {
            log.info("> Copying");
            log.info("  fromFile=" + fromFile);
            log.info("  toFile=" + toFile);
            copyFile(fromFile, toFile, password);
        } finally {
            long delta = stopWatch.click();
            log.info("< DONE, delta=" + delta);
        }
    }

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

    private SecretKey computeKey(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return computeKey(password, EncryptionHelper.DEFAULT_SALT_BYTES);
    }

    private SecretKey computeKey(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        int iterationCount = 2048;
        int keyLength = 128;
        log.info("keySize=" + keyLength);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

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
                    log.warn(e);
                } finally {
                    out = null;
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    in = null;
                }
            }
        }
    }

}
