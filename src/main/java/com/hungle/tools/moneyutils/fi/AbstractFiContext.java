package com.hungle.tools.moneyutils.fi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.UUID;

public class AbstractFiContext {

    private static final String DEFAULT_TEMPLATE = "vanguard-req-2-all.vm";

    public static final String LANGUAGE_ENG = "ENG";

    public static final String NONE = "NONE";

    public static final String ENCODING_UNICODE = "UNICODE";
    public static final String ENCODING_USASCII = "USASCII";
    private static final String DEFAULT_ENCODING = ENCODING_USASCII;
    private String encoding = DEFAULT_ENCODING;

    private String newFileUid = NONE;

    private String dtClient = createDtClient();

    private String userId;
    private String userPass;
    private String language = LANGUAGE_ENG;
    private String org;

    // Client-assigned globally-unique ID for this transaction, trnuid
    private String trnUid = createTrnUid();

    private String cltCookie = createCltCookie();

    private String brokerId;

    private String acctId;

    private String dtStart;

    private String dtAsOf;

    private String uri;

    private String template = DEFAULT_TEMPLATE;

    public AbstractFiContext() {
        super();
    }

    public static String createDtClient() {
        return createDtClient(null);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getNewFileUid() {
        return newFileUid;
    }

    public void setNewFileUid(String newFileUid) {
        this.newFileUid = newFileUid;
    }

    public String getDtClient() {
        return dtClient;
    }

    public void setDtClient(String dtClient) {
        this.dtClient = dtClient;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getTrnUid() {
        return trnUid;
    }

    public void setTrnUid(String trnUid) {
        this.trnUid = trnUid;
    }

    public String getCltCookie() {
        return cltCookie;
    }

    public void setCltCookie(String cltCookie) {
        this.cltCookie = cltCookie;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getDtStart() {
        return dtStart;
    }

    public void setDtStart(String dtStart) {
        this.dtStart = dtStart;
    }

    public String getDtAsOf() {
        return dtAsOf;
    }

    public void setDtAsOf(String dtAsOf) {
        this.dtAsOf = dtAsOf;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public static String createDtClient(String pattern) {
        // example: A8C53164-B771-42EA-944F-05ADCF517D9E
        if ((pattern == null) || (pattern.length() <= 0)) {
            pattern = "yyyyMMddHHmmss.SSS";
        }
        // <DTCLIENT>19961029101000 <!-- Oct. 29, 1996, 10:10:00 am -->
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        formatter.setCalendar(cal);
        return formatter.format(new Date());
    }

    public static String createTrnUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String createCltCookie() {
        Random random = new Random();
        // return "" + Math.abs(random.nextInt());
        return "1";
    }
}