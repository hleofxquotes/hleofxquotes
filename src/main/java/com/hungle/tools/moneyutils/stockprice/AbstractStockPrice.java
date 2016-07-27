package com.le.tools.moneyutils.stockprice;

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

import com.le.tools.moneyutils.annotation.PropertyAnnotation;

public abstract class AbstractStockPrice {
    private static final Logger log = Logger.getLogger(AbstractStockPrice.class);

    private static BeanUtilsBean beanUtilsBean;

    private Set<FieldInfo> properties;
    static {
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

        // For type Price, user converter=PriceConverter
        convertUtilsBean.deregister(Price.class);
        convertUtilsBean.register(new PriceConverter(), Price.class);

        AbstractStockPrice.beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
    }

    protected abstract void addAnnotatedPropertyFields();

    protected void addAnnotatedPropertyFields(Field[] fields) {
        if (log.isDebugEnabled()) {
            log.debug("fields.length=" + fields.length);
        }
        for (Field field : fields) {
            if (log.isDebugEnabled()) {
                log.debug("field=" + field.getName());
            }
            Annotation annotation = field.getAnnotation(PropertyAnnotation.class);
            if (log.isDebugEnabled()) {
                log.debug("annotation=" + annotation);
            }
            if (annotation != null) {
                PropertyAnnotation propertyAnnotation = (PropertyAnnotation) annotation;
                FieldInfo fieldInfo = new FieldInfo(propertyAnnotation.index(), propertyAnnotation.key(), field.getName());
                properties.add(fieldInfo);
            }
        }
    }

    private void setBeanProperties(CsvRow row, AbstractStockPrice bean) {
        if (log.isDebugEnabled()) {
            log.debug(row.getRawRecord());
        }
        Set<FieldInfo> properties = getProperties();
        if (log.isDebugEnabled()) {
            log.debug("properties=" + properties);
        }

        for (FieldInfo fieldInfo : properties) {
            if (fieldInfo == null) {
                continue;
            }
            try {
                String value = row.getColumnValue(fieldInfo.getIndex());
                if (log.isDebugEnabled()) {
                    log.debug(fieldInfo + ", " + value);
                }
                beanUtilsBean.setProperty(bean, fieldInfo.getName(), value);
            } catch (IllegalAccessException e) {
                log.warn("Cannot set property=" + fieldInfo, e);
            } catch (InvocationTargetException e) {
                log.warn("Cannot set property=" + fieldInfo, e);
            } catch (IOException e) {
                log.warn("Cannot set property=" + fieldInfo, e);
            }
        }
    }

    public AbstractStockPrice(CsvRow row, Set<FieldInfo> properties) throws IOException {
        this();

        if (properties != null) {
            setProperties(properties);
        }

        if (log.isDebugEnabled()) {
            String rawRecord = row.getRawRecord();
            log.debug("rawRecord=" + rawRecord);
        }

        setBeanProperties(row, this);
    }

    public AbstractStockPrice(CsvRow row) throws IOException {
        this(row, null);
    }

    public AbstractStockPrice() {
        super();
        this.properties = FieldInfo.createFieldInfoSet();
        addAnnotatedPropertyFields();
    }

    public String getFormat() {
        StringBuilder sb = new StringBuilder();
        for (FieldInfo property : properties) {
            sb.append(property.getKey());
        }
        return sb.toString();
    }

    public Set<FieldInfo> getProperties() {
        return properties;
    }

    public void setProperties(Set<FieldInfo> properties) {
        this.properties = properties;
    }

    public abstract String getCurrency();

    public abstract void setCurrency(String currency);

    public abstract String getStockSymbol();

    public abstract Price getLastPrice();

    public abstract String getStockName();

    public abstract String getLastTradeDate();

    public abstract Date getLastTrade();

    public abstract void setUnits(double units);

    public abstract void updateLastPriceCurrency();

    public abstract FxSymbol getFxSymbol();

    public abstract double getUnits();

    public abstract boolean isMf();
    
    public abstract boolean isBond();
}