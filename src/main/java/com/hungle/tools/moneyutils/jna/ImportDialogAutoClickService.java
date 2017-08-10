package com.hungle.tools.moneyutils.jna;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ImportDialogAutoClickService.
 */
public class ImportDialogAutoClickService {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(ImportDialogAutoClickService.class);

    /** The thread pool. */
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);

    /** The enable. */
    private boolean enable;

    /** The shutting down. */
    private boolean shuttingDown = false;

    /** The semaphore. */
    private final Semaphore semaphore = new Semaphore(1);

    /**
     * Schedule.
     */
    public void schedule() {
        long initialDelay = 1L;
        TimeUnit unit = TimeUnit.SECONDS;
        long period = 1L;
        threadPool.scheduleAtFixedRate(new ImportDialogAutoClickTask(this), initialDelay, period, unit);
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        LOGGER.info("> shutdown");
        this.shuttingDown = true;

        if (threadPool == null) {
            return;
        }
        if (threadPool.isShutdown()) {
            return;
        }
        if (threadPool.isTerminated()) {
            return;
        }

        try {
            int maxWait = 5;
            try {
                if (!semaphore.tryAcquire(maxWait, TimeUnit.SECONDS)) {
                    LOGGER.warn("Timeout waiting to obtain semaphore during shutdown");
                }
            } catch (InterruptedException e1) {
                LOGGER.error(e1, e1);
            }

            List<Runnable> tasks = threadPool.shutdownNow();
            LOGGER.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;
            try {
                LOGGER.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                if (!threadPool.awaitTermination(timeout, unit)) {
                    LOGGER.warn("Timed-out waiting for threadPool.awaitTermination");
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } finally {
                LOGGER.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            }
        } finally {
            threadPool = null;
        }
    }

    /**
     * Checks if is shutting down.
     *
     * @return true, if is shutting down
     */
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     * Checks if is enable.
     *
     * @return true, if is enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Sets the enable.
     *
     * @param enable the new enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Gets the semaphore.
     *
     * @return the semaphore
     */
    public Semaphore getSemaphore() {
        return semaphore;
    }

}
