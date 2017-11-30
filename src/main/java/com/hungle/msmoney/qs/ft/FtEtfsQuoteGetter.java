package com.hungle.msmoney.qs.ft;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class FtEtfsQuoteGetter extends FtEquitiesQuoteGetter {
    private static final Logger LOGGER = Logger.getLogger(FtEtfsQuoteGetter.class);

    public FtEtfsQuoteGetter() {
        super();
        setBucketSize(1);
    }

    @Override
    protected String getUrlString(String symbol) throws IOException {
        String urlString = null;
        try {
            URL url = FtPriceModel.getFtEtfURL(symbol);
            urlString = url.toString();
        } catch (MalformedURLException e) {
            throw new IOException(e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
        return urlString;
    }

}