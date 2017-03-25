package com.hungle.tools.moneyutils.ofx.quotes.net;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.OfxPostClient;
import com.hungle.tools.moneyutils.fi.OfxPostClientParams;

// TODO: Auto-generated Javadoc
/**
 * The Class SendRequestCmd.
 */
public class SendRequestCmd {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(SendRequestCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String uri = "https://vesnc.vanguard.com/us/OfxDirectConnectServlet";
        File reqFile = new File("fi/vanguard2/req.ofx");
        File respFile = new File("fi/vanguard2/resp.ofx");
        try {
            LOGGER.info("uri=" + uri);
            LOGGER.info("reqFile=" + reqFile);
            LOGGER.info("respFile=" + respFile);
            // TODO
            OfxPostClient.sendRequest(new OfxPostClientParams(uri, reqFile, respFile, null));
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

}
