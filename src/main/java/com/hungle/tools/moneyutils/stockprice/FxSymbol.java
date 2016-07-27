package com.le.tools.moneyutils.stockprice;

import com.le.tools.moneyutils.fi.props.PropertiesUtils;

public class FxSymbol {
    private String fromCurrency;
    private String toCurrency;
    private double rate;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public static FxSymbol parse(String symbol) {
        FxSymbol fxSymbol = null;

        // ALLUSD=X
        if (symbol == null) {
            return null;
        }

        if (PropertiesUtils.isNull(symbol)) {
            return null;
        }

        if (symbol.length() != 8) {
            return null;
        }

        if (!symbol.endsWith("=X")) {
            return null;
        }

        fxSymbol = new FxSymbol();
        fxSymbol.setFromCurrency(symbol.substring(0, 3));
        fxSymbol.setToCurrency(symbol.substring(3, 6));

        return fxSymbol;
    }
}
