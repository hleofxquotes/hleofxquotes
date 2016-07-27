package com.le.tools.moneyutils.ofx.quotes.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public interface HttpQuoteGetter {

    HttpResponse httpGet(List<String> stocks, String format) throws URISyntaxException, IOException, ClientProtocolException;

    List<AbstractStockPrice> httpEntityToStockPriceBean(HttpEntity entity, boolean skipIfNoPrice) throws IOException;

}
