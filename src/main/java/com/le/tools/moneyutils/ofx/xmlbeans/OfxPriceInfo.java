package com.le.tools.moneyutils.ofx.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.le.tools.moneyutils.data.SymbolMapper;
import com.le.tools.moneyutils.ofx.quotes.FxTable;
import com.le.tools.moneyutils.ofx.quotes.OfxUtils;
import com.le.tools.moneyutils.ofx.quotes.SymbolMapperEntry;
import com.le.tools.moneyutils.ofx.quotes.Utils;
import com.le.tools.moneyutils.ofx.quotes.XmlBeansUtils;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.FxSymbol;
import com.le.tools.moneyutils.stockprice.StockPrice;

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

public class OfxPriceInfo {
    private final static Logger log = Logger.getLogger(OfxPriceInfo.class);

    public static final String DEFAULT_LAST_TRADE_DATE_PATTERN = "MM/dd/yyyy";

    private static final String DEFAULT_UNIQUE_ID_TYPE = "TICKER";

    private static final String DEFAULT_CURRATE = "1.00";

    private static final long OFFSET_SECOND = 1000L;

    private static final long OFFSET_MINUTE = 60 * OFFSET_SECOND;

    private static final long OFFSET_HOUR = 60 * OFFSET_MINUTE;

    public static final long OFFSET_DAY = 24 * OFFSET_HOUR;

    private static final String DEFAULT_MEMO = "Price as of date based on closing price";

    private final String memo = DEFAULT_MEMO;

    private static final String DEFAULT_BROKER_ID = "le.com";
    
    public static final String DEFAULT_ACCOUNT_ID = "0123456789";

    private List<AbstractStockPrice> stockPrices;

    private OFXDocument ofxDocument;

    private Date currentDateTime;

    private NumberFormat priceFormatter;

    private NumberFormat unitFormatter;

    private String brokerId = DEFAULT_BROKER_ID;

    private String accountId = DEFAULT_ACCOUNT_ID;

    private Double stockPriceOffset = 0.0;

    private XmlOptions xmlOptions;

    private CurrencyEnum.Enum curDef = CurrencyEnum.USD;

    private SymbolMapper symbolMapper = new SymbolMapper();

    private boolean allowInsertComment = true;

    private boolean forceGeneratingINVTRANLIST = false;

    private String lastTradeDatePattern = DEFAULT_LAST_TRADE_DATE_PATTERN;

    private SimpleDateFormat lastTradeDateFormatter = new SimpleDateFormat(lastTradeDatePattern);

    private Date minLastTradeDate;

    private Date maxLastTradeDate;

    private Integer dateOffset = 0;

    private Boolean convertToBaseCurrency = Boolean.TRUE;

    private FxTable fxTable;

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

