package com.le.tools.moneyutils.stockprice;

import java.io.IOException;

public interface CsvRow {

    String getRawRecord();

    String getColumnValue(int i) throws IOException;

}
