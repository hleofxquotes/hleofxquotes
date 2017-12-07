package com.hungle.msmoney.gui.task;

import java.io.File;
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

    /**
     * Instantiates a new stock prices received task.
     *
     * @param prices
     *            the beans
     * @param badPrice
     *            the bad price
     * @param fxTable
     *            the fx table
     * @param hasWrappedShareCount
     *            the has wrapped share count
     * @param symbolMapper
     *            the symbol mapper
     * @param quoteSource
     *            the quote source
     * @param gui
     *            TODO
     */
    public StockPricesReceivedTask(GUI gui, List<AbstractStockPrice> prices, Double badPrice, FxTable fxTable,
            boolean hasWrappedShareCount, SymbolMapper symbolMapper, QuoteSource quoteSource) {
        this.gui = gui;
        this.prices = prices;
        this.badPrice = badPrice;
        this.fxTable = fxTable;
        this.hasWrappedShareCount = hasWrappedShareCount;
        this.symbolMapper = symbolMapper;
        this.quoteSource = quoteSource;
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

        EventList<AbstractStockPrice> priceList = this.gui.getPriceList();
        priceList.getReadWriteLock().writeLock().lock();
        try {
            priceList.clear();
            priceList.addAll(prices);
        } finally {
            priceList.getReadWriteLock().writeLock().unlock();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("< stockPricesReceived");
            }
        }

        List<AbstractStockPrice> newExchangeRates = null;
        if (quoteSource != null) {
            newExchangeRates = quoteSource.getExchangeRates();
        }
//        FxTableUtils.updateFxTable(newExchangeRates, this.gui.getExchangeRates());

        try {
            boolean onePerFile = quoteSource.isHistoricalQuotes();
            List<File> ofxFiles = this.gui.saveToOFX(prices, symbolMapper, fxTable, onePerFile);
            for (File ofxFile : ofxFiles) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("ofxFile=" + ofxFile);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                File csvFile = this.gui.saveToCsv(prices);
                LOGGER.debug("Saved csvFile=" + csvFile);
            }

//            MapperTableUtils.updateMapperTable(symbolMapper, this.gui.getMapper());
        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            Runnable doRun = null;

            if (hasWrappedShareCount) {
                doRun = new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(StockPricesReceivedTask.this.gui,
                                "Incrementally increased share count has wrapped around.\n"
                                        + "Next import will not update price(s).",
                                "Share count has wrapped around", JOptionPane.WARNING_MESSAGE, null);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            }

            if (badPrice != null) {
                doRun = new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(StockPricesReceivedTask.this.gui,
                                "Incoming price from quote source has\n" + "suspicious price: " + badPrice, "Suspicious Price",
                                JOptionPane.WARNING_MESSAGE, null);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            }
            
            if (this.gui.getResultView() != null) {
                doRun = new UpdateResultViewTask(this.gui);
                SwingUtilities.invokeLater(doRun);
            }

            JTabbedPane bottomTabs = this.gui.getBottomTabs();
            if (bottomTabs != null) {
                doRun = new Runnable() {

                    @Override
                    public void run() {
                        StockPricesReceivedTask.this.gui.getBottomTabs().setSelectedIndex(0);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            }
        }
    }
}