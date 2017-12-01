package com.hungle.msmoney.gui.qs.menu;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.qs.AbstractLoadStockSymbolsTask;
import com.hungle.msmoney.qs.QuoteSourceListener;

final class LoadLSETask extends AbstractLoadStockSymbolsTask {
    private static final long serialVersionUID = 1L;

    LoadLSETask(String name, QuoteSourceListener listener, ExecutorService threadPool) {
        super(name, listener, threadPool);
    }

    @Override
    protected List<String> getStocks() throws IOException {
        return OfxUtils.getLSEList();
    }
}