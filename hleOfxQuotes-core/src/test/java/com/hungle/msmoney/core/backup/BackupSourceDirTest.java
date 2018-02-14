package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BackupSourceDirTest {
    @Test
    public void testThreeDifferentDates() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        File file = null;
        Calendar now = Calendar.getInstance();
        
        // now
        file = PerDayFileTest.newTodayFile(temporaryFolder, now);

        // tomorrow
        file = PerDayFileTest.newTomorrowFile(temporaryFolder, now);

        // yesterday
        file = PerDayFileTest.newYesterdayFile(temporaryFolder, now);

        Map<Calendar, PerDayFile> buckets = BackupSourceDir.createBuckets(temporaryFolder.getRoot());
        Assert.assertNotNull(buckets);
        Assert.assertEquals(3, buckets.size());
    }

    @Test
    public void testThreeSameDates() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        File file = null;
        Calendar cal = Calendar.getInstance();

        // now
        cal = Calendar.getInstance();

        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        Map<Calendar, PerDayFile> buckets = BackupSourceDir.createBuckets(temporaryFolder.getRoot());
        Assert.assertNotNull(buckets);
        Assert.assertEquals(1, buckets.size());
    }

    @Test
    public void testTwoOne() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        File file = null;
        Calendar cal = Calendar.getInstance();

        // now
        cal = Calendar.getInstance();

        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        // tomorrow
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());

        Map<Calendar, PerDayFile> buckets = BackupSourceDir.createBuckets(temporaryFolder.getRoot());
        Assert.assertNotNull(buckets);
        Assert.assertEquals(2, buckets.size());
    }
}
