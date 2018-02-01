package com.hungle.msmoney.core.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.csvreader.CsvReader;

public class MetaStockSymbolTest {
    private static final Logger LOGGER = Logger.getLogger(MetaStockSymbolTest.class);

    @Test
    public void testParse() throws IOException {
        String symbol = null;
        MetaStockSymbol metaStockSymbol = null;

        symbol = "GB0033772517";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("GB0033772517", metaStockSymbol.getSymbol());
        Assert.assertEquals("GB0033772517", metaStockSymbol.getQsSymbol());
        Assert.assertNull(metaStockSymbol.getQsCurrency());
        Assert.assertNull(metaStockSymbol.getCurrency());

        symbol = "ALO:PAR";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("ALO:PAR", metaStockSymbol.getSymbol());
        Assert.assertEquals("ALO:PAR", metaStockSymbol.getQsSymbol());
        Assert.assertNull(metaStockSymbol.getQsCurrency());
        Assert.assertNull(metaStockSymbol.getCurrency());

        symbol = "GB00BTLX1Q39/USD/GBP";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("GB00BTLX1Q39", metaStockSymbol.getSymbol());
        Assert.assertEquals("GB00BTLX1Q39", metaStockSymbol.getQsSymbol());
        Assert.assertEquals("USD", metaStockSymbol.getQsCurrency());
        Assert.assertEquals("GBP", metaStockSymbol.getCurrency());

        symbol = "GB00BTLX1Q39/USD/GBX";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("GB00BTLX1Q39", metaStockSymbol.getSymbol());
        Assert.assertEquals("GB00BTLX1Q39", metaStockSymbol.getQsSymbol());
        Assert.assertEquals("USD", metaStockSymbol.getQsCurrency());
        Assert.assertEquals("GBX", metaStockSymbol.getCurrency());

        symbol = "AAPL";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("AAPL", metaStockSymbol.getSymbol());
        Assert.assertEquals("AAPL", metaStockSymbol.getQsSymbol());
        Assert.assertNull(metaStockSymbol.getQsCurrency());
        Assert.assertNull(metaStockSymbol.getCurrency());

        symbol = "AAPL/USD/GBP";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("AAPL", metaStockSymbol.getSymbol());
        Assert.assertEquals("AAPL", metaStockSymbol.getQsSymbol());
        Assert.assertEquals("USD", metaStockSymbol.getQsCurrency());
        Assert.assertEquals("GBP", metaStockSymbol.getCurrency());

        // GB00B2PLJJ36/GBX/GBP__ converts pence into pounds
        symbol = "AAPL/USD/GBP";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("AAPL", metaStockSymbol.getSymbol());
        Assert.assertEquals("AAPL", metaStockSymbol.getQsSymbol());
        Assert.assertEquals("USD", metaStockSymbol.getQsCurrency());
        Assert.assertEquals("GBP", metaStockSymbol.getCurrency());

        // XMRC:LSE:GBX____________ no manipulation needed
        symbol = "XMRC:LSE:GBX";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("XMRC:LSE:GBX", metaStockSymbol.getSymbol());
        Assert.assertEquals("XMRC:LSE:GBX", metaStockSymbol.getQsSymbol());
        Assert.assertNull(metaStockSymbol.getQsCurrency());
        Assert.assertNull(metaStockSymbol.getCurrency());

        // XMRC:LSE:GBX/GBX/GBP____ converts pence into pounds
        symbol = "XMRC:LSE:GBX/GBX/GBP";
        metaStockSymbol = new MetaStockSymbol(symbol);
        Assert.assertNotNull(metaStockSymbol);
        Assert.assertEquals("XMRC:LSE:GBX", metaStockSymbol.getSymbol());
        Assert.assertEquals("XMRC:LSE:GBX", metaStockSymbol.getQsSymbol());
        Assert.assertEquals("GBX", metaStockSymbol.getQsCurrency());
        Assert.assertEquals("GBP", metaStockSymbol.getCurrency());
    }

    @Test
    public void testParseList() throws IOException {
        List<MetaStockSymbol> metaStockSymbols = null;

        String[] list1 = {

        };
        metaStockSymbols = MetaStockSymbol.parse(Arrays.asList(list1));
        Assert.assertTrue(metaStockSymbols.size() == 0);

        String[] list2 = {
                "GB0033772517",
                "ALO:PAR",
                "GB00BTLX1Q39/USD/GBP",
                "GB00BTLX1Q39/USD/GBX",
                "AAPL",
                "AAPL/USD/GBP",
                "GB00B2PLJJ36/GBX/GBP",
                "XMRC:LSE:GBX",
                "XMRC:LSE:GBX/GBX/GBP",
        };
        metaStockSymbols = MetaStockSymbol.parse(Arrays.asList(list2));
        Assert.assertTrue(metaStockSymbols.size() == 5);
    }
    
