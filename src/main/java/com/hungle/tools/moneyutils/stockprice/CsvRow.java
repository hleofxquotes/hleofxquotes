package com.hungle.tools.moneyutils.stockprice;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Interface CsvRow.
 */
public interface CsvRow {

    /**
     * Gets the raw record.
     *
     * @return the raw record
     */
    String getRawRecord();

    /**
     * Gets the column value.
     *
     * @param i the i
     * @return the column value
     * @throws IOException Signals that an I/O exception has occurred.
     */
    String getColumnValue(int i) throws IOException;

}