    private void calculateMinMaxDates(List<AbstractStockPrice> stockPrices) {
        minLastTradeDate = null;
        maxLastTradeDate = null;
        for (AbstractStockPrice stockPrice : stockPrices) {
            Date date = null;

            date = stockPrice.getLastTrade();
            if (date == null) {
                String lastTradeDate = stockPrice.getLastTradeDate();
                if (!Utils.isNull(lastTradeDate)) {
                    try {
                        date = lastTradeDateFormatter.parse(lastTradeDate);
                    } catch (ParseException e) {
                        log.warn(e);
                    }
                }
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

        log.info("minLastTradeDate=" + minLastTradeDate);
        log.info("maxLastTradeDate=" + maxLastTradeDate);
    }

    private void addInvestmentStatementResponseMessageSetV1(OFX ofx) {
        addInvestmentStatementResponseMessageSetV1(ofx, brokerId, accountId);
    }

    /**
     * Investment Statement Message Set Response Messages
     * 
     * @param ofx
     * @param brokerId
     * @param accountId
     * @return
     */
    private InvestmentStatementResponseMessageSetV1 addInvestmentStatementResponseMessageSetV1(OFX ofx, String brokerId, String accountId) {
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
        statementResponse.setDTASOF(XmlBeansUtils.formatGmt(dtAsOfDate));
        String comment = "DTASOF local time is " + XmlBeansUtils.formatLocal(dtAsOfDate);
        insertComment(statementResponse, comment);
        if (log.isDebugEnabled()) {
            log.debug("setCURDEF to " + curDef);
        }
        statementResponse.setCURDEF(curDef);
        InvestmentAccount investmentAccount = statementResponse.addNewINVACCTFROM();
        investmentAccount.setBROKERID(brokerId);
        investmentAccount.setACCTID(accountId);

        if (forceGeneratingINVTRANLIST) {
            // work-around for MM2005UK, which want INVTRANLIST/{DTSTART, DTEND}
            // to set the download statement date
            InvestmentTransactionList investmentTransactionList = statementResponse.addNewINVTRANLIST();
            insertComment(investmentTransactionList, "work-around for MM2005UK to set the download statement date");
            String dtStart = XmlBeansUtils.formatGmt(dtAsOfDate);
            investmentTransactionList.setDTSTART(dtStart);
            String dtEnd = XmlBeansUtils.formatGmt(dtAsOfDate);
            investmentTransactionList.setDTEND(dtEnd);
        }

        InvestmentPositionList investmentPositions = statementResponse.addNewINVPOSLIST();
        for (AbstractStockPrice stockPrice : stockPrices) {
            addPosition(investmentPositions, stockPrice, dtAsOfDate);
        }

        return root;
    }

    private void addPosition(InvestmentPositionList investmentPositions, AbstractStockPrice stockPrice, Date dtAsOfDate) {
        String quoteSourceSymbol = stockPrice.getStockSymbol();
        List<SymbolMapperEntry> list = symbolMapper.entriesByQuoteSource(quoteSourceSymbol);
        String msMoneySymbol = null;
        if (list != null) {
            for (SymbolMapperEntry entry : list) {
                msMoneySymbol = entry.getMsMoneySymbol();
                addPosition(investmentPositions, stockPrice, dtAsOfDate, quoteSourceSymbol, msMoneySymbol);
            }
        } else {
            msMoneySymbol = quoteSourceSymbol;
            addPosition(investmentPositions, stockPrice, dtAsOfDate, quoteSourceSymbol, msMoneySymbol);
        }
    }

    private void addPosition(InvestmentPositionList investmentPositions, AbstractStockPrice stockPrice, Date dtAsOfDate, String quoteSourceSymbol,
            String msMoneySymbol) {
        // String stockSourceTicker = null;
        // if (msMoneySymbol != null) {
        // stockSourceTicker = quoteSourceSymbol;
        // quoteSourceSymbol = msMoneySymbol;
        // }
        if (Utils.isNull(msMoneySymbol)) {
            msMoneySymbol = quoteSourceSymbol;
        }
        double units = stockPrice.getUnits();
        String unitsStr = unitFormatter.format(units);
        Double unitPrice = getStockPrice(stockPrice);
        String currency = stockPrice.getCurrency();

        // currency conversion
        boolean convertedFromGBX = false;
        if (currency != null) {
            if (currency.compareToIgnoreCase("GBX") == 0) {
                unitPrice = unitPrice / 100.00;
                currency = "GBP";
                convertedFromGBX = true;
            }
        }
        boolean convertToBase = false;
        if (convertToBaseCurrency) {
            if (currency != null) {
                String defaultCurrency = curDef.toString();
                if (currency.compareToIgnoreCase(defaultCurrency) != 0) {
                    if (fxTable != null) {
                        Double rate = fxTable.getRateString(currency, defaultCurrency);
                        if (rate != null) {
                            unitPrice = unitPrice * rate;
                            log.info("Currency conversion: " + currency + "->" + defaultCurrency + ", rate=" + rate);
                            currency = defaultCurrency;
                            convertToBase = true;
                        }
                    }
                }
            }
        }

        String unitPriceStr = priceFormatter.format(unitPrice);
        String marketValue = priceFormatter.format(units * unitPrice);
        // DTPRICEASOF
        String dtPriceAsOfString = null;
        dtPriceAsOfString = XmlBeansUtils.formatGmt(dtAsOfDate);
        String lastTradeDate = stockPrice.getLastTradeDate();
        if (lastTradeDate != null) {
            try {
                dtPriceAsOfString = convertLastTradeDateToDtPriceAsOf(lastTradeDate);
                if (dtPriceAsOfString == null) {
                    dtPriceAsOfString = XmlBeansUtils.formatGmt(dtAsOfDate);
                }
            } catch (ParseException e) {
                log.warn("Failed to parse lastTradeDate=" + lastTradeDate + ", tickerName=" + quoteSourceSymbol);
            }
        }
        if (isMutualFund(stockPrice)) {
            addPositionMutualFund(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue, dtPriceAsOfString, currency);
        } else if (isOptions(stockPrice)) {
            addPositionOptions(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue, dtPriceAsOfString, currency);
        } else if (isBond(stockPrice)) {
            addPositionBond(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue, dtPriceAsOfString, currency);
        } else {
            addPositionStock(investmentPositions, msMoneySymbol, quoteSourceSymbol, unitsStr, unitPriceStr, marketValue, dtPriceAsOfString, currency);
        }
    }

    private Date getDtAsOfDate() {
        Date dtAsOfDate = currentDateTime;
        if (maxLastTradeDate != null) {
            dtAsOfDate = maxLastTradeDate;
        } else if (minLastTradeDate != null) {
            dtAsOfDate = minLastTradeDate;
        }

        if (dateOffset != 0) {
            long offset = dateOffset * OFFSET_DAY;
            dtAsOfDate = new Date(dtAsOfDate.getTime() + offset);
        }

        return dtAsOfDate;
    }

    private String createTrnUid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    private String convertLastTradeDateToDtPriceAsOf(String lastTradeDate) throws ParseException {
        Date date = lastTradeDateFormatter.parse(lastTradeDate);

        if (dateOffset != 0) {
            long offset = dateOffset * OFFSET_DAY;
            date = new Date(date.getTime() + offset);
        }

        String str = XmlBeansUtils.formatGmt(date);
        return str;
    }

    private void addPositionMutualFund(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol, String units, String unitPrice,
            String marketValue, String dtPriceAsOf, String currency) {
        PositionMutualFund mf = investmentPositions.addNewPOSMF();
        addPosition(mf, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOf, currency);
        mf.setREINVDIV(BooleanType.Y);
        mf.setREINVCG(BooleanType.Y);
    }

    private void addPositionBond(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol, String units, String unitPrice,
            String marketValue, String dtPriceAsOf, String currency) {
        PositionOther other = investmentPositions.addNewPOSOTHER();
        addPosition(other, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOf, currency);
    }

    private void addPositionOptions(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol, String units, String unitPrice,
            String marketValue, String dtPriceAsOf, String currency) {
        PositionOption options = investmentPositions.addNewPOSOPT();
        addPosition(options, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOf, currency);
    }

    private void addPositionStock(InvestmentPositionList investmentPositions, String ticker, String quoteSourceSymbol, String units, String unitPrice,
            String marketValue, String dtPriceAsOf, String currency) {
        PositionStock stock = investmentPositions.addNewPOSSTOCK();
        addPosition(stock, ticker, quoteSourceSymbol, units, unitPrice, marketValue, dtPriceAsOf, currency);
        stock.setREINVDIV(BooleanType.Y);
        // stock.setREINVCG(BooleanType.Y);
    }

    private InvestmentPosition addPosition(AbstractPositionBase position, String ticker, String quoteSourceSymbol, String units, String unitPrice,
            String marketValue, String dtPriceAsOf, String currency) {
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
        investmentPosition.setDTPRICEASOF(dtPriceAsOf);
        try {
            String comment = "DTPRICEASOF local time is " + XmlBeansUtils.parseGmt(dtPriceAsOf).toString();
            insertComment(secId, comment);
        } catch (ParseException e) {
            log.warn(e);
        }
        if (currency != null) {
            Enum s = CurrencyEnum.Enum.forString(currency);
            if (s != null) {
                Currency c = investmentPosition.addNewCURRENCY();
                c.setCURSYM(s);
                c.setCURRATE(DEFAULT_CURRATE); // TODO
                investmentPosition.setCURRENCY(c);
            } else {
                log.warn("Cannot lookup CurrencyEnum for currency=" + currency);
            }
        }
        investmentPosition.setMEMO(memo);
        return investmentPosition;
    }

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

    private SecurityList addSecurityList(OFX ofx) {
        SecurityList securityList = ofx.addNewSECLISTMSGSRSV1().addNewSECLIST();
        for (AbstractStockPrice stockPrice : stockPrices) {
            addGeneralSecurityInfo(securityList, stockPrice);
        }
        return securityList;
    }

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

    private void addGeneralSecurityInfo(SecurityList securityList, AbstractStockPrice stockPrice, String quoteSourceSymbol, String msMoneySymbol) {
        if (Utils.isNull(msMoneySymbol)) {
            msMoneySymbol = quoteSourceSymbol;
        }

        String secName = stockPrice.getStockName();
        Double stockPriceValue = getStockPrice(stockPrice);
        String currency = stockPrice.getCurrency();

        // currency conversion
        boolean convertedFromGBX = false;
        if (currency != null) {
            if (currency.compareToIgnoreCase("GBX") == 0) {
                stockPriceValue = stockPriceValue / 100.00;
                currency = "GBP";
                convertedFromGBX = true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("convertedFromGBX=" + convertedFromGBX);
        }
        boolean convertToBase = false;
        if (convertToBaseCurrency) {
            if (currency != null) {
                String defaultCurrency = curDef.toString();
                if (currency.compareToIgnoreCase(defaultCurrency) != 0) {
                    if (fxTable != null) {
                        Double rate = fxTable.getRateString(currency, defaultCurrency);
                        if (rate != null) {
                            log.info("Currency conversion: " + currency + "->" + defaultCurrency + ", rate=" + rate);
                            stockPriceValue = stockPriceValue * rate;
                            currency = defaultCurrency;
                            convertToBase = true;
                        }
                    }
                }
            }
        }

        String unitPrice = priceFormatter.format(stockPriceValue);

        // DTASOF
        Date dtAsOfDate = getDtAsOfDate();
        String dtAsOf = null;
        dtAsOf = XmlBeansUtils.formatGmt(dtAsOfDate);
        String lastTradeDate = stockPrice.getLastTradeDate();
        if (lastTradeDate != null) {
            try {
                dtAsOf = convertLastTradeDateToDtPriceAsOf(lastTradeDate);
                if (dtAsOf == null) {
                    dtAsOf = XmlBeansUtils.formatGmt(dtAsOfDate);
                }
            } catch (ParseException e) {
                log.warn("Failed to parse lastTradeDate=" + lastTradeDate + ", tickerName=" + quoteSourceSymbol);
            }
        }
        if (isMutualFund(stockPrice)) {
            addMutualFundInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOf, currency);
        } else if (isOptions(stockPrice)) {
            addOptionsInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOf, currency);
        } else if (isBond(stockPrice)) {
            addBondInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOf, currency);
        } else {
            addStockInfo(securityList, msMoneySymbol, quoteSourceSymbol, secName, unitPrice, dtAsOf, currency);
        }
    }

