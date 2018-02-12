package com.hungle.msmoney.core.backup;

import java.io.File;

public class BackupToDir {

    /**
     * Gets the latest file.
     *
     * @param dir
     *            the to dir
     * @return the latest file
     */
    static final File getLastModifiedFile(File dir) {
        File lastModifiedFile = null;
    
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (BackupToDir.ignoreFile(file)) {
                    continue;
                }
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

    private static boolean ignoreFile(File file) {
        if (file == null) {
            return true;
        }
        if (file.isDirectory()) {
            return true;
        }
        String name = file.getName();
        if (name.endsWith(HashFile.MD5_FILE_SUFFIX)) {
            return true;
        }
        if (name.endsWith(HashFile.SHA256_FILE_SUFFIX)) {
            return true;
        }
        return false;
    }

}
