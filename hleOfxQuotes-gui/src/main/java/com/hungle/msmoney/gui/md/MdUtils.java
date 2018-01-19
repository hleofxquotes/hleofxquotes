package com.hungle.msmoney.gui.md;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.qif.VelocityUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;
import com.hungle.msmoney.core.stockprice.Price;

import ca.odell.glazedlists.EventList;

public class MdUtils {
    private static final Logger LOGGER = Logger.getLogger(MdUtils.class);

    private static final String MD_CSV_HEADERS = "Price,Ticker_ISIN";

    private static final String DEFAULT_SEPARATOR = ",";

    public static void saveToCsv(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        saveToCsvUsingVelocity(priceList, convert, defaultCurrency, symbolMapper, fxTable, file);
    }

    private static void saveToCsvUsingWriter(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            MdUtils.saveToCsv(priceList, convert, defaultCurrency, symbolMapper, fxTable, writer);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    private static void saveToCsvUsingVelocity(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file) throws IOException {
        List<MdCsvBean> beans = toMdCsvBeans(priceList, convert, defaultCurrency, symbolMapper, fxTable);
        VelocityContext context = new VelocityContext();

        context.put("header", MdUtils.getMdCsvHeader());
        context.put("rows", beans);
        context.put("util", new MdUtils());

        String encoding = "UTF-8";
        String template = "/templates/mdcsv.vm";
        VelocityUtils.mergeTemplate(context, template, encoding, file);
    }

    private static void saveToCsv(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, PrintWriter writer) {

        List<MdCsvBean> rows = toMdCsvBeans(priceList, convert, defaultCurrency, symbolMapper, fxTable);

        saveToCsv(rows, writer);
    }

    private static List<MdCsvBean> toMdCsvBeans(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable) {
        List<MdCsvBean> rows = new ArrayList<MdCsvBean>();
        for (AbstractStockPrice price : priceList) {
            MdCsvBean row = toMdCsvBean(convert, symbolMapper, price, fxTable, defaultCurrency);
            rows.add(row);
        }

        // Now figure out the derived FX symbols
        for (AbstractStockPrice price : priceList) {
            FxSymbol fxSymbol = price.getFxSymbol();
            if (fxSymbol == null) {
                continue;
            }
            rows.add(toMdCsvBeanAsDerivedValue(fxSymbol, price));
        }
        return rows;
    }

    private static MdCsvBean toMdCsvBean(boolean convert, SymbolMapper symbolMapper, AbstractStockPrice price, FxTable fxTable,
            String defaultCurrency) {
        MdCsvBean mdCsvBean = new MdCsvBean();

        // Closing price
        Price lastPrice = price.getLastPrice();
        if (convert) {
            if (price.getFxSymbol() == null) {
                lastPrice = FxTableUtils.getPrice(price.getStockSymbol(), price.getLastPrice(), defaultCurrency, symbolMapper,
                        fxTable);
            }
        }
        mdCsvBean.setPrice(lastPrice);

        String symbol = price.getStockSymbol();
        if (price.getFxSymbol() != null) {
            // MD specific
            symbol = symbol + "=X";
        } else {
            if (convert) {
                symbol = SymbolMapper.getStockSymbol(price.getStockSymbol(), symbolMapper);
            }
        }
        if (symbol == null) {
            symbol = price.getStockName();
        }
        if (symbol == null) {
            symbol = "";
        }
        mdCsvBean.setSymbol(symbol);

        return mdCsvBean;
    }

    private static void saveToCsv(List<MdCsvBean> rows, PrintWriter writer) {
        // HEADER
        // Price,Ticker ISIN
        writer.println(getMdCsvHeader());
        // ROWS
        for (MdCsvBean row : rows) {
            // ROW
            String str = toCsvRow(row);
            writer.println(str);
        }
    }

    private static String toCsvRow(MdCsvBean row) {
        String separator = DEFAULT_SEPARATOR;

        StringBuilder sb = new StringBuilder();

        // Closing price
        Price lastPrice = row.getPrice();
        sb.append(lastPrice.getPriceFormatter().format(lastPrice));

        String symbol = row.getSymbol();
        sb.append(separator);
        // sb.append("\"");
        sb.append(symbol);
        // sb.append("\"");

        return sb.toString();
    }

    private static MdCsvBean toMdCsvBeanAsDerivedValue(FxSymbol fxSymbol, AbstractStockPrice price) {
        MdCsvBean mdCsvBean = new MdCsvBean();

        Price lastPrice = price.getLastPrice().clonePrice();
        lastPrice.setPrice(new Double(1.00) / lastPrice.getPrice());
        mdCsvBean.setPrice(lastPrice);

        String symbol = fxSymbol.getToCurrency() + fxSymbol.getFromCurrency() + "=X";
        mdCsvBean.setSymbol(symbol);

        return mdCsvBean;
    }

    public static String getMdCsvHeader() {
        return MD_CSV_HEADERS;
    }

    public static String toPriceString(Price lastPrice) {
        String language = null;
        String country = null;
        return toPriceString(lastPrice, language, country);
    }

    public static String toPriceString(Price lastPrice, String language, String country) {
        return Price.toPriceString(lastPrice, language, country);
    }
}
