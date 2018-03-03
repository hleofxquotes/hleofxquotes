package com.hungle.msmoney.qs.net;

import java.util.List;

import org.apache.http.HttpEntity;

import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

public class DefaultGetQuotesListener implements GetQuotesListener {

    @Override
    public void started(List<String> stocks) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSubTaskSize(int size) {
        // TODO Auto-generated method stub
    
    }

    @Override
    public void httpEntityReceived(HttpEntity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta) {
        // TODO Auto-generated method stub

    }

}
