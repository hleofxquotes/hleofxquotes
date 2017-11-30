package com.hungle.msmoney.qs.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hungle.msmoney.stmt.fi.AbstractFiDir;
import com.hungle.msmoney.stmt.fi.DefaultFiDir;
import com.hungle.msmoney.stmt.fi.props.OFX;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckOfxVersion.
 */
public class CheckOfxVersion {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(CheckOfxVersion.class);

    /**
     * The Class MyUpdateFiDir.
     */
    private final class MyFiDir extends DefaultFiDir {

        /** The version. */
        private final String version;

        /**
         * Instantiates a new my update fi dir.
         *
         * @param dir
         *            the dir
         * @param version
         *            the version
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private MyFiDir(File dir, String version) throws IOException {
            super(dir);
            this.version = version;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.hungle.tools.moneyutils.fi.AbstractUpdateFiDir#checkRespFile(java
         * .io.File, com.hungle.tools.moneyutils.fi.props.OFX)
         */
        @Override
        protected void checkRespFile(File respFile, OFX ofx) throws IOException {
            setOfxVersion(ofx);
            super.checkRespFile(respFile, ofx);
        }

        private void setOfxVersion(OFX ofx) {
            if (version.compareToIgnoreCase("v1") == 0) {
                ofx.setVersion("1");
            } else if (version.compareToIgnoreCase("v2") == 0) {
                ofx.setVersion("2");
            } else {
                ofx.setVersion("1");
            }
        }
    }

    /** The account id pattern. */
    private Pattern accountIdPattern = Pattern.compile("\\<ACCTID\\>" + "([a-zA-Z0-9]+)");

    /** The bank id pattern. */
    private Pattern bankIdPattern = Pattern.compile("\\<BANKID\\>" + "([a-zA-Z0-9]+)");

    /**
     * Parses the account inquiry response.
     *
     * @param version
     *            the version
     * @param respFile
     *            the resp file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void parseAccountInquiryResponse(String version, File respFile) throws IOException {
        List<String> bankIds = new ArrayList<String>();
        List<String> accountIds = new ArrayList<String>();

        if (version.equals("v1")) {
            parseAccountInquiryResponseV1(version, respFile, bankIds, accountIds);
        } else if (version.equals("v2")) {
            parseAccountInquiryResponseV2(version, respFile, bankIds, accountIds);
        }
    }

    private void parseAccountInquiryResponseV2(String version, File respFile, List<String> bankIds,
            List<String> accountIds) throws IOException {
        PrintWriter writer = null;
        try {
            bankIds.clear();
            accountIds.clear();

            File outFile = new File(respFile.getAbsoluteFile().getParentFile(),
                    "accountInquiry-" + version + "-info.txt");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(respFile);
            doc.getDocumentElement().normalize();

            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            findBANKIDV2(doc, xpath, bankIds);

            findACCTIDV2(doc, xpath, accountIds);

            LOGGER.info("Writing account info to file=" + outFile);
            writeAccountInfo(bankIds, accountIds, writer);
        } catch (SAXException e) {
            throw new IOException(e);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }

    private void findACCTIDV2(Document doc, XPath xpath, List<String> accountIds) throws XPathExpressionException {
        Object result;
        NodeList nodes;
        XPathExpression accountIdExpression = xpath.compile("//ACCTID/text()");
        result = accountIdExpression.evaluate(doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            String accountId = nodes.item(i).getNodeValue();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ACCTID=" + accountId);
            }
            accountIds.add(accountId);
        }
    }

    private void findBANKIDV2(Document doc, XPath xpath, List<String> bankIds) throws XPathExpressionException {
        Object result;
        NodeList nodes;
        XPathExpression bankIdExpression = xpath.compile("//BANKID/text()");
        result = bankIdExpression.evaluate(doc, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
            String bankId = nodes.item(i).getNodeValue();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("BANKID=" + bankId);
            }
            bankIds.add(bankId);
        }
    }

    private void parseAccountInquiryResponseV1(String version, File respFile, List<String> bankIds,
            List<String> accountIds) throws FileNotFoundException, IOException {
        bankIds.clear();
        accountIds.clear();

        File outFile = new File(respFile.getAbsoluteFile().getParentFile(), "accountInquiry-" + version + "-info.txt");
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(respFile));
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(line);
                }

                findBANKIDV1(line, bankIds);

                findACCTIDV1(line, accountIds);
            }

            LOGGER.info("Writing account info to file=" + outFile);
            writeAccountInfo(bankIds, accountIds, writer);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    reader = null;
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }

    private void findACCTIDV1(String line, List<String> accountIds) {
        Matcher matcher;
        matcher = accountIdPattern.matcher(line);
        while (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("MATCHED=" + matcher.group());
            }
            if (matcher.groupCount() != 1) {
                LOGGER.warn("Matcher found more than one group");
            } else {
                String accountId = matcher.group(1);
                LOGGER.info("ACCTID=" + accountId);
                accountIds.add(accountId);
            }
        }
    }

    private void findBANKIDV1(String line, List<String> bankIds) {
        Matcher matcher;
        matcher = bankIdPattern.matcher(line);
        while (matcher.find()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("MATCHED=" + matcher.group());
            }

            if (matcher.groupCount() != 1) {
                LOGGER.warn("Matcher found more than one group");
            } else {
                String bankId = matcher.group(1);
                LOGGER.info("BANKID=" + bankId);
                bankIds.add(bankId);
            }
        }
    }

    /**
     * Write account info.
     *
     * @param bankIds
     *            the bank ids
     * @param accountIds
     *            the account ids
     * @param writer
     *            the writer
     */
    private void writeAccountInfo(List<String> bankIds, List<String> accountIds, PrintWriter writer) {
        int bankIdsCount = bankIds.size();
        int accountIdsCount = accountIds.size();
        String lastBankId = null;
        String lastAccountId = null;
        int max = Math.max(bankIdsCount, accountIdsCount);
        writer.println("");
        writer.println("# Number of accounts");
        writer.println("accounts=" + max);

        for (int i = 0; i < max; i++) {
            if (bankIdsCount > 0) {
                if (i < bankIdsCount) {
                    lastBankId = bankIds.get(i);
                }
            }

            if (accountIdsCount > 0) {
                if (i < accountIdsCount) {
                    lastAccountId = accountIds.get(i);
                }
            }

            if (lastBankId != null) {
                // account.1.id=371515742121005
                writer.println("account." + (i + 1) + ".bankId=" + lastBankId);
                // account.1.type=SAVINGS
                writer.println("account." + (i + 1) + ".type=" + "CHECKING");
            }
            if (lastAccountId != null) {
                // account.1.id=371515742121005
                writer.println("account." + (i + 1) + ".id=" + lastAccountId);
            }

            writer.flush();
        }
    }

