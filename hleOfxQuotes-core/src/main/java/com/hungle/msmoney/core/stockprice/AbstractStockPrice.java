package com.hungle.msmoney.core.stockprice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.annotation.PropertyAnnotation;
import com.hungle.msmoney.core.misc.CheckNullUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractStockPrice.
 */
public abstract class AbstractStockPrice implements Cloneable {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(AbstractStockPrice.class);

    /** The Constant DEFAULT_LAST_TRADE_DATE_PATTERN. */
    public static final String DEFAULT_LAST_TRADE_DATE_PATTERN = "MM/dd/yyyy";

    public static final String DEFAULT_LAST_TRADE_TIME_PATTERN = "hh:mma";

    public static final String DEFAULT_LAST_TRADE_DATE_TIME_PATTERN = DEFAULT_LAST_TRADE_DATE_PATTERN + " "
            + DEFAULT_LAST_TRADE_TIME_PATTERN;

    /** The bean utils bean. */
    private static BeanUtilsBean beanUtilsBean;

    /** The properties. */
    private Set<FieldInfo> properties;

    /** The last trade date pattern. */
    private String lastTradeDatePattern = DEFAULT_LAST_TRADE_DATE_PATTERN;

    private String lastTradeTimePattern = DEFAULT_LAST_TRADE_TIME_PATTERN;

    private String lastTradeDateTimePattern = DEFAULT_LAST_TRADE_DATE_TIME_PATTERN;

    /** The last trade date formatter. */
    protected SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(lastTradeDatePattern);

    protected SimpleDateFormat lastTradeTimeFormatter = new SimpleDateFormat(lastTradeTimePattern);

    protected SimpleDateFormat lastTradeDateTimeFormatter = new SimpleDateFormat(lastTradeDateTimePattern);

    static {
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

        // For type Price, user converter=PriceConverter
        convertUtilsBean.deregister(Price.class);
        convertUtilsBean.register(new PriceConverter(), Price.class);

        AbstractStockPrice.beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
    }

    public static final Date createLastTradeDate(String lastTradeDate, String lastTradeTime,
            SimpleDateFormat lastTradeDateTimeFormatter, SimpleDateFormat lastTradeDateFormatter,
            SimpleDateFormat lastTradeTimeFormatter) throws ParseException {
        Date date = null;
        String dateString = lastTradeDate + " " + lastTradeTime;
        if ((!CheckNullUtils.isEmpty(lastTradeDate)) && (!CheckNullUtils.isEmpty(lastTradeTime))) {
            dateString = lastTradeDate + " " + lastTradeTime;
            date = lastTradeDateTimeFormatter.parse(dateString);
        } else if (!CheckNullUtils.isEmpty(lastTradeDate)) {
            dateString = lastTradeDate;
            date = lastTradeDateFormatter.parse(dateString);
        } else if (!CheckNullUtils.isEmpty(lastTradeTime)) {
            dateString = lastTradeTime;
            date = lastTradeTimeFormatter.parse(lastTradeTime);
        } else {
            date = null;
        }
        
        LOGGER.info("dateString=" + dateString);
        LOGGER.info("date=" + date);
        
        return date;
    }

    /**
     * Adds the annotated property fields.
     */
    protected abstract void addAnnotatedPropertyFields();

