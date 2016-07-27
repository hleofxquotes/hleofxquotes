package com.le.tools.moneyutils.ofx.quotes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.StockPrice;

public abstract class AbstractGetQuotesTask<V> implements Callable<V> {
    private static final Logger log = Logger.getLogger(AbstractGetQuotesTask.class);

    private final HttpQuoteGetter httpQuoteGetter;
    private List<String> stocks;
    private GetQuotesListener listener;
    private boolean skipIfNoPrice = true;

    public AbstractGetQuotesTask(HttpQuoteGetter quoteGetter, List<String> stocks, boolean skipIfNoPrice) {
        super();
        this.httpQuoteGetter = quoteGetter;
        this.stocks = stocks;
        this.skipIfNoPrice = skipIfNoPrice;
    }

    private HttpEntity callToGetHttpEntity(GetQuotesListener listener) throws URISyntaxException, IOException, ClientProtocolException {
        HttpEntity entity = null;
        StopWatch stopWatch = new StopWatch();
        if (listener != null) {
            listener.started(stocks);
        }
        if (httpQuoteGetter != null) {
            try {
                StockPrice stockPriceBean = new StockPrice();
                String format = stockPriceBean.getFormat();
                if (log.isDebugEnabled()) {
                    log.debug("format=" + format);
                }
                HttpResponse response = this.httpQuoteGetter.httpGet(stocks, format);
                entity = response.getEntity();
                if (listener != null) {
                    listener.httpEntityReceived(entity);
                }
            } finally {
                long delta = stopWatch.click();
                if (listener != null) {
                    listener.ended(stocks, null, delta);
                }
            }
        } else {
            log.warn("httpQuoteGetter is null");
        }
        return entity;
    }

    protected List<AbstractStockPrice> callToGetStockPriceBeans() throws URISyntaxException, IOException, ClientProtocolException {
        List<AbstractStockPrice> beans = null;
        StopWatch stopWatch = new StopWatch();
        if (listener != null) {
            listener.started(stocks);
        }
        try {
            HttpEntity httpEntity = callToGetHttpEntity(null);
            if (listener != null) {
                listener.httpEntityReceived(httpEntity);
            }
            if (httpEntity != null) {
                beans = entityToStockPriceBean(httpEntity);
            }
        } finally {
            long delta = stopWatch.click();
            if (listener != null) {
                listener.ended(stocks, beans, delta);
            }
        }
        return beans;
    }

    protected List<AbstractStockPrice> entityToStockPriceBean(HttpEntity entity) throws IOException {
        if (httpQuoteGetter != null) {
            return httpQuoteGetter.httpEntityToStockPriceBean(entity, skipIfNoPrice);
        } else {
            return null;
        }
    }

    public List<String> getStocks() {
        return stocks;
    }

    public void setStocks(List<String> stocks) {
        this.stocks = stocks;
    }

    public GetQuotesListener getListener() {
        return listener;
    }

    public void setListener(GetQuotesListener listener) {
        this.listener = listener;
    }

    public boolean isSkipIfNoPrice() {
        return skipIfNoPrice;
    }

    public void setSkipIfNoPrice(boolean skipNoPrice) {
        this.skipIfNoPrice = skipNoPrice;
    }

}