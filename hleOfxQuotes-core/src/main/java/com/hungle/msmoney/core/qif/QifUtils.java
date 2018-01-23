package com.hungle.msmoney.core.qif;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;
import com.hungle.msmoney.core.template.TemplateUtils;

import ca.odell.glazedlists.EventList;

// TODO: Auto-generated Javadoc
/**
 * The Class QifUtils.
 */
public class QifUtils {
    private static final Logger LOGGER = Logger.getLogger(QifUtils.class);

    /** The calendar. */
    private static Calendar calendar = Calendar.getInstance();

    public static final String QIF_HEADERS = "!Type:Prices";

    private static final QifPlugin qifPlugin = null; // QifPlugin.createQifPlugin();

    public static void saveToQif(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file, String templateDecimalSeparator) throws IOException {
        saveToQifUsingVelocity(priceList, convert, defaultCurrency, symbolMapper, fxTable, file, templateDecimalSeparator);
    }

    /**
     * Save to qif.
     *
     * @param priceList
     *            the price list
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void saveToQifUsingWriter(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file, String templateDecimalSeparator) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            QifUtils.saveToQif(priceList, convert, defaultCurrency, symbolMapper, fxTable, writer, templateDecimalSeparator);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    public static void saveToQifUsingVelocity(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, File file, String templateDecimalSeparator) throws IOException {
        List<QifBean> qifBeans = toQifBeans(priceList, convert, defaultCurrency, symbolMapper, fxTable);
        VelocityContext context = new VelocityContext();

        context.put("header", QifUtils.getQifHeader());
        context.put("rows", qifBeans);
        context.put("util", new QifUtils());
        
        LOGGER.info("templateDecimalSeparator=" + templateDecimalSeparator);
        String language = "";
        String country = "";
        if (templateDecimalSeparator == null) {
            language = "";
            country = "";
        } else {
            if (templateDecimalSeparator.compareTo(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_DEFAULT) == 0) {
                language = "";
                country = "";
            } else if (templateDecimalSeparator.compareTo(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_PERIOD) == 0) {
                language = "en";
                country = "US";
            } else if (templateDecimalSeparator.compareTo(TemplateUtils.TEMPLATE_DECIMAL_SEPARATOR_COMMA) == 0) {
                language = "fr";
                country = "FR";
            }
        }
        context.put("language", language);
        LOGGER.info("language=" + language);
        context.put("country", country);
        LOGGER.info("country=" + country);

        String encoding = "UTF-8";
        String template = "/templates/qif.vm";
        VelocityUtils.mergeTemplate(context, template, encoding, file);
    }

    /**
     * Save to qif.
     *
     * @param priceList
     *            the price list
     * @param writer
     *            the writer
     */
    static void saveToQif(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable, PrintWriter writer, String templateDecimalSeparator) {

        List<QifBean> qifBeans = toQifBeans(priceList, convert, defaultCurrency, symbolMapper, fxTable);

        // HEADER
        writer.println(QifUtils.getQifHeader());

        // ROWS
        for (QifBean qifBean : qifBeans) {
            String rowString = toRowString(qifBean);
            writer.println(rowString);
        }
    }

    private static List<QifBean> toQifBeans(EventList<AbstractStockPrice> priceList, boolean convert, String defaultCurrency,
            SymbolMapper symbolMapper, FxTable fxTable) {
        List<QifBean> qifBeans = new ArrayList<QifBean>();
        for (AbstractStockPrice price : priceList) {
            QifBean qifBean = toQifBean(price, convert, defaultCurrency, symbolMapper, fxTable);
            qifBeans.add(qifBean);
        }
        return qifBeans;
    }

    private static QifBean toQifBean(AbstractStockPrice price, boolean convert, String defaultCurrency, SymbolMapper symbolMapper,
            FxTable fxTable) {
        QifBean qifBean = new QifBean();

        // symbol
        String symbol = price.getStockSymbol();
        if (convert) {
            symbol = SymbolMapper.getStockSymbol(price.getStockSymbol(), symbolMapper);
        }
        if (symbol == null) {
            symbol = price.getStockName();
        }
        if (symbol == null) {
            symbol = "";
        }
        qifBean.setSymbol(symbol);
        Price lastPrice = price.getLastPrice();
        if (convert) {
            if (price.getFxSymbol() == null) {
                lastPrice = FxTableUtils.getPrice(price.getStockSymbol(), price.getLastPrice(), defaultCurrency, symbolMapper,
                        fxTable);
            }
        }
        qifBean.setPrice(lastPrice);

        // Date
        Date lastTrade = price.getLastTrade();
        qifBean.setDate(lastTrade);

        // Day's high price
        qifBean.setDayHigh(null);

        // Day's low price
        qifBean.setDayLow(null);

        // Volume
        qifBean.setVolume(null);

        return qifBean;
    }

    private static String toRowString(QifBean qifBean) {
        // "010869AQ",105.730,"6/6/03",,,0
        // the QIF Type:Prices fields are:
        // Stock Exchange code for stock (above is the ASX
        // code for
        // Commonwealth Bank of Australia, CBA)
        // Closing price
        // Date
        // Day's high price (empty in above example)
        // Day's low price (empty in above example)
        // Volume
        String separator = ",";

        StringBuilder sb = new StringBuilder();

        // Symbol
        String symbol = qifBean.getSymbol();
        sb.append("\"" + symbol + "\"");

        // Closing price
        Price lastPrice = qifBean.getPrice();
        String qifPriceString = null;
        if (qifPlugin != null) {
            qifPriceString = qifPlugin.toQifPriceString(lastPrice.getPrice());
        } else {
            qifPriceString = toQifPriceString(lastPrice);
        }
        if (qifPriceString == null) {
            qifPriceString = toQifPriceString(lastPrice);
        }
        sb.append(separator);
        sb.append("\"" + qifPriceString + "\"");

        // Date
        Date lastTrade = qifBean.getDate();
        sb.append(separator);
        sb.append("\"" + toQifDateString(lastTrade) + "\"");

        // Day's high price
        sb.append(separator);

        // Day's low price
        sb.append(separator);

        // Volume
        sb.append(separator);

        return sb.toString();
    }

    public static String getQifHeader() {
        return QifUtils.QIF_HEADERS;
    }

    public static String toQifPriceString(Price lastPrice) {
        String language = null;
        String country = null;
        return toQifPriceString(lastPrice, language, country);
    }

    public static String toQifPriceString(Price lastPrice, String language, String country) {
        return Price.toPriceString(lastPrice, language, country);
    }

    /**
     * To qif date string.
     *
     * @param date
     *            the date
     * @return the string
     */
    public static String toQifDateString(Date date) {
        StringBuilder sb = new StringBuilder();

        calendar.setTime(date);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if (year < 2000) {
            // 12/31/1999
            sb.append(month + 1);
            sb.append("/");
            sb.append(dayOfMonth);
            sb.append("/");
            sb.append(year);
        } else {
            // 10/7'2002
            sb.append(month + 1);
            sb.append("/");
            sb.append(dayOfMonth);
            sb.append("'");
            sb.append(year);
        }
        return sb.toString();
    }

}
