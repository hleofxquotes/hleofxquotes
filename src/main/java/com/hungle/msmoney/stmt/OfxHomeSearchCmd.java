package com.hungle.msmoney.stmt;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxHomeSearchCmd.
 */
public class OfxHomeSearchCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(OfxHomeSearchCmd.class);

    /** The Constant API_SEARCH_URL. */
    private static final String API_SEARCH_URL = "http://www.ofxhome.com/api.php?search=";

    /**
     * The Class FI.
     */
    private class FI {
        
        /**
         * Instantiates a new fi.
         *
         * @param name the name
         * @param id the id
         */
        public FI(String name, String id) {
            super();
            this.name = name;
            this.id = id;
        }

        /** The id. */
        private String id;
        
        /** The name. */
        private String name;

        /**
         * Gets the id.
         *
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the id.
         *
         * @param id the new id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name.
         *
         * @param name the new name
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Search.
     *
     * @param searchName the search name
     * @return the list
     * @throws ClientProtocolException the client protocol exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<FI> search(String searchName) throws ClientProtocolException, IOException {
        // http://www.ofxhome.com/api.txt
        HttpClient httpClient = HttpClientBuilder.create().build();
        String uri = API_SEARCH_URL + URLEncoder.encode(searchName, "UTF-8");
        HttpGet httpGet = new HttpGet(uri);

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        if (log.isDebugEnabled()) {
            log.debug("response Content-Type: " + responseEntity.getContentType());
        }

        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error(e, e);
        }

        List<FI> fis = new ArrayList<FI>();
        Document responseDocument = null;
        try {
            responseDocument = builder.parse(responseEntity.getContent());
            NodeList n = responseDocument.getElementsByTagName("institutionid");
            Integer i;
            for (i = 0; i < n.getLength(); i++) {
                Element e = (Element) n.item(i);

                String name = e.getAttribute("name");
                log.info("name=" + name);
                String id = e.getAttribute("id");
                log.info("id=" + id);

                FI fi = new FI(name, id);
                fis.add(fi);
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return fis;

    }

    /**
     * Gets the fi details.
     *
     * @param fi the fi
     * @return the fi details
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void getFiDetails(FI fi) throws IOException {
        // http://www.ofxhome.com/api.php?lookup=1234
        HttpClient httpClient = HttpClientBuilder.create().build();
        String uri = "http://www.ofxhome.com/api.php" + "?" + "lookup=" + URLEncoder.encode(fi.getId(), "UTF-8");
        HttpGet httpGet = new HttpGet(uri);

        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity responseEntity = httpResponse.getEntity();
        if (log.isDebugEnabled()) {
            log.debug("response Content-Type: " + responseEntity.getContentType());
        }

        DocumentBuilder builder = null;
        Document response = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            response = builder.parse(responseEntity.getContent());

            // Get name
            NodeList n = response.getElementsByTagName("name");
            if (n.getLength() > 0) {
                Text a = (Text) n.item(0).getFirstChild();
                if (a != null) {
                    log.info("name=" + a.getNodeValue());
                }
            }

            // Get fid
            n = response.getElementsByTagName("fid");
            if (n.getLength() > 0) {
                Text a = (Text) n.item(0).getFirstChild();
                if (a != null) {
                    log.info("fid=" + a.getNodeValue());
                }
            }

            // Get org
            n = response.getElementsByTagName("org");
            if (n.getLength() > 0) {
                Text a = (Text) n.item(0).getFirstChild();
                if (a != null) {
                    log.info("org=" + a.getNodeValue());
                }
            }

            // Get url
            n = response.getElementsByTagName("url");
            if (n.getLength() > 0) {
                Text a = (Text) n.item(0).getFirstChild();
                if (a != null) {
                    log.info("url=" + a.getNodeValue());
                }
            }

            // Get brokerId
            n = response.getElementsByTagName("brokerid");
            if (n.getLength() > 0) {
                Text a = (Text) n.item(0).getFirstChild();
                if (a != null) {
                    log.info("brokerid=" + a.getNodeValue());
                }
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (IllegalStateException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Class<OfxHomeSearchCmd> clz = OfxHomeSearchCmd.class;
            System.out.println("Usage: java " + clz.getName() + " searchString ...");
            System.exit(1);
        }
        for (String arg : args) {
            OfxHomeSearchCmd searcher = new OfxHomeSearchCmd();
            String searchName = arg;
            try {
                log.info("Searching ofx info for " + searchName + " ...");
                List<FI> fis = searcher.search(searchName);
                for (FI fi : fis) {
                    log.info("");
                    log.info("### " + fi.getName() + " , " + fi.getId());
                    searcher.getFiDetails(fi);
                }
            } catch (IOException e) {
                log.error(e, e);
            }
        }
    }

}
