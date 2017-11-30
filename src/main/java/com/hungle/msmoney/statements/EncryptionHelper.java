package com.hungle.msmoney.statements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class EncryptionHelper.
 */
public class EncryptionHelper {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(EncryptionHelper.class);

    /** The algorithm. */
    private String algorithm = "AES";
    
    /** The block mode. */
    private String blockMode = "CBC";
    
    /** The padding. */
    private String padding = "PKCS5PADDING";

    /** The Constant CBC_SALT. */
    private static final IvParameterSpec CBC_SALT = new IvParameterSpec(new byte[] { 7, 34, 56, 78, 90, 87, 65, 43, 12, 34, 56, 78, -123, 87, 65, 43 });

    /**
     * Encrypt.
     *
     * @param key the key
     * @param in the in
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void encrypt(byte[] key, InputStream in, OutputStream out) throws IOException {
        CipherOutputStream cout = null;
        try {
            SecretKeySpec k = new SecretKeySpec(key, algorithm);
            Cipher c = Cipher.getInstance(algorithm + "/" + blockMode + "/" + padding);
            c.init(Cipher.ENCRYPT_MODE, k, CBC_SALT);

            cout = new CipherOutputStream(out, c);
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int n = 0;
            while ((n = in.read(buffer, 0, bufSize)) != -1) {
                cout.write(buffer, 0, n);
            }
        } catch (InvalidKeyException e) {
            throw new IOException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (NoSuchPaddingException e) {
            throw new IOException(e);
        } finally {
            if (cout != null) {
                try {
                    cout.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    cout = null;
                }
            }
        }
    }

    /**
     * Decrypt.
     *
     * @param key the key
     * @param in the in
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void decrypt(byte[] key, InputStream in, OutputStream out) throws IOException {
        CipherInputStream cin = null;
        try {
            SecretKeySpec k = new SecretKeySpec(key, algorithm);
            Cipher c = Cipher.getInstance(algorithm + "/" + blockMode + "/" + padding);
            c.init(Cipher.DECRYPT_MODE, k, CBC_SALT);
            cin = new CipherInputStream(in, c);
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int n = 0;
            while ((n = cin.read(buffer, 0, bufSize)) != -1) {
                out.write(buffer, 0, n);
            }
        } catch (InvalidKeyException e) {
            throw new IOException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (NoSuchPaddingException e) {
            throw new IOException(e);
        } finally {
            if (cin != null) {
                try {
                    cin.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    cin = null;
                }
            }
        }
    }

    /**
     * Decrypt file.
     *
     * @param password the password
     * @param fromFileName the from file name
     * @param toFileName the to file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void decryptFile(String password, String fromFileName, String toFileName) throws IOException {
        EncryptionHelper helper = new EncryptionHelper();
        byte[] key = toKey(password, 16);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new Base64InputStream(new FileInputStream(new File(fromFileName))));
            out = new BufferedOutputStream(new FileOutputStream(new File(toFileName)));
            helper.decrypt(key, in, out);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    in = null;
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    out = null;
                }
            }
        }
    }

    /**
     * Encrypt file.
     *
     * @param password the password
     * @param fromFileName the from file name
     * @param toFileName the to file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void encryptFile(String password, String fromFileName, String toFileName) throws IOException {
        EncryptionHelper helper = new EncryptionHelper();
        byte[] key = toKey(password, 16);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(fromFileName)));
            out = new BufferedOutputStream(new Base64OutputStream(new FileOutputStream(new File(toFileName))));
            helper.encrypt(key, in, out);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    in = null;
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    out = null;
                }
            }
        }
    }

    /**
     * To key.
     *
     * @param password the password
     * @param max the max
     * @return the byte[]
     */
    private static byte[] toKey(String password, int max) {
        byte[] key = new byte[max];
        byte[] bytes = password.getBytes();
        int bytesLength = bytes.length;
        for (int i = 0; i < key.length; i++) {
            key[i] = bytes[i % bytesLength];
        }
        return key;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // encrypt 012345678901234567890123456789 target/in.jar
        // target/encrypt.txt
        // decrypt 012345678901234567890123456789 target/encrypt.txt
        // target/decrypt.txt
        // encrypt 123hle target/in.jar target/encrypt.txt
        // decrypt 123hle target/encrypt.txt target/decrypt.txt
        if (args.length != 4) {
            Class<EncryptionHelper> clz = EncryptionHelper.class;
            System.out.println("Usage: java " + clz.getName() + " encrypt/decrypt password fromFile toFile");
            System.exit(1);
        }

        String command = args[0];
        String password = args[1];
        String fromFileName = args[2];
        String toFileName = args[3];
        try {
            if (command.compareToIgnoreCase("encrypt") == 0) {
                log.info("encrypt: " + fromFileName + " -> " + toFileName);
                EncryptionHelper.encryptFile(password, fromFileName, toFileName);
            } else if (command.compareToIgnoreCase("decrypt") == 0) {
                log.info("decrypt: " + fromFileName + " -> " + toFileName);
                EncryptionHelper.decryptFile(password, fromFileName, toFileName);
            } else {
                log.error("Not a valid command=" + command);
                System.exit(1);
            }
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
