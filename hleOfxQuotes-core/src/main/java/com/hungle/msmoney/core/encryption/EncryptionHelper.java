package com.hungle.msmoney.core.encryption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.common.ReaderInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class EncryptionHelper.
 */
public class EncryptionHelper {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(EncryptionHelper.class);

    /** The Constant ENCRYPTED_SUFFIX. */
    private static final String ENCRYPTED_SUFFIX = "-enc";

    /** The Constant DEFAULT_PADDING. */
    private static final String DEFAULT_PADDING = "PKCS5Padding";

    /** The Constant DEFAULT_BLOCK_MODE. */
    private static final String DEFAULT_BLOCK_MODE = "CBC";

    /** The Constant DEFAULT_ALGORITHM. */
    private static final String DEFAULT_ALGORITHM = "AES";

    /** The Constant DEFAULT_SALT_BYTES. */
    public static final byte[] DEFAULT_SALT_BYTES = new byte[] { 7, 34, 56, 78, 90, 87, 65, 43, 12, 34, 56, 78, -123, 87, 65, 43 };
    
    /** The Constant DEFAULT_SALT. */
    private static final IvParameterSpec DEFAULT_SALT = new IvParameterSpec(DEFAULT_SALT_BYTES);

    /** The algorithm. */
    private String algorithm = DEFAULT_ALGORITHM;
    
    /** The block mode. */
    private String blockMode = DEFAULT_BLOCK_MODE;
    
    /** The padding. */
    private String padding = DEFAULT_PADDING;

    /** The transformation. */
    private String transformation;
    
    /** The salt. */
    private final IvParameterSpec salt;

    /** The cipher. */
    private final Cipher cipher;

    /** The key store file name. */
    private String keyStoreFileName = ".lfz";

    /** The tag. */
    private String tag = "hleofxquotes";

    /**
     * Instantiates a new encryption helper.
     *
     * @param salt the salt
     * @throws EncryptionHelperException the encryption helper exception
     */
    public EncryptionHelper(IvParameterSpec salt) throws EncryptionHelperException {
        super();
        this.transformation = algorithm + "/" + blockMode + "/" + padding;
        try {
            this.cipher = Cipher.getInstance(transformation);
            if (salt == null) {
                this.salt = DEFAULT_SALT;
            } else {
                this.salt = salt;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionHelperException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionHelperException(e);
        }
    }

    /**
     * Instantiates a new encryption helper.
     *
     * @throws EncryptionHelperException the encryption helper exception
     */
    public EncryptionHelper() throws EncryptionHelperException {
        this(null);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File inFile = null;
        String suffix = ENCRYPTED_SUFFIX;

        String fileName = null;

        if (args.length == 1) {
            fileName = args[0];
        } else {
            Class<EncryptionHelper> clz = EncryptionHelper.class;
            System.out.println("Usage: java " + clz.getName() + " file[" + suffix + "]");
            System.exit(1);
        }

        inFile = new File(fileName + suffix);

        EncryptionHelper encryptionHelper = null;
        try {
            encryptionHelper = new EncryptionHelper();
            encryptionHelper.processFile(inFile);
        } catch (EncryptionHelperException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

    /**
     * Process file.
     *
     * @param inFile the in file
     * @throws EncryptionHelperException the encryption helper exception
     */
    public void processFile(File inFile) throws EncryptionHelperException {
        SecretKey key;

        try {
            key = getKey(inFile);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("key=" + key.getAlgorithm() + "/" + key.getFormat());
            }
            LOGGER.info("inFile=" + inFile);
            if (isEncryptedFile(inFile)) {
                decryptFile(inFile, key);
            } else {
                encryptFile(inFile, key);
            }
        } catch (Exception e) {
            throw new EncryptionHelperException(e);
        }
    }

    /**
     * Gets the key.
     *
     * @param inFile the in file
     * @return the key
     * @throws EncryptionHelperException the encryption helper exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public SecretKey getKey(File inFile) throws EncryptionHelperException, IOException {
        SecretKey key;
        File keyFile = getKeyFile(inFile);
        try {
            if (keyFile.exists()) {
                key = readKey(keyFile);
            } else {
                String keyAlgorithm = algorithm;
                key = generatingKey(keyAlgorithm, keyFile, transformation, salt);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionHelperException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionHelperException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionHelperException(e);
        }
        if (key == null) {
            throw new EncryptionHelperException("No available key.");
        }
        return key;
    }

    /**
     * Encrypt file.
     *
     * @param inFile the in file
     * @param key the key
     * @throws InvalidKeyException the invalid key exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void encryptFile(File inFile, SecretKey key) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        int encryptMode = Cipher.ENCRYPT_MODE;
        cipher.init(encryptMode, key, salt);
        encryptFile(inFile, cipher);
    }

    /**
     * Decrypt file.
     *
     * @param inFile the in file
     * @param key the key
     * @throws InvalidKeyException the invalid key exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void decryptFile(File inFile, SecretKey key) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        int encryptMode = Cipher.DECRYPT_MODE;
        cipher.init(encryptMode, key, salt);
        decryptFile(inFile, cipher);
    }

    /**
     * Generating key.
     *
     * @param keyAlgorithm the key algorithm
     * @param keyFile the key file
     * @param transformation the transformation
     * @param salt the salt
     * @return the secret key spec
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     * @throws NoSuchPaddingException the no such padding exception
     */
    private SecretKeySpec generatingKey(final String keyAlgorithm, File keyFile, String transformation, IvParameterSpec salt) throws NoSuchAlgorithmException,
            IOException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        SecretKeySpec key = null;
        int[] keySizes = { 256, 192, 128, };
        for (int keySize : keySizes) {
            try {

                key = generatingSecretKeySpec(keyAlgorithm, keySize);

                // test the key
                boolean testKey = true;
                if (testKey) {
                    int encryptMode = 0;
                    Cipher cipher = Cipher.getInstance(transformation);
                    encryptMode = Cipher.ENCRYPT_MODE;
                    cipher.init(encryptMode, key, salt);
                    encryptMode = Cipher.DECRYPT_MODE;
                    cipher.init(encryptMode, key, salt);
                }

                writeKey(key, keyFile);

                return key;
            } catch (InvalidKeyException e) {
                LOGGER.warn("Cannot generate a key, keyAlgorithm=" + keyAlgorithm + ", keySize=" + keySize + " ...");

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.warn(e);
                }
            }
        }
        return null;
    }

