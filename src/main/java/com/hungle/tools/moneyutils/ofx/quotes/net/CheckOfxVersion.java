package com.hungle.tools.moneyutils.ofx.quotes.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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

import com.hungle.tools.moneyutils.fi.UpdateFiDir;
import com.hungle.tools.moneyutils.fi.VelocityUtils;
import com.hungle.tools.moneyutils.fi.props.OFX;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckOfxVersion.
 */
public class CheckOfxVersion {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CheckOfxVersion.class);
    
    /** The account id pattern. */
    private Pattern accountIdPattern = Pattern.compile("\\<ACCTID\\>" + "([a-zA-Z0-9]+)");
    
    /** The bank id pattern. */
    private Pattern bankIdPattern = Pattern.compile("\\<BANKID\\>" + "([a-zA-Z0-9]+)");

    /**
     * Check.
     *
     * @param args the args
     */
    public void check(String[] args) {

        for (String arg : args) {
            File dir = new File(arg);
            log.info("> START checking dir=" + dir);
            check(dir);
        }
    }

    /**
     * Parses the account inquiry response.
     *
     * @param version the version
     * @param respFile the resp file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void parseAccountInquiryResponse(String version, File respFile) throws IOException {
        List<String> bankIds = new ArrayList<String>();
        List<String> accountIds = new ArrayList<String>();

        if (version.equals("v1")) {
            bankIds.clear();
            accountIds.clear();

            File outFile = new File(respFile.getAbsoluteFile().getParentFile(), "accountInquiry-" + version + "-info.txt");
            PrintWriter writer = null;
            BufferedReader reader = null;

            Matcher matcher = null;
            try {
                reader = new BufferedReader(new FileReader(respFile));
                writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (log.isDebugEnabled()) {
                        log.debug(line);
                    }
                    matcher = bankIdPattern.matcher(line);
                    while (matcher.find()) {
                        if (log.isDebugEnabled()) {
                            log.debug("MATCHED=" + matcher.group());
                        }

                        if (matcher.groupCount() != 1) {
                            log.warn("Matcher found more than one group");
                        } else {
                            String bankId = matcher.group(1);
                            log.info("BANKID=" + bankId);
                            bankIds.add(bankId);
                        }
                    }

                    matcher = accountIdPattern.matcher(line);
                    while (matcher.find()) {
                        log.info("MATCHED=" + matcher.group());
                        if (matcher.groupCount() != 1) {
                            log.warn("Matcher found more than one group");
                        } else {
                            String accountId = matcher.group(1);
                            log.info("ACCTID=" + accountId);
                            accountIds.add(accountId);
                        }
                    }
                }
                log.info("Writing account info to file=" + outFile);
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
        } else if (version.equals("v2")) {
            PrintWriter writer = null;
            try {
                bankIds.clear();
                accountIds.clear();

                File outFile = new File(respFile.getAbsoluteFile().getParentFile(), "accountInquiry-" + version + "-info.txt");

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(respFile);
                doc.getDocumentElement().normalize();

                writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));

                Object result = null;
                NodeList nodes = null;
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();

                XPathExpression bankIdExpression = xpath.compile("//BANKID/text()");
                result = bankIdExpression.evaluate(doc, XPathConstants.NODESET);
                nodes = (NodeList) result;
                for (int i = 0; i < nodes.getLength(); i++) {
                    String bankId = nodes.item(i).getNodeValue();
                    log.info("BANKID=" + bankId);
                    bankIds.add(bankId);
                }

                XPathExpression accountIdExpression = xpath.compile("//ACCTID/text()");
                result = accountIdExpression.evaluate(doc, XPathConstants.NODESET);
                nodes = (NodeList) result;
                for (int i = 0; i < nodes.getLength(); i++) {
                    String accountId = nodes.item(i).getNodeValue();
                    log.info("ACCTID=" + accountId);
                    accountIds.add(accountId);
                }

                log.info("Writing account info to file=" + outFile);
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
    }

    /**
     * Write account info.
     *
     * @param bankIds the bank ids
     * @param accountIds the account ids
     * @param writer the writer
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
                writer.println("bankId." + (i + 1) + ".id=" + lastBankId);
            }
            if (lastAccountId != null) {
                // account.1.id=371515742121005
                writer.println("account." + (i + 1) + ".id=" + lastAccountId);
            }

        }
    }

    /**
     * Check.
     *
     * @param dir the dir
     */
    public void check(File dir) {
        String[] versions = { "v2", "v1", };
        for (String version : versions) {
            log.info("");
            log.info("Checking if FI supports version=" + version);
            final String v = version;
            UpdateFiDir updater = new UpdateFiDir(dir) {

                @Override
                protected void checkRespFile(File respFile, OFX ofx) throws IOException {
                    if (v.compareToIgnoreCase("v1") == 0) {
                        ofx.setVersion("1");
                    } else if (v.compareToIgnoreCase("v2") == 0) {
                        ofx.setVersion("2");
                    } else {
                        ofx.setVersion("1");
                    }
                    super.checkRespFile(respFile, ofx);
                }
            };
            boolean isSupported = false;
            try {
                String type = "accountInquiry-" + version;
                updater.setTemplate(type + ".vm");
                updater.setRequestFileName(type + "-req.ofx");
                updater.setRespFileName(type + "-resp.ofx");
                updater.update();
                isSupported = true;
                notifyVersionIsSupported(version, updater.getRespFile(), updater);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.error(e, e);
                } else {
                    log.warn(e);
                }
                if (isSupported) {
                    notifyVersionIsNotSupported(version, updater.getRespFile(), e);
                }
            } finally {
                log.info("");
                log.info("< DONE checking dir=" + dir);
            }
        }
    }

    /**
     * Notify version is not supported.
     *
     * @param version the version
     * @param respponseFile the respponse file
     * @param e the e
     */
    protected void notifyVersionIsNotSupported(String version, File respponseFile, Exception e) {
        log.error("Not OK. version=" + version + " is NOT support. " + e.getMessage());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(respponseFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e1) {
            log.warn(e1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    log.warn(e1);
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Notify version is supported.
     *
     * @param version the version
     * @param responseFile the response file
     * @param updater the updater
     */
    protected void notifyVersionIsSupported(String version, File responseFile, UpdateFiDir updater) {
        log.info("OK. version=" + version + " is supported.");
        try {
            parseAccountInquiryResponse(version, updater.getRespFile());
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Class clz = CheckOfxVersion.class;
            System.err.println("Usage: java " + clz.getName() + " fiDir1 ...");
            System.exit(1);
        }

        VelocityUtils.initVelocity();

        CheckOfxVersion checker = new CheckOfxVersion();
        checker.check(args);
    }
}
