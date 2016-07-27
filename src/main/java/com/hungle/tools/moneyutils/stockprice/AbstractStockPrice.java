package com.hungle.tools.moneyutils.stockprice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.annotation.PropertyAnnotation;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractStockPrice.
 */
public abstract class AbstractStockPrice {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(AbstractStockPrice.class);

    /** The bean utils bean. */
    private static BeanUtilsBean beanUtilsBean;

    /** The properties. */
    private Set<FieldInfo> properties;
    static {
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

        // For type Price, user converter=PriceConverter
        convertUtilsBean.deregister(Price.class);
        convertUtilsBean.register(new PriceConverter(), Price.class);

        AbstractStockPrice.beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
    }

    /**
     * Adds the annotated property fields.
     */
    protected abstract void addAnnotatedPropertyFields();

    /**
     * Adds the annotated property fields.
     *
     * @param fields the fields
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
                FieldInfo fieldInfo = new FieldInfo(propertyAnnotation.index(), propertyAnnotation.key(), field.getName());
                properties.add(fieldInfo);
            }
        }
    }

    /**
     * Sets the bean properties.
     *
     * @param row the row
     * @param bean the bean
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
     * @param row the row
     * @param properties the properties
     * @throws IOException Signals that an I/O exception has occurred.
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
     * @param row the row
     * @throws IOException Signals that an I/O exception has occurred.
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
     * @param properties the new properties
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
     * @param currency the new currency
     */
    public abstract void setCurrency(String currency);

    /**
     * Gets the stock symbol.
     *
     * @return the stock symbol
     */
    public abstract String getStockSymbol();

    /**
     * Gets the last price.
     *
     * @return the last price
     */
    public abstract Price getLastPrice();

    /**
     * Gets the stock name.
     *
     * @return the stock name
     */
    public abstract String getStockName();

    /**
     * Gets the last trade date.
     *
     * @return the last trade date
     */
    public abstract String getLastTradeDate();

    /**
     * Gets the last trade.
     *
     * @return the last trade
     */
    public abstract Date getLastTrade();

    /**
     * Sets the units.
     *
     * @param units the new units
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
}