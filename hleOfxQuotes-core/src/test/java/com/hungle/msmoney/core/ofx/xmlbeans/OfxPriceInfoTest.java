package com.hungle.msmoney.core.ofx.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.StockPrice;

public class OfxPriceInfoTest {
    private final static Logger LOGGER = Logger.getLogger(OfxPriceInfoTest.class);

    private static final class OfxDifferenceEvaluator implements DifferenceEvaluator {

        private String[] ignoreNodes = { "DTSERVER", "TRNUID", "DTASOF", "DTPRICEASOF" };

        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL)
                return outcome; // only evaluate differences.
            final Node controlNode = comparison.getControlDetails().getTarget();
            final Node testNode = comparison.getTestDetails().getTarget();
            if (controlNode.getParentNode() instanceof Element && testNode.getParentNode() instanceof Element) {
                Element controlElement = (Element) controlNode.getParentNode();
                Element testElement = (Element) testNode.getParentNode();
                if (isIgnorable(controlElement, testElement)) {
                    if (isDateNode(testElement)) {
                        if (checkDateNode(testElement)) {
                            return ComparisonResult.SIMILAR;
                        } else {
                            return outcome;
                        }
                    }
                    return ComparisonResult.SIMILAR;
                }
            }
            return outcome;
        }

        private boolean checkDateNode(Element testElement) {
            final String testValue = testElement.getTextContent();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("testValue=" + testValue);
            }
            // 20171113104504.993[-0800:PST]
            Pattern pattern = Pattern.compile("\\d{14}\\.\\d{1,3}\\[[\\+\\-0-9]+:[A-Z]{3}\\]");
            Matcher matcher = pattern.matcher(testValue);
            return matcher.matches();
        }

        private boolean isDateNode(Element testElement) {
            String nodeName = testElement.getNodeName();
            if (nodeName.startsWith("DT")) {
                return true;
            }
            return false;
        }

        private boolean isIgnorable(Element controlElement, Element testElement) {
            for (String elementName : ignoreNodes) {
                boolean matched = controlElement.getNodeName().equals(elementName);
                if (matched) {
                    return true;
                }
            }
            return false;
        }

    }

    @Test
    public void testSaveEmpty() throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        String controlFileName = "testSaveEmpty";
        String controlFullFileName = "src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/" + controlFileName
                + ".xml";
        testOfxSave(stockPrices, controlFullFileName);
    }

    @Test
    public void testSaveOne() throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        StockPrice stockPrice = new StockPrice("HLE001", new Date(), 1.00);
        stockPrices.add(stockPrice);

        String controlFileName = "testSaveOne";
        String controlFullFileName = "src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/" + controlFileName
                + ".xml";
        testOfxSave(stockPrices, controlFullFileName);
    }

    @Test
    public void testSaveOneMFund() throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        StockPrice stockPrice = new StockPrice("HLE001", new Date(), 1.00, -1, -1);
        stockPrices.add(stockPrice);

        String controlFileName = "testSaveOneMFund";
        String controlFullFileName = "src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/" + controlFileName
                + ".xml";
        testOfxSave(stockPrices, controlFullFileName);
    }

    @Test
    public void testSaveStockMFund() throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        StockPrice stockPrice = null;
        // MFUND
        stockPrice = new StockPrice("MFUND001", new Date(), 1.00, -1, -1);
        stockPrices.add(stockPrice);
        // Stock
        stockPrice = new StockPrice("STOCK001", new Date(), 2.00);
        stockPrices.add(stockPrice);

        String controlFileName = "testSaveStockMFund";
        String controlFullFileName = "src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/" + controlFileName
                + ".xml";
        testOfxSave(stockPrices, controlFullFileName);
    }

    @Test
    public void testSaveWithCurrencyCode() throws IOException {
        List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
        StockPrice stockPrice = null;
        // MFUND
        stockPrice = new StockPrice("MFUND001", new Date(), 1.00, -1, -1);
        stockPrice.setCurrency("EUR");
        stockPrices.add(stockPrice);
        // Stock
        stockPrice = new StockPrice("STOCK001", new Date(), 2.00);
        stockPrice.setCurrency("GBP");
        stockPrices.add(stockPrice);

        SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();
        FxTable fxTable = FxTableUtils.loadFxFile();

        List<AbstractStockPrice> exchangeRates = new ArrayList<AbstractStockPrice>();
        // currency
        // GBPUSD=X
        stockPrice = new StockPrice("GBPUSD=X", new Date(), 0.90, -1, -1);
        exchangeRates.add(stockPrice);

        // EURUSD=X
        stockPrice = new StockPrice("EURUSD=X", new Date(), 1.22, -1, -1);
        exchangeRates.add(stockPrice);

        FxTableUtils.addExchangeRates(exchangeRates, fxTable);

        String controlFileName = "testSaveWithCurrencyCode";
        String controlFullFileName = "src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/" + controlFileName
                + ".xml";

        String defaultCurrency = CurrencyUtils.getDefaultCurrency();
        boolean forceGeneratingINVTRANLIST = false;

        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(defaultCurrency);
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);

        testOfxSave(stockPrices, controlFullFileName, params, symbolMapper, fxTable);
    }

    private void testOfxSave(List<AbstractStockPrice> stockPrices, String controlFullFileName) throws IOException {
        String defaultCurrency = CurrencyUtils.getDefaultCurrency();
        boolean forceGeneratingINVTRANLIST = false;

        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(defaultCurrency);
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);

        SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();
        FxTable fxTable = FxTableUtils.loadFxFile();

        testOfxSave(stockPrices, controlFullFileName, params, symbolMapper, fxTable);
    }

    private void testOfxSave(List<AbstractStockPrice> stockPrices, String controlFileName, OfxSaveParameter params,
            SymbolMapper symbolMapper, FxTable fxTable) throws IOException {
        File outFile = File.createTempFile("ofxPriceInfoTest", ".ofx");
        outFile.deleteOnExit();
        LOGGER.info("Saving to outputFile=" + outFile);
        OfxPriceInfo.save(stockPrices, outFile, params, symbolMapper, fxTable);

        // src/test/resources/com/hungle/msmoney/core/ofx/xmlbeans/testSaveEmpty.xml
        File inFile = new File(controlFileName);
        if (!inFile.exists()) {
            // no control file, ignore for now
            return;
        }
        Source controlSource = Input.fromFile(inFile).build();
        Assert.assertNotNull(controlSource);

        Source testSource = Input.fromFile(outFile).build();
        Assert.assertNotNull(testSource);

        Diff myDiff = DiffBuilder.compare(controlSource).withTest(testSource)
                .withDifferenceEvaluator(new OfxDifferenceEvaluator()).ignoreComments().ignoreWhitespace()
                .checkForSimilar().build();
        Assert.assertNotNull(myDiff);
        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
    }

    @Test
    public void testCreateOfxPriceInfo() {

    }
}
