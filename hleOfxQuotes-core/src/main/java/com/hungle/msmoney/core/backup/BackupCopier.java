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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.encryption.EncryptionHelper;
import com.hungle.msmoney.core.encryption.EncryptionHelperException;

public class BackupCopier {
    private static final Logger LOGGER = Logger.getLogger(BackupCopier.class);

    static final void copyFile(File fromFile, File toFile) throws IOException {
        copyFileUsingStandardCopyOption(fromFile, toFile);
    }

    static final void copyFileWithPassword(File fromFile, File toFile, String password) throws IOException {
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

    private static final void copyFileUsingStandardCopyOption(File fromFile, File toFile) throws IOException {
        Path source = fromFile.toPath();
        Path target = toFile.toPath();
        CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
                // StandardCopyOption.COPY_ATTRIBUTES,
                // StandardCopyOption.ATOMIC_MOVE,
        };
        Files.copy(source, target, options);
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
    private static final SecretKey computeKey(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
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
    private static final SecretKey computeKey(String password, byte[] salt)
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
     * Copy file.
     *
     * @param fromFile
     *            the from file
     * @param toFile
     *            the to file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static final void copyFileUsingStream(File fromFile, File toFile) throws IOException {
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
