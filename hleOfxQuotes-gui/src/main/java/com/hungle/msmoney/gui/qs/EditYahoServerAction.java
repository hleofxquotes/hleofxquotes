package com.hungle.msmoney.gui.qs;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.yahoo.YahooQuotesGetter;

/**
 * The Class EditYahoServerAction.
 */
final class EditYahoServerAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(EditYahoServerAction.class);

    /**
     * 
     */
    private final YahooQuoteSourcePanel yahooQuoteSourcePanel;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new edits the yaho server action.
     *
     * @param name
     *            the name
     * @param yahooQuoteSourcePanel
     *            TODO
     */
    public EditYahoServerAction(YahooQuoteSourcePanel yahooQuoteSourcePanel, String name) {
        super(name);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Set<String> keys = YahooQuotesGetter.QUOTE_HOSTS.keySet();
        String[] possibilities = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            possibilities[i++] = key;
        }
        Icon icon = null;
        String s = (String) JOptionPane.showInputDialog(this.yahooQuoteSourcePanel,
                "Current: " + this.yahooQuoteSourcePanel.quoteServer + "\n" + "Available:", "Set Yahoo Quote Server",
                JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = YahooQuotesGetter.QUOTE_HOSTS.get(s);
            LOGGER.info("Selected new Yahoo Quote Server: " + value);
            this.yahooQuoteSourcePanel.quoteServer = value;
            this.yahooQuoteSourcePanel.prefs.put(YahooQuoteSourcePanel.QUOTE_SERVER_PREFS_KEY,
                    this.yahooQuoteSourcePanel.quoteServer);
        } else {
        }

    }
}