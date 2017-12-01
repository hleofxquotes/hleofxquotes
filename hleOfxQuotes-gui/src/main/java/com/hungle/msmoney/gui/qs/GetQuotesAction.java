package com.hungle.msmoney.gui.qs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * The Class GetQuotesAction.
 */
final class GetQuotesAction extends AbstractAction {
    
    /**
     * 
     */
    private final YahooQuoteSourcePanel yahooQuoteSourcePanel;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new gets the quotes action.
     *
     * @param name the name
     * @param yahooQuoteSourcePanel TODO
     */
    GetQuotesAction(YahooQuoteSourcePanel yahooQuoteSourcePanel, String name) {
        super(name);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        this.yahooQuoteSourcePanel.getQuotes();
    }
}