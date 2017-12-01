package com.hungle.msmoney.gui.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.qs.AbstractLoadStockSymbolsTask;
import com.hungle.msmoney.qs.QuoteSourceListener;

/**
 * The Class LoadBookmarkAction.
 */
final class LoadBookmarkAction extends AbstractLoadStockSymbolsTask {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The file. */
    private File file;

    /**
     * Instantiates a new load bookmark action.
     *
     * @param file the file
     * @param listener the listener
     * @param threadPool the thread pool
     */
    public LoadBookmarkAction(File file, QuoteSourceListener listener, ExecutorService threadPool) {
        super(file.getName(), listener, threadPool);
        this.file = file;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#getStocks()
     */
    @Override
    protected List<String> getStocks() throws IOException {
        List<String> stocks = new ArrayList<String>();
        OfxUtils.addToList(file.toURI().toURL(), stocks);
        return stocks;
    }

}