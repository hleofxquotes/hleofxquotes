package com.le.tools.moneyutils.ofx.quotes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

public class Utils {
    private static final Logger log = Logger.getLogger(Utils.class);

    public static List<List<String>> splitToSubLists(List<String> stocks, int bucketSize) {
        List<List<String>> subLists = new ArrayList<List<String>>();
        int fromIndex = 0;
        int toIndex = 0;
        int size = stocks.size();
        while (toIndex < size) {
            fromIndex = toIndex;
            toIndex += bucketSize;
            toIndex = Math.min(toIndex, size);
            List<String> subList = stocks.subList(fromIndex, toIndex);
            subLists.add(subList);
        }
        return subLists;
    }

    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        }

        if (str.length() <= 0) {
            return true;
        }

        return false;
    }

    public static boolean compareFiles(File file1, File file2, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest md;
        byte[] digest1;
        byte[] digest2;
        md = MessageDigest.getInstance(algorithm);
        digest1 = Utils.calculateMessageDigest(md, file1);

        md = MessageDigest.getInstance(algorithm);
        digest2 = Utils.calculateMessageDigest(md, file2);

        if (!Arrays.equals(digest1, digest2)) {
            return false;
        }

        return true;
    }

    public static byte[] calculateMessageDigest(MessageDigest md, File file) throws IOException {
        byte[] digest = null;
        DigestInputStream mdIn = null;

        try {
            mdIn = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), md);
            while (mdIn.read() != -1)
                ;

            digest = md.digest();
        } finally {
            if (mdIn != null) {
                mdIn.close();
            }
        }
        return digest;
    }

    public static void copyFile(File fromFile, File toFile) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fromFile));
            writer = new BufferedWriter(new FileWriter(toFile));

            char[] buffer = new char[1024];
            int n = 0;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    writer = null;
                }
            }
        }

    }

    public static boolean compareFiles(File file1, File file2) throws IOException {
        try {
            String algorithm = null;

            algorithm = "SHA1";
            if (!compareFiles(file1, file2, algorithm)) {
                return false;
            }

            algorithm = "MD5";
            if (!compareFiles(file1, file2, algorithm)) {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }

        return true;
    }

    static void copyToFile(URL url, File file) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(url.openStream());
            out = new BufferedOutputStream(new FileOutputStream(file));
            int bufSize = 1024;
            int n = 0;
            byte[] buffer = new byte[bufSize];
            while ((n = in.read(buffer, 0, bufSize)) != -1) {
                out.write(buffer, 0, n);
            }
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }

    public static String encodeHexString(byte[] data) {
        boolean decorated = true;
        return encodeHexString(data, decorated);
    }

    public static String encodeHexString(byte[] data, boolean decorated) {
        String str = Hex.encodeHexString(data);

        if (decorated) {
            str = str.toUpperCase();
            StringBuilder sb = new StringBuilder();
            int len = str.length();
            for (int i = 0; i < len; i++) {
                char c = str.charAt(i);
                if (i > 0) {
                    if ((i % 2) == 0) {
                        sb.append(':');
                    }
                }
                sb.append(c);
            }
            str = sb.toString();
        }

        return str;
    }

    public static String encodeHexString(MessageDigest md) {
        return encodeHexString(md.digest());
    }

}
