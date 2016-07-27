package com.le.tools.moneyutils.stockprice;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.annotation.PropertyAnnotation;
import com.le.tools.moneyutils.ofx.quotes.Utils;

public class StockPrice extends AbstractStockPrice {
    private static final Logger log = Logger.getLogger(StockPrice.class);

    static final String DEFAULT_LAST_TRADE_DATE_PATTERN = "MM/dd/yyyy";

    // http://dirk.eddelbuettel.com/code/yahooquote.html
    @PropertyAnnotation(key = "s", index = 0)
    private String stockSymbol;

    @PropertyAnnotation(key = "n", index = 1)
    private String stockName;

    @PropertyAnnotation(key = "l1", index = 2)
    private Price lastPrice;

    @PropertyAnnotation(key = "d1", index = 3)
    private String lastTradeDate;

    @PropertyAnnotation(key = "t1", index = 4)
    private String lastTradeTime;

    @PropertyAnnotation(key = "g", index = 5)
    private Price dayLow;

    @PropertyAnnotation(key = "h", index = 6)
    private Price dayHigh;

    private String currency;

    private FxSymbol fxSymbol;

    private String secType;

    private double units = 0.0;

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private Date lastTrade;

    //
    private String lastTradeDatePattern = DEFAULT_LAST_TRADE_DATE_PATTERN;
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(lastTradeDatePattern);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public StockPrice() {
        super();
    }

    public StockPrice(CsvRow row, Set<FieldInfo> properties) throws IOException {
        super(row, properties);

        init();
    }

    public StockPrice(CsvRow row) throws IOException {
        super(row);

        init();
    }

    public void init() {
        String stockSymbol = getStockSymbol();

        if (isNull(getStockName())) {
            setStockName(stockSymbol);
        }

        fxSymbol = FxSymbol.parse(stockSymbol);
        if (log.isDebugEnabled()) {
            log.debug("fxSymbol=" + fxSymbol + ", stockSymbol=" + stockSymbol);
        }
        if (fxSymbol != null) {
            Price price = getLastPrice();
            if (price != null) {
                price.setCurrency(fxSymbol.getToCurrency());
                fxSymbol.setRate(price.getPrice());
            }
        }

        calculateSecType();

        updateLastPriceCurrency();

        updateLastTrade(stockSymbol);
    }

    private void updateLastTrade(String stockSymbol) {
        String lastTradeDate = getLastTradeDate();
        if (!Utils.isNull(lastTradeDate)) {
            try {
                Date date = lastTradeDateFormatter.parse(lastTradeDate);
                setLastTrade(date);
            } catch (ParseException e) {
                log.warn("stockSymbol: " + stockSymbol + " - " + e);
            }
        }
        // hh:mm am/pm
        // String lastTradeTime = getLastTradeTime();
    }

    private boolean isNull(String str) {
        if (str == null) {
            return true;
        }

        if (str.length() <= 0) {
            return true;
        }

        return false;
    }

    @Override
    public void updateLastPriceCurrency() {
        if (currency != null) {
            if (lastPrice.getCurrency() != null) {
                lastPrice.setCurrency(currency);
            }
        }
    }

    public void calculateSecType() {
        if (isMutualFund(this)) {
            this.setSecType("MFINFO");
        } else {
            this.setSecType("STOCKINFO");
        }
    }

    @Override
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    @Override
    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    @Override
    public boolean isMf() {
        String secType = getSecType();
        if (secType == null) {
            return false;
        }
        return secType.equals("MFINFO");
    }

    @Override
    public Price getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Price lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Price getDayLow() {
        return dayLow;
    }

    public void setDayLow(Price dayLow) {
        this.dayLow = dayLow;
    }

    public Price getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(Price dayHigh) {
        this.dayHigh = dayHigh;
    }

    @Override
    public String getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(String lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    public String getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(String lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    @Override
    protected void addAnnotatedPropertyFields() {
        addAnnotatedPropertyFields(this.getClass().getDeclaredFields());
    }

    private boolean isMutualFund(StockPrice bean) {
        if (bean == null) {
            return false;
        }
        Price dayLow = bean.getDayLow();
        if (dayLow == null) {
            return false;
        }
        Price dayHigh = bean.getDayHigh();
        if (dayHigh == null) {
            return false;
        }
        return (dayLow.getPrice() <= 0.0) && (dayHigh.getPrice() <= 0.0);
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public double getUnits() {
        return this.units;
    }

    @Override
    public void setUnits(double units) {
        this.units = units;
    }

    @Override
    public FxSymbol getFxSymbol() {
        return fxSymbol;
    }

    @Override
    public Date getLastTrade() {
        return lastTrade;
    }

    public void setLastTrade(Date lastTrade) {
        this.lastTrade = lastTrade;
    }

    @Override
    public boolean isBond() {
        String secType = getSecType();
        if (secType == null) {
            return false;
        }
        return secType.equals("BOND");
    }
}
