package com.hungle.tools.moneyutils.ofx.quotes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitToSubListsTest.
 */
public class SplitToSubListsTest {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(SplitToSubListsTest.class);

    /**
     * Split to sub lists test.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void splitToSubListsTest() throws IOException {
        List<String> stocks = OfxUtils.getNYSEList();
        int size = stocks.size();
        for (int i = 0; i < size; i++) {
            int bucketSize = i + 1;
            List<List<String>> subLists = Utils.splitToSubLists(stocks, bucketSize);
            int expected = (size / bucketSize) + (((size % bucketSize) > 0) ? 1 : 0);
            int actual = subLists.size();

            if (log.isDebugEnabled()) {
                log.debug("size=" + size + ", bucketSize=" + bucketSize + ", subLists.size=" + subLists.size());
            }

            Assert.assertEquals(expected, actual);

            List<String> newList = new ArrayList<String>();
            for (List<String> subList : subLists) { 
                newList.addAll(subList);
            }
            Assert.assertTrue(stocks.size() == newList.size());
            Assert.assertTrue(Arrays.equals(stocks.toArray(), newList.toArray()));
        }
    }
}