    /**
     * Notify version is not supported.
     *
     * @param version
     *            the version
     * @param responseFile
     *            the respponse file
     * @param e
     *            the e
     */
    protected void notifyVersionIsNotSupported(String version, File responseFile, Exception e) {
        String exceptionMessage = null;
        if (e != null) {
            exceptionMessage = e.getMessage();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.warn("Not OK. version=" + version + " is NOT support. exception=" + exceptionMessage);
        }
        if (responseFile == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("responseFile is null.");
            }
            return;
        }
        if (!responseFile.exists()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("responseFile=" + responseFile + " does not exist.");
            }
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            printFileContent(responseFile);
        }
    }

    private void printFileContent(File responseFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(responseFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e1) {
            LOGGER.warn(e1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    LOGGER.warn(e1);
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Notify version is supported.
     *
     * @param version
     *            the version
     * @param responseFile
     *            the response file
     * @param updater
     *            the updater
     */
    protected void notifyVersionIsSupported(String version, File responseFile, AbstractFiDir updater) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("OK. version=" + version + " is supported.");
        }
        try {
            parseAccountInquiryResponse(version, updater.getRespFile());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Check.
     *
     * @param dir
     *            the dir
     */
    public void check(File dir) {
        Map<String, Boolean> resuts = new TreeMap<String, Boolean>();
        String[] versions = { "v2", "v1", };
        for (String version : versions) {
            boolean isSupported = checkVersion(dir, version);
            resuts.put(version, isSupported);
        }
        for (String version : resuts.keySet()) {
            Boolean isSupported = resuts.get(version);
            LOGGER.info(version + ", " + isSupported);
        }
    }

    /**
     * Check version.
     *
     * @param dir
     *            the dir
     * @param version
     *            the version
     * @return
     */
    private boolean checkVersion(File dir, String version) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> START checking if FI supports version=" + version);
        }
        boolean isSupported = false;
        Exception exception = null;
        AbstractFiDir updater = null;
        try {
            updater = new MyFiDir(dir, version);
            String type = "accountInquiry-" + version;
            updater.setTemplate(type + ".vm");
            updater.setRequestFileName(type + "-req.ofx");
            updater.setRespFileName(type + "-resp.ofx");
            if (updater.sendRequest()) {
                isSupported = true;
            } else {
                exception = new IOException("SKIP sending request, fi.url is null.");
                isSupported = false;
            }
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(e, e);
            }
            exception = e;
        } finally {
            if (isSupported) {
                notifyVersionIsSupported(version, updater.getRespFile(), updater);
            } else {
                notifyVersionIsNotSupported(version, updater.getRespFile(), exception);
            }
            if (updater != null) {
                updater = null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("< DONE checking dir=" + dir);
            }
        }

        return isSupported;
    }

    /**
     * Check.
     *
     * @param args
     *            the args
     */
    public void check(String[] args) {

        for (String arg : args) {
            File dir = new File(arg);
            LOGGER.info("> START checking dir=" + dir);
            check(dir);
        }
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Class<CheckOfxVersion> clz = CheckOfxVersion.class;
            System.err.println("Usage: java " + clz.getName() + " fiDir1 ...");
            System.exit(1);
        }

        // VelocityUtils.initVelocity();

        CheckOfxVersion checker = new CheckOfxVersion();
        checker.check(args);
    }
}
