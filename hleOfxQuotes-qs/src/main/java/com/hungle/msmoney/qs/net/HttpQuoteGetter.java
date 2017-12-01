package com.hungle.msmoney.qs.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Interface HttpQuoteGetter.
 */
public interface HttpQuoteGetter {

    /**
     * Http get.
     *
     * @param stocks the stocks
     * @param format the format
     * @return the http response
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClientProtocolException the client protocol exception
     */
    HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException;

    /**
     * Http entity to stock price bean.
     *
     * @param entity the entity
     * @param skipIfNoPrice the skip if no price
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException;

}
