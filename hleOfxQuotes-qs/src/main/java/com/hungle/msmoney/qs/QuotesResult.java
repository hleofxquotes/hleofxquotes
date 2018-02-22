package com.hungle.msmoney.qs;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class QuotesResult {
    private EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
    private EventList<AbstractStockPrice> convertedPriceList = new BasicEventList<AbstractStockPrice>();
    private EventList<AbstractStockPrice> notFoundPriceList = new BasicEventList<AbstractStockPrice>();

    public QuotesResult(EventList<AbstractStockPrice> priceList, EventList<AbstractStockPrice> convertedPriceList,
            EventList<AbstractStockPrice> notFoundPriceList) {
        super();
        this.priceList = priceList;
        this.convertedPriceList = convertedPriceList;
        this.notFoundPriceList = notFoundPriceList;
    }

    public EventList<AbstractStockPrice> getPriceList() {
        return priceList;
    }

    public void setPriceList(EventList<AbstractStockPrice> priceList) {
        this.priceList = priceList;
    }

    public EventList<AbstractStockPrice> getConvertedPriceList() {
        return convertedPriceList;
    }

    public void setConvertedPriceList(EventList<AbstractStockPrice> convertedPriceList) {
        this.convertedPriceList = convertedPriceList;
    }

    public EventList<AbstractStockPrice> getNotFoundPriceList() {
        return notFoundPriceList;
    }

    public void setNotFoundPriceList(EventList<AbstractStockPrice> notFoundPriceList) {
        this.notFoundPriceList = notFoundPriceList;
    }

    public static EventList<AbstractStockPrice> copyList(EventList<AbstractStockPrice> sourceEventList) {
        EventList<AbstractStockPrice> destEventList = new BasicEventList<AbstractStockPrice>();
        destEventList.addAll(sourceEventList);
        return destEventList;
    }
}
