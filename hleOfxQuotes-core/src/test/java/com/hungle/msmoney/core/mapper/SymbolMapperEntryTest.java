package com.hungle.msmoney.core.mapper;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.csvreader.CsvReader;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapperEntryTest.
 */
public class SymbolMapperEntryTest {

    /**
     * Test.
     * @throws IOException 
     */
    @Test
    public void test() throws IOException {
        SymbolMapperEntry entry = new SymbolMapperEntry();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String header : SymbolMapperEntry.HEADERS) {
            if (count > 0) {
                sb.append(",");
            }
            sb.append(header);
            count++;
        }
        StringReader reader = new StringReader(sb.toString());
        CsvReader csvReader = new CsvReader(reader);
        entry.load(csvReader);
    }

}
