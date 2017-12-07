package com.hungle.msmoney.core.ofx.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.mapper.SymbolMapperEntry;
import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.FxSymbol;
import com.hungle.msmoney.core.stockprice.StockPrice;

import net.ofx.types.x2003.x04.AbstractPositionBase;
import net.ofx.types.x2003.x04.AbstractSecurityInfo;
import net.ofx.types.x2003.x04.BooleanType;
import net.ofx.types.x2003.x04.Currency;
import net.ofx.types.x2003.x04.CurrencyEnum;
import net.ofx.types.x2003.x04.CurrencyEnum.Enum;
import net.ofx.types.x2003.x04.GeneralSecurityInfo;
import net.ofx.types.x2003.x04.InvestmentAccount;
import net.ofx.types.x2003.x04.InvestmentPosition;
import net.ofx.types.x2003.x04.InvestmentPositionList;
import net.ofx.types.x2003.x04.InvestmentStatementResponse;
import net.ofx.types.x2003.x04.InvestmentStatementResponseMessageSetV1;
import net.ofx.types.x2003.x04.InvestmentStatementTransactionResponse;
import net.ofx.types.x2003.x04.InvestmentTransactionList;
import net.ofx.types.x2003.x04.LanguageEnum;
import net.ofx.types.x2003.x04.MutualFundInfo;
import net.ofx.types.x2003.x04.MutualFundTypeEnum;
import net.ofx.types.x2003.x04.OFX;
import net.ofx.types.x2003.x04.OFXDocument;
import net.ofx.types.x2003.x04.OptionInfo;
import net.ofx.types.x2003.x04.OtherInfo;
import net.ofx.types.x2003.x04.PositionMutualFund;
import net.ofx.types.x2003.x04.PositionOption;
import net.ofx.types.x2003.x04.PositionOther;
import net.ofx.types.x2003.x04.PositionStock;
import net.ofx.types.x2003.x04.PositionTypeEnum;
import net.ofx.types.x2003.x04.SecurityId;
import net.ofx.types.x2003.x04.SecurityList;
import net.ofx.types.x2003.x04.SeverityEnum;
import net.ofx.types.x2003.x04.SignonResponse;
import net.ofx.types.x2003.x04.SignonResponseMessageSetV1;
import net.ofx.types.x2003.x04.Status;
import net.ofx.types.x2003.x04.StockInfo;
import net.ofx.types.x2003.x04.SubAccountEnum;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxPriceInfo.
 */
public class OfxPriceInfo {

    /** The Constant log. */
    private final static Logger LOGGER = Logger.getLogger(OfxPriceInfo.class);

    private static final String TICKER = "TICKER";

    private static final String POUND_STERLING_SYMBOL = "GBP";

    private static final String PENCE_STERLING_SYMBOL = "GBX";

    /** The Constant DEFAULT_LAST_TRADE_DATE_PATTERN. */
    public static final String DEFAULT_LAST_TRADE_DATE_PATTERN = "MM/dd/yyyy";

    /** The Constant DEFAULT_UNIQUE_ID_TYPE. */
    private static final String DEFAULT_UNIQUE_ID_TYPE = TICKER;

    /** The Constant DEFAULT_CURRATE. */
    private static final String DEFAULT_CURRATE = "1.00";

    /** The Constant OFFSET_SECOND. */
    private static final long OFFSET_SECOND = 1000L;

    /** The Constant OFFSET_MINUTE. */
    private static final long OFFSET_MINUTE = 60 * OFFSET_SECOND;

    /** The Constant OFFSET_HOUR. */
    private static final long OFFSET_HOUR = 60 * OFFSET_MINUTE;

    /** The Constant OFFSET_DAY. */
    public static final long OFFSET_DAY = 24 * OFFSET_HOUR;

    /** The Constant DEFAULT_MEMO. */
    private static final String DEFAULT_MEMO = "Price as of date based on closing price";

    /** The Constant DEFAULT_BROKER_ID. */
    private static final String DEFAULT_BROKER_ID = "hungle.com";

    /** The Constant DEFAULT_ACCOUNT_ID. */
    public static final String DEFAULT_ACCOUNT_ID = "0123456789";

    private static final Boolean DEFAULT_CONVERT_TO_BASE_CURRENCY = Boolean.FALSE;

    /** The memo. */
    private final String memo = DEFAULT_MEMO;

    /** The stock prices. */
    private List<AbstractStockPrice> stockPrices;

    /** The ofx document. */
    private OFXDocument ofxDocument;

    /** The current date time. */
    private Date currentDateTime;

    /** The price formatter. */
    private NumberFormat priceFormatter;

    /** The unit formatter. */
    private NumberFormat unitFormatter;

    /** The broker id. */
    private String brokerId = DEFAULT_BROKER_ID;

    /** The account id. */
    private String accountId = DEFAULT_ACCOUNT_ID;

    /** The stock price offset. */
    private Double stockPriceOffset = 0.0;

    /** The xml options. */
    private XmlOptions xmlOptions;

    /** The cur def. */
    private CurrencyEnum.Enum curDef = CurrencyEnum.USD;

    /** The symbol mapper. */
    private SymbolMapper symbolMapper = new SymbolMapper();

    /** The allow insert comment. */
    private boolean allowInsertComment = true;

    /** The force generating INVTRANLIST. */
    private boolean forceGeneratingINVTRANLIST = false;

    /** The last trade date pattern. */
    private String lastTradeDatePattern = DEFAULT_LAST_TRADE_DATE_PATTERN;

    /** The last trade date formatter. */
    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(lastTradeDatePattern);

    /** The min last trade date. */
    private Date minLastTradeDate;

    /** The max last trade date. */
    private Date maxLastTradeDate;

    /** The date offset. */
    private Integer dateOffset = 0;

    /** The convert to base currency. */
    private Boolean convertToBaseCurrency = DEFAULT_CONVERT_TO_BASE_CURRENCY;

    /** The fx table. */
    private FxTable fxTable;

