package com.hungle.msmoney.gui.task;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.qs.QuoteSource;

import ca.odell.glazedlists.EventList;

/**
 * The Class StockPricesReceivedTask.
 */
public final class StockPricesReceivedTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(StockPricesReceivedTask.class);

    /**
     * 
     */
    private final GUI gui;

    /** The beans. */
    private final List<AbstractStockPrice> prices;

    /** The bad price. */
    private final Double badPrice;

    /** The fx table. */
    private final FxTable fxTable;

    /** The has wrapped share count. */
    private final boolean hasWrappedShareCount;

    /** The symbol mapper. */
    private final SymbolMapper symbolMapper;

    /** The quote source. */
    private final QuoteSource quoteSource;

    private List<AbstractStockPrice> convertedPrices;

    /**
     * Instantiates a new stock prices received task.
     * 
     * @param gui
     *            TODO
     * @param prices
     *            the beans
     * @param quoteSource
     *            the quote source
     * @param symbolMapper
     *            the symbol mapper
     * @param fxTable
     *            the fx table
     * @param badPrice
     *            the bad price
     * @param hasWrappedShareCount
     *            the has wrapped share count
     */
    public StockPricesReceivedTask(GUI gui, List<AbstractStockPrice> prices, QuoteSource quoteSource,
            SymbolMapper symbolMapper, FxTable fxTable, Double badPrice, boolean hasWrappedShareCount) {
        this.gui = gui;
        this.prices = prices;
        this.quoteSource = quoteSource;
        this.symbolMapper = symbolMapper;
        this.fxTable = fxTable;
        this.badPrice = badPrice;
        this.hasWrappedShareCount = hasWrappedShareCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> stockPricesReceived, size=" + prices.size());
        }

        try {
            updatePriceList(prices);

            convertedPrices = updateConvertedPriceList(prices);

            getGui().saveToOFX(convertedPrices);

        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            Runnable doRun = null;

            if (hasWrappedShareCount) {
                showDialogWrappedShareCount();
            }

            if (badPrice != null) {
                showDialogBadPrice();
            }

            if (this.getGui().getResultView() != null) {
                doRun = new UpdateResultViewTask(this.getGui());
                SwingUtilities.invokeLater(doRun);
            }

            JTabbedPane bottomTabs = this.getGui().getBottomTabs();
            if (bottomTabs != null) {
                doRun = new Runnable() {

                    @Override
                    public void run() {
                        StockPricesReceivedTask.this.getGui().getBottomTabs().setSelectedIndex(0);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            }
        }
    }

    private List<AbstractStockPrice> updateConvertedPriceList(List<AbstractStockPrice> prices) {
        String defaultCurrency = this.getGui().getDefaultCurrency();
        ConvertedPriceContext convertedPriceContext = new ConvertedPriceContext(defaultCurrency, symbolMapper, fxTable);
        List<AbstractStockPrice> convertedPrices = ConvertedPriceUtils.toConvertedPrices(prices, convertedPriceContext);
        EventList<AbstractStockPrice> convertedPriceList = this.getGui().getConvertedPriceList();
        updateEventList(convertedPrices, convertedPriceList);
        return convertedPrices;
    }

    private void updatePriceList(List<AbstractStockPrice> prices) {
        EventList<AbstractStockPrice> priceList = this.getGui().getPriceList();
        updateEventList(prices, priceList);
    }

    private void showDialogBadPrice() {
        Runnable doRun;
        doRun = new Runnable() {

            @Override
            public void run() {
                JOptionPane.showMessageDialog(StockPricesReceivedTask.this.getGui(),
                        "Incoming price from quote source has\n" + "suspicious price: " + badPrice, "Suspicious Price",
                        JOptionPane.WARNING_MESSAGE, null);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    private void showDialogWrappedShareCount() {
        Runnable doRun;
        doRun = new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(StockPricesReceivedTask.this.getGui(),
                        "Incrementally increased share count has wrapped around.\n"
                                + "Next import will not update price(s).",
                        "Share count has wrapped around", JOptionPane.WARNING_MESSAGE, null);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    private static final void updateEventList(List<AbstractStockPrice> prices,
            EventList<AbstractStockPrice> eventList) {
        eventList.getReadWriteLock().writeLock().lock();
        try {
            eventList.clear();
            eventList.addAll(prices);
        } finally {
            eventList.getReadWriteLock().writeLock().unlock();
        }
    }

    private GUI getGui() {
        return gui;
    }
}