package com.hungle.tools.moneyutils.ofx.quotes;

import java.util.List;

import org.apache.http.HttpEntity;

import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving getQuotes events.
 * The class that is interested in processing a getQuotes
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGetQuotesListener<code> method. When
 * the getQuotes event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GetQuotesEvent
 */
public interface GetQuotesListener {

    /**
     * Started.
     *
     * @param stocks the stocks
     */
    void started(List<String> stocks);

    /**
     * Http entity received.
     *
     * @param entity the entity
     */
    void httpEntityReceived(HttpEntity entity);

    /**
     * Ended.
     *
     * @param stocks the stocks
     * @param beans the beans
     * @param delta the delta
     */
    void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta);

    /**
     * Sets the sub task size.
     *
     * @param size the new sub task size
     */
    void setSubTaskSize(int size);

}
