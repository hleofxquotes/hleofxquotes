/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.swing.AbstractAction;

public abstract class AbstractLoadStockSymbolsTask extends AbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private QuoteSourceListener listener = null;

    private int maxSymbolsPerLine = 20;

    private ExecutorService threadPool;

    public AbstractLoadStockSymbolsTask(String name, QuoteSourceListener listener, ExecutorService threadPool) {
        super(name);
        this.listener = listener;
        this.threadPool = threadPool;
    }

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

    protected void stockSymbolsStringReceived(String stockSymbolsString) {
        int maxTokens = 5;
        String sep = ",";
        String lines = OfxUtils.breakLines(stockSymbolsString, maxTokens, sep);
        if (this.listener != null) {
            QuoteSource quoteSource = null;
            this.listener.stockSymbolsStringReceived(quoteSource, lines);
        }
    }

    protected abstract List<String> getStocks() throws IOException;

    public int getMaxSymbolsPerLine() {
        return maxSymbolsPerLine;
    }

    public void setMaxSymbolsPerLine(int maxSymbolsPerLine) {
        this.maxSymbolsPerLine = maxSymbolsPerLine;
    }
}