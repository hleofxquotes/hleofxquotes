package com.hungle.msmoney.core.backup;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

public class HashFile {
    private static final Logger LOGGER = Logger.getLogger(HashFile.class);

    static void generateHashFiles(File sourceFile) {
        try {
            File hashFile = null;
            String hashName = null;

            hashName = MD5;
            String md5String = new DigestUtils(hashName).digestAsHex(sourceFile);
            hashFile = writeHash(md5String, sourceFile.getName(), toHashFile(sourceFile, HashFile.MD5_FILE_SUFFIX));
            if (hashFile != null) {
                LOGGER.info("Created hashFile=" + hashFile.getAbsolutePath());
            }

            hashName = SHA_256;
            String sha256String = new DigestUtils(hashName).digestAsHex(sourceFile);
            hashFile = writeHash(sha256String, sourceFile.getName(),
                    toHashFile(sourceFile, HashFile.SHA256_FILE_SUFFIX));
            if (hashFile != null) {
                LOGGER.info("Created hashFile=" + hashFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.warn(e);
        }

    }

    private static File writeHash(String hashString, String sourceFileName, File file) throws IOException {
        if (file.exists()) {
            return null;
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            writeHash(hashString, sourceFileName, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }

        return file;
    }

    private static void writeHash(String hashString, String sourceFileName, PrintWriter writer) {
        writer.println(hashString + " *" + sourceFileName);
    }

    private static File toHashFile(File sourceFile, String suffix) {
        sourceFile = sourceFile.getAbsoluteFile();

        File parentDir = sourceFile.getParentFile();
        String name = sourceFile.getName();
        name = name + suffix;

        File file = new File(parentDir, name);
        return file;
    }

    static final String SHA256_FILE_SUFFIX = ".sha256";
    static final String MD5_FILE_SUFFIX = ".md5";

}
