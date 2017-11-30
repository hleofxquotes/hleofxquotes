/**
 * 
 */
package com.hungle.tools.moneyutils.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.swing.AbstractAction;

import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.QuoteSource;
import com.hungle.tools.moneyutils.ofx.quotes.QuoteSourceListener;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLoadStockSymbolsTask.
 */
public abstract class AbstractLoadStockSymbolsTask extends AbstractAction {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The listener. */
    private QuoteSourceListener listener = null;

    /** The max symbols per line. */
    private int maxSymbolsPerLine = 20;

    /** The thread pool. */
    private ExecutorService threadPool;

    /**
     * Instantiates a new abstract load stock symbols task.
     *
     * @param name the name
     * @param listener the listener
     * @param threadPool the thread pool
     */
    public AbstractLoadStockSymbolsTask(String name, QuoteSourceListener listener, ExecutorService threadPool) {
        super(name);
        this.listener = listener;
        this.threadPool = threadPool;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        AbstractLoadStockSymbols command = new AbstractLoadStockSymbols() {

            @Override
            protected List<String> loadStockSymbols() throws IOException {
                return AbstractLoadStockSymbolsTask.this.getStocks();
            }

            @Override
            protected void stockSymbolsReceived(String stockSymbols) {
                AbstractLoadStockSymbolsTask.this.stockSymbolsStringReceived(stockSymbols);
            }

        };
        command.setMaxSymbolsPerLine(maxSymbolsPerLine);
        threadPool.execute(command);
    }

    /**
     * Stock symbols string received.
     *
     * @param stockSymbolsString the stock symbols string
     */
    protected void stockSymbolsStringReceived(String stockSymbolsString) {
        int maxTokens = 5;
        String sep = ",";
        String lines = OfxUtils.breakLines(stockSymbolsString, maxTokens, sep);
        if (this.listener != null) {
            QuoteSource quoteSource = null;
            this.listener.stockSymbolsStringReceived(quoteSource, lines);
        }
    }

    /**
     * Gets the stocks.
     *
     * @return the stocks
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected abstract List<String> getStocks() throws IOException;

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