/**
 * 
 */
package com.hungle.tools.moneyutils.ofx.quotes;

import java.util.List;

import com.hungle.tools.moneyutils.ofx.quotes.net.HttpQuoteGetter;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

// TODO: Auto-generated Javadoc
/**
 * The Class GetQuotesTask.
 */
public class GetQuotesTask extends AbstractGetQuotesTask<List<AbstractStockPrice>> {
    
    /**
     * Instantiates a new gets the quotes task.
     *
     * @param quoteGetter the quote getter
     * @param stocks the stocks
     * @param skipIfNoPrice the skip if no price
     */
    public GetQuotesTask(HttpQuoteGetter quoteGetter, List<String> stocks, boolean skipIfNoPrice) {
        super(quoteGetter, stocks, skipIfNoPrice);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public List<AbstractStockPrice> call() throws Exception {
        return getStockPriceBeans();
    }

}