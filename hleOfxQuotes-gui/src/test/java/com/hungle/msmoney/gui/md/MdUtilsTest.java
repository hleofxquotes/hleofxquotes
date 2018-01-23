package com.hungle.msmoney.gui.md;

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
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.stockprice.StockPrice;
import com.hungle.msmoney.core.template.TemplateUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class MdUtilsTest {
    private static final Logger LOGGER = Logger.getLogger(MdUtilsTest.class);

    @Test
    public void testMdCsvEmptyPriceList() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testMdCsv", ".csv");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();
        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(MdUtils.getMdCsvHeader());
    }
    
    @Test
    public void testMdCsvOnePrice() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testMdCsv", ".csv");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        priceList.add(new StockPrice("IBM", new Date(0L), 1.00));
        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(
                MdUtils.getMdCsvHeader(), 
                "\"1.0000\",\"IBM\"");
    }
    
    @Test
    public void testMdCsvTwoPrices() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testMdCsv", ".csv");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        priceList.add(new StockPrice("IBM", new Date(0L), 1.00));
        priceList.add(new StockPrice("AAPL", new Date(7 * 24 * 60 * 60 * 1000L), 2.00));
        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(
                MdUtils.getMdCsvHeader(),
                "\"1.0000\",\"IBM\"",
                "\"2.0000\",\"AAPL\""
                );
    }
    
    @Test
    public void testMdCsvWithFx() throws IOException {
        EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
        boolean convert = false;
        String defaultCurrency = "USD";
        SymbolMapper symbolMapper = new SymbolMapper();
        FxTable fxTable = new FxTable();
        File expectedFile = File.createTempFile("testMdCsv", ".csv");
        LOGGER.info("expectedFile=" + expectedFile);
        expectedFile.deleteOnExit();

        Price price = null;

        price = new Price(1.00);
        price.setCurrency("GBP");
        priceList.add(new StockPrice("IBM", new Date(0L), price));

        price = new Price(2.00);
        price.setCurrency("EUR");
        priceList.add(new StockPrice("AAPL", new Date(7 * 24 * 60 * 60 * 1000L), price));

        price = new Price(2.22);
        price.setCurrency("USD");
        priceList.add(new StockPrice("GBPUSD", new Date(14 * 24 * 60 * 60 * 1000L), price));
        
        FxTableEntry fxTableEntry = null;
        
        fxTableEntry = new FxTableEntry("GBP", "USD", "2.22");
        fxTable.add(fxTableEntry);
        
        fxTableEntry = new FxTableEntry("EUR", "USD", "3.33");
        fxTable.add(fxTableEntry);

        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(
                MdUtils.getMdCsvHeader(), 
                "\"1.0000\",\"IBM\"",
                "\"2.0000\",\"AAPL\"",
                "\"2.2200\",\"GBPUSD=X\"",
                "\"0.4505\",\"USDGBP=X\""
                );
        
        convert = true;
        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(
                MdUtils.getMdCsvHeader(), 
                "\"2.2200\",\"IBM\"",
                "\"6.6600\",\"AAPL\"",
                "\"2.2200\",\"GBPUSD=X\"",
                "\"0.4505\",\"USDGBP=X\""
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
        MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, expectedFile, TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT);
        assertThat(linesOf(expectedFile)).containsExactly(
                MdUtils.getMdCsvHeader(), 
                "\"2.2200\",\"IBM.X\"",
                "\"6.6600\",\"AAPL.Y\"",
                "\"2.2200\",\"GBPUSD=X\"",
                "\"0.4505\",\"USDGBP=X\""
                );
    }
}
