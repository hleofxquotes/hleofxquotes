package com.hungle.msmoney.gui.qs;

import javax.swing.JPanel;

import com.hungle.msmoney.qs.QuoteSource;

public class QuoteSourcePanel extends JPanel {

    /** The quote source. */
//    protected final QuoteSource quoteSource = new DefaultYahooQuoteSource();
    protected QuoteSource quoteSource;

    /**
     * Gets the quote source.
     *
     * @return the quote source
     */
    public QuoteSource getQuoteSource() {
        return quoteSource;
    }

}
