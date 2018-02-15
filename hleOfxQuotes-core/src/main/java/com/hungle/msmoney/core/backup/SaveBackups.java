package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.StopWatch;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveBackups.
 */
public class SaveBackups {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SaveBackups.class);

    private SaveBackupsListener listener;

    /**
     * Save backups.
     *
     * @param fromDir
     *            the in dir
     * @param toDir
     *            the out dir
     * @param password
     *            the password
     * @return
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public SaveBackupsResult saveBackups(File fromDir, File toDir, String password) throws IOException {
        SaveBackupsResult result = new SaveBackupsResult();
        StopWatch stopWatch = new StopWatch();

        try {
            if (listener != null) {
                listener.notifyStartBackup();
            }

            Map<Calendar, PerDayFile> buckets = BackupSourceDir.createBuckets(fromDir);
            result = saveBackups(buckets, toDir, password);
        } finally {
            result.setElapsed(stopWatch.click());

            if (listener != null) {
                listener.notifyDoneBackup();
            }
        }
        return result;
    }

    public void setListener(SaveBackupsListener saveBackupsListener) {
        this.listener = saveBackupsListener;
    }

    private SaveBackupsResult saveBackups(Map<Calendar, PerDayFile> buckets, File toTopDir, String password)
            throws IOException {
        SaveBackupsResult result = new SaveBackupsResult();

        SimpleDateFormat dirNameFormatter = new SimpleDateFormat("yyyy/MM/dd");
        for (PerDayFile perDayFile : buckets.values()) {
            try {
                File bucketDir = toBucketDir(toTopDir, dirNameFormatter, perDayFile);
                File file = perDayFile.getFile();
                saveBackup(file, bucketDir, password, buckets, result);
            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }

        return result;
    }

    private void saveBackup(File file, File dir, String password, Map<Calendar, PerDayFile> buckets,
            SaveBackupsResult result) throws IOException {
        boolean copied = false;
        try {
            if (listener != null) {
                listener.notifyStartCopyFile(file, dir, buckets.size());
            }
            copied = copyFileToDir(file, dir, password);
            if (copied) {
                result.incCopiedCount();
            }
            result.intCount();
        } finally {
            if (listener != null) {
                listener.notifyDoneCopyFile(file, dir, buckets.size(), copied);
            }
        }
    }

    private File toBucketDir(File toDir, SimpleDateFormat dirNameFormatter, PerDayFile bucketFile) {
        String dirName = dirNameFormatter.format(bucketFile.getCalendar().getTime());
        LOGGER.info("###");
        LOGGER.info("> dirName=" + dirName);
        File dir = new File(toDir, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Copy file to dir.
     *
     * @param file
     *            the from file
     * @param dir
     *            the to dir
     * @param password
     *            the password
     * @return
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private boolean copyFileToDir(File file, File dir, String password) throws IOException {
        boolean copied = false;

        File toFile = null;

        List<File> newestList = BackupDestDir.getNewestList(dir);
        
        if ((newestList != null) && (newestList.size() > 0)) {
            File newestFile = newestList.get(0);
            if (file.lastModified() > newestFile.lastModified()) {
                toFile = null;
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Already has latest backup file for toDir=" + dir);
                    LOGGER.debug("  currentBackupFile=" + newestFile);
                    LOGGER.debug("  backupFile=" + file);
                }
                toFile = newestFile;
            }
        }

        if (toFile == null) {
            toFile = new File(dir, file.getName());
            StopWatch stopWatch = new StopWatch();

            try {
                LOGGER.info("> Copying");
                LOGGER.info("  fromFile=" + file);
                LOGGER.info("  toFile=" + toFile);
                copyFile(file, toFile, password);
                copied = true;
            } finally {
                long delta = stopWatch.click();
                LOGGER.info("< DONE, delta=" + delta);
            }
        }

        generateHashFiles(toFile);

        if (newestList.size() > 1) {
            cleanup(newestList.subList(1, newestList.size() - 1));
        }

        return copied;
    }

    private void cleanup(List<File> oldFiles) {
        for (File oldFile : oldFiles) {
            LOGGER.warn("CLEANUP, oldFile=" + oldFile);
        }
    }

    private void generateHashFiles(File file) {
        if (file != null) {
            String name = file.getName();
            if (name.endsWith(HashFile.MD5_FILE_SUFFIX)) {
                // SKIP
            } else if (name.endsWith(HashFile.SHA256_FILE_SUFFIX)) {
                // SKIP
            } else {
                HashFile.generateHashFiles(file);
            }
        }
    }

    /**
     * Copy file.
     *
     * @param fromFile
     *            the from file
     * @param toFile
     *            the to file
     * @param password
     *            the password
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void copyFile(File fromFile, File toFile, String password) throws IOException {
        if (listener != null) {
            listener.notifyCopyFile(fromFile, toFile, password);
        }
        if (password == null) {
            BackupCopier.copyFile(fromFile, toFile);
        } else {
            BackupCopier.copyFileWithPassword(fromFile, toFile, password);
        }
    }

}
