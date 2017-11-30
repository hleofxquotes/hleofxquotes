package com.hungle.msmoney.statements;

import java.io.File;
import java.util.Date;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.EventList;

/**
 * The Class DownloadStatementsTask.
 */
abstract class DownloadStatementsTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(DownloadStatementsTask.class);

    /** The progress monitor. */
    private final ProgressMonitor progressMonitor;

    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    /** The fi beans. */
    private final EventList<FiBean> fiBeans;

    private final class UpdateProgressMonitorTask implements Runnable {
        private final int progress;
        private final String note;

        private UpdateProgressMonitorTask(int progress, String note) {
            this.progress = progress;
            this.note = note;
        }

        @Override
        public void run() {
            if (progressMonitor != null) {
                progressMonitor.setProgress(progress);
                progressMonitor.setNote(note);
            }
        }
    }

    /**
     * Instantiates a new download statements task.
     * 
     * @param fiBeans
     *            the fi beans
     * @param progressMonitor
     *            the progress monitor
     */
    DownloadStatementsTask(EventList<FiBean> fiBeans, ProgressMonitor progressMonitor) {
        this.fiBeans = fiBeans;
        this.progressMonitor = progressMonitor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {

            int counter = 0;
            for (FiBean fiBean : fiBeans) {

                if (progressMonitor != null) {
                    if (progressMonitor.isCanceled()) {
                        LOGGER.info("User cancels before full completion.");
                        break;
                    }
                }

                final int progress = counter;
                final String note = fiBean.getName() + " (since " + fiBean.getFi().getStartDate() + ")";
                final Runnable updateProgressMonitorTask = new UpdateProgressMonitorTask(progress, note);
                SwingUtilities.invokeLater(updateProgressMonitorTask);

                updatingFi(fiBean);

                counter++;
            }
        } finally {
            updateCompleted();
        }
    }

    protected abstract void updateCompleted(); // {
    // this.statementPanel.updateCompleted(fiBeans, progressMonitor);
    // }

    private void updatingFi(FiBean fiBean) {
        Exception exception = null;
        boolean skip = false;
        LOGGER.info("> START updating fi=" + fiBean.getName());

        try {
            fiBean.setStatus("START");
            fiBean.setException(exception);
            skip = !fiBean.getUpdater().sendRequest();
        } catch (Exception e) {
            LOGGER.error(e);
            exception = e;
        } finally {
            fiBean.setException(exception);

            File respFile = fiBean.getUpdater().getRespFile();
            if ((respFile != null) && (respFile.exists())) {
                fiBean.setLastDownloaded(new Date(respFile.lastModified()));
            } else {
                fiBean.setLastDownloaded(null);
            }

            if (exception == null) {
                if (skip) {
                    fiBean.setStatus("SKIP");
                } else {
                    fiBean.setStatus("SUCCESS");
                }
            } else {
                // fiBean.setStatus("ERROR");
                fiBean.setStatus("ERROR - " + exception.toString());
            }
            fiBean.setDownloaded(true);

            LOGGER.info("< DONE updating fi=" + fiBean.getName());
        }
    }
}