    /**
     * Instantiates a new ofx price info.
     *
     * @param stockPrices
     *            the stock prices
     * @param stockPriceOffset
     *            the stock price offset
     */
    public OfxPriceInfo(List<AbstractStockPrice> stockPrices, Double stockPriceOffset) {
        this.priceFormatter = NumberFormat.getNumberInstance();
        this.priceFormatter.setGroupingUsed(false);
        this.priceFormatter.setMinimumFractionDigits(2);
        this.priceFormatter.setMaximumFractionDigits(10);

        this.unitFormatter = NumberFormat.getNumberInstance();
        this.unitFormatter.setGroupingUsed(false);
        this.unitFormatter.setMinimumFractionDigits(3);
        this.unitFormatter.setMaximumFractionDigits(3);

        this.stockPrices = stockPrices;

        this.currentDateTime = new Date();
        this.stockPriceOffset = stockPriceOffset;

        // this.ofxDocument = createOfxResponseDocument(stockPrices);
    }

    /**
     * Creates the ofx response document.
     *
     * @param stockPrices
     *            the stock prices
     * @return the OFX document
     */
    public OFXDocument createOfxResponseDocument(List<AbstractStockPrice> stockPrices) {
        calculateMinMaxDates(stockPrices);

        this.xmlOptions = XmlBeansUtils.createXmlOptions();
        OFXDocument ofxDocument = OFXDocument.Factory.newInstance(xmlOptions);
        OFX ofx = ofxDocument.addNewOFX();

        XmlBeansUtils.insertProcInst(ofx);

        SignonResponseMessageSetV1 node = addSignonResponseMessageSetV1(ofx);
        insertComment(node, "Created by hleOfxQuotes on: " + currentDateTime);

        addInvestmentStatementResponseMessageSetV1(ofx);

        addSecurityList(ofx);

        OfxUtils.localizeXmlFragment(ofxDocument);

        return ofxDocument;
    }

    /**
     * Calculate min max dates.
     *
     * @param stockPrices
     *            the stock prices
     */
    private void calculateMinMaxDates(List<AbstractStockPrice> stockPrices) {
        minLastTradeDate = null;
        maxLastTradeDate = null;

        for (AbstractStockPrice stockPrice : stockPrices) {
            Date date = null;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(stockPrice.getStockSymbol());
            }

            date = stockPrice.getLastTrade();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("    lastTradeDate=" + date);
            }
            if (date == null) {
                // String lastTradeDate = stockPrice.getLastTradeDate();
                // if (!PropertiesUtils.isNull(lastTradeDate)) {
                // try {
                // date = lastTradeDateFormatter.parse(lastTradeDate);
                // } catch (ParseException e) {
                // LOGGER.warn(e);
                // }
                // }
                continue;
            }

