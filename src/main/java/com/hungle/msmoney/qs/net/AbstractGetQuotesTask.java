package com.hungle.msmoney.qs.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.StopWatch;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.StockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractGetQuotesTask.
 *
 * @param <V> the value type
 */
public abstract class AbstractGetQuotesTask<V> implements Callable<V> {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(AbstractGetQuotesTask.class);

    /** The http quote getter. */
    private final HttpQuoteGetter httpQuoteGetter;
    
    /** The stocks. */
    private final List<String> stocks;
    
    /** The listener. */
    private GetQuotesListener listener;
    
    /** The skip if no price. */
    private boolean skipIfNoPrice = true;

    /**
     * Instantiates a new abstract get quotes task.
     *
     * @param quoteGetter the quote getter
     * @param stocks the stocks
     * @param skipIfNoPrice the skip if no price
     */
    public AbstractGetQuotesTask(HttpQuoteGetter quoteGetter, List<String> stocks, boolean skipIfNoPrice) {
        super();
        this.httpQuoteGetter = quoteGetter;
        this.stocks = stocks;
        this.skipIfNoPrice = skipIfNoPrice;
    }

    /**
     * Call to get http entity.
     *
     * @param listener the listener
     * @return the http entity
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClientProtocolException the client protocol exception
     */
    private HttpEntity getHttpEntity(GetQuotesListener listener) throws URISyntaxException, IOException, ClientProtocolException {
        HttpEntity entity = null;
        StopWatch stopWatch = new StopWatch();
        if (listener != null) {
            listener.started(stocks);
        }
        if (httpQuoteGetter != null) {
            try {
                AbstractStockPrice stockPriceBean = new StockPrice();
                String format = stockPriceBean.getFormat();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("format=" + format);
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
            LOGGER.warn("httpQuoteGetter is null");
        }
        return entity;
    }

    /**
     * Call to get stock price beans.
     *
     * @return the list
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClientProtocolException the client protocol exception
     */
    protected List<AbstractStockPrice> getStockPriceBeans() throws URISyntaxException, IOException, ClientProtocolException {
        List<AbstractStockPrice> beans = null;
        StopWatch stopWatch = new StopWatch();
        if (listener != null) {
            listener.started(stocks);
        }
        try {
            HttpEntity httpEntity = getHttpEntity(null);
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

    /**
     * Entity to stock price bean.
     *
     * @param entity the entity
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<AbstractStockPrice> entityToStockPriceBean(HttpEntity entity) throws IOException {
        if (httpQuoteGetter != null) {
            return httpQuoteGetter.httpEntityToStockPriceBean(entity, skipIfNoPrice);
        } else {
            return null;
        }
    }

    /**
     * Gets the stocks.
     *
     * @return the stocks
     */
    public List<String> getStocks() {
        return stocks;
    }

    /**
     * Gets the listener.
     *
     * @return the listener
     */
    public GetQuotesListener getListener() {
        return listener;
    }

    /**
     * Sets the listener.
     *
     * @param listener the new listener
     */
    public void setListener(GetQuotesListener listener) {
        this.listener = listener;
    }

    /**
     * Checks if is skip if no price.
     *
     * @return true, if is skip if no price
     */
    public boolean isSkipIfNoPrice() {
        return skipIfNoPrice;
    }

    /**
     * Sets the skip if no price.
     *
     * @param skipNoPrice the new skip if no price
     */
    public void setSkipIfNoPrice(boolean skipNoPrice) {
        this.skipIfNoPrice = skipNoPrice;
    }

}