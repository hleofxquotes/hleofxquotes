package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PerDayFileTest {
    @Test
    public void testCreate() throws IOException {
        Calendar now = Calendar.getInstance();

        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        PerDayFile perDayFile = null;
        PerDayFile perDayFile2 = null;

        File file = PerDayFileTest.newTodayFile(temporaryFolder, now);
        perDayFile = new PerDayFile(file);

        File file2 = null;

        file2 = PerDayFileTest.newTodayFile(temporaryFolder, now);
        perDayFile2 = new PerDayFile(file2);
        Assert.assertTrue(perDayFile.getCalendar().compareTo(perDayFile2.getCalendar()) == 0);

        file2 = PerDayFileTest.newTomorrowFile(temporaryFolder, now);
        perDayFile2 = new PerDayFile(file2);
        Assert.assertTrue(perDayFile.getCalendar().compareTo(perDayFile2.getCalendar()) < 0);

        file2 = PerDayFileTest.newYesterdayFile(temporaryFolder, now);
        perDayFile2 = new PerDayFile(file2);
        Assert.assertTrue(perDayFile.getCalendar().compareTo(perDayFile2.getCalendar()) > 0);
    }

    public static final File newTestFile(TemporaryFolder temporaryFolder, Calendar templateCal, int offset) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal = (Calendar) cal.clone();
        
        cal.add(Calendar.DATE, offset);
        File file = temporaryFolder.newFile();
        file.setLastModified(cal.getTimeInMillis());
        
        return file;
    }

    public static File newYesterdayFile(TemporaryFolder temporaryFolder, Calendar templateCal) throws IOException {
        return newTestFile(temporaryFolder, templateCal, -1);
    }

    public static File newTodayFile(TemporaryFolder temporaryFolder, Calendar templateCal) throws IOException {
        return newTestFile(temporaryFolder, templateCal, 0);
    }

    public static File newTomorrowFile(TemporaryFolder temporaryFolder, Calendar templateCal) throws IOException {
        return newTestFile(temporaryFolder, templateCal, 1);
    }
}
