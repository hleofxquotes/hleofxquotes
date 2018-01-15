package com.hungle.msmoney.core.qif;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.linesOf;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableEntry;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.mapper.SymbolMapperEntry;
import com.hungle.msmoney.core.qif.QifUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class QifUtilsTest {
    private static final Logger LOGGER = Logger.getLogger(QifUtilsTest.class);

    @Test
    public void testQifEmptyPriceList() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testQif", ".qif");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();
        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(QifUtils.QIF_HEADERS);
    }

    @Test
    public void testQifOnePrice() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testQif", ".qif");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        priceList.add(new StockPrice("IBM", new Date(0L), 1.00));
        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(
                QifUtils.QIF_HEADERS, 
                "\"IBM\",\"1.0000\",\"12/31/1969\",,,");
    }

    @Test
    public void testQifTwoPrices() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testQif", ".qif");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        priceList.add(new StockPrice("IBM", new Date(0L), 1.00));
        priceList.add(new StockPrice("AAPL", new Date(7 * 24 * 60 * 60 * 1000L), 2.00));
        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(
                QifUtils.QIF_HEADERS, 
                "\"IBM\",\"1.0000\",\"12/31/1969\",,,",
                "\"AAPL\",\"2.0000\",\"1/7/1970\",,,"
                );
    }

    @Test
    public void testQifWithFx() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testQif", ".qif");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        Price price = null;

        price = new Price(1.00);
        price.setCurrency("GBP");
        priceList.add(new StockPrice("IBM", new Date(0L), price));

        price = new Price(2.00);
        price.setCurrency("EUR");
        priceList.add(new StockPrice("AAPL", new Date(7 * 24 * 60 * 60 * 1000L), price));
        
        FxTableEntry fxTableEntry = null;
        
        fxTableEntry = new FxTableEntry("GBP", "USD", "2.22");
        fxTable.add(fxTableEntry);
        
        fxTableEntry = new FxTableEntry("EUR", "USD", "3.33");
        fxTable.add(fxTableEntry);

        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(
                QifUtils.QIF_HEADERS, 
                "\"IBM\",\"1.0000\",\"12/31/1969\",,,",
                "\"AAPL\",\"2.0000\",\"1/7/1970\",,,"
                );
        
        convert = true;
        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(
                QifUtils.QIF_HEADERS, 
                "\"IBM\",\"2.2200\",\"12/31/1969\",,,",
                "\"AAPL\",\"6.6600\",\"1/7/1970\",,,"
                );
        
        SymbolMapperEntry symbolMapperEntry = null;
        
        symbolMapperEntry = new SymbolMapperEntry();
        symbolMapperEntry.setQuotesSourceSymbol("IBM");
        symbolMapperEntry.setMsMoneySymbol("IBM.X");
        symbolMapper.add(symbolMapperEntry);
        
        symbolMapperEntry = new SymbolMapperEntry();
        symbolMapperEntry.setQuotesSourceSymbol("AAPL");
        symbolMapperEntry.setMsMoneySymbol("AAPL.Y");
        symbolMapper.add(symbolMapperEntry);
        
        convert = true;
        QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile);
        assertThat(linesOf(expectedFile)).containsExactly(
                QifUtils.QIF_HEADERS, 
                "\"IBM.X\",\"2.2200\",\"12/31/1969\",,,",
                "\"AAPL.Y\",\"6.6600\",\"1/7/1970\",,,"
                );
    }
}
