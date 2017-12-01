package com.hungle.msmoney.gui.qs;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.http.HttpEntity;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.qs.net.GetQuotesListener;

/**
 * The Class GetQuotesProgressMonitor.
 */
final class GetQuotesProgressMonitor implements GetQuotesListener {
    private static final Logger LOGGER = Logger.getLogger(GetQuotesProgressMonitor.class);

    /** The sub task size. */
    private AtomicInteger subTaskSize = new AtomicInteger(0);

    /** The completed tasks. */
    private AtomicInteger completedTasks = new AtomicInteger(0);

    /** The progress bar. */
    private JProgressBar progressBar = null;

    /**
     * Instantiates a new gets the quotes progress monitor.
     *
     * @param progressBar
     *            the progress bar
     */
    public GetQuotesProgressMonitor(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#started(java.
     * util.List)
     */
    @Override
    public void started(List<String> stocks) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> STARTED stocks=" + stocks);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#ended(java.util.
     * List, java.util.List, long)
     */
    @Override
    public void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> ENDED stocks=" + stocks + ", delta=" + delta);
        }
        completedTasks.getAndIncrement();

        Runnable doRun = new Runnable() {

            @Override
            public void run() {
                int percentage = (completedTasks.get() * 100) / subTaskSize.get();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("  progressBar % " + percentage);
                }
                if (progressBar != null) {
                    progressBar.setValue(percentage);
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#setSubTaskSize(
     * int)
     */
    @Override
    public void setSubTaskSize(int size) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setSubTaskSize=" + size);
        }
        this.subTaskSize.set(size);
        this.completedTasks.set(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#
     * httpEntityReceived(org.apache.http.HttpEntity)
     */
    @Override
    public void httpEntityReceived(HttpEntity entity) {
        // TODO Auto-generated method stub

    }
}