    /**
     * Read key.
     *
     * @param keyFile the key file
     * @return the secret key
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private SecretKey readKey(File keyFile) throws IOException {
        SecretKey secretKey = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read key from keyFile=" + keyFile);
        }

        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(keyFile));

            KeyStore ks = getKeyStoreInstance();
            char[] password = getPassword();
            ks.load(in, password);

            ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry pkEntry = (KeyStore.SecretKeyEntry) ks.getEntry(tag, protParam);
            secretKey = pkEntry.getSecretKey();
        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (CertificateException e) {
            throw new IOException(e);
        } catch (UnrecoverableEntryException e) {
            throw new IOException(e);
        } finally {
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
        return secretKey;
    }

    /**
     * Gets the key store instance.
     *
     * @return the key store instance
     * @throws KeyStoreException the key store exception
     */
    private KeyStore getKeyStoreInstance() throws KeyStoreException {
        return KeyStore.getInstance("JCEKS");
    }

    /**
     * Write key.
     *
     * @param secretKey the secret key
     * @param keyFile the key file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeKey(SecretKey secretKey, File keyFile) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("write key to keyFile=" + keyFile);
        }

        BufferedOutputStream out = null;
        try {
            KeyStore ks = getKeyStoreInstance();
            char[] password = getPassword();
            ks.load(null, password);

            ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);
            ks.setEntry(tag, skEntry, protParam);
            out = new BufferedOutputStream(new FileOutputStream(keyFile));
            ks.store(out, password);
        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (CertificateException e) {
            throw new IOException(e);
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
        }

    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    private char[] getPassword() {
        return tag.toCharArray();
    }

    /**
     * Generating secret key spec.
     *
     * @param keyAlgorithm the key algorithm
     * @param keySize the key size
     * @return the secret key spec
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static SecretKeySpec generatingSecretKeySpec(final String keyAlgorithm, int keySize) throws NoSuchAlgorithmException, IOException {
        LOGGER.info("> generating key, keyAlgorithm=" + keyAlgorithm + ", keySize=" + keySize + " ...");

        final SecretKey secretKey = generatingSecretKey(keyAlgorithm, keySize);

        final byte[] keyAsBytes = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(keyAsBytes, keyAlgorithm);
        return key;
    }

    /**
     * Generating secret key.
     *
     * @param algorithm the algorithm
     * @param keySize the key size
     * @return the secret key
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static SecretKey generatingSecretKey(final String algorithm, int keySize) throws NoSuchAlgorithmException {
        final KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(keySize);
        final SecretKey secretKey = kg.generateKey();
        return secretKey;
    }

    /**
     * Gets the key file.
     *
     * @param file the file
     * @return the key file
     */
    private File getKeyFile(File file) {
        File parentFile = file.getAbsoluteFile().getParentFile();
        File keyFile = new File(parentFile, keyStoreFileName);
        return keyFile;
    }

