package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CheckBackupDir extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = Logger.getLogger(CheckBackupDir.class);
    private FileFilter isFilefilter;

    public CheckBackupDir() {
        isFilefilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        };
    }

    public static void main(String[] args) {
        Path start = null;

        if (args.length == 1) {

        } else {
            Class<CheckBackupDir> clz = CheckBackupDir.class;
            System.out.println("Usage: java " + clz.getName() + " dir");
            System.exit(1);
        }

        File dir = new File(args[0]);
        if (!dir.exists()) {
            LOGGER.error("Does not exist, dir=" + dir.getAbsolutePath());
            System.exit(1);
        }
        if (!dir.isDirectory()) {
            LOGGER.error("Not a directory, dir=" + dir.getAbsolutePath());
            System.exit(1);
        }

        start = dir.toPath();
        CheckBackupDir visitor = new CheckBackupDir();
        try {
            Files.walkFileTree(start, visitor);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }

    }

    @Override
    public FileVisitResult postVisitDirectory(Path pathDir, IOException exc) throws IOException {
        File dir = pathDir.toFile();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dir=" + dir.getAbsolutePath());
        }

        List<File> foundFiles = listFiles(dir, isFilefilter);
        int filesCount = foundFiles.size();

        if (filesCount == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn(
                        "Directory=" + dir.getAbsolutePath() + ": has " + filesCount + " files. Should have only 1.");
            }
        } else if (filesCount == 1) {
            // OK
        } else {
            LOGGER.warn("Directory=" + dir.getAbsolutePath() + ": has " + filesCount + " files. Should have only 1.");
            for (File file : foundFiles) {
                LOGGER.warn("  " + file.getAbsolutePath());
            }
        }

        return super.postVisitDirectory(pathDir, exc);
    }

    private List<File> listFiles(File dir, FileFilter filter) {
        List<File> filesFound = new ArrayList<File>();

        File[] files = dir.listFiles(filter);
        if (files == null) {
            // OK
        } else {
            if (files.length == 0) {
                // OK
            } else {
                // has some files
                for (File file : files) {
                    String name = file.getName();
                    if (name.endsWith(HashFile.MD5_FILE_SUFFIX)) {
                        // IGNORE
                    } else if (name.endsWith(HashFile.SHA256_FILE_SUFFIX)) {
                        // IGNORE
                    } else {
                        filesFound.add(file);
                    }
                }
            }
        }
        return filesFound;
    }
}
