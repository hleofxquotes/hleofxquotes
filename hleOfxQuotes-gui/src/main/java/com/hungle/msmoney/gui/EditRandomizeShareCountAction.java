package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.QuoteSource;

/**
 * The Class EditRandomizeShareCountAction.
 */
final class EditRandomizeShareCountAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(EditRandomizeShareCountAction.class);

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new edits the randomize share count action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    EditRandomizeShareCountAction(GUI gui, String name) {
        super(name);
        this.gui = gui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
     * ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String[] possibilities = { "true", "false" };
        Icon icon = null;
        String s = (String) JOptionPane.showInputDialog(this.gui,
                "Current: " + this.gui.randomizeShareCount + "\n" + "Choices:", "Set Randomize Share Count",
                JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = s;
            LOGGER.info("Selected new 'Randomize Share Count': " + value);
            Boolean newValue = Boolean.valueOf(value);
            if (newValue.compareTo(this.gui.randomizeShareCount) != 0) {
                this.gui.randomizeShareCount = newValue;
                GUI.PREFS.put(GUI.PREF_RANDOMIZE_SHARE_COUNT, this.gui.randomizeShareCount.toString());
                // to clear the pricing table
                QuoteSource quoteSource = null;
                this.gui.stockSymbolsStringReceived(quoteSource, null);
            }
        } else {
        }
    }
}