    @Test
    public void testAddAttributes() throws IOException {
        SymbolMapper symbolMapper = new SymbolMapper();
        symbolMapper.load(getCsvReader("AddAttributes"));
        
        String[] symbolsWithAttributes = {
                "GB0033772517",
                "ALO:PAR",
                "GB00BTLX1Q39/USD/GBP",
                "GB00BTLX1Q39/USD/GBX",
                "AAPL",
                "AAPL/USD/GBP",
                "GB00B2PLJJ36/GBX/GBP",
                "XMRC:LSE:GBX",
                "XMRC:LSE:GBX/GBX/GBP",
                "IBM/HAL",
                "IE00B3BMD843/ IE00B3BMD843.IR"
        };
        List<String> stockSymbols = new ArrayList<String>();
        stockSymbols.addAll(Arrays.asList(symbolsWithAttributes));
        symbolMapper.addAttributes(stockSymbols);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(stockSymbols);
        }
        
        Assert.assertEquals(symbolsWithAttributes.length, stockSymbols.size());
        Assert.assertTrue(stockSymbols.contains("GB0033772517"));
        
        Assert.assertTrue(stockSymbols.contains("ALO:PAR"));
        
        Assert.assertFalse(stockSymbols.contains("GB00BTLX1Q39/USD/GBP"));
        Assert.assertTrue(stockSymbols.contains("GB00BTLX1Q39"));

        Assert.assertFalse(stockSymbols.contains("GB00BTLX1Q39/USD/GBX"));
        Assert.assertTrue(stockSymbols.contains("GB00BTLX1Q39"));

        Assert.assertTrue(stockSymbols.contains("AAPL"));

        Assert.assertFalse(stockSymbols.contains("AAPL/USD/GBP"));
        Assert.assertTrue(stockSymbols.contains("AAPL"));
        
//        "GB00B2PLJJ36/GBX/GBP",
        Assert.assertFalse(stockSymbols.contains("GB00B2PLJJ36/GBX/GBP"));
        Assert.assertTrue(stockSymbols.contains("GB00B2PLJJ36"));
//        "XMRC:LSE:GBX",
        Assert.assertTrue(stockSymbols.contains("XMRC:LSE:GBX"));

        //        "XMRC:LSE:GBX/GBX/GBP",
        Assert.assertFalse(stockSymbols.contains("XMRC:LSE:GBX/GBX/GBP"));
        Assert.assertTrue(stockSymbols.contains("XMRC:LSE:GBX"));

        Assert.assertFalse(stockSymbols.contains("IBM/HAL"));
        Assert.assertFalse(stockSymbols.contains("IBM"));
        Assert.assertTrue(stockSymbols.contains("HAL"));
        
        SymbolMapperEntry symbolMapperEntry = null;

        Assert.assertTrue(symbolMapper.isBond("GB00BTLX1Q39"));
        symbolMapperEntry = symbolMapper.getSymbolMapperEntry("GB00BTLX1Q39");
        Assert.assertNotNull(symbolMapperEntry);
        Assert.assertTrue(symbolMapperEntry.isBond());
        Assert.assertNull(symbolMapperEntry.getBondDivider());

        Assert.assertTrue(symbolMapper.isBond("GB00BTLX1Q39-100"));
        symbolMapperEntry = symbolMapper.getSymbolMapperEntry("GB00BTLX1Q39-100");
        Assert.assertNotNull(symbolMapperEntry);
        Assert.assertTrue(symbolMapperEntry.isBond());
        Assert.assertNotNull(symbolMapperEntry.getBondDivider());
        Assert.assertEquals(100, symbolMapperEntry.getBondDivider().intValue());
        
        //                 "IE00B3BMD843/ IE00B3BMD843.IR"
        Assert.assertTrue(symbolMapper.isBond("IE00B3BMD843"));
        Assert.assertTrue(symbolMapper.isBond("IE00B3BMD843.IR"));
        symbolMapperEntry = symbolMapper.getSymbolMapperEntry("GB00BTLX1Q39");
        Assert.assertNotNull(symbolMapperEntry);
        Assert.assertTrue(symbolMapperEntry.isBond());
        Assert.assertNull(symbolMapperEntry.getBondDivider());

    }

    private CsvReader getCsvReader(String id) throws IOException {
        String[] tokens = getClass().getName().split("\\.");

        String resourceName = tokens[tokens.length - 1] + "-mapper" + id + ".csv";
        LOGGER.info("resourceName=" + resourceName);
        InputStream in = getClass().getResourceAsStream(resourceName);
        Assert.assertNotNull(in);

        Charset charset = Charset.defaultCharset();
        CsvReader csvReader = new CsvReader(in, charset);
        csvReader.readHeaders();
        return csvReader;
    }
}
