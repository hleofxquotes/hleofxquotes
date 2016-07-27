package com.le.tools.moneyutils.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.csvreader.CsvReader;

public class SymbolMapperTest {
    private static final Logger log = Logger.getLogger(SymbolMapperTest.class);

    private SymbolMapper mapper;

    private InputStream in = null;
    private CsvReader csvReader = null;

    @Before
    public void setup() throws IOException {
        mapper = new SymbolMapper();
    }

    private void load(String id) throws IOException {
        String[] tokens = getClass().getName().split("\\.");

        String resourceName = tokens[tokens.length - 1] + "-mapper" + id + ".csv";
        log.info("resourceName=" + resourceName);
        in = getClass().getResourceAsStream(resourceName);
        Assert.assertNotNull(in);

        Charset charset = Charset.defaultCharset();
        csvReader = new CsvReader(in, charset);
        csvReader.readHeaders();
        mapper.load(csvReader);
    }

    @After
    public void teardown() {
        mapper = null;
        if (csvReader != null) {
            csvReader.close();
            csvReader = null;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.warn(e);
            }
        }
    }

    @Test
    public void test01() throws IOException {
        String id = "01";
        load(id);
        Assert.assertEquals(0, mapper.getEntries().size());
    }

    @Test
    public void test02() throws IOException {
        String id = "02";
        load(id);
        Assert.assertEquals(9, mapper.getEntries().size());
    }
    
    @Test
    public void test03() throws IOException {
        String id = "03";
        load(id);
        Assert.assertEquals(3, mapper.getEntries().size());
    }    
}
