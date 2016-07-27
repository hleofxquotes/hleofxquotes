/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public abstract class AbstractLoadStockSymbols implements Runnable {
    private static final Logger log = Logger.getLogger(AbstractLoadStockSymbols.class);

    private int maxSymbolsPerLine;

    protected abstract List<String> loadStockSymbols() throws IOException;

    /**
     * Will get run in Swing's dispatch loop.
     * 
     * @param stockSymbolsString
     */
    protected abstract void stockSymbolsReceived(final String stockSymbolsString);

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
            log.warn(e);
        }
    }

    private String getStockSymbolsString(int maxSymbolsPerLine) throws IOException {
        String stockSymbolsString = null;
        StopWatch stopWatch = new StopWatch();
        try {
            List<String> stocks = loadStockSymbols();
            stockSymbolsString = OfxUtils.toSeparatedString(stocks);
        } finally {
            long delta = stopWatch.click();
            if (log.isDebugEnabled()) {
                log.debug("< getStockSymbolsString, delta=" + delta);
            }
        }
        return stockSymbolsString;
    }

    public int getMaxSymbolsPerLine() {
        return maxSymbolsPerLine;
    }

    public void setMaxSymbolsPerLine(int maxSymbolsPerLine) {
        this.maxSymbolsPerLine = maxSymbolsPerLine;
    }

}