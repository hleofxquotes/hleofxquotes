package com.hungle.tools.moneyutils.fi;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.hungle.tools.moneyutils.fi.props.HttpProperties;

public class OfxPostClientTest {
    @Test
    public void testInit() throws IOException {
        OfxPostClient client = new OfxPostClient();
        Assert.assertNotNull(client);
        
        String url = null;
        File reqFile = File.createTempFile("test", ".req");
        reqFile.deleteOnExit();
        File respFile = File.createTempFile("test", ".reso");
        respFile.deleteOnExit();
        HttpProperties httpProperties = new HttpProperties();
        OfxPostClientParams params = new OfxPostClientParams(url, reqFile, respFile, httpProperties);
        Assert.assertNotNull(params);
        
        client.sendRequest(params);
    }
}
