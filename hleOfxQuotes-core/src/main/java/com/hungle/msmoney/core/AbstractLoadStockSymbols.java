/**
 * 
 */
package com.hungle.msmoney.core;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.StopWatch;
import com.hungle.msmoney.core.ofx.OfxUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLoadStockSymbols.
 */
public abstract class AbstractLoadStockSymbols implements Runnable {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(AbstractLoadStockSymbols.class);

    /** The max symbols per line. */
    private int maxSymbolsPerLine;

    /**
     * Load stock symbols.
     *
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected abstract List<String> loadStockSymbols() throws IOException;

    /**
     * Will get run in Swing's dispatch loop.
     *
     * @param stockSymbolsString the stock symbols string
     */
    protected abstract void stockSymbolsReceived(final String stockSymbolsString);

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            final String stockSymbolsString = getStockSymbolsString(maxSymbolsPerLine);
            if (stockSymbolsString != null) {
                Runnable doRun = new Runnable() {

                    @Override
                    public void run() {
                        stockSymbolsReceived(stockSymbolsString);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            }
        } catch (IOException e) {
            LOGGER.warn(e);
        }
    }

    /**
     * Gets the stock symbols string.
     *
     * @param maxSymbolsPerLine the max symbols per line
     * @return the stock symbols string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String getStockSymbolsString(int maxSymbolsPerLine) throws IOException {
        String stockSymbolsString = null;
        StopWatch stopWatch = new StopWatch();
        try {
            List<String> stocks = loadStockSymbols();
            stockSymbolsString = OfxUtils.toSeparatedString(stocks);
        } finally {
            long delta = stopWatch.click();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("< getStockSymbolsString, delta=" + delta);
            }
        }
        return stockSymbolsString;
    }

    /**
     * Gets the max symbols per line.
     *
     * @return the max symbols per line
     */
    public int getMaxSymbolsPerLine() {
        return maxSymbolsPerLine;
    }

    /**
     * Sets the max symbols per line.
     *
     * @param maxSymbolsPerLine the new max symbols per line
     */
    public void setMaxSymbolsPerLine(int maxSymbolsPerLine) {
        this.maxSymbolsPerLine = maxSymbolsPerLine;
    }

}