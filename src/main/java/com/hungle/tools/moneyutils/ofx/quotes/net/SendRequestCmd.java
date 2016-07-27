package com.hungle.tools.moneyutils.ofx.quotes.net;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.OfxPostClient;
import com.hungle.tools.moneyutils.fi.OfxPostClientParams;

public class SendRequestCmd {
    private static final Logger log = Logger.getLogger(SendRequestCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        String uri = "https://vesnc.vanguard.com/us/OfxDirectConnectServlet";
        File reqFile = new File("fi/vanguard2/req.ofx");
        File respFile = new File("fi/vanguard2/resp.ofx");
        try {
            log.info("uri=" + uri);
            log.info("reqFile=" + reqFile);
            log.info("respFile=" + respFile);
            // TODO
            OfxPostClient.sendRequest(new OfxPostClientParams(uri, reqFile, respFile, null));
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
