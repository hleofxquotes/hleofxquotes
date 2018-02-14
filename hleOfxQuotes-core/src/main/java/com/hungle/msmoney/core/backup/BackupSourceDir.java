package com.hungle.msmoney.core.backup;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class BackupSourceDir {
    private static final Logger LOGGER = Logger.getLogger(BackupSourceDir.class);

    static final Map<Calendar, PerDayFile> createBuckets(File dir) {
        // buckets.clear();
        Map<Calendar, PerDayFile> buckets = new TreeMap<Calendar, PerDayFile>();

        File[] files = dir.listFiles();
        for (File file : files) {
            PerDayFile dailyFile = new PerDayFile(file);
            Calendar key = dailyFile.getCalendar();
            PerDayFile currentDailyFile = buckets.get(key);

            if (currentDailyFile == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New file for cal=" + key.getTime());
                    LOGGER.debug("    > " + dailyFile.getFile());
                }
                buckets.put(key, dailyFile);
            } else {
                if (dailyFile.isNewer(currentDailyFile)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Newer file for cal=" + key.getTime());
                        LOGGER.debug("    < " + currentDailyFile.getFile());
                        LOGGER.debug("    > " + dailyFile.getFile());
                    }
                    buckets.put(key, dailyFile);
                }
            }
        }

        return buckets;
    }

}
