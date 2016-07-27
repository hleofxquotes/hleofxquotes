package com.le.tools.moneyutils.csv2ofx;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SimpleTimeZone;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.csvreader.CsvReader;
import com.le.tools.moneyutils.ofx.quotes.OfxUtils;

public class Csv2Ofx {
    private static final Logger log = Logger.getLogger(Csv2Ofx.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String COLUMN_MEMO = "column.MEMO";
    private static final String COLUMN_NAME = "column.NAME";
    private static final String COLUMN_FITID = "column.FITID";
    private static final String COLUMN_TRNAMT = "column.TRNAMT";
    private static final String COLUMN_DTUSER = "column.DTUSER";
    private static final String COLUMN_DTPOSTED = "column.DTPOSTED";
    private static final String COLUMN_TRNTYPE = "column.TRNTYPE";

    private static final String DEFAULT_TEMPLATE = "/templates/csv2ofx.vm";
    private static final String DEFAULT_MAP_FILE = "samples/csv2ofx.props";

    private static final String DEFAULT_CSV_DATE_FORMAT_STRING = "MM/dd/yyyy";

    private List<STMTTRN> transactions;

    private LEDGERBAL ledgerBalance = new LEDGERBAL();

    private SimpleDateFormat csvDateFormat = new SimpleDateFormat(DEFAULT_CSV_DATE_FORMAT_STRING);

    private SimpleDateFormat ofxDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private Calendar calendar;

    private Date dtStart = null;
    private Date dtEnd = null;

    private Map<String, String> mapper;

    private int maxNameLength = 32;

    private int maxMemoLength = 255;

    public Csv2Ofx() {
        calendar = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
        ofxDateFormat.setCalendar(calendar);
    }

    public int convert(File csvFile, File ofxFile, File mapFile) throws IOException {
        int count = 0;
        PrintWriter writer = null;
        try {
            VelocityContext context = new VelocityContext();
            transactions = createTransactions(csvFile, mapFile);
            count = transactions.size();
            
            context.put("transactions", transactions);

            context.put("ledgerBalance", ledgerBalance);

            context.put("DTSTART", toOfxDateString(dtStart));
            context.put("DTEND", toOfxDateString(dtEnd));

            context.put("DTSERVER", toOfxDateString(new Date()));

            context.put("ORG", mapper.get("ORG"));
            context.put("FID", mapper.get("FID"));

            // Account info
            context.put("CURDEF", mapper.get("CURDEF"));
            context.put("BANKID", mapper.get("BANKID"));
            context.put("ACCTID", mapper.get("ACCTID"));
            context.put("ACCTTYPE", mapper.get("ACCTTYPE"));

            writer = new PrintWriter(new BufferedWriter(new FileWriter(ofxFile)));

            convert(context, writer);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
        
        return count;
    }

    private List<STMTTRN> createTransactions(File csvFile, File mapFile) throws IOException {
        List<STMTTRN> transactions = null;

        BufferedReader reader = null;
        CsvReader csvReader = null;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            csvReader = new CsvReader(reader);
            mapper = createMapper(mapFile);
            String dateFormatString = mapper.get("");
            if (dateFormatString != null) {
                dateFormatString = dateFormatString.trim();
                csvDateFormat = new SimpleDateFormat(dateFormatString);
            }
            transactions = createTransactions(csvReader, mapper);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } finally {
                    csvReader = null;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e, e);
                } finally {
                    reader = null;
                }
            }
        }

        return transactions;
    }

    private List<STMTTRN> createTransactions(CsvReader csvReader, Map<String, String> mapper) throws IOException {
        boolean hasHeader = true;
        return createTransactions(csvReader, mapper, hasHeader);
    }

    private Map<String, String> createMapper(File mapFile) throws IOException {
        log.info("mapFile=" + mapFile);
        Map<String, String> mapper = new HashMap<String, String>();
        Properties props = new Properties();
        Reader reader = null;
        try {
            if (mapFile == null) {
                String resourceName = DEFAULT_MAP_FILE;
                URL url = OfxUtils.getResource(resourceName);
                log.info("url=" + url);
                if (url == null) {
                    throw new IOException("Cannot find resource=" + resourceName);
                }
                reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
            } else {
                reader = new BufferedReader(new FileReader(mapFile));
            }
            props.load(reader);
            Enumeration<Object> keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();

                String value = props.getProperty(key);
                if (log.isDebugEnabled()) {
                    log.debug(key + ", " + value);
                }

                mapper.put(key, value);
            }
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
        return mapper;
    }

    private Map<String, String> createDefaultMapper() {
        // Date,Transaction Type,Check Number,Description,Amount

        Map<String, String> mapper = new HashMap<String, String>();

        mapper.put(COLUMN_TRNTYPE, "Transaction Type");
        mapper.put(COLUMN_DTPOSTED, "Date");
        // mapper.put("column.DTUSER", "Date");
        mapper.put(COLUMN_TRNAMT, "Amount");
        mapper.put(COLUMN_FITID, "FITID");
        mapper.put(COLUMN_NAME, "Description");
        mapper.put(COLUMN_MEMO, "Check Number");

        return mapper;
    }

    private Map<String, String> createDefaultMapper2() {
        Map<String, String> mapper = new HashMap<String, String>();

        mapper.put(COLUMN_TRNTYPE, "TRNTYPE");
        mapper.put(COLUMN_DTPOSTED, "DTPOSTED");
        mapper.put(COLUMN_DTUSER, "DTUSER");
        mapper.put(COLUMN_TRNAMT, "TRNAMT");
        mapper.put(COLUMN_FITID, "FITID");
        mapper.put(COLUMN_NAME, "NAME");
        mapper.put(COLUMN_MEMO, "MEMO");

        return mapper;
    }

    private List<STMTTRN> createTransactions(CsvReader csvReader, Map<String, String> mapper, boolean hasHeader) throws IOException {
        List<STMTTRN> transactions = new ArrayList<STMTTRN>();
        if (hasHeader) {
            csvReader.readHeaders();
        }

        while (csvReader.readRecord()) {
            STMTTRN transaction = null;
            try {
                transaction = createTransaction(csvReader, mapper);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            } catch (IOException e) {
                log.warn(e);
            }
        }

        return transactions;
    }

    private String toOfxDateString(String dateString) throws IOException {
        Date date = toOfxDate(dateString);
        return toOfxDateString(date);
    }

    private Date toOfxDate(String dateString) throws IOException {
        Date date = null;
        try {
            if (dateString != null) {
                date = csvDateFormat.parse(dateString);
            } else {
                date = new Date();
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
        return date;
    }

    private String toOfxDateString(Date date) {
        if (date == null) {
            date = new Date();
        }
        return ofxDateFormat.format(date);
    }

    private STMTTRN createTransaction(CsvReader csvReader, Map<String, String> mapper) throws IOException {
        STMTTRN transaction = new STMTTRN();
        String header = null;
        String value = null;
        Date date = null;

        // <DTPOSTED> - MUST
        String dateString = csvReader.get(mapper.get(COLUMN_DTPOSTED));
        date = toOfxDate(dateString);
        transaction.setDTPOSTED(toOfxDateString(date));
        dtStart = calculateDtStart(dtStart, date);
        dtEnd = calculateDtEnd(dtEnd, date);

        // <DTUSER> - optional
        header = mapper.get(COLUMN_DTUSER);
        if ((header == null) || (header.length() <= 0)) {
            transaction.setDTUSER(null);
        } else {
            dateString = csvReader.get(header);
            transaction.setDTUSER(toOfxDateString(dateString));
        }

        // <TRNAMT> - MUST
        value = csvReader.get(mapper.get(COLUMN_TRNAMT));
        // ($47.81)
        // $47.81
        Number amount = null;
        NumberFormat csvAmountFormat = NumberFormat.getCurrencyInstance();
        NumberFormat ofxAmountFormat = NumberFormat.getInstance();
        ofxAmountFormat.setGroupingUsed(false);
        try {
            amount = csvAmountFormat.parse(value);
        } catch (ParseException e) {
            throw new IOException(e);
        }
        transaction.setTRNAMT(ofxAmountFormat.format(amount.doubleValue()));

        // <TRNTYPE> - MUST
        value = csvReader.get(mapper.get(COLUMN_TRNTYPE));
        if (log.isDebugEnabled()) {
            log.debug(COLUMN_TRNTYPE + ", " + value);
        }
        if ((value != null) && (value.length() > 0)) {
            // OK - user specified
        } else {
            // default type
            if (amount.doubleValue() < 0.0) {
                value = "DEBIT";
            } else {
                value = "CREDIT";
            }
        }
        transaction.setTRNTYPE(value);

        // <FITID> - MUST
        value = csvReader.get(mapper.get(COLUMN_FITID));
        if ((value != null) && (value.length() > 0)) {
            // OK
            // User specified
        } else {
            String data = csvReader.getRawRecord();
            value = DigestUtils.md5Hex(data);
        }
        transaction.setFITID(value);

        // <NAME> - optional
        value = csvReader.get(mapper.get(COLUMN_NAME));
        if ((maxNameLength > 0) && (value.length() >= maxNameLength)) {
            log.warn("Truncate NAME to first " + maxNameLength + " chars: " + value);
            value = value.substring(0, maxNameLength);
        }
        value = StringEscapeUtils.escapeHtml(value);
        if ((value != null) && (value.length() > 0)) {
            // OK
        } else {
            value = null;
        }
        transaction.setNAME(value);
        String ledgerBalanceString = "***LEDGERBAL***";
        if (value.equals(ledgerBalanceString)) {
            ledgerBalance.setBALAMT(transaction.getTRNAMT());
            ledgerBalance.setDTASOF(transaction.getDTPOSTED());

            log.info("Found ledger balance value: " + ledgerBalance.getBALAMT() + ", " +
                    ledgerBalance.getDTASOF());
            // this is not a real transaction
            return null;
        }

        value = csvReader.get(mapper.get(COLUMN_MEMO));
        if ((maxMemoLength > 0) && (value.length() >= maxMemoLength)) {
            log.warn("Truncate MEMO to first " + maxNameLength + " chars: " + value);
            value = value.substring(0, maxMemoLength);
        }
        value = StringEscapeUtils.escapeHtml(value);
        if ((value != null) && (value.length() > 0)) {
            // OK
        } else {
            value = null;
        }
        transaction.setMEMO(value);

        return transaction;
    }

    private Date calculateDtEnd(Date dtEnd, Date date) {
        if (dtEnd == null) {
            return date;
        }

        if (date.compareTo(dtEnd) > 0) {
            dtEnd = date;
        }

        return dtEnd;
    }

    private Date calculateDtStart(Date dtStart, Date date) {
        if (dtStart == null) {
            return date;
        }

        if (date.compareTo(dtEnd) < 0) {
            dtStart = date;
        }

        return dtStart;
    }

    private void convert(VelocityContext context, PrintWriter writer) {
        String template = DEFAULT_TEMPLATE;
        String encoding = DEFAULT_ENCODING;
        Velocity.mergeTemplate(template, encoding, context, writer);
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public void setMaxNameLength(int maxNameLength) {
        this.maxNameLength = maxNameLength;
    }

    public int getMaxMemoLength() {
        return maxMemoLength;
    }

    public void setMaxMemoLength(int maxMemoLength) {
        this.maxMemoLength = maxMemoLength;
    }

}
