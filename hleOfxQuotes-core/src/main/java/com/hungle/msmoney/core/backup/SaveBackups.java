package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        SaveBackupsResult result = null;
        StopWatch stopWatch = new StopWatch();

        try {
            if (listener != null) {
                listener.notifyStartBackup();
            }

            Map<Calendar, DailyFile> buckets = BackupSourceDir.createBuckets(fromDir);
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

    private SaveBackupsResult saveBackups(Map<Calendar, DailyFile> buckets, File toTopDir, String password)
            throws IOException {
        SaveBackupsResult result = new SaveBackupsResult();

        SimpleDateFormat dirNameFormatter = new SimpleDateFormat("yyyy/MM/dd");
        for (DailyFile bucketFile : buckets.values()) {
            File bucketDir = toBucketDir(toTopDir, dirNameFormatter, bucketFile);
            File file = bucketFile.getFile();
            saveBackup(file, bucketDir, password, buckets, result);
        }

        return result;
    }

    private void saveBackup(File file, File dir, String password, Map<Calendar, DailyFile> buckets,
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

    private File toBucketDir(File toDir, SimpleDateFormat dirNameFormatter, DailyFile bucketFile) {
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

        File lastModifiedFile = BackupToDir.getLastModifiedFile(dir);
        if (lastModifiedFile != null) {
            DailyFile currentDailyFile = new DailyFile(lastModifiedFile);
            DailyFile dailyFile = new DailyFile(file);
            if (DailyFile.isNewer(currentDailyFile, dailyFile)) {
                LOGGER.info("Already has latest backup file for toDir=" + dir);
                LOGGER.info("  currentBackupFile=" + currentDailyFile.getFile());
                LOGGER.info("  backupFile=" + dailyFile.getFile());
                // return false;
                toFile = lastModifiedFile;
                copied = false;
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

        return copied;
    }

    private void generateHashFiles(File toFile) {
        if (toFile != null) {
            String name = toFile.getName();
            if (name.endsWith(HashFile.MD5_FILE_SUFFIX)) {
                // SKIP
            } else if (name.endsWith(HashFile.SHA256_FILE_SUFFIX)) {
                // SKIP
            } else {
                HashFile.generateHashFiles(toFile);
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