    /**
     * Checks if is encrypted file.
     *
     * @param inFile the in file
     * @return true, if is encrypted file
     */
    private static boolean isEncryptedFile(File inFile) {
        return inFile.getName().endsWith(ENCRYPTED_SUFFIX);
    }

    /**
     * Decrypt file.
     *
     * @param inFile the in file
     * @param cipher the cipher
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void decryptFile(File inFile, Cipher cipher) throws IOException {
        File outFile = getDecryptedFile(inFile);
        LOGGER.info("outFile=" + outFile);
        decryptFile(inFile, outFile, cipher);
    }

    /**
     * Decrypt file.
     *
     * @param inFile the in file
     * @param outFile the out file
     * @param cipher the cipher
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void decryptFile(File inFile, File outFile, Cipher cipher) throws IOException {
        LOGGER.info("> decryptFile");
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new CipherInputStream(new FileInputStream(inFile), cipher);
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            long bytes = 0;
            try {
                LOGGER.info("> START copying ...");
                bytes = copyStreams(in, out);
            } finally {
                LOGGER.info("< DONE copying, bytes=" + bytes);
            }
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

    /**
     * Gets the decrypted file.
     *
     * @param file the file
     * @return the decrypted file
     */
    private static File getDecryptedFile(File file) {
        // fileName.xxx-enc -> fileName.xxx

        File parentFile = file.getAbsoluteFile().getParentFile();
        String name = file.getName();
        name = name.substring(0, name.length() - 4);
        File decryptedFile = new File(parentFile, name);

        return decryptedFile;
    }

    /**
     * Encrypt file.
     *
     * @param inFile the in file
     * @param cipher the cipher
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static File encryptFile(File inFile, Cipher cipher) throws IOException {
        File outFile = getEncryptedFile(inFile);
        LOGGER.info("outFile=" + outFile);

        encryptFile(inFile, outFile, cipher);
        return outFile;
    }

    /**
     * Gets the encrypted file.
     *
     * @param file the file
     * @return the encrypted file
     */
    private static File getEncryptedFile(File file) {
        // fileName.xxx -> fileName.xxx-enc

        File parentFile = file.getAbsoluteFile().getParentFile();
        String name = file.getName();
        name = name + ENCRYPTED_SUFFIX;
        File encryptedFile = new File(parentFile, name);

        return encryptedFile;
    }

    /**
     * Encrypt file.
     *
     * @param inFile the in file
     * @param outFile the out file
     * @param cipher the cipher
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void encryptFile(File inFile, File outFile, Cipher cipher) throws IOException {
        LOGGER.info("> encryptFile");
        LOGGER.info("  cipher=" + cipher.getAlgorithm());
        LOGGER.info("  outFile=" + outFile);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(inFile));
            out = new CipherOutputStream(new FileOutputStream(outFile), cipher);
            long bytes = 0;
            try {
                LOGGER.info("> START copying ...");
                bytes = copyStreams(in, out);
            } finally {
                LOGGER.info("< DONE copying, bytes=" + bytes);
            }
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

    /**
     * Copy streams.
     *
     * @param in the in
     * @param out the out
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static long copyStreams(InputStream in, OutputStream out) throws IOException {
        long bytes = 0;
        int bufSize = 1024;
        byte[] buffer = new byte[bufSize];
        int n = 0;
        while ((n = in.read(buffer, 0, bufSize)) != -1) {
            out.write(buffer, 0, n);
            bytes += n;
        }
        return bytes;
    }

    /**
     * Encrypt.
     *
     * @param reader the reader
     * @param outFile the out file
     * @param key the key
     * @throws EncryptionHelperException the encryption helper exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void encrypt(BufferedReader reader, File outFile, SecretKey key) throws EncryptionHelperException, IOException {
        try {
            int encryptMode = Cipher.ENCRYPT_MODE;
            cipher.init(encryptMode, key, salt);
            LOGGER.info("> encrypt");

            InputStream in = null;
            OutputStream out = null;
            try {
                String encoding = "UTF-8";
                in = new BufferedInputStream(new ReaderInputStream(reader, encoding));
                out = new CipherOutputStream(new FileOutputStream(outFile), cipher);
                long bytes = 0;
                try {
                    LOGGER.info("> START copying ...");
                    copyStreams(in, out);
                } finally {
                    LOGGER.info("< DONE copying, bytes=" + bytes);
                }
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
        } catch (InvalidKeyException e) {
            throw new EncryptionHelperException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionHelperException(e);
        }
    }

    /**
     * Gets the cipher.
     *
     * @return the cipher
     */
    public Cipher getCipher() {
        return cipher;
    }

    /**
     * Gets the salt.
     *
     * @return the salt
     */
    public IvParameterSpec getSalt() {
        return salt;
    }
}
