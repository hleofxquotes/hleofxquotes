package com.hungle.msmoney.qs.ft;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FtPriceModel {
    private static final Logger LOGGER = Logger.getLogger(FtPriceModel.class);

    public static final String FT_ETFS_BASE_URL = "https://markets.ft.com/data/etfs/tearsheet/summary";

    public static final String FT_FUNDS_BASE_URL = "https://markets.ft.com/data/funds/tearsheet/summary";

    public static final String FT_EQUITIES_BASE_URL = "https://markets.ft.com/data/equities/tearsheet/summary";

    private String symbol;

    private Double price;

    private String currency;

    private Date date;

    private String timeZone;

    private String name;
    
    @Override
    public String toString() {
        return "FtPriceModel [symbol=" + symbol + ", price=" + price + ", currency=" + currency + ", date=" + date + ", timeZone="
                + timeZone + ", name=" + name + "]";
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public static final FtPriceModel parseFtDoc(Document doc, String symbol) throws IOException {
        try {
            FtPriceModel model = new FtPriceModel();
            model.setSymbol(symbol);

            String cssQuery = null;
            Element topDiv = null;
            
            // name
            // <h1 class="mod-tearsheet-overview__header__name mod-tearsheet-overview__header__name--large">
            // <div class="mod-tearsheet-overview__header"
            cssQuery = "div.mod-tearsheet-overview__header";
            topDiv = doc.selectFirst(cssQuery);
            if (topDiv == null) {
                throw new IOException("Cannot find " + cssQuery);
            }
            cssQuery = "h1";
            Elements items = topDiv.select(cssQuery);
            if (items == null) {
                throw new IOException("Cannot find " + cssQuery);
            }
            Element item = items.get(0);
            if (item == null) {
                throw new IOException("Cannot find first <li>");
            }
            model.setName(item.text());
            
            // <div class="mod-tearsheet-overview__quote">
            cssQuery = "div.mod-tearsheet-overview__quote";
            topDiv = doc.selectFirst(cssQuery);
            if (topDiv == null) {
                throw new IOException("Cannot find " + cssQuery);
            }

            parsePrice(topDiv, model);

            parseDate(topDiv, model);

            return model;
        } catch (IOException e) {
            throw new IOException("Cannot parse response for symbol=" + symbol, e);
        }
    }

    private static void parsePrice(Element topDiv, FtPriceModel model) throws IOException {
        String cssQuery;
        cssQuery = "li";
        Elements items = topDiv.select(cssQuery);
        if (items == null) {
            throw new IOException("Cannot find " + cssQuery);
        }
    
        for (Element item : items) {
            // <span class="mod-ui-data-list__label">Price (USD)</span>
            String cssQueryLabel = "span.mod-ui-data-list__label";
            Element label = item.selectFirst(cssQueryLabel);
            if (label == null) {
                throw new IOException("Cannot find " + cssQueryLabel);
            }
            String labelText = label.text();
    
            // <span class="mod-ui-data-list__value">152.47</span>
            String cssQueryValue = "span.mod-ui-data-list__value";
            Element value = item.selectFirst(cssQueryValue);
            if (value == null) {
                throw new IOException("Cannot find " + cssQueryValue);
            }
            String valueText = value.text();
    
            if (labelText != null) {
                labelText = labelText.trim();
                if (labelText.startsWith("Price")) {
                    setPriceAndCurrency(labelText, valueText, model);
                }
            }
    
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(labelText + "=" + valueText);
            }
    
        }
    }

    private static void parseDate(Element topDiv, FtPriceModel model) throws IOException {
        String cssQuery;
        // <div class="mod-disclaimer">Data delayed at least 15 minutes, as
        // of Nov 28 2017 21:01 GMT.</div>
        cssQuery = "div.mod-disclaimer";
        Element disclaimer = topDiv.selectFirst(cssQuery);
        if (disclaimer == null) {
            throw new IOException("Cannot find " + cssQuery);
        }

        String disclaimerText = disclaimer.text();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(disclaimerText);
        }
        // Data delayed at least 15 minutes, as of Nov 28 2017 21:01 GMT.
        if (disclaimerText != null) {
            disclaimerText = disclaimerText.trim();
            String patternString = "as of (.*)\\.";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(disclaimerText);
            String date = null;
            while (matcher.find()) {
                if (date != null) {
                    continue;
                }
                date = matcher.group(1);
                // Nov 28 2017 21:01 GMT
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("date: " + date);
                }
                model.setDate(toDate(date));
                String[] tokens = date.split(" ");
                if (tokens != null) {
                    if (tokens.length == 5) {
                        String timeZone = tokens[4];
                        model.setTimeZone(timeZone);
                    }
                }
            }
        }
    }

    private static void setPriceAndCurrency(String labelText, String valueText, FtPriceModel model) {
        // Price (USD)
        String patternString = "\\((.*)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(labelText);
        String currency = null;
        while (matcher.find()) {
            if (currency != null) {
                continue;
            }
            currency = matcher.group(1);
            model.setCurrency(currency);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("currency: " + currency);
            }
        }

        if (valueText != null) {
            valueText = valueText.trim();
            Number price = null;
            try {
                // FT price is ALWAYS in English format
                Locale locale = Locale.ENGLISH;
                NumberFormat format = NumberFormat.getInstance(locale);
                // format.setGroupingUsed(false);
                price = format.parse(valueText);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("  valueText=" + valueText);
                    LOGGER.debug("  price=" + price);
                }
            } catch (ParseException e) {
                LOGGER.warn(e);
            }
            if (price != null) {
                model.setPrice(price.doubleValue());
            }
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public static final URL getFtEtfURL(String etf) throws MalformedURLException, UnsupportedEncodingException {
        return new URL(FT_ETFS_BASE_URL + "?s=" + URLEncoder.encode(etf, "UTF-8"));
    }

    public static final URL getFtFundURL(String fund) throws MalformedURLException, UnsupportedEncodingException {
        return new URL(FT_FUNDS_BASE_URL + "?s=" + URLEncoder.encode(fund, "UTF-8"));
    }

    public static final URL getFtEquityURL(String equity) throws MalformedURLException, UnsupportedEncodingException {
        return new URL(FT_EQUITIES_BASE_URL + "?s=" + URLEncoder.encode(equity, "UTF-8"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static final Date toDate(String source) {
        Date date = null;
        // Nov 28 2017 21:01 GMT
        // MMM dd yyyy HH:MM z
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Java date source=" + source);
        }
        SimpleDateFormat format = null;

        format = new SimpleDateFormat("MMM d yyyy HH:mm z");
        try {
            date = format.parse(source);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Java date=" + date);
            }
        } catch (ParseException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn(e);
            }
        }
        if (date == null) {
            // try again with no HH:mm z
            format = new SimpleDateFormat("MMM d yyyy");
            try {
                date = format.parse(source);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Java date=" + date);
                }
            } catch (ParseException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.warn(e);
                }
            }
        }
        return date;
    }
}
