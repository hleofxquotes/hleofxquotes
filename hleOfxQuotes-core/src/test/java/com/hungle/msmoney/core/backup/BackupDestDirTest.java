package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BackupDestDirTest {
    @Test
    public void testDirWithThreeSameFiles() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        Calendar now = Calendar.getInstance();

        // create three files with today dates
        List<File> files = new ArrayList<File>();
        for (int i = 0; i < 3; i++) {
            File file = PerDayFileTest.newTodayFile(temporaryFolder, now);
            files.add(file);
        }

        List<File> sortedList = BackupDestDir.getNewestList(temporaryFolder.getRoot());
        Assert.assertNotNull(sortedList);
        Assert.assertTrue(sortedList.size() > 0);
        File newestFile = sortedList.get(0);
        Assert.assertNotNull(newestFile);
        File file = files.get(0);
        Assert.assertTrue(file.getAbsolutePath().compareTo(newestFile.getAbsolutePath()) == 0);
    }

    @Test
    public void testDirWithThreeDifferentFiles() throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        Calendar now = Calendar.getInstance();

        // create three files with different dates
        List<File> files = new ArrayList<File>();
        for (int i = 0; i < 3; i++) {
            File file = PerDayFileTest.newTestFile(temporaryFolder, now, i);
            files.add(file);
        }

        List<File> sortedList = BackupDestDir.getNewestList(temporaryFolder.getRoot());
        Assert.assertNotNull(sortedList);
        Assert.assertTrue(sortedList.size() > 0);
        File newestFile = sortedList.get(0);
        Assert.assertNotNull(newestFile);

        File file = files.get(files.size() - 1);
        Assert.assertTrue(file.getAbsolutePath().compareTo(newestFile.getAbsolutePath()) == 0);
    }
}
