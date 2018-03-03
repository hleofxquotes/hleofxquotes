package com.hungle.msmoney.qs.multi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.qs.ft.FtEquitiesQuoteGetter;
import com.hungle.msmoney.qs.net.GetQuotesListener;
import com.hungle.msmoney.qs.net.QuoteGetter;
import com.hungle.msmoney.qs.yahoo.YahooSS2QuoteGetter;

public class MultiSourcesQuoteGetter implements QuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(MultiSourcesQuoteGetter.class);

    public static final String YAHOO2_QUOTE_SOURCE_NAME = "yahoo2";

    public static final String FT_QUOTE_SOURCE_NAME = "ft";

    private Map<String, QuoteGetter> quoteGetterMap;

    private Map<String, String> quoteGetterAliases;

    private Map<String, List<String>> quoteGetterSymbols;

    private List<AbstractStockPrice> fxSymbols;

    private List<String> notFoundSymbols;

    public MultiSourcesQuoteGetter() {
        super();

        initQuoteGetterMap();

        initQuoteGetterAliases();

        quoteGetterSymbols = new HashMap<String, List<String>>();
    }

    private void initQuoteGetterMap() {
        quoteGetterMap = new LinkedHashMap<String, QuoteGetter>();
        QuoteGetter getter = null;

        getter = new YahooSS2QuoteGetter();
        quoteGetterMap.put(YAHOO2_QUOTE_SOURCE_NAME, getter);

        getter = new FtEquitiesQuoteGetter();
        quoteGetterMap.put(FT_QUOTE_SOURCE_NAME, getter);
    }

    private void initQuoteGetterAliases() {
        quoteGetterAliases = new HashMap<String, String>();

        quoteGetterAliases.put("yahoo", YAHOO2_QUOTE_SOURCE_NAME);
        quoteGetterAliases.put("ft.com", FT_QUOTE_SOURCE_NAME);
    }

    @Override
    public void close() throws IOException {
        for (QuoteGetter getter : quoteGetterMap.values()) {
            try {
                getter.close();
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        }
    }

    @Override
    public List<AbstractStockPrice> getQuotes(List<String> qsNames, GetQuotesListener listener) throws IOException {
        fxSymbols = new ArrayList<AbstractStockPrice>();
        notFoundSymbols = new ArrayList<String>();

        List<AbstractStockPrice> allStockPrices = new ArrayList<AbstractStockPrice>();
        for (String qsName : qsNames) {
            QuoteGetter getter = getQuoteGetter(qsName);
            if (getter == null) {
                LOGGER.warn("Cannot find quoteGetter for qsName=" + qsName);
                continue;
            }

            List<String> symbols = getQuoteSourceSymbols(qsName);
            try {
                List<AbstractStockPrice> stockPrices = getter.getQuotes(symbols, listener);
                for (AbstractStockPrice stockPrice : stockPrices) {
                    stockPrice.setQuoteSourceName(qsName);
                }

                allStockPrices = mergeStockPrices(stockPrices, allStockPrices);
                fxSymbols = mergeFxSymbols(getter.getFxSymbols(), fxSymbols);
                notFoundSymbols = mergeNotFoundSymbols(getter.getNotFoundSymbols(), notFoundSymbols);
            } catch (IOException e) {
                LOGGER.warn(e, e);
            }
        }

        allStockPrices = normalizeStockPrices(allStockPrices);
        fxSymbols = normalizeFxSymbols(fxSymbols);
        notFoundSymbols = normalizeNotFoundSymbols(notFoundSymbols);

        return allStockPrices;
    }

    private List<AbstractStockPrice> normalizeStockPrices(List<AbstractStockPrice> allStockPrices) {
        Map<String, List<AbstractStockPrice>> map = new LinkedHashMap<String, List<AbstractStockPrice>>();

        for (AbstractStockPrice stockPrice : allStockPrices) {
            String key = stockPrice.getStockSymbol();
            List<AbstractStockPrice> list = map.get(key);
            if (list == null) {
                list = new ArrayList<AbstractStockPrice>();
                map.put(key, list);
            }
            list.add(stockPrice);
        }

        List<List<AbstractStockPrice>> duplicatesList = getDuplicatesList(map);
        if (duplicatesList.size() == 0) {
            return allStockPrices;
        } else {
            List<AbstractStockPrice> result = new ArrayList<AbstractStockPrice>();
            for (List<AbstractStockPrice> list : map.values()) {
                AbstractStockPrice stockPrice = getUnique(list);
                result.add(stockPrice);
            }
            return result;
        }
    }

    private List<AbstractStockPrice> normalizeFxSymbols(List<AbstractStockPrice> fxSymbols) {
        return normalizeStockPrices(fxSymbols);
    }

    private List<String> normalizeNotFoundSymbols(List<String> notFoundSymbols) {
        Set<String> set = new HashSet<String>();
        set.addAll(notFoundSymbols);
        notFoundSymbols = new ArrayList<String>();
        notFoundSymbols.addAll(set);
        return notFoundSymbols;
    }

    private AbstractStockPrice getUnique(List<AbstractStockPrice> stockPrices) {
        AbstractStockPrice bestPrice = null;
        if (stockPrices.size() == 1) {
            bestPrice = stockPrices.get(0);
        } else {
            for (AbstractStockPrice stockPrice : stockPrices) {
                bestPrice = chooseBestPrice(stockPrice, bestPrice);
            }
        }
        return bestPrice;
    }

    private AbstractStockPrice chooseBestPrice(AbstractStockPrice stockPrice, AbstractStockPrice bestPrice) {
        if (bestPrice == null) {
            bestPrice = stockPrice;
        } else {
            Date date1 = stockPrice.getLastTrade();
            Date date2 = bestPrice.getLastTrade();

            int rank = 0;
            if ((date1 == null) && (date2 == null)) {
                // can't decide yet
                rank = 0;
            } else if (date1 == null) {
                // no change bestPrice
                rank = -1;
            } else if (date2 == null) {
                rank = 1;
            } else {
                rank = date1.compareTo(date2);
            }

            if (rank == 0) {
                // last win
                bestPrice = stockPrice;
            }
        }

        return bestPrice;
    }

    private List<List<AbstractStockPrice>> getDuplicatesList(Map<String, List<AbstractStockPrice>> map) {
        List<List<AbstractStockPrice>> result = new ArrayList<List<AbstractStockPrice>>();
        for (List<AbstractStockPrice> stockPrices : map.values()) {
            if (stockPrices.size() > 1) {
                result.add(stockPrices);
            }
        }

        return result;
    }

    private List<AbstractStockPrice> mergeStockPrices(List<AbstractStockPrice> stockPrices,
            List<AbstractStockPrice> allStockPrices) {
        allStockPrices.addAll(stockPrices);
        return allStockPrices;
    }

    private List<String> mergeNotFoundSymbols(List<String> notFoundSymbols, List<String> allNotFoundSymbols) {
        allNotFoundSymbols.addAll(notFoundSymbols);
        return allNotFoundSymbols;
    }

    private List<AbstractStockPrice> mergeFxSymbols(List<AbstractStockPrice> fxSymbols, List<AbstractStockPrice> allFxSymbols) {
        allFxSymbols.addAll(fxSymbols);
        return fxSymbols;
    }

    public String normalizeQsName(String qsName) {
        String key = qsName.toLowerCase();
        key = quoteGetterAliases.get(key);
        if (key == null) {
            key = qsName;
        }
        return key;
    }

    private QuoteGetter getQuoteGetter(String qsName) {
        String key = normalizeQsName(qsName);
        return quoteGetterMap.get(key);
    }

    private List<String> getQuoteSourceSymbols(String qsName) {
        String key = normalizeQsName(qsName);
        List<String> list = quoteGetterSymbols.get(key);
        if (list == null) {
            list = new ArrayList<String>();
        }
        return list;
    }

    public void setSymbols(String qsName, List<String> symbols) {
        String key = normalizeQsName(qsName);
        quoteGetterSymbols.put(key, symbols);
    }

    public List<AbstractStockPrice> getFxSymbols() {
        return fxSymbols;
    }

    public List<String> getNotFoundSymbols() {
        return notFoundSymbols;
    }
}
