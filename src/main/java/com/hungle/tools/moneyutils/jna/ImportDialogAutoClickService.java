package com.le.tools.moneyutils.jna;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ImportDialogAutoClickService {
    private static final Logger log = Logger.getLogger(ImportDialogAutoClickService.class);

    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);

    private boolean enable;

    private boolean shuttingDown = false;

    private final Semaphore semaphore = new Semaphore(1);

    public void schedule() {
        long initialDelay = 1L;
        TimeUnit unit = TimeUnit.SECONDS;
        long period = 1L;
        threadPool.scheduleAtFixedRate(new ImportDialogAutoClickTask(this), initialDelay, period, unit);
    }

    public void shutdown() {
        log.info("> shutdown");
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
                    log.warn("Timeout waiting to obtain semaphore during shutdown");
                }
            } catch (InterruptedException e1) {
                log.error(e1, e1);
            }

            List<Runnable> tasks = threadPool.shutdownNow();
            log.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;
            try {
                log.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                if (!threadPool.awaitTermination(timeout, unit)) {
                    log.warn("Timed-out waiting for threadPool.awaitTermination");
                }
            } catch (InterruptedException e) {
                log.error(e, e);
            } finally {
                log.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            }
        } finally {
            threadPool = null;
        }
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

}
