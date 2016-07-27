package com.le.tools.moneyutils.ofx.quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.CsvUtils;

public class HttpUtils {

    public static List<AbstractStockPrice> toStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = null;

        if (entity == null) {
            return beans;
        }

        Reader reader = null;
        try {
            reader = toReader(entity);
            beans = CsvUtils.toStockPrices(reader, skipIfNoPrice);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } finally {
                    reader = null;
                }
            }
        }
        return beans;
    }

    public static Reader toReader(HttpEntity entity) throws IOException {
        InputStream in = entity.getContent();
        Charset charset = HttpUtils.getCharset(entity);
        Reader reader = new BufferedReader(new InputStreamReader(in, charset));
        return reader;
    }

    public static Charset getCharset(HttpEntity entity) {
        String charsetName = EntityUtils.getContentCharSet(entity);
        if (charsetName == null) {
            charsetName = org.apache.http.protocol.HTTP.DEFAULT_CONTENT_CHARSET;
        }
        Charset charset = Charset.forName(charsetName);
        return charset;
    }

}