            if (date != null) {
                if (minLastTradeDate == null) {
                    minLastTradeDate = date;
                } else {
                    if (date.compareTo(minLastTradeDate) < 0) {
                        minLastTradeDate = date;
                    }
                }

                if (maxLastTradeDate == null) {
                    maxLastTradeDate = date;
                } else {
                    if (date.compareTo(maxLastTradeDate) > 0) {
                        maxLastTradeDate = date;
                    }
                }
            }
        }

        LOGGER.info("minLastTradeDate=" + minLastTradeDate);
        LOGGER.info("maxLastTradeDate=" + maxLastTradeDate);
    }

    /**
     * Adds the investment statement response message set V 1.
     *
     * @param ofx
     *            the ofx
     */
    private void addInvestmentStatementResponseMessageSetV1(OFX ofx) {
        addInvestmentStatementResponseMessageSetV1(ofx, brokerId, accountId);
    }

    /**
     * Investment Statement Message Set Response Messages.
     *
     * @param ofx
     *            the ofx
     * @param brokerId
     *            the broker id
     * @param accountId
     *            the account id
     * @return the investment statement response message set V 1
     */
    private InvestmentStatementResponseMessageSetV1 addInvestmentStatementResponseMessageSetV1(OFX ofx, String brokerId,
            String accountId) {
        InvestmentStatementResponseMessageSetV1 root = ofx.addNewINVSTMTMSGSRSV1();

        // Investment Statement Response
        InvestmentStatementTransactionResponse transactionResponse = root.addNewINVSTMTTRNRS();
        // Client-assigned globally unique ID for this transaction, trnuid
        // String trnUid = "" + Math.abs(XmlBeansUtils.getRandom().nextLong());
        String trnUid = createTrnUid();
        transactionResponse.setTRNUID(trnUid);

        // Status aggregate
        Status status = transactionResponse.addNewSTATUS();
        status.setCODE("0");
        status.setSEVERITY(SeverityEnum.INFO);

        InvestmentStatementResponse statementResponse = transactionResponse.addNewINVSTMTRS();

        Date dtAsOfDate = getDtAsOfDate();
        statementResponse.setDTASOF(OfxDateTimeUtils.getStatementResponseDtAsOf(dtAsOfDate));
        // String comment = "DTASOF local time is " +
        // XmlBeansUtils.formatLocal(dtAsOfDate);
        String comment = "DTASOF local time is " + dtAsOfDate;
        insertComment(statementResponse, comment);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setCURDEF to " + curDef);
        }
        statementResponse.setCURDEF(curDef);

        InvestmentAccount investmentAccount = statementResponse.addNewINVACCTFROM();
        investmentAccount.setBROKERID(brokerId);
        investmentAccount.setACCTID(accountId);

        if (forceGeneratingINVTRANLIST) {
            forceGeneratingINVTRANLIST(statementResponse);
        }

        InvestmentPositionList investmentPositions = statementResponse.addNewINVPOSLIST();
        for (AbstractStockPrice stockPrice : stockPrices) {
            addPosition(investmentPositions, stockPrice);
        }

        return root;
    }

    private void forceGeneratingINVTRANLIST(InvestmentStatementResponse statementResponse) {
        Date dtAsOfDate = getDtAsOfDate();
        // work-around for MM2005UK, which want INVTRANLIST/{DTSTART, DTEND}
        // to set the download statement date
        InvestmentTransactionList investmentTransactionList = statementResponse.addNewINVTRANLIST();
        insertComment(investmentTransactionList, "work-around for MM2005UK to set the download statement date");
        String dtStart = OfxDateTimeUtils.getforceGeneratingINVTRANLISTDtStart(dtAsOfDate);
        investmentTransactionList.setDTSTART(dtStart);
        String dtEnd = OfxDateTimeUtils.getforceGeneratingINVTRANLISTDtEnd(dtAsOfDate);
        investmentTransactionList.setDTEND(dtEnd);
    }

    /**
     * Adds the position.
     *
     * @param investmentPositions
     *            the investment positions
     * @param stockPrice
     *            the stock price
     */
    private void addPosition(InvestmentPositionList investmentPositions, AbstractStockPrice stockPrice) {
        String quoteSourceSymbol = stockPrice.getStockSymbol();
        List<SymbolMapperEntry> list = symbolMapper.entriesByQuoteSource(quoteSourceSymbol);
        String msMoneySymbol = null;
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                msMoneySymbol = entry.getMsMoneySymbol();
                addPosition(investmentPositions, stockPrice, quoteSourceSymbol, msMoneySymbol);
            }
        } else {
            msMoneySymbol = quoteSourceSymbol;
            addPosition(investmentPositions, stockPrice, quoteSourceSymbol, msMoneySymbol);
        }
    }

    /**
     * Adds the position.
     *
     * @param investmentPositions
     *            the investment positions
     * @param stockPrice
     *            the stock price
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param msMoneySymbol
     *            the ms money symbol
     */
    private void addPosition(InvestmentPositionList investmentPositions, AbstractStockPrice stockPrice,
            String quoteSourceSymbol, String msMoneySymbol) {
        // String stockSourceTicker = null;
        // if (msMoneySymbol != null) {
        // stockSourceTicker = quoteSourceSymbol;
        // quoteSourceSymbol = msMoneySymbol;
        // }
        if (CheckNullUtils.isNull(msMoneySymbol)) {
            msMoneySymbol = quoteSourceSymbol;
        }
        double units = stockPrice.getUnits();
        String unitsStr = unitFormatter.format(units);
        Double unitPrice = getStockPrice(stockPrice);
        String currency = stockPrice.getCurrency();
        LOGGER.info("quoteSourceSymbol=" + quoteSourceSymbol + ", msMoneySymbol=" + msMoneySymbol + ", unitPrice="
                + unitPrice + ", currency=" + currency);

        // currency conversion
        boolean convertedFromGBX = false;
        if (currency != null) {
            if (currency.compareToIgnoreCase(PENCE_STERLING_SYMBOL) == 0) {
                unitPrice = unitPrice / 100.00;
                currency = POUND_STERLING_SYMBOL;
                convertedFromGBX = true;
            }
        }

        boolean convertedToBase = false;
        if (convertToBaseCurrency) {
            if (currency != null) {
                String defaultCurrency = curDef.toString();
                if (currency.compareToIgnoreCase(defaultCurrency) != 0) {
                    if (fxTable != null) {
                        Double rate = fxTable.getCurrencyRate(currency, defaultCurrency);
                        if (rate != null) {
                            unitPrice = unitPrice * rate;
                            LOGGER.info("Currency conversion: " + currency + "->" + defaultCurrency + ", rate=" + rate);
                            currency = defaultCurrency;
                            convertedToBase = true;
                        }
                    }
                }
            }
        }
        if (convertedToBase) {
            LOGGER.info("convertedToBase=" + convertedToBase);
        }

        String unitPriceStr = priceFormatter.format(unitPrice);
        String marketValue = priceFormatter.format(units * unitPrice);
        // DTPRICEASOF
        Date dtPriceAsOf = getDtAsOfDate(stockPrice);
        String dtPriceAsOfString = OfxDateTimeUtils.getInvestmentPositionListDtPriceAsOf(dtPriceAsOf);
        // String lastTradeDate = stockPrice.getLastTradeDate();
        // if (lastTradeDate != null) {
        // try {
        // dtPriceAsOfString = convertLastTradeDateToDtPriceAsOf(lastTradeDate);
        // if (dtPriceAsOfString == null) {
        // dtPriceAsOfString = XmlBeansUtils.formatGmt(dtAsOfDate);
        // }
        // } catch (ParseException e) {
        // LOGGER.warn("Failed to parse lastTradeDate=" + lastTradeDate + ",
        // tickerName=" + quoteSourceSymbol);
        // }
        // }

        if (isMutualFund(stockPrice, symbolMapper)) {
            addPositionMutualFund(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr,
                    marketValue, dtPriceAsOfString, dtPriceAsOf, currency);
        } else if (isOptions(stockPrice, symbolMapper)) {
            addPositionOptions(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr,
                    marketValue, dtPriceAsOfString, dtPriceAsOf, currency);
        } else if (isBond(stockPrice, symbolMapper)) {
            addPositionBond(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue,
                    dtPriceAsOfString, dtPriceAsOf, currency);
        } else {
            addPositionStock(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue,
                    dtPriceAsOfString, dtPriceAsOf, currency);
        }
    }

    private Date getDtAsOfDate() {
        return getDtAsOfDate(null);
    }

    /**
     * Gets the dt as of date.
     * 
     * @param stockPrice
     *
     * @return the dt as of date
     */
    private Date getDtAsOfDate(AbstractStockPrice stockPrice) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> getDtAsOfDate, minLastTradeDate=" + minLastTradeDate);
            LOGGER.debug("> getDtAsOfDate, maxLastTradeDate=" + maxLastTradeDate);
        }

        // TODO: ???
        Date dtAsOfDate = null;

        if (stockPrice != null) {
            dtAsOfDate = stockPrice.getLastTrade();
        }

        if (dtAsOfDate == null) {
            if (maxLastTradeDate != null) {
                dtAsOfDate = maxLastTradeDate;
            } else if (minLastTradeDate != null) {
                dtAsOfDate = minLastTradeDate;
            }
        }
        if (dtAsOfDate == null) {
            dtAsOfDate = currentDateTime;
        }

        if (dateOffset != 0) {
            long offset = dateOffset * OFFSET_DAY;
            dtAsOfDate = new Date(dtAsOfDate.getTime() + offset);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("< getDtAsOfDate, " + dtAsOfDate);
        }

        return dtAsOfDate;
    }

    /**
     * Creates the trn uid.
     *
     * @return the string
     */
    private String createTrnUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    /**
     * Adds the position mutual fund.
     *
     * @param investmentPositions
     *            the investment positions
     * @param ticker
     *            the ticker
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param units
     *            the units
     * @param unitPrice
     *            the unit price
     * @param marketValue
     *            the market value
     * @param dtPriceAsOfString
     *            the dt price as of
     * @param dtPriceAsOf
     * @param currency
     *            the currency
     */
    private void addPositionMutualFund(InvestmentPositionList investmentPositions, String ticker,
            String quoteSourceSymbol, String units, String unitPrice, String marketValue, String dtPriceAsOfString,
            Date dtPriceAsOf, String currency) {
        PositionMutualFund mf = investmentPositions.addNewPOSMF();
        addPosition(mf, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOfString, dtPriceAsOf,
                currency);
        mf.setREINVDIV(BooleanType.Y);
        mf.setREINVCG(BooleanType.Y);
    }

    /**
     * Adds the position bond.
     *
     * @param investmentPositions
     *            the investment positions
     * @param ticker
     *            the ticker
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param units
     *            the units
     * @param unitPrice
     *            the unit price
     * @param marketValue
     *            the market value
     * @param dtPriceAsOfString
     *            the dt price as of
     * @param dtPriceAsOf
     * @param currency
     *            the currency
     */
    private void addPositionBond(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol,
            String units, String unitPrice, String marketValue, String dtPriceAsOfString, Date dtPriceAsOf,
            String currency) {
        PositionOther other = investmentPositions.addNewPOSOTHER();
        addPosition(other, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOfString, dtPriceAsOf,
                currency);
    }

    /**
     * Adds the position options.
     *
     * @param investmentPositions
     *            the investment positions
     * @param ticker
     *            the ticker
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param units
     *            the units
     * @param unitPrice
     *            the unit price
     * @param marketValue
     *            the market value
     * @param dtPriceAsOfString
     *            the dt price as of
     * @param dtPriceAsOf
     * @param currency
     *            the currency
     */
    private void addPositionOptions(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol,
            String units, String unitPrice, String marketValue, String dtPriceAsOfString, Date dtPriceAsOf,
            String currency) {
        PositionOption options = investmentPositions.addNewPOSOPT();
        addPosition(options, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOfString, dtPriceAsOf,
                currency);
    }

    /**
     * Adds the position stock.
     *
     * @param investmentPositions
     *            the investment positions
     * @param ticker
     *            the ticker
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param units
     *            the units
     * @param unitPrice
     *            the unit price
     * @param marketValue
     *            the market value
     * @param dtPriceAsOfString
     *            the dt price as of
     * @param dtPriceAsOf
     * @param currency
     *            the currency
     */
    private void addPositionStock(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol,
            String units, String unitPrice, String marketValue, String dtPriceAsOfString, Date dtPriceAsOf,
            String currency) {
        PositionStock stock = investmentPositions.addNewPOSSTOCK();
        addPosition(stock, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOfString, dtPriceAsOf,
                currency);
        stock.setREINVDIV(BooleanType.Y);
        // stock.setREINVCG(BooleanType.Y);
    }

    /**
     * Adds the position.
     *
     * @param position
     *            the position
     * @param ticker
     *            the ticker
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param units
     *            the units
     * @param unitPrice
     *            the unit price
     * @param marketValue
     *            the market value
     * @param dtPriceAsOfString
     *            the dt price as of
     * @param dtPriceAsOf
     * @param currency
     *            the currency
     * @return the investment position
     */
    private InvestmentPosition addPosition(AbstractPositionBase position, String ticker, String quoteSourceSymbol,
            String units, String unitPrice, String marketValue, String dtPriceAsOfString, Date dtPriceAsOf,
            String currency) {
        InvestmentPosition investmentPosition = position.addNewINVPOS();
        SecurityId secId = investmentPosition.addNewSECID();

        String uniqueId = ticker;
        secId.setUNIQUEID(uniqueId);
        secId.setUNIQUEIDTYPE(DEFAULT_UNIQUE_ID_TYPE);
        if (quoteSourceSymbol != null) {
            String comment = "Ticker from quote source is: " + quoteSourceSymbol;
            insertComment(secId, comment);
        }

        investmentPosition.setHELDINACCT(SubAccountEnum.OTHER);
        investmentPosition.setPOSTYPE(PositionTypeEnum.LONG);
        investmentPosition.setUNITS(units);
        investmentPosition.setUNITPRICE(unitPrice);
        investmentPosition.setMKTVAL(marketValue);
        investmentPosition.setDTPRICEASOF(dtPriceAsOfString);

        try {
            String comment = null;

            // comment = "DTPRICEASOF local time is " +
            // XmlBeansUtils.parseGmt(dtPriceAsOf).toString();
            comment = "DTPRICEASOF local time is " + dtPriceAsOf;
            insertComment(investmentPosition, comment);
            // comment = "DTPRICEASOF GMT time is " + dtPriceAsOf;
            // insertComment(investmentPosition, comment);
            // } catch (ParseException e) {
            // LOGGER.warn(e);
            // }
        } finally {

        }
        if (currency != null) {
            Enum currencyEnum = CurrencyEnum.Enum.forString(currency);
            if (currencyEnum != null) {
                Currency c = investmentPosition.addNewCURRENCY();
                c.setCURSYM(currencyEnum);
                String curRate = getCurRate(currency);
                if (curRate == null) {
                    curRate = DEFAULT_CURRATE;
                    String comment = "Using default currency rate of " + curRate + " which is likely WRONG";
                    insertComment(c, comment);
                    comment = "Suggesting using fx.cvs file or add symbol " + currency + "USD=X";
                    insertComment(c, comment);
                }
                c.setCURRATE(curRate);
                investmentPosition.setCURRENCY(c);
            } else {
                LOGGER.warn("Cannot lookup CurrencyEnum for currency=" + currency);
            }
        }
        investmentPosition.setMEMO(memo);
        return investmentPosition;
    }

    /**
     * Insert comment.
     *
     * @param xmlObject
     *            the xml object
     * @param comment
     *            the comment
     */
    private void insertComment(XmlObject xmlObject, String comment) {
        if (!allowInsertComment) {
            return;
        }
        XmlCursor cursor = null;
        try {
            cursor = xmlObject.newCursor();
            cursor.insertComment(comment);
        } finally {
            if (cursor != null) {
                try {
                    cursor.dispose();
                } finally {
                    cursor = null;
                }
            }
        }
    }

    /**
     * Adds the security list.
     *
     * @param ofx
     *            the ofx
     * @return the security list
     */
    private SecurityList addSecurityList(OFX ofx) {
        SecurityList securityList = ofx.addNewSECLISTMSGSRSV1().addNewSECLIST();
        for (AbstractStockPrice stockPrice : stockPrices) {
            addGeneralSecurityInfo(securityList, stockPrice);
        }
        return securityList;
    }

    /**
     * Adds the general security info.
     *
     * @param securityList
     *            the security list
     * @param stockPrice
     *            the stock price
     */
    private void addGeneralSecurityInfo(SecurityList securityList, AbstractStockPrice stockPrice) {
        String quoteSourceSymbol = stockPrice.getStockSymbol();
        List<SymbolMapperEntry> list = symbolMapper.entriesByQuoteSource(quoteSourceSymbol);
        String msMoneySymbol = null;
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                msMoneySymbol = entry.getMsMoneySymbol();
                addGeneralSecurityInfo(securityList, stockPrice, quoteSourceSymbol, msMoneySymbol);
            }
        } else {
            msMoneySymbol = quoteSourceSymbol;
            addGeneralSecurityInfo(securityList, stockPrice, quoteSourceSymbol, msMoneySymbol);
        }
    }

    /**
     * Adds the general security info.
     *
     * @param securityList
     *            the security list
     * @param stockPrice
     *            the stock price
     * @param quoteSourceSymbol
     *            the quote source symbol
     * @param msMoneySymbol
     *            the ms money symbol
     */
    private void addGeneralSecurityInfo(SecurityList securityList, AbstractStockPrice stockPrice,
            String quoteSourceSymbol, String msMoneySymbol) {
        if (CheckNullUtils.isNull(msMoneySymbol)) {
            msMoneySymbol = quoteSourceSymbol;
        }

        String secName = stockPrice.getStockName();
        Double stockPriceValue = getStockPrice(stockPrice);
        String currency = stockPrice.getCurrency();

        // currency conversion
        boolean convertedFromGBX = false;
        if (currency != null) {
            if (currency.compareToIgnoreCase(PENCE_STERLING_SYMBOL) == 0) {
                stockPriceValue = stockPriceValue / 100.00;
                currency = POUND_STERLING_SYMBOL;
                convertedFromGBX = true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("convertedFromGBX=" + convertedFromGBX);
        }
        boolean convertedToBase = false;
        if (convertToBaseCurrency) {
            if (currency != null) {
                String defaultCurrency = curDef.toString();
                if (currency.compareToIgnoreCase(defaultCurrency) != 0) {
                    if (fxTable != null) {
                        Double rate = fxTable.getCurrencyRate(currency, defaultCurrency);
                        if (rate != null) {
                            LOGGER.info("Currency conversion: " + currency + "->" + defaultCurrency + ", rate=" + rate);
                            stockPriceValue = stockPriceValue * rate;
                            currency = defaultCurrency;
                            convertedToBase = true;
                        }
                    }
                }
            }
        }
        if (convertedToBase) {
            LOGGER.info("convertedToBase=" + convertedToBase);
        }

        String unitPrice = priceFormatter.format(stockPriceValue);

        // DTASOF
        Date dtAsOf = getDtAsOfDate(stockPrice);
        String dtAsOfString = OfxDateTimeUtils.getGeneralSecurityInfoDateAsOf(dtAsOf);
        // String lastTradeDate = stockPrice.getLastTradeDate();
        // if (lastTradeDate != null) {
        // try {
        // dtAsOf = convertLastTradeDateToDtPriceAsOf(lastTradeDate);
        // if (dtAsOf == null) {
        // dtAsOf = XmlBeansUtils.formatGmt(dtAsOfDate);
        // }
        // } catch (ParseException e) {
        // LOGGER.warn("Failed to parse lastTradeDate=" + lastTradeDate + ",
        // tickerName=" + quoteSourceSymbol);
        // }
        // }
        if (isMutualFund(stockPrice, symbolMapper)) {
            addMutualFundInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOfString, dtAsOf,
                    currency);
        } else if (isOptions(stockPrice, symbolMapper)) {
            addOptionsInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOfString, dtAsOf,
                    currency);
        } else if (isBond(stockPrice, symbolMapper)) {
            addBondInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOfString, dtAsOf,
                    currency);
        } else {
            addStockInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOfString, dtAsOf,
                    currency);
        }
    }

    /**
     * Checks if is mutual fund.
     *
     * @param stockPrice
     *            the stock price
     * @return true, if is mutual fund
     */
    public static boolean isMutualFund(AbstractStockPrice stockPrice, SymbolMapper symbolMapper) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper != null) {
            if (symbolMapper.hasEntry(ticker)) {
                return symbolMapper.getIsMutualFund(ticker);
            }
        }
        return stockPrice.isMf();
    }

    /**
     * Checks if is options.
     *
     * @param stockPrice
     *            the stock price
     * @return true, if is options
     */
    public static boolean isOptions(AbstractStockPrice stockPrice, SymbolMapper symbolMapper) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper != null) {
            if (symbolMapper.hasEntry(ticker)) {
                return symbolMapper.getIsOptions(ticker);
            }
        }
        return false;
    }

    /**
     * Checks if is bond.
     *
     * @param stockPrice
     *            the stock price
     * @return true, if is bond
     */
    public static boolean isBond(AbstractStockPrice stockPrice, SymbolMapper symbolMapper) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper != null) {
            if (symbolMapper.hasEntry(ticker)) {
                return symbolMapper.getIsBond(ticker);
            }
        }
        return false;
    }

    /**
     * Gets the stock price.
     *
     * @param stockPrice
     *            the stock price
     * @return the stock price
     */
    private Double getStockPrice(AbstractStockPrice stockPrice) {
        if (this.stockPriceOffset > 0.00) {
            LOGGER.info("> stockPriceOffset=" + this.stockPriceOffset);
        }
        Double price = stockPrice.getLastPrice().getPrice();

        // TODO: bond is quoted in 100 unit
        boolean isBond = isBond(stockPrice, symbolMapper);
        if (isBond) {
            price = price / 100.0;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("isBond=" + isBond);
                LOGGER.debug("  new price=" + price);
            }
        }

        return price + stockPriceOffset;
    }

    /**
     * Adds the mutual fund info.
     *
     * @param securityList
     *            the security list
     * @param ticker
     *            the ticker
     * @param quoteSourceTicker
     *            the quote source ticker
     * @param secName
     *            the sec name
     * @param unitPrice
     *            the unit price
     * @param dtAsOfString
     *            the dt as of
     * @param dtAsOf
     * @param currency
     *            the currency
     * @return the mutual fund info
     */
    private MutualFundInfo addMutualFundInfo(SecurityList securityList, String ticker, String quoteSourceTicker,
            String secName, String unitPrice, String dtAsOfString, Date dtAsOf, String currency) {
        MutualFundInfo root = securityList.addNewMFINFO();

        GeneralSecurityInfo secInfo = addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice,
                dtAsOfString, dtAsOf, currency);
        insertComment(secInfo, "Security is treated as Mutual Fund");

        root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }

    /**
     * Adds the options info.
     *
     * @param securityList
     *            the security list
     * @param ticker
     *            the ticker
     * @param quoteSourceTicker
     *            the quote source ticker
     * @param secName
     *            the sec name
     * @param unitPrice
     *            the unit price
     * @param dtAsOfString
     *            the dt as of
     * @param dtAsOf
     * @param currency
     *            the currency
     */
    private void addOptionsInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName,
            String unitPrice, String dtAsOfString, Date dtAsOf, String currency) {
        OptionInfo root = securityList.addNewOPTINFO();

        GeneralSecurityInfo secInfo = addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice,
                dtAsOfString, dtAsOf, currency);
        insertComment(secInfo, "Security is treated as Options");

        // <OPTTYPE>PUT</OPTTYPE>
        // <STRIKEPRICE>35.00</STRIKEPRICE><!--Strike price $35/share-->
        // <DTEXPIRE>20050121</DTEXPIRE><!--Option expires Jan 21, 2005-->
        // <SHPERCTRCT>100</SHPERCTRCT> <!--100 shares per contract-->
        OptionsType optionsType = null;
        String sharesPerContract = "1";
        try {
            optionsType = new OptionsType((quoteSourceTicker != null) ? quoteSourceTicker : ticker);
            String indicator = optionsType.getIndicator();
            net.ofx.types.x2003.x04.OptionTypeEnum.Enum optType = net.ofx.types.x2003.x04.OptionTypeEnum.Enum
                    .forString(indicator);
            root.setOPTTYPE(optType);
            Double value = 1.0;
            try {
                value = Double.valueOf(optionsType.getStrikePrice());
            } catch (Exception e2) {
                LOGGER.warn(e2);
                value = 1.0;
            }
            root.setSTRIKEPRICE(priceFormatter.format(value));
            root.setDTEXPIRE("20" + optionsType.getDate());
            root.setSHPERCTRCT(sharesPerContract);
        } catch (IOException e) {
            LOGGER.warn(e);
            net.ofx.types.x2003.x04.OptionTypeEnum.Enum optType = net.ofx.types.x2003.x04.OptionTypeEnum.CALL;
            root.setOPTTYPE(optType);
            Double value = 1.0;
            try {
                value = Double.valueOf(unitPrice);
            } catch (Exception e2) {
                LOGGER.warn(e2);
                value = 1.0;
            }
            root.setSTRIKEPRICE(priceFormatter.format(value));
            root.setDTEXPIRE(dtAsOfString);
            root.setSHPERCTRCT(sharesPerContract);
        }
    }

    /**
     * Adds the bond info.
     *
     * @param securityList
     *            the security list
     * @param ticker
     *            the ticker
     * @param quoteSourceTicker
     *            the quote source ticker
     * @param secName
     *            the sec name
     * @param unitPrice
     *            the unit price
     * @param dtAsOfString
     *            the dt as of
     * @param dtAsOf
     * @param currency
     *            the currency
     * @return the other info
     */
    private OtherInfo addBondInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName,
            String unitPrice, String dtAsOfString, Date dtAsOf, String currency) {
        OtherInfo root = securityList.addNewOTHERINFO();

        XmlObject secInfo = addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice, dtAsOfString,
                dtAsOf, currency);
        insertComment(secInfo, "Security is treated as Bond");

        // root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }

    /**
     * Adds the stock info.
     *
     * @param securityList
     *            the security list
     * @param ticker
     *            the ticker
     * @param quoteSourceTicker
     *            the quote source ticker
     * @param secName
     *            the sec name
     * @param unitPrice
     *            the unit price
     * @param dtAsOfString
     *            the dt as of
     * @param dtAsOf
     * @param currency
     *            the currency
     * @return the stock info
     */
    private StockInfo addStockInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName,
            String unitPrice, String dtAsOfString, Date dtAsOf, String currency) {
        StockInfo root = securityList.addNewSTOCKINFO();

        GeneralSecurityInfo secInfo = addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice,
                dtAsOfString, dtAsOf, currency);
        insertComment(secInfo, "Security is treated as Stock");

        // root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }

    /**
     * Adds the general security info.
     *
     * @param securityInfo
     *            the security info
     * @param ticker
     *            the ticker
     * @param quoteSourceTicker
     *            the quote source ticker
     * @param secName
     *            the sec name
     * @param unitPrice
     *            the unit price
     * @param dtAsOfString
     *            the dt as of
     * @param dtAsOf
     * @param currency
     *            the currency
     * @return the general security info
     */
    private GeneralSecurityInfo addGeneralSecurityInfo(AbstractSecurityInfo securityInfo, String ticker,
            String quoteSourceTicker, String secName, String unitPrice, String dtAsOfString, Date dtAsOf,
            String currency) {
        GeneralSecurityInfo secInfo = securityInfo.addNewSECINFO();
        SecurityId secId = secInfo.addNewSECID();
        String uniqueId = ticker;
        secId.setUNIQUEID(uniqueId);
        secId.setUNIQUEIDTYPE(DEFAULT_UNIQUE_ID_TYPE);

        if (quoteSourceTicker != null) {
            String comment = "Ticker from quote source is: " + quoteSourceTicker;
            insertComment(secInfo, comment);
        }
        if (CheckNullUtils.isNull(secName)) {
            secName = ticker;
        }
        secInfo.setSECNAME(secName);
        secInfo.setTICKER(ticker);
        secInfo.setUNITPRICE(unitPrice);
        secInfo.setDTASOF(dtAsOfString);
        try {
            // String comment = "DTASOF local time is " +
            // XmlBeansUtils.parseGmt(dtAsOf).toString();
            String comment = "DTASOF local time is " + dtAsOf;
            insertComment(secInfo, comment);
            // } catch (ParseException e) {
            // LOGGER.warn(e);
            // }
        } finally {

        }
        if (currency != null) {
            Enum currencyEnum = CurrencyEnum.Enum.forString(currency);
            if (currencyEnum == null) {
                // let's try all upper case
                currencyEnum = CurrencyEnum.Enum.forString(currency.toUpperCase());
            }
            if (currencyEnum != null) {
                Currency ofxCurrency = secInfo.addNewCURRENCY();
                ofxCurrency.setCURSYM(currencyEnum);
                String curRate = getCurRate(currency);
                if (curRate == null) {
                    curRate = DEFAULT_CURRATE;
                    String comment = "Using default currency rate of " + curRate + " which is likely WRONG";
                    insertComment(ofxCurrency, comment);
                    comment = "Suggesting using fx.cvs file or add symbol " + currency + "USD=X";
                    insertComment(ofxCurrency, comment);
                }
                ofxCurrency.setCURRATE(curRate);
                secInfo.setCURRENCY(ofxCurrency);
            } else {
                LOGGER.warn("Cannot lookup CurrencyEnum for currency=" + currency);
            }
        }
        secInfo.setMEMO(memo);
        return secInfo;
    }

    private String getCurRate(String currency) {
        String curRateStr = null;

        String defaultCurrency = curDef.toString();
        if (currency.compareToIgnoreCase(defaultCurrency) == 0) {
            curRateStr = DEFAULT_CURRATE;
        } else {
            if (fxTable != null) {
                Double rate = fxTable.getCurrencyRate(currency, defaultCurrency);
                if (rate != null) {
                    LOGGER.info("Currency conversion: " + currency + "->" + defaultCurrency + ", rate=" + rate);
                    curRateStr = "" + rate;
                }
            }
        }

        if (curRateStr == null) {
            LOGGER.warn("Currency conversion: " + currency + "->" + defaultCurrency + ", found NO rate.");
        }
        return curRateStr;
    }

    /**
     * Gets the ofx document.
     *
     * @return the ofx document
     */
    public OFXDocument getOfxDocument() {
        return ofxDocument;
    }

    /**
     * Save.
     *
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void save(File file) throws IOException {
        if (file == null) {
            return;
        }
        ofxDocument.save(file, xmlOptions);
    }

    /**
     * Save.
     *
     * @param stockPrices
     *            the stock prices
     * @param outFile
     *            the out file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void save(List<AbstractStockPrice> stockPrices, File outFile) throws IOException {
        String defaultCurrency = CurrencyUtils.getDefaultCurrency();
        boolean forceGeneratingINVTRANLIST = false;

        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(defaultCurrency);
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);

        SymbolMapper symbolMapper = SymbolMapper.loadMapperFile();
        FxTable fxTable = FxTableUtils.loadFxFile();

        save(stockPrices, outFile, params, symbolMapper, fxTable);
    }

    /**
     * Save.
     *
     * @param stockPrices
     *            the stock prices
     * @param outFile
     *            the out file
     * @param params
     *            the params
     * @param symbolMapper
     *            the symbol mapper
     * @param fxTable
     *            the fx table
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void save(List<AbstractStockPrice> stockPrices, File outFile, OfxSaveParameter params,
            SymbolMapper symbolMapper, FxTable fxTable) throws IOException {
        double stockPriceOffset = 0.00;

        OfxPriceInfo ofxPriceInfo = new OfxPriceInfo(stockPrices, stockPriceOffset);

        ofxPriceInfo.setForceGeneratingINVTRANLIST(params.isForceGeneratingINVTRANLIST());

        ofxPriceInfo.setDateOffset(params.getDateOffset());

        if (params.getDefaultCurrency() != null) {
            CurrencyEnum.Enum curDef = CurrencyEnum.Enum.forString(params.getDefaultCurrency());
            if (curDef == null) {
                LOGGER.warn("Cannot convert defaultCurrency=" + params.getDefaultCurrency()
                        + " to internal representation.");
            } else {
                ofxPriceInfo.setCurDef(curDef);
            }
        }
        if (!CheckNullUtils.isNull(params.getAccountId())) {
            ofxPriceInfo.setAccountId(params.getAccountId());
        }

        ofxPriceInfo.setSymbolMapper(symbolMapper);
        ofxPriceInfo.setFxTable(fxTable);

        ofxPriceInfo.setOfxDocument(ofxPriceInfo.createOfxResponseDocument(stockPrices));

        LOGGER.info("defaultCurrency=" + params.getDefaultCurrency());
        ofxPriceInfo.save(outFile);
    }

    /**
     * Removes the fx symbols.
     *
     * @param stockPrices
     *            the stock prices
     * @param ignoredStockPrices
     *            the ignored stock prices
     * @return the list
     */
    private static List<StockPrice> removeFxSymbols(List<StockPrice> stockPrices, List<StockPrice> ignoredStockPrices) {
        List<StockPrice> filtered = new ArrayList<StockPrice>();
        for (StockPrice stockPrice : stockPrices) {
            FxSymbol fxSymbol = stockPrice.getFxSymbol();
            if (fxSymbol == null) {
                filtered.add(stockPrice);
            } else {
                ignoredStockPrices.add(stockPrice);
                LOGGER.info("SKIP OFX save fxSymbol=" + stockPrice.getStockSymbol());
            }
        }

        return filtered;
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
     * @param brokerId
     *            the new broker id
     */
    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * Gets the account id.
     *
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the account id.
     *
     * @param accountId
     *            the new account id
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Adds the signon response message set V 1.
     *
     * @param ofx
     *            the ofx
     * @return the signon response message set V 1
     */
    public SignonResponseMessageSetV1 addSignonResponseMessageSetV1(OFX ofx) {
        // The Signon Message Set
        SignonResponseMessageSetV1 root = ofx.addNewSIGNONMSGSRSV1();

        // Signon Response
        SignonResponse sonrs = root.addNewSONRS();

        // Error Reporting <STATUS>
        Status status = sonrs.addNewSTATUS();
        status.setCODE("0");
        status.setSEVERITY(SeverityEnum.INFO);
        // message is optional
        status.setMESSAGE(XmlBeansUtils.SUCCESSFUL_SIGN_ON);

        /**
         * Date and time of the server response, datetime (YYYYMMDDHHMMSS.XXX)
         * 19961005132200.124[-5:EST] represents October 5, 1996, at 1:22 and
         * 124 milliseconds p.m., in Eastern Standard Time
         */
        // String dateTime = getCurrentDateTime();
        sonrs.setDTSERVER(OfxDateTimeUtils.getDtServer(currentDateTime));
//        String comment = "DTSERVER local time is " + XmlBeansUtils.formatLocal(currentDateTime);
        String comment = "DTSERVER local time is " + currentDateTime;
        insertComment(status, comment);
        if (dateOffset != 0) {
            comment = "User requests to set trade date offset to: " + dateOffset;
            insertComment(status, comment);
        }

        // Language used in text responses, language
        sonrs.setLANGUAGE(LanguageEnum.ENG);

        /*
         * Date and time of last update to profile information for any service
         * supported by this FI (see Chapter 7, "FI Profile"), datetime
         */
        // sonrs.setDTPROFUP("TODO-20010918083000");
        // Financial-Institution-identification aggregate
        // sonrs.addNewFI().setORG("TODO-Vanguard");
        return root;
    }

    /**
     * Gets the cur def.
     *
     * @return the cur def
     */
    public CurrencyEnum.Enum getCurDef() {
        return curDef;
    }

    /**
     * Sets the cur def.
     *
     * @param curDef
     *            the new cur def
     */
    public void setCurDef(CurrencyEnum.Enum curDef) {
        this.curDef = curDef;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> setCurDef, this.curDef=" + this.curDef);
        }
    }

    /**
     * Sets the ofx document.
     *
     * @param ofxDocument
     *            the new ofx document
     */
    public void setOfxDocument(OFXDocument ofxDocument) {
        this.ofxDocument = ofxDocument;
    }

    /**
     * Gets the symbol mapper.
     *
     * @return the symbol mapper
     */
    public SymbolMapper getSymbolMapper() {
        return symbolMapper;
    }

    /**
     * Sets the symbol mapper.
     *
     * @param symbolMapper
     *            the new symbol mapper
     */
    public void setSymbolMapper(SymbolMapper symbolMapper) {
        this.symbolMapper = symbolMapper;
    }

    /**
     * Checks if is allow insert comment.
     *
     * @return true, if is allow insert comment
     */
    public boolean isAllowInsertComment() {
        return allowInsertComment;
    }

    /**
     * Sets the allow insert comment.
     *
     * @param allowInsertComment
     *            the new allow insert comment
     */
    public void setAllowInsertComment(boolean allowInsertComment) {
        this.allowInsertComment = allowInsertComment;
    }

    /**
     * Checks if is force generating INVTRANLIST.
     *
     * @return true, if is force generating INVTRANLIST
     */
    public boolean isForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    /**
     * Sets the force generating INVTRANLIST.
     *
     * @param forceGeneratingINVTRANLIST
     *            the new force generating INVTRANLIST
     */
    public void setForceGeneratingINVTRANLIST(boolean forceGeneratingINVTRANLIST) {
        this.forceGeneratingINVTRANLIST = forceGeneratingINVTRANLIST;
    }

    /**
     * Gets the date offset.
     *
     * @return the date offset
     */
    public Integer getDateOffset() {
        return dateOffset;
    }

    /**
     * Sets the date offset.
     *
     * @param dateOffset
     *            the new date offset
     */
    public void setDateOffset(Integer dateOffset) {
        this.dateOffset = dateOffset;
    }

    /**
     * Gets the fx table.
     *
     * @return the fx table
     */
    public FxTable getFxTable() {
        return fxTable;
    }

    /**
     * Sets the fx table.
     *
     * @param fxTable
     *            the new fx table
     */
    public void setFxTable(FxTable fxTable) {
        this.fxTable = fxTable;
    }
}
