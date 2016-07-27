package com.hungle.tools.moneyutils.ofx.quotes;

// TODO: Auto-generated Javadoc
/**
 * The Class StopWatch.
 */
public class StopWatch {
    
    /** The start time. */
    private long startTime;
    
    /** The end time. */
    private long endTime;

    /**
     * Instantiates a new stop watch.
     */
    public StopWatch() {
        click();
    }

    /**
     * Click.
     *
     * @return the long
     */
    public long click() {
        long delta = 0L;

        endTime = System.currentTimeMillis();

        delta = endTime - startTime;

        startTime = endTime;

        return delta;

    }

}
