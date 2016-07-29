package com.hungle.tools.moneyutils.stockprice;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.annotation.PropertyAnnotation;
import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class StockPrice.
 */
public class StockPrice extends AbstractStockPrice {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(StockPrice.class);

    /** The Constant DEFAULT_LAST_TRADE_DATE_PATTERN. */
    public static final String DEFAULT_LAST_TRADE_DATE_PATTERN = "MM/dd/yyyy";

    /** The stock symbol. */
    // http://dirk.eddelbuettel.com/code/yahooquote.html
    @PropertyAnnotation(key = "s", index = 0)
    private String stockSymbol;

    /** The stock name. */
    @PropertyAnnotation(key = "n", index = 1)
    private String stockName;

    /** The last price. */
    @PropertyAnnotation(key = "l1", index = 2)
    private Price lastPrice;

    /** The last trade date. */
    @PropertyAnnotation(key = "d1", index = 3)
    private String lastTradeDate;

    /** The last trade time. */
    @PropertyAnnotation(key = "t1", index = 4)
    private String lastTradeTime;

    /** The day low. */
    @PropertyAnnotation(key = "g", index = 5)
    private Price dayLow;

    /** The day high. */
    @PropertyAnnotation(key = "h", index = 6)
    private Price dayHigh;

    /** The currency. */
    private String currency;

    /** The fx symbol. */
    private FxSymbol fxSymbol;

    /** The sec type. */
    private String secType;

    /** The units. */
    private double units = 0.0;

    /** The support. */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    /** The last trade. */
    private Date lastTrade;

    /** The last trade date pattern. */
    //
    private String lastTradeDatePattern = DEFAULT_LAST_TRADE_DATE_PATTERN;
    
    /** The last trade date formatter. */
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(lastTradeDatePattern);

    /**
     * Adds the property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes the property change listener.
     *
     * @param listener the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Instantiates a new stock price.
     */
    public StockPrice() {
        super();
    }

    /**
     * Instantiates a new stock price.
     *
     * @param row the row
     * @param properties the properties
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public StockPrice(CsvRow row, Set<FieldInfo> properties) throws IOException {
        super(row, properties);

        init();
    }

    /**
     * Instantiates a new stock price.
     *
     * @param row the row
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public StockPrice(CsvRow row) throws IOException {
        super(row);

        init();
    }

    /**
     * Inits the.
     */
    public void init() {
        String stockSymbol = getStockSymbol();

        if (PropertiesUtils.isNull(getStockName())) {
            setStockName(stockSymbol);
        }

        fxSymbol = FxSymbol.parse(stockSymbol);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fxSymbol=" + fxSymbol + ", stockSymbol=" + stockSymbol);
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

    /**
     * Update last trade.
     *
     * @param stockSymbol the stock symbol
     */
    private void updateLastTrade(String stockSymbol) {
        String lastTradeDate = getLastTradeDate();
        if (!PropertiesUtils.isNull(lastTradeDate)) {
            try {
                Date date = lastTradeDateFormatter.parse(lastTradeDate);
                setLastTrade(date);
            } catch (ParseException e) {
                LOGGER.warn("stockSymbol: " + stockSymbol + " - " + e);
            }
        }
        // hh:mm am/pm
        // String lastTradeTime = getLastTradeTime();
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#updateLastPriceCurrency()
     */
    @Override
    public void updateLastPriceCurrency() {
        if (currency != null) {
            if (lastPrice.getCurrency() != null) {
                lastPrice.setCurrency(currency);
            }
        }
    }

    /**
     * Calculate sec type.
     */
    public void calculateSecType() {
        if (isMutualFund(this)) {
            this.setSecType("MFINFO");
        } else {
            this.setSecType("STOCKINFO");
        }
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getStockSymbol()
     */
    @Override
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     * Sets the stock symbol.
     *
     * @param stockSymbol the new stock symbol
     */
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getStockName()
     */
    @Override
    public String getStockName() {
        return stockName;
    }

    /**
     * Sets the stock name.
     *
     * @param stockName the new stock name
     */
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    /**
     * Gets the sec type.
     *
     * @return the sec type
     */
    public String getSecType() {
        return secType;
    }

    /**
     * Sets the sec type.
     *
     * @param secType the new sec type
     */
    public void setSecType(String secType) {
        this.secType = secType;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#isMf()
     */
    @Override
    public boolean isMf() {
        String secType = getSecType();
        if (secType == null) {
            return false;
        }
        return secType.equals("MFINFO");
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getLastPrice()
     */
    @Override
    public Price getLastPrice() {
        return lastPrice;
    }

    /**
     * Sets the last price.
     *
     * @param lastPrice the new last price
     */
    public void setLastPrice(Price lastPrice) {
        this.lastPrice = lastPrice;
    }

    /**
     * Gets the day low.
     *
     * @return the day low
     */
    public Price getDayLow() {
        return dayLow;
    }

    /**
     * Sets the day low.
     *
     * @param dayLow the new day low
     */
    public void setDayLow(Price dayLow) {
        this.dayLow = dayLow;
    }

    /**
     * Gets the day high.
     *
     * @return the day high
     */
    public Price getDayHigh() {
        return dayHigh;
    }

    /**
     * Sets the day high.
     *
     * @param dayHigh the new day high
     */
    public void setDayHigh(Price dayHigh) {
        this.dayHigh = dayHigh;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getLastTradeDate()
     */
    @Override
    public String getLastTradeDate() {
        return lastTradeDate;
    }

    /**
     * Sets the last trade date.
     *
     * @param lastTradeDate the new last trade date
     */
    public void setLastTradeDate(String lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    /**
     * Gets the last trade time.
     *
     * @return the last trade time
     */
    public String getLastTradeTime() {
        return lastTradeTime;
    }

    /**
     * Sets the last trade time.
     *
     * @param lastTradeTime the new last trade time
     */
    public void setLastTradeTime(String lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#addAnnotatedPropertyFields()
     */
    @Override
    protected void addAnnotatedPropertyFields() {
        addAnnotatedPropertyFields(this.getClass().getDeclaredFields());
    }

    /**
     * Checks if is mutual fund.
     *
     * @param bean the bean
     * @return true, if is mutual fund
     */
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

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getCurrency()
     */
    @Override
    public String getCurrency() {
        return currency;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#setCurrency(java.lang.String)
     */
    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getUnits()
     */
    @Override
    public double getUnits() {
        return this.units;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#setUnits(double)
     */
    @Override
    public void setUnits(double units) {
        this.units = units;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getFxSymbol()
     */
    @Override
    public FxSymbol getFxSymbol() {
        return fxSymbol;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#getLastTrade()
     */
    @Override
    public Date getLastTrade() {
        return lastTrade;
    }

    /**
     * Sets the last trade.
     *
     * @param lastTrade the new last trade
     */
    public void setLastTrade(Date lastTrade) {
        this.lastTrade = lastTrade;
    }

    /* (non-Javadoc)
     * @see com.le.tools.moneyutils.stockprice.AbstractStockPrice#isBond()
     */
    @Override
    public boolean isBond() {
        String secType = getSecType();
        if (secType == null) {
            return false;
        }
        return secType.equals("BOND");
    }
}