    private boolean isMutualFund(AbstractStockPrice stockPrice) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper.hasEntry(ticker)) {
            return symbolMapper.getIsMutualFund(ticker);
        }
        return stockPrice.isMf();
    }

    private boolean isOptions(AbstractStockPrice stockPrice) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper.hasEntry(ticker)) {
            return symbolMapper.getIsOptions(ticker);
        }
        return false;
    }

    private boolean isBond(AbstractStockPrice stockPrice) {
        String ticker = stockPrice.getStockSymbol();
        if (symbolMapper.hasEntry(ticker)) {
            return symbolMapper.getIsBond(ticker);
        }
        return false;
    }
    
    private Double getStockPrice(AbstractStockPrice stockPrice) {
        if (this.stockPriceOffset > 0.00) {
            log.info("> stockPriceOffset=" + this.stockPriceOffset);
        }
        Double price = stockPrice.getLastPrice().getPrice();
        
        // TODO: bond is quoted in 100 unit
        boolean isBond = isBond(stockPrice); 
        if (isBond) {
            price = price / 100.0;
            if (log.isDebugEnabled()) {
                log.debug("isBond=" + isBond);
                log.debug("  new price=" + price);
            }
        }
        
        return price + stockPriceOffset;
    }

    private MutualFundInfo addMutualFundInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName, String unitPrice,
            String dtAsOf, String currency) {
        MutualFundInfo root = securityList.addNewMFINFO();

        addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice, dtAsOf, currency);

        root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }

    private void addOptionsInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName, String unitPrice, String dtAsOf,
            String currency) {
        OptionInfo root = securityList.addNewOPTINFO();
        addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice, dtAsOf, currency);
        // <OPTTYPE>PUT</OPTTYPE>
        // <STRIKEPRICE>35.00</STRIKEPRICE><!--Strike price $35/share-->
        // <DTEXPIRE>20050121</DTEXPIRE><!--Option expires Jan 21, 2005-->
        // <SHPERCTRCT>100</SHPERCTRCT> <!--100 shares per contract-->
        OptionsType optionsType = null;
        String sharesPerContract = "1";
        try {
            optionsType = new OptionsType((quoteSourceTicker != null) ? quoteSourceTicker : ticker);
            String indicator = optionsType.getIndicator();
            net.ofx.types.x2003.x04.OptionTypeEnum.Enum optType = net.ofx.types.x2003.x04.OptionTypeEnum.Enum.forString(indicator);
            root.setOPTTYPE(optType);
            Double value = 1.0;
            try {
                value = Double.valueOf(optionsType.getStrikePrice());
            } catch (Exception e2) {
                log.warn(e2);
                value = 1.0;
            }
            root.setSTRIKEPRICE(priceFormatter.format(value));
            root.setDTEXPIRE("20" + optionsType.getDate());
            root.setSHPERCTRCT(sharesPerContract);
        } catch (IOException e) {
            log.warn(e);
            net.ofx.types.x2003.x04.OptionTypeEnum.Enum optType = net.ofx.types.x2003.x04.OptionTypeEnum.CALL;
            root.setOPTTYPE(optType);
            Double value = 1.0;
            try {
                value = Double.valueOf(unitPrice);
            } catch (Exception e2) {
                log.warn(e2);
                value = 1.0;
            }
            root.setSTRIKEPRICE(priceFormatter.format(value));
            root.setDTEXPIRE(dtAsOf);
            root.setSHPERCTRCT(sharesPerContract);
        }
    }

    private OtherInfo addBondInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName, String unitPrice, String dtAsOf,
            String currency) {
        OtherInfo root = securityList.addNewOTHERINFO();

        addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice, dtAsOf, currency);

        // root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }
    
    private StockInfo addStockInfo(SecurityList securityList, String ticker, String quoteSourceTicker, String secName, String unitPrice, String dtAsOf,
            String currency) {
        StockInfo root = securityList.addNewSTOCKINFO();

        addGeneralSecurityInfo(root, ticker, quoteSourceTicker, secName, unitPrice, dtAsOf, currency);

        // root.setMFTYPE(MutualFundTypeEnum.OPENEND);

        return root;
    }

    private GeneralSecurityInfo addGeneralSecurityInfo(AbstractSecurityInfo securityInfo, String ticker, String quoteSourceTicker, String secName,
            String unitPrice, String dtAsOf, String currency) {
        GeneralSecurityInfo secInfo = securityInfo.addNewSECINFO();
        SecurityId secId = secInfo.addNewSECID();
        String uniqueId = ticker;
        secId.setUNIQUEID(uniqueId);
        secId.setUNIQUEIDTYPE(DEFAULT_UNIQUE_ID_TYPE);

        if (quoteSourceTicker != null) {
            String comment = "Ticker from quote source is: " + quoteSourceTicker;
            insertComment(secInfo, comment);
        }
        if (Utils.isNull(secName)) {
            secName = ticker;
        }
        secInfo.setSECNAME(secName);
        secInfo.setTICKER(ticker);
        secInfo.setUNITPRICE(unitPrice);
        secInfo.setDTASOF(dtAsOf);
        try {
            String comment = "DTASOF local time is " + XmlBeansUtils.parseGmt(dtAsOf).toString();
            insertComment(secId, comment);
        } catch (ParseException e) {
            log.warn(e);
        }
        if (currency != null) {
            Enum s = CurrencyEnum.Enum.forString(currency);
            if (s != null) {
                Currency c = secInfo.addNewCURRENCY();
                c.setCURSYM(s);
                c.setCURRATE(DEFAULT_CURRATE); // TODO
                secInfo.setCURRENCY(c);
            } else {
                log.warn("Cannot lookup CurrencyEnum for currency=" + currency);
            }
        }
        secInfo.setMEMO(memo);
        return secInfo;
    }

    public OFXDocument getOfxDocument() {
        return ofxDocument;
    }

    public void save(File file) throws IOException {
        if (file == null) {
            return;
        }
        ofxDocument.save(file, xmlOptions);
    }

    public static void save(List<AbstractStockPrice> stockPrices, File outFile, OfxSaveParameter params, SymbolMapper symbolMapper, FxTable fxTable)
            throws IOException {
        double stockPriceOffset = 0.00;

        OfxPriceInfo ofxPriceInfo = new OfxPriceInfo(stockPrices, stockPriceOffset);

        ofxPriceInfo.setForceGeneratingINVTRANLIST(params.isForceGeneratingINVTRANLIST());

        ofxPriceInfo.setDateOffset(params.getDateOffset());

        if (params.getDefaultCurrency() != null) {
            CurrencyEnum.Enum curDef = CurrencyEnum.Enum.forString(params.getDefaultCurrency());
            if (curDef == null) {
                log.warn("Cannot convert defaultCurrency=" + params.getDefaultCurrency() + " to internal representation.");
            } else {
                ofxPriceInfo.setCurDef(curDef);
            }
        }
        if (!Utils.isNull(params.getAccountId())) {
            ofxPriceInfo.setAccountId(params.getAccountId());
        }
        // SymbolMapper symbolMapper = new SymbolMapper();
        // File symbolMapperFile = new File("mapper.csv");
        // log.info("Looking for mapper=" +
        // symbolMapperFile.getAbsoluteFile().getAbsolutePath());
        // if (symbolMapperFile.exists()) {
        // try {
        // symbolMapper.load(symbolMapperFile);
        // log.info("Loaded symbolMapperFile=" + symbolMapperFile);
        // } catch (IOException e) {
        // log.warn("Cannot load symbolMapperFile=" + symbolMapperFile);
        // }
        // } else {
        // log.info("No mapper.csv file.");
        // }
        ofxPriceInfo.setSymbolMapper(symbolMapper);
        ofxPriceInfo.setFxTable(fxTable);
        // for (StockPriceBean stockPrice : stockPrices) {
        // String symbol = stockPrice.getStockSymbol();
        // if (isNull(symbol)) {
        // continue;
        // }
        // List<SymbolMapperEntry> entries =
        // symbolMapper.entriesByQuoteSource(symbol);
        // if (entries == null) {
        // continue;
        // }
        // for (SymbolMapperEntry entry : entries) {
        // String currency = entry.getQuotesSourceCurrency();
        // if (!isNull(currency)) {
        // stockPrice.setCurrency(currency);
        // break;
        // }
        // }
        // }

        ofxPriceInfo.setOfxDocument(ofxPriceInfo.createOfxResponseDocument(stockPrices));

        log.info("defaultCurrency=" + params.getDefaultCurrency());
        ofxPriceInfo.save(outFile);

        // return symbolMapper;
    }

    private static List<StockPrice> removeFxSymbols(List<StockPrice> stockPrices, List<StockPrice> ignoredStockPrices) {
        List<StockPrice> filtered = new ArrayList<StockPrice>();
        for (StockPrice stockPrice : stockPrices) {
            FxSymbol fxSymbol = stockPrice.getFxSymbol();
            if (fxSymbol == null) {
                filtered.add(stockPrice);
            } else {
                ignoredStockPrices.add(stockPrice);
                log.info("SKIP OFX save fxSymbol=" + stockPrice.getStockSymbol());
            }
        }

        return filtered;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

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
        sonrs.setDTSERVER(XmlBeansUtils.formatGmt(currentDateTime));
        String comment = "DTSERVER local time is " + XmlBeansUtils.formatLocal(currentDateTime);
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

    public CurrencyEnum.Enum getCurDef() {
        return curDef;
    }

    public void setCurDef(CurrencyEnum.Enum curDef) {
        this.curDef = curDef;
        if (log.isDebugEnabled()) {
            log.debug("> setCurDef, this.curDef=" + this.curDef);
        }
    }

    public void setOfxDocument(OFXDocument ofxDocument) {
        this.ofxDocument = ofxDocument;
    }

    public SymbolMapper getSymbolMapper() {
        return symbolMapper;
    }

    public void setSymbolMapper(SymbolMapper symbolMapper) {
        this.symbolMapper = symbolMapper;
    }

    public boolean isAllowInsertComment() {
        return allowInsertComment;
    }

    public void setAllowInsertComment(boolean allowInsertComment) {
        this.allowInsertComment = allowInsertComment;
    }

    public boolean isForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    public void setForceGeneratingINVTRANLIST(boolean forceGeneratingINVTRANLIST) {
        this.forceGeneratingINVTRANLIST = forceGeneratingINVTRANLIST;
    }

    public Integer getDateOffset() {
        return dateOffset;
    }

    public void setDateOffset(Integer dateOffset) {
        this.dateOffset = dateOffset;
    }

    public FxTable getFxTable() {
        return fxTable;
    }

    public void setFxTable(FxTable fxTable) {
        this.fxTable = fxTable;
    }
}
