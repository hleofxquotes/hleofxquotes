package com.hungle.tools.moneyutils.ofx.quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.StockPriceCsvUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpUtils.
 */
public class HttpUtils {

    /**
     * To stock price bean.
     *
     * @param entity the entity
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<AbstractStockPrice> toStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException {
        List<AbstractStockPrice> beans = null;

        if (entity == null) {
            return beans;
        }

        Reader reader = null;
        try {
            reader = toReader(entity);
            beans = StockPriceCsvUtils.toStockPrices(reader, skipIfNoPrice);
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

    /**
     * To reader.
     *
     * @param entity the entity
     * @return the reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Reader toReader(HttpEntity entity) throws IOException {
        InputStream in = entity.getContent();
        Charset charset = HttpUtils.getCharset(entity);
        Reader reader = new BufferedReader(new InputStreamReader(in, charset));
        return reader;
    }

    /**
     * Gets the charset.
     *
     * @param entity the entity
     * @return the charset
     */
    public static Charset getCharset(HttpEntity entity) {
        String charsetName = EntityUtils.getContentCharSet(entity);
        if (charsetName == null) {
            charsetName = org.apache.http.protocol.HTTP.DEFAULT_CONTENT_CHARSET;
        }
        Charset charset = Charset.forName(charsetName);
        return charset;
    }

}
