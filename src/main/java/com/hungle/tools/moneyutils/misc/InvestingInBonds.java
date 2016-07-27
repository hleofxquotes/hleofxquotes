package com.le.tools.moneyutils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class InvestingInBonds {
    private static final Logger log = Logger.getLogger(InvestingInBonds.class);

    private void getPrice(String cusip) throws IOException {
        URL url = null;

        String urlString = null;

        urlString = "http://www.investinginbonds.com/corporatebonds/(fankgo455babvs45gazt3k55)/cusip.aspx?action=all&cusip=" + "761713AT3";
        InputStream stream = null;
        try {
            url = new URL(urlString);
            stream = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            Document document = createDocument(stream);
            printDocument(document, System.out);
        } catch (MalformedURLException e) {
            throw new IOException(e);
        } catch (TransformerException e) {
            throw new IOException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    stream = null;
                }
            }
        }

    }

    private Document createDocument(InputStream stream) throws IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = domFactory.newDocumentBuilder();
            document = builder.parse(stream);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
        return document;
    }

    private static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // http://www.investinginbonds.com/CorporateBonds/(lhihtg4525aiueu4cdx3fvml)/Disclaimer.aspx?ReturnUrl=%2fcorporatebonds%2fcusip.aspx%3faction%3dall%26cusip%3d25746RAE6&action=all&cusip=25746RAE6&CheckBox1=on&__VIEWSTATE=dDwzMTA3NDEwMjM7O2w8Q2hlY2tCb3gxOz4%2BzrrFMWXev264rmMzyiwn6sz0nYM%3D&btnContinue=Continue
        /*
         * Host: www.investinginbonds.com User-Agent: Mozilla/5.0 (Windows; U;
         * Windows NT 6.0; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (
         * .NET CLR 3.5.30729; .NET4.0E) Accept-Language: en-us,en;q=0.5
         * Accept-Encoding: gzip,deflate Accept-Charset:
         * ISO-8859-1,utf-8;q=0.7,*;q=0.7 Keep-Alive: 115 Connection: keep-alive
         * Referer:
         * http://www.investinginbonds.com/corporatebonds/(lhihtg4525aiueu4cdx3fvml
         * )/cusip.aspx?action=all&cusip=25746RAE6 Cookie: Trace=
         * C31CB261FD52E2C6F052F784133F6273CB1FE63BA7580A88989E39BBA22F5D9C74748456B75E5F3429960E48710A1E27AAE184E945C3A79D5E5E37A87663D7EF6C72D1620BE668EBB23499964A0D399A03ABD8CC647E740D711517FE6DE8010F8F9E5B935CB33E04
         * ; WT_FPC=id=70.231.237.53-1174528768.30131967:lv=1297134736265:ss=
         * 1297134736265; ASPSESSIONIDCSCSQDCC=GGJDNGCAKOOMFNHKGNEALDPE;
         * style=default; ASPSESSIONIDAQDTQDCD=NKIDNGCAPELFIEFFIFPFOGGO;
         * ASPSESSIONIDCQCSQDDD=GLIDNGCAODJMKHPHLODOLKOB;
         * ASP.NET_SessionId=r5w2ykjtfmceqd55ulvx4vzg;
         * ASPSESSIONIDCSBSRDCC=GLIDNGCAGKIAPHDILPDJGHKF X-lori-time-1:
         * 1297133672053
         */
        /*
         * http://www.investinginbonds.com/CorporateBonds/(lhihtg4525aiueu4cdx3fvml
         * )
         * /Disclaimer.aspx?ReturnUrl=%2fcorporatebonds%2fcusip.aspx%3faction%3d
         * all%26cusip%3d25746RAE6&action=all&cusip=25746RAE6
         */
        InvestingInBonds i = new InvestingInBonds();
        String cusip = null;
        cusip = "761713AT3 ";
        try {
            i.getPrice(cusip);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

}
