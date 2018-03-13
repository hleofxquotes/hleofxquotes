package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.hungle.msmoney.core.ofx.CurrencyUtils;
import com.hungle.msmoney.gui.GUI;

/**
 * The Class EditCurrencyAction.
 */
public final class EditCurrencyAction extends AbstractAction {

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
     * @param gui
     *            TODO
     */
    public EditCurrencyAction(GUI gui, String name) {
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
        keys = addCurrentJavaLocaleCurrency(keys);

        String[] possibilities = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            possibilities[i++] = key;
        }
        Icon icon = null;
        String s = (String) JOptionPane.showInputDialog(this.gui, "Current: " + this.gui.getDefaultCurrency() + "\n" + "Available:",
                "Set Currency", JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = CurrencyUtils.CURRENCIES.get(s);
            if ((value == null) || (value.length() <= 0)) {
                value = s;
            }
            this.gui.selectNewCurrency(value);
        } else {
        }
    }

    private Set<String> addCurrentJavaLocaleCurrency(Set<String> currentSet) {
        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        if (currentSet.contains(currencyCode)) {
            return currentSet;
        }

        Set<String> set = new TreeSet<>();
        set.addAll(currentSet);
        set.add(currencyCode);

        return set;
    }
}