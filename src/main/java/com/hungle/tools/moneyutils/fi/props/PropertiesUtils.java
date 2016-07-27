package com.le.tools.moneyutils.fi.props;

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

import com.le.tools.moneyutils.fi.Utils;
import com.le.tools.moneyutils.scrubber.IngDirectScrubber;
import com.le.tools.moneyutils.scrubber.OfxScrubber;
import com.le.tools.moneyutils.scrubber.ResponseFilter;

public class PropertiesUtils {
    private static final Logger log = Logger.getLogger(PropertiesUtils.class);

    private static final String DEFAULT_APP_VER = "1900";
    private static final String DEFAULT_APP_ID = "QWIN";

    public static VelocityContext createVelocityContext(File propsFile) throws IOException {
        VelocityContext context = new VelocityContext();

        Properties props = loadProperties(propsFile);
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());

        // FI
        FIBean fi = FIBean.parseFI(props, beanUtilsBean);
        if (log.isDebugEnabled()) {
            log.debug("fi=" + fi);
        }
        context.put("fi", fi);

        // OFX
        OFX ofx = OFX.parseOFX(props, beanUtilsBean);
        if (log.isDebugEnabled()) {
            log.debug("ofx=" + ofx);
            log.debug("ofx.version=" + ofx.getVersion());
        }
        context.put("ofx", ofx);

        // requestType
        String requestType = null;
        String property = props.getProperty("requestType");
        if (!isNull(property)) {
            requestType = property;
        }
        if (log.isDebugEnabled()) {
            log.debug("requestType=" + requestType);
        }
        context.put("requestType", requestType);

        // startDate
        String startDate = parseStartDate(props);
        if (log.isDebugEnabled()) {
            log.debug("startDate=" + startDate);
        }
        context.put("startDate", startDate);
        fi.setStartDate(startDate);

        // DefaultAppID = 'QWIN'
        // DefaultAppVer = '1900'
        String appID = props.getProperty("appId");
        if (isNull(appID)) {
            appID = DEFAULT_APP_ID;
        }
        context.put("appId", appID);

        String appVer = props.getProperty("appVer");
        if (isNull(appVer)) {
            appVer = DEFAULT_APP_VER;
        }
        context.put("appVer", appVer);

        // Authentication
        Authentication auth = Authentication.parseAuthentication(props, beanUtilsBean);
        if (log.isDebugEnabled()) {
            log.debug("auth=" + auth);
        }
        context.put("auth", auth);

        // Filters
        List<ResponseFilter> responseFilters = createDefaultResponseFilters();
        context.put("filters.onResponse", responseFilters);

        // Accounts
        List<Account> accounts = Account.parseAccounts(props, beanUtilsBean);
        if (log.isDebugEnabled()) {
            log.debug("accounts=" + accounts);
        }
        context.put("accounts", accounts);

        // Http connection
        HttpProperties httpProperties = HttpProperties.parseHttpProperties(props, beanUtilsBean);
        if (log.isDebugEnabled()) {
            log.debug("httpProperties=" + httpProperties);
        }
        context.put("httpProperties", httpProperties);

        Utils utils = new Utils();
        context.put("utils", utils);

        return context;
    }

    private static String parseStartDate(Properties props) {
        String startDate = null;
        String property = props.getProperty("startDate");
        if (!isNull(property)) {
            property = property.trim();
            if (property.charAt(0) == '-') {
                try {
                    Long offset = Long.valueOf(property);
                    startDate = parseStartDate(offset);
                } catch (NumberFormatException e) {
                    log.warn(e);
                }
            } else {
                startDate = property;
            }
        }
        return startDate;
    }

    private static List<ResponseFilter> createDefaultResponseFilters() {
        List<ResponseFilter> responseFilters = new ArrayList<ResponseFilter>();
        ResponseFilter responseFilter = createDefaultResponseFilter();
        responseFilters.add(responseFilter);
        return responseFilters;
    }

    private static ResponseFilter createDefaultResponseFilter() {
        ResponseFilter responseFilter;
        responseFilter = new ResponseFilter() {

            @Override
            public void filter(File respFile, VelocityContext context) {
                FIBean fi = (FIBean) context.get("fi");
                if (fi == null) {
                    return;
                }
                String url = fi.getUrl();
                if (PropertiesUtils.isNull(url)) {
                    return;
                }
                if (!url.contains("ofx.ingdirect.com")) {
                    return;
                }

                OFX ofx = (OFX) context.get("ofx");
                if (ofx == null) {
                    return;
                }
                String version = ofx.getVersion();
                if (PropertiesUtils.isNull(version)) {
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
                log.info("> ResponseFilter: " + "url=" + fi.getUrl() + "ofx.version=" + ofx.getVersion() + ", respFile=" + respFile);
                OfxScrubber scrubber = new IngDirectScrubber();
                File outFile = new File(respFile.getAbsoluteFile().getParentFile(), respFile.getName() + "-scrubbed.txt");
                File backupFile = new File(respFile.getAbsoluteFile().getParentFile(), respFile.getName() + "-bak.txt");
                try {
                    scrubber.scrub(respFile, outFile);
                    copyFile(respFile, backupFile);
                    copyFile(outFile, respFile);
                } catch (IOException e) {
                    log.error(e, e);
                }
            }

            private void copyFile(File srcFile, File destFile) throws IOException {
                com.le.tools.moneyutils.ofx.quotes.Utils.copyFile(srcFile, destFile);
            }
        };
        return responseFilter;
    }

    public static String parseStartDate(Long offset) {
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

    public static boolean isNull(String property) {
        if (property == null) {
            return true;
        }
    
        if (property.length() <= 0) {
            return true;
        }
    
        return false;
    }

    public static void setProperties(BeanUtilsBean beanUtilsBean, Object bean, String prefix, Collection<String> keys, Properties props) {
        for (String key : keys) {
            String property = props.getProperty(prefix + "." + key);
            if (!isNull(property)) {
                try {
                    property = property.trim();
                    beanUtilsBean.setProperty(bean, key, property);
                } catch (IllegalAccessException e) {
                    log.warn(e);
                } catch (InvocationTargetException e) {
                    log.warn(e);
                }
            }
        }
    }

    // TODO_ENCRYPTION
    private static Properties loadProperties(File file) throws IOException {
        Properties props = new Properties();
        if (log.isDebugEnabled()) {
            log.debug("file=" + file);
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
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
        return props;
    }
}
