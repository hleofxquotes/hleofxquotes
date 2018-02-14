package com.hungle.msmoney.core.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BackupDestDir {

    /**
     * Gets the latest file.
     *
     * @param dir
     *            the to dir
     * @return the latest file
     */
    static final List<File> getNewestList(File dir) {
        List<File> list = new ArrayList<File>();

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (BackupDestDir.ignoreFile(file)) {
                    continue;
                }
                list.add(file);
            }
        }

        Comparator<File> comparator = new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                Long v1 = o1.lastModified();
                Long v2 = o2.lastModified();

                // reverse order
                return -(v1.compareTo(v2));
            }
        };
        Collections.sort(list, comparator);

        return list;
    }

    private static boolean isNewer(File file, File file2) {
        return file.lastModified() > file2.lastModified();
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
