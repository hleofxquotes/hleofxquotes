package com.hungle.msmoney.statements.fi;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.statements.fi.OfxClient;
import com.hungle.msmoney.statements.fi.OfxPostClientParams;
import com.hungle.msmoney.statements.fi.props.HttpProperties;

public class OfxClientTest {
    private static final Logger LOGGER = Logger.getLogger(OfxClientTest.class);

    private static final String OFX_URL_VANGUARD = "https://vesnc.vanguard.com/us/OfxDirectConnectServlet";

    private static final String OFX_URL_FIDELITY = "https://ofx.fidelity.com/ftgw/OFX/clients/download";

    private static final String OFX_URL_FIDELITY_NET_BENEFITS = "https://nbofx.fidelity.com/netbenefits/ofx/download";
    private static final String OFX_URL_WELLSFARGO = "https://ofxdc.wellsfargo.com/ofx/process.ofx";

    private static final String OFX_URL_DISCOVERCARD = "https://ofx.discovercard.com/";
    private static final String OFX_URL_AE = "https://online.americanexpress.com/myca/ofxdl/desktop/desktopDownload.do?request_type=nl_ofxdownload";

    @Test
    public void testInit() throws IOException {
        OfxClient client = new OfxClient();
        Assert.assertNotNull(client);

        String url = null;
        File reqFile = File.createTempFile("test", ".req");
        reqFile.deleteOnExit();
        File respFile = File.createTempFile("test", ".resp");
        respFile.deleteOnExit();
        HttpProperties httpProperties = new HttpProperties();
        OfxPostClientParams params = new OfxPostClientParams(url, reqFile, respFile, httpProperties);
        Assert.assertNotNull(params);

        client.sendRequest(params);
    }

    @Test
    public void testConnect() throws IOException {
        int errors = 0;
        
        String urls[] = { OFX_URL_AE, 
                OFX_URL_DISCOVERCARD, 
                OFX_URL_FIDELITY_NET_BENEFITS, 
                OFX_URL_FIDELITY,
                OFX_URL_VANGUARD,
                OFX_URL_WELLSFARGO,
        };
        errors = testConnect(urls);
        Assert.assertTrue(errors == 0);
        
        String badUrls[] = { 
                "https://localhost",
                "https://123qwe.123"
        };
        errors = testConnect(badUrls);
        Assert.assertTrue(errors > 0);        
    }

    private int testConnect(String[] urls) throws IOException {
        OfxClient client = new OfxClient();
        Assert.assertNotNull(client);

        File reqFile = File.createTempFile("test", ".req");
        reqFile.deleteOnExit();
        
        File respFile = File.createTempFile("test", ".resp");
        respFile.deleteOnExit();

        HttpProperties httpProperties = new HttpProperties();

        int errors = 0;
        for (String url : urls) {
            OfxPostClientParams params = new OfxPostClientParams(url, reqFile, respFile, httpProperties);
            Assert.assertNotNull(params);

            LOGGER.info("Checking url=" + url);
            try {
                client.checkUrl(url, params);
                LOGGER.info("  OK, url=" + url);
            } catch (Exception e) {
                errors++;
                LOGGER.error(e);
            }
        }
        return errors;
    }
}
