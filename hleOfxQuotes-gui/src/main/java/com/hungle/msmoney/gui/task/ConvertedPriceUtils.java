package com.hungle.msmoney.gui.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTableUtils;
import com.hungle.msmoney.core.mapper.SymbolMapper;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

public class ConvertedPriceUtils {
    private static final Logger LOGGER = Logger.getLogger(ConvertedPriceUtils.class);

    static final List<AbstractStockPrice> toConvertedPrices(List<AbstractStockPrice> prices,
            ConvertedPriceContext convertedPriceContext) {
        List<AbstractStockPrice> convertedPrices = new ArrayList<AbstractStockPrice>();
        for (AbstractStockPrice price : prices) {
            AbstractStockPrice convertedPrice;
            try {
                convertedPrice = toConvertedPrice(price, convertedPriceContext);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("convertedPrice=" + convertedPrice);
                }

                convertedPrices.add(convertedPrice);
            } catch (CloneNotSupportedException e) {
                LOGGER.error(e, e);
            }
        }
        return convertedPrices;
    }

    private static final AbstractStockPrice toConvertedPrice(AbstractStockPrice price,
            ConvertedPriceContext convertedPriceContext) throws CloneNotSupportedException {
        AbstractStockPrice convertedPrice = price.clonePrice();

        String symbol = SymbolMapper.getStockSymbol(price.getStockSymbol(), convertedPriceContext.getSymbolMapper());
        if (symbol == null) {
            symbol = price.getStockName();
        }
        if (symbol == null) {
            symbol = "";
        }
        convertedPrice.setStockSymbol(symbol);

        Price lastPrice = price.getLastPrice();
        if (price.getFxSymbol() == null) {
            lastPrice = FxTableUtils.getPrice(price.getStockSymbol(), price.getLastPrice(),
                    convertedPriceContext.getDefaultCurrency(), convertedPriceContext.getSymbolMapper(),
                    convertedPriceContext.getFxTable());
        }
        convertedPrice.setLastPrice(lastPrice);
        convertedPrice.setCurrency(lastPrice.getCurrency());

        return convertedPrice;
    }

}
