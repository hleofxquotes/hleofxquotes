package com.le.tools.moneyutils.ofx.quotes;

import java.util.List;

import org.apache.http.HttpEntity;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public interface GetQuotesListener {

    void started(List<String> stocks);

    void httpEntityReceived(HttpEntity entity);

    void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta);

    void setSubTaskSize(int size);

}
