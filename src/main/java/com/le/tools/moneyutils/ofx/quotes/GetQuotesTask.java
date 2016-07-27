/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.le.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class GetQuotesTask extends AbstractGetQuotesTask<List<AbstractStockPrice>> {
    public GetQuotesTask(HttpQuoteGetter quoteGetter, List<String> stocks, boolean skipIfNoPrice) {
        super(quoteGetter, stocks, skipIfNoPrice);
    }

    @Override
    public List<AbstractStockPrice> call() throws Exception {
        return callToGetStockPriceBeans();
    }

}