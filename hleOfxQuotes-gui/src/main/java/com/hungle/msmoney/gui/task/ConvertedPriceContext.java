package com.hungle.msmoney.gui.task;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.mapper.SymbolMapper;

public class ConvertedPriceContext {
    public ConvertedPriceContext(String defaultCurrency, SymbolMapper symbolMapper, FxTable fxTable) {
        super();
        this.defaultCurrency = defaultCurrency;
        this.symbolMapper = symbolMapper;
        this.fxTable = fxTable;
    }

    private String defaultCurrency;
    private SymbolMapper symbolMapper;
    private FxTable fxTable;

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public SymbolMapper getSymbolMapper() {
        return symbolMapper;
    }

    public void setSymbolMapper(SymbolMapper symbolMapper) {
        this.symbolMapper = symbolMapper;
    }

    public FxTable getFxTable() {
        return fxTable;
    }

    public void setFxTable(FxTable fxTable) {
        this.fxTable = fxTable;
    }
}
