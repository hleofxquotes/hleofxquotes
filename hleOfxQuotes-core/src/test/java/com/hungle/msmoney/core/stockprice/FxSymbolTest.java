package com.hungle.msmoney.core.stockprice;

import org.junit.Assert;
import org.junit.Test;

public class FxSymbolTest {

    @Test
    public void testInit() {
        FxSymbol fxSymbol = new FxSymbol();
        Assert.assertNotNull(fxSymbol);
    }

    @Test
    public void testParse() {
        FxSymbol fxSymbol = null;

        String symbol = null;

        symbol = null;
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);

        symbol = "";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);

        symbol = "A";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);

        symbol = "EURUSD";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);

        symbol = "EURUSD=";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);

        symbol = "EUR=X";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNull(fxSymbol);
        
        symbol = "EURUSD=X";
        fxSymbol = FxSymbol.parse(symbol);
        Assert.assertNotNull(fxSymbol);
        Assert.assertEquals("EUR", fxSymbol.getFromCurrency());
        Assert.assertEquals("USD", fxSymbol.getToCurrency());
        Assert.assertEquals(new Double(0.0), new Double(fxSymbol.getRate()));
    }
}
