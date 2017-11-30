package com.hungle.msmoney.qs.yahoo;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;

// TODO: Auto-generated Javadoc
/**
 * The Class GetYahooQuotes.
 */
public class YahooQuotesGetter extends AbstractHttpQuoteGetter {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooQuotesGetter.class);
    
    /** The Constant DEFAULT_HOST. */
    public static final String DEFAULT_HOST = "download.finance.yahoo.com";

    /** The Constant QUOTE_HOSTS. */
    public static final Map<String, String> QUOTE_HOSTS = new TreeMap<String, String>();
    static {
        QUOTE_HOSTS.put("US", "download.finance.yahoo.com");
        QUOTE_HOSTS.put("Argentina", "ar.finance.yahoo.com");
        QUOTE_HOSTS.put("Australia & NZ", "au.finance.yahoo.com");
        QUOTE_HOSTS.put("Brazil", "br.finance.yahoo.com");
        QUOTE_HOSTS.put("Canada", "ca.finance.yahoo.com");
        QUOTE_HOSTS.put("China", "cn.finance.yahoo.com");
        // QUOTE_HOSTS.put("Chinese", "chinese.finance.yahoo.com");
        QUOTE_HOSTS.put("France", "fr.finance.yahoo.com");
        QUOTE_HOSTS.put("French Canada", "cf.finance.yahoo.com");
        QUOTE_HOSTS.put("Germany", "de.finance.yahoo.com");
        QUOTE_HOSTS.put("Hong Kong", "hk.finance.yahoo.com");
        QUOTE_HOSTS.put("India", "in.finance.yahoo.com");
        QUOTE_HOSTS.put("Italy", "it.finance.yahoo.com");
        QUOTE_HOSTS.put("Japan", "finance.yahoo.co.jp");
        QUOTE_HOSTS.put("Korea", "kr.finance.yahoo.com");
        QUOTE_HOSTS.put("Mexico", "mx.finance.yahoo.com");
        QUOTE_HOSTS.put("Singapore", "sg.finance.yahoo.com");
        QUOTE_HOSTS.put("Spain", "es.finance.yahoo.com");
        QUOTE_HOSTS.put("Spanish", "espanol.finance.yahoo.com");
        QUOTE_HOSTS.put("Taiwan", "tw.stock.yahoo.com");
        QUOTE_HOSTS.put("UK & Ireland", "uk.finance.yahoo.com");
    }
}
