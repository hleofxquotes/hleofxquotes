package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;

/**
 * The Class EditWarnSuspiciousPriceAction.
 */
public final class EditWarnSuspiciousPriceAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(EditWarnSuspiciousPriceAction.class);

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new edits the warn suspicious price action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    public EditWarnSuspiciousPriceAction(GUI gui, String name) {
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
    public void actionPerformed(ActionEvent event) {
        String[] possibilities = null;
        Icon icon = null;
        String s = (String) JOptionPane.showInputDialog(this.gui,
                "To guard against bad price from quote source,\n"
                        + "you can set a value above which will trigger a warning dialog.\n"
                        + "To disable: set to -1.\n" + "\n" + "Current: " + this.gui.getSuspiciousPrice() + "\n" + "Price:",
                "Set a price", JOptionPane.PLAIN_MESSAGE, icon, possibilities, this.gui.getSuspiciousPrice().toString());

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = s;
            LOGGER.info("Selected new 'Warn Suspicious Price': " + value);
            try {
                Integer newValue = Integer.valueOf(value);
                if (newValue.compareTo(this.gui.getSuspiciousPrice()) != 0) {
                    this.gui.setSuspiciousPrice(newValue);
                    le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(GUI.PREF_SUSPICIOUS_PRICE, this.gui.getSuspiciousPrice().toString());
                    // to clear the pricing table
                    // stockSymbolsStringReceived(null);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this.gui, "Not a valid number - " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
        }
    }
}