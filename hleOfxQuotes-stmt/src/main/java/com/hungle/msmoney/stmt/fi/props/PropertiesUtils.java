package com.hungle.msmoney.stmt.fi.props;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.SimpleTimeZone;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.stmt.scrubber.IngDirectScrubber;
import com.hungle.msmoney.stmt.scrubber.OfxScrubber;
import com.hungle.msmoney.stmt.scrubber.ResponseFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertiesUtils.
 */
public class PropertiesUtils {

    private static final String KEY_UTILS = "utils";

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class);

    private static final String KEY_FI = "fi";

    private static final String KEY_OFX = "ofx";

    public static final String KEY_REQUEST_TYPE = "requestType";

    private static final String KEY_START_DATE = "startDate";

    private static final String KEY_APP_ID = "appId";

    private static final String KEY_APP_VER = "appVer";

    private static final String KEY_AUTH_KEY = "auth";

    private static final String KEY_ACCOUNTS = "accounts";

    private static final String KEY_HTTP_PROPERTIES = "httpProperties";

    /** The Constant DEFAULT_APP_VER. */
    private static final String DEFAULT_APP_VER = "2500";

    /** The Constant DEFAULT_APP_ID. */
    private static final String DEFAULT_APP_ID = "QWIN";

    /**
     * Creates the velocity context.
     *
     * @param propsFile
     *            the props file
     * @return the velocity context
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static VelocityContext createVelocityContext(File propsFile) throws IOException {
        Properties props = loadProperties(propsFile);
        BeanUtilsBean beanUtilsBean = getDefaultBeanUtilsBean();

        return createVelocityContext(props, beanUtilsBean);
    }

    /**
     * Creates the velocity context.
     *
     * @param props the props
     * @param beanUtilsBean the bean utils bean
     * @return the velocity context
     */
    private static VelocityContext createVelocityContext(Properties props, BeanUtilsBean beanUtilsBean) {
        VelocityContext context = new VelocityContext();
        
        // FI
        FIBean fi = FIBean.parse(props, beanUtilsBean);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fi=" + fi);
        }
        context.put(KEY_FI, fi);

        // OFX
        OFX ofx = OFX.parse(props, beanUtilsBean);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ofx=" + ofx);
            LOGGER.debug("ofx.version=" + ofx.getVersion());
        }
        context.put(KEY_OFX, ofx);

        // requestType
        String requestType = null;
        String property = props.getProperty(KEY_REQUEST_TYPE);
        if (!CheckNullUtils.isEmpty(property)) {
            requestType = property;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("requestType=" + requestType);
        }
        context.put(KEY_REQUEST_TYPE, requestType);

        // startDate
        String startDate = parseStartDate(props);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("startDate=" + startDate);
        }
        context.put(KEY_START_DATE, startDate);
        fi.setStartDate(startDate);

        // DefaultAppID = 'QWIN'
        // DefaultAppVer = '1900'
        String appID = props.getProperty(KEY_APP_ID);
        if (CheckNullUtils.isEmpty(appID)) {
            appID = DEFAULT_APP_ID;
        }
        context.put(KEY_APP_ID, appID);

        String appVer = props.getProperty(KEY_APP_VER);
        if (CheckNullUtils.isEmpty(appVer)) {
            appVer = DEFAULT_APP_VER;
        }
        context.put(KEY_APP_VER, appVer);

        // Authentication
        Authentication auth = Authentication.parse(props, beanUtilsBean);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("auth=" + auth);
        }
        context.put(KEY_AUTH_KEY, auth);

        // Filters
        List<ResponseFilter> responseFilters = createDefaultResponseFilters();
        context.put("filters.onResponse", responseFilters);

        // Accounts
        List<Account> accounts = Account.parse(props, beanUtilsBean);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("accounts=" + accounts);
        }
        context.put(KEY_ACCOUNTS, accounts);

        // Http connection
        HttpProperties httpProperties = HttpProperties.parse(props, beanUtilsBean);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("httpProperties=" + httpProperties);
        }
        context.put(KEY_HTTP_PROPERTIES, httpProperties);

        Utils utils = new Utils();
        context.put(KEY_UTILS, utils);

        return context;
    }

    /**
     * Gets the default bean utils bean.
     *
     * @return the default bean utils bean
     */
    public static BeanUtilsBean getDefaultBeanUtilsBean() {
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
        return beanUtilsBean;
    }

    /**
     * Parses the start date.
     *
     * @param props
     *            the props
     * @return the string
     */
    private static String parseStartDate(Properties props) {
        String startDate = null;
        String property = props.getProperty(KEY_START_DATE);
        if (!CheckNullUtils.isEmpty(property)) {
            property = property.trim();
            if (property.charAt(0) == '-') {
                try {
                    Long offset = Long.valueOf(property);
                    startDate = parseStartDate(offset);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e);
                }
            } else {
                startDate = property;
            }
        }
        return startDate;
    }

    /**
     * Creates the default response filters.
     *
     * @return the list
     */
    private static List<ResponseFilter> createDefaultResponseFilters() {
        List<ResponseFilter> responseFilters = new ArrayList<ResponseFilter>();
        ResponseFilter responseFilter = createDefaultResponseFilter();
        responseFilters.add(responseFilter);
        return responseFilters;
    }

    /**
     * Creates the default response filter.
     *
     * @return the response filter
     */
    private static ResponseFilter createDefaultResponseFilter() {
        ResponseFilter responseFilter;
        responseFilter = new ResponseFilter() {

            @Override
            public void filter(File respFile, VelocityContext context) {
                FIBean fi = (FIBean) context.get(KEY_FI);
                if (fi == null) {
                    return;
                }
                String url = fi.getUrl();
                if (CheckNullUtils.isEmpty(url)) {
                    return;
                }
                if (!url.contains("ofx.ingdirect.com")) {
                    return;
                }

                OFX ofx = (OFX) context.get(KEY_OFX);
                if (ofx == null) {
                    return;
                }
                String version = ofx.getVersion();
                if (CheckNullUtils.isEmpty(version)) {
                    return;
                }
                int v = 0;
                try {
                    v = Integer.valueOf(version);
                } catch (NumberFormatException e) {
                    v = 0;
                }
                if (v != 1) {
                    return;
                }
                if (respFile == null) {
                    return;
                }
                if (!respFile.exists()) {
                    return;
                }
                LOGGER.info("> ResponseFilter: " + "url=" + fi.getUrl() + "ofx.version=" + ofx.getVersion()
                        + ", respFile=" + respFile);
                OfxScrubber scrubber = new IngDirectScrubber();
                File outFile = new File(respFile.getAbsoluteFile().getParentFile(),
                        respFile.getName() + "-scrubbed.txt");
                File backupFile = new File(respFile.getAbsoluteFile().getParentFile(), respFile.getName() + "-bak.txt");
                try {
                    scrubber.scrub(respFile, outFile);
                    copyFile(respFile, backupFile);
                    copyFile(outFile, respFile);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }

            private void copyFile(File srcFile, File destFile) throws IOException {
                com.hungle.msmoney.core.misc.Utils.copyFile(srcFile, destFile);
            }
        };
        return responseFilter;
    }

    /**
     * Parses the start date.
     *
     * @param offset
     *            the offset
     * @return the string
     */
    private static String parseStartDate(Long offset) {
        String startDate;
        offset = offset * (1000L * 60 * 60 * 24);
        Date date = new Date(System.currentTimeMillis() + offset);
        String pattern = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        startDate = formatter.format(date);
        return startDate;
    }

    /**
     * Pre jackson/json date. For given prefix.key, get the value in props and
     * set the matching property in 'bean'.
     * 
     * @param prefix
     *            the prefix
     * @param keys
     *            the keys
     * @param props
     *            the props
     * @param bean
     *            the bean
     * @param beanUtilsBean
     *            the bean utils bean
     */
    public static void setProperties(String prefix, Collection<String> keys, Properties props, Object bean,
            BeanUtilsBean beanUtilsBean) {
        for (String key : keys) {
            String property = null;
            if (CheckNullUtils.isEmpty(prefix)) {
                property = props.getProperty(key);
            } else {
                property = props.getProperty(prefix + "." + key);
            }
            if (!CheckNullUtils.isEmpty(property)) {
                try {
                    property = property.trim();
                    beanUtilsBean.setProperty(bean, key, property);
                } catch (IllegalAccessException e) {
                    LOGGER.warn(e);
                } catch (InvocationTargetException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }

    /**
     * Load a property file.
     *
     * @param file
     *            the file
     * @return the properties
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    // TODO_ENCRYPTION
    private static Properties loadProperties(File file) throws IOException {
        Properties props = new Properties();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("file=" + file);
        }
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            props.load(reader);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
        return props;
    }

    public static final OFX getOfx(VelocityContext context) {
        return (com.hungle.msmoney.stmt.fi.props.OFX) context.get(KEY_OFX);
    }

    public static final List<ResponseFilter> getResponseFilters(VelocityContext context) {
        return (List<ResponseFilter>) context.get("filters.onResponse");
    }

    public static final FIBean getFiBean(VelocityContext context) {
        return (FIBean) context.get(KEY_FI);
    }

    public static final HttpProperties getHttpProperties(VelocityContext context) {
        return (HttpProperties) context.get(KEY_HTTP_PROPERTIES);
    }

    public static final String getRequestType(VelocityContext context) {
        return (String) context.get(KEY_REQUEST_TYPE);
    }
}