    /**
     * Adds the annotated property fields.
     *
     * @param fields
     *            the fields
     */
    protected void addAnnotatedPropertyFields(Field[] fields) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fields.length=" + fields.length);
        }
        for (Field field : fields) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("field=" + field.getName());
            }
            Annotation annotation = field.getAnnotation(PropertyAnnotation.class);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("annotation=" + annotation);
            }
            if (annotation != null) {
                PropertyAnnotation propertyAnnotation = (PropertyAnnotation) annotation;
                FieldInfo fieldInfo = new FieldInfo(propertyAnnotation.index(), propertyAnnotation.key(),
                        field.getName());
                properties.add(fieldInfo);
            }
        }
    }

    /**
     * Sets the bean properties.
     *
     * @param row
     *            the row
     * @param bean
     *            the bean
     */
    private void setBeanProperties(CsvRow row, AbstractStockPrice bean) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(row.getRawRecord());
        }
        Set<FieldInfo> properties = getProperties();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("properties=" + properties);
        }

        for (FieldInfo fieldInfo : properties) {
            if (fieldInfo == null) {
                continue;
            }
            try {
                String value = row.getColumnValue(fieldInfo.getIndex());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(fieldInfo + ", " + value);
                }
                beanUtilsBean.setProperty(bean, fieldInfo.getName(), value);
            } catch (IllegalAccessException e) {
                LOGGER.warn("Cannot set property=" + fieldInfo, e);
            } catch (InvocationTargetException e) {
                LOGGER.warn("Cannot set property=" + fieldInfo, e);
            } catch (IOException e) {
                LOGGER.warn("Cannot set property=" + fieldInfo, e);
            }
        }
    }

    /**
     * Instantiates a new abstract stock price.
     *
     * @param row
     *            the row
     * @param properties
     *            the properties
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public AbstractStockPrice(CsvRow row, Set<FieldInfo> properties) throws IOException {
        this();

        if (properties != null) {
            setProperties(properties);
        }

        if (LOGGER.isDebugEnabled()) {
            String rawRecord = row.getRawRecord();
            LOGGER.debug("rawRecord=" + rawRecord);
        }

        setBeanProperties(row, this);
    }

    /**
     * Instantiates a new abstract stock price.
     *
     * @param row
     *            the row
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public AbstractStockPrice(CsvRow row) throws IOException {
        this(row, null);
    }

    /**
     * Instantiates a new abstract stock price.
     */
    public AbstractStockPrice() {
        super();
        this.properties = FieldInfo.createFieldInfoSet();
        addAnnotatedPropertyFields();
    }

    @Override
    public String toString() {
        return "AbstractStockPrice [getCurrency()=" + getCurrency() + ", getStockSymbol()=" + getStockSymbol() + ", getLastPrice()="
                + getLastPrice() + ", getStockName()=" + getStockName() + ", getLastTrade()=" + getLastTrade() + ", getFxSymbol()="
                + getFxSymbol() + ", getUnits()=" + getUnits() + ", isMf()=" + isMf() + ", isBond()=" + isBond() + "]";
    }

    /**
     * Gets the format.
     *
     * @return the format
     */
    public String getFormat() {
        StringBuilder sb = new StringBuilder();
        for (FieldInfo property : properties) {
            sb.append(property.getKey());
        }
        return sb.toString();
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Set<FieldInfo> getProperties() {
        return properties;
    }

    /**
     * Sets the properties.
     *
     * @param properties
     *            the new properties
     */
    public void setProperties(Set<FieldInfo> properties) {
        this.properties = properties;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public abstract String getCurrency();

    /**
     * Sets the currency.
     *
     * @param currency
     *            the new currency
     */
    public abstract void setCurrency(String currency);

    /**
     * Gets the stock symbol.
     *
     * @return the stock symbol
     */
    public abstract String getStockSymbol();

    /**
     * Sets the stock symbol.
     *
     * @param stockSymbol
     *            the new stock symbol
     */
    public abstract void setStockSymbol(String stockSymbol);

    /**
     * Gets the last price.
     *
     * @return the last price
     */
    public abstract Price getLastPrice();

    /**
     * Sets the last price.
     *
     * @param price
     *            the new last price
     */
    public abstract void setLastPrice(Price price);

    /**
     * Gets the stock name.
     *
     * @return the stock name
     */
    public abstract String getStockName();

    /**
     * Sets the stock name.
     *
     * @param stockName
     *            the new stock name
     */
    public abstract void setStockName(String stockName);

    /**
     * Gets the last trade date.
     *
     * @return the last trade date
     */
    public abstract String getLastTradeDate();

    /**
     * Sets the last trade date string.
     *
     * @param dateString
     *            the new last trade date string
     */
     public abstract void setLastTradeDate(String dateString);

    /**
     * Gets the last trade.
     *
     * @return the last trade
     */
    public abstract Date getLastTrade();

    /**
     * Sets the last trade date.
     *
     * @param date
     *            the new last trade date
     */
    public abstract void setLastTrade(Date date);

    /**
     * Gets the last trade time.
     *
     * @return the last trade time
     */
     public abstract String getLastTradeTime();

    /**
     * Sets the last trade time string.
     *
     * @param timeString
     *            the new last trade time string
     */
     public abstract void setLastTradeTime(String timeString);

    /**
     * Sets the units.
     *
     * @param units
     *            the new units
     */
    public abstract void setUnits(double units);

    /**
     * Update last price currency.
     */
    public abstract void updateLastPriceCurrency();

    /**
     * Gets the fx symbol.
     *
     * @return the fx symbol
     */
    public abstract FxSymbol getFxSymbol();

    /**
     * Gets the units.
     *
     * @return the units
     */
    public abstract double getUnits();

    /**
     * Checks if is mf.
     *
     * @return true, if is mf
     */
    public abstract boolean isMf();

    /**
     * Checks if is bond.
     *
     * @return true, if is bond
     */
    public abstract boolean isBond();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public AbstractStockPrice clonePrice() throws CloneNotSupportedException {
        return (AbstractStockPrice) this.clone();
    }
}