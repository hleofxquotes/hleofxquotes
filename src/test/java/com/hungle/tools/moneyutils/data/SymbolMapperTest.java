package com.hungle.tools.moneyutils.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.csvreader.CsvReader;
import com.hungle.msmoney.core.data.SymbolMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapperTest.
 */
public class SymbolMapperTest {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(SymbolMapperTest.class);

    /** The mapper. */
    private SymbolMapper mapper;

    /** The in. */
    private InputStream in = null;
    
    /** The csv reader. */
    private CsvReader csvReader = null;

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void setup() throws IOException {
        mapper = new SymbolMapper();
    }

    /**
     * Load.
     *
     * @param id the id
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void load(String id) throws IOException {
        String[] tokens = getClass().getName().split("\\.");

        String resourceName = tokens[tokens.length - 1] + "-mapper" + id + ".csv";
        LOGGER.info("resourceName=" + resourceName);
        in = getClass().getResourceAsStream(resourceName);
        Assert.assertNotNull(in);

        Charset charset = Charset.defaultCharset();
        csvReader = new CsvReader(in, charset);
        csvReader.readHeaders();
        mapper.load(csvReader);
    }

    /**
     * Teardown.
     */
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
                LOGGER.warn(e);
            }
        }
    }

    /**
     * Test 01.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void test01() throws IOException {
        String id = "01";
        load(id);
        Assert.assertEquals(0, mapper.getEntries().size());
    }

    /**
     * Test 02.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void test02() throws IOException {
        String id = "02";
        load(id);
        Assert.assertEquals(9, mapper.getEntries().size());
    }
    
    /**
     * Test 03.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void test03() throws IOException {
        String id = "03";
        load(id);
        Assert.assertEquals(3, mapper.getEntries().size());
    }    
}
