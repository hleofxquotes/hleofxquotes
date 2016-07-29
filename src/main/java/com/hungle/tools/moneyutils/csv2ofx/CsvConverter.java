package com.hungle.tools.moneyutils.csv2ofx;

import java.io.IOException;

import com.csvreader.CsvReader;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;

public interface CsvConverter {

    public abstract AbstractStockPrice convert(CsvReader csvReader) throws IOException;

    public abstract void setStockName(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    public abstract void setStockSymbol(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    public abstract void setLastPrice(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    public abstract void setLastTrade(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    public abstract void setCurrency(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

    public abstract void setUnits(CsvReader csvReader, AbstractStockPrice AbstractStockPrice) throws IOException;

}