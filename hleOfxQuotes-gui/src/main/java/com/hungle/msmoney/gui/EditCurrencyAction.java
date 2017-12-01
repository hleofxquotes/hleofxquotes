package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.hungle.msmoney.core.ofx.CurrencyUtils;

/**
 * The Class EditCurrencyAction.
 */
final class EditCurrencyAction extends AbstractAction {

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new edits the currency action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    EditCurrencyAction(GUI gui, String name) {
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
        Set<String> keys = CurrencyUtils.CURRENCIES.keySet();
        String[] possibilities = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            possibilities[i++] = key;
        }
        Icon icon = null;
        String s = (String) JOptionPane.showInputDialog(this.gui,
                "Current: " + this.gui.getDefaultCurrency() + "\n" + "Available:", "Set Currency", JOptionPane.PLAIN_MESSAGE,
                icon, possibilities, null);

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = CurrencyUtils.CURRENCIES.get(s);
            this.gui.selectNewCurrency(value);
        } else {
        }
    }
}