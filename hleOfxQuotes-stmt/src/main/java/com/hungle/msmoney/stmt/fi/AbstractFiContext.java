package com.hungle.msmoney.stmt.fi;

import java.util.Random;
import java.util.UUID;

import com.hungle.msmoney.core.ofx.xmlbeans.OfxDateTimeUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractFiContext.
 */
public class AbstractFiContext {

    /** The Constant LANGUAGE_ENG. */
    public static final String LANGUAGE_ENG = "ENG";

    /** The Constant NONE. */
    public static final String NONE = "NONE";

    /** The Constant ENCODING_UNICODE. */
    public static final String ENCODING_UNICODE = "UNICODE";
    
    /** The Constant ENCODING_USASCII. */
    public static final String ENCODING_USASCII = "USASCII";
    
    /** The Constant DEFAULT_ENCODING. */
    private static final String DEFAULT_ENCODING = ENCODING_USASCII;
    
    /** The encoding. */
    private String encoding = DEFAULT_ENCODING;

    /** The new file uid. */
    private String newFileUid = NONE;

    /** The dt client. */
    private String dtClient = createDtClient();

    /** The user id. */
    private String userId;
    
    /** The user pass. */
    private String userPass;
    
    /** The language. */
    private String language = LANGUAGE_ENG;
    
    /** The org. */
    private String org;

    /** The trn uid. */
    // Client-assigned globally-unique ID for this transaction, trnuid
    private String trnUid = createTrnUid();

    /** The clt cookie. */
    private String cltCookie = createCltCookie();

    /** The broker id. */
    private String brokerId;

    /** The acct id. */
    private String acctId;

    /** The dt start. */
    private String dtStart;

    /** The dt as of. */
    private String dtAsOf;

    /** The uri. */
    private String uri;

    /** The template. */
    private String template = null;

    /**
     * Instantiates a new abstract fi context.
     */
    public AbstractFiContext() {
        super();
    }

    /**
     * Creates the dt client.
     *
     * @return the string
     */
    public static String createDtClient() {
        return OfxDateTimeUtils.createDtClient(null);
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     *
     * @param encoding the new encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the new file uid.
     *
     * @return the new file uid
     */
    public String getNewFileUid() {
        return newFileUid;
    }

    /**
     * Sets the new file uid.
     *
     * @param newFileUid the new new file uid
     */
    public void setNewFileUid(String newFileUid) {
        this.newFileUid = newFileUid;
    }

    /**
     * Gets the dt client.
     *
     * @return the dt client
     */
    public String getDtClient() {
        return dtClient;
    }

    /**
     * Sets the dt client.
     *
     * @param dtClient the new dt client
     */
    public void setDtClient(String dtClient) {
        this.dtClient = dtClient;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the user pass.
     *
     * @return the user pass
     */
    public String getUserPass() {
        return userPass;
    }

    /**
     * Sets the user pass.
     *
     * @param userPass the new user pass
     */
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the org.
     *
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * Sets the org.
     *
     * @param org the new org
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * Gets the trn uid.
     *
     * @return the trn uid
     */
    public String getTrnUid() {
        return trnUid;
    }

    /**
     * Sets the trn uid.
     *
     * @param trnUid the new trn uid
     */
    public void setTrnUid(String trnUid) {
        this.trnUid = trnUid;
    }

    /**
     * Gets the clt cookie.
     *
     * @return the clt cookie
     */
    public String getCltCookie() {
        return cltCookie;
    }

    /**
     * Sets the clt cookie.
     *
     * @param cltCookie the new clt cookie
     */
    public void setCltCookie(String cltCookie) {
        this.cltCookie = cltCookie;
    }

    /**
     * Gets the broker id.
     *
     * @return the broker id
     */
    public String getBrokerId() {
        return brokerId;
    }

    /**
     * Sets the broker id.
     *
     * @param brokerId the new broker id
     */
    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * Gets the acct id.
     *
     * @return the acct id
     */
    public String getAcctId() {
        return acctId;
    }

    /**
     * Sets the acct id.
     *
     * @param acctId the new acct id
     */
    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    /**
     * Gets the dt start.
     *
     * @return the dt start
     */
    public String getDtStart() {
        return dtStart;
    }

    /**
     * Sets the dt start.
     *
     * @param dtStart the new dt start
     */
    public void setDtStart(String dtStart) {
        this.dtStart = dtStart;
    }

    /**
     * Gets the dt as of.
     *
     * @return the dt as of
     */
    public String getDtAsOf() {
        return dtAsOf;
    }

    /**
     * Sets the dt as of.
     *
     * @param dtAsOf the new dt as of
     */
    public void setDtAsOf(String dtAsOf) {
        this.dtAsOf = dtAsOf;
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri.
     *
     * @param uri the new uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the template.
     *
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the template.
     *
     * @param template the new template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Creates the trn uid.
     *
     * @return the string
     */
    public static String createTrnUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Creates the clt cookie.
     *
     * @return the string
     */
    public static String createCltCookie() {
        Random random = new Random();
        // return "" + Math.abs(random.nextInt());
        return "1";
    }
}