package com.hungle.msmoney.core.backup;

import java.io.File;
import java.util.Calendar;

/**
 * The Class BackupFile.
 */
final class PerDayFile {

    /** The file. */
    private final File file;

    /** The last modified. */
    private final long lastModified;

    /** The calendar. */
    private final Calendar calendar;

    /**
     * Instantiates a new backup file.
     *
     * @param file
     *            the file
     */
    public PerDayFile(File file) {
        this.file = file;
        this.lastModified = file.lastModified();

        this.calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastModified);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the last modified.
     *
     * @return the last modified
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    private static boolean isNewer(PerDayFile file1, PerDayFile file2) {
        return file1.getLastModified() > file2.getLastModified();
    }

    public boolean isNewer(PerDayFile file2) {
        return isNewer(this, file2);
    }

}