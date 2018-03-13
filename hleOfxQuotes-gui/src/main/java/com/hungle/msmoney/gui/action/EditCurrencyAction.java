package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.CurrencyUtils;
import com.hungle.msmoney.gui.GUI;

/**
 * The Class EditCurrencyAction.
 */
public final class EditCurrencyAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(EditCurrencyAction.class);

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
        Set<String> labels = CurrencyUtils.CURRENCIES.keySet();
        labels = addCurrentJavaLocaleCurrency(labels);

        String[] possibilities = new String[labels.size()];
        int i = 0;
        for (String key : labels) {
            possibilities[i++] = key;
        }

        Icon icon = null;
        String title = "Currency: current " + this.gui.getDefaultCurrency();

        JComboBox<String> jcb = new JComboBox<String>(possibilities);
        jcb.setEditable(true);
        int confirm = JOptionPane.showConfirmDialog(this.gui, jcb, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                icon);
        if (confirm == JOptionPane.CANCEL_OPTION) {
            return;
        }

        String selectedLabel = (String) jcb.getSelectedItem();

        // If a string was returned, say so.
        if ((selectedLabel != null) && (selectedLabel.length() > 0)) {
            String currencyCode = null;
            currencyCode = CurrencyUtils.CURRENCIES.get(selectedLabel);
            if ((currencyCode == null) || (currencyCode.length() <= 0)) {
                currencyCode = selectedLabel;
            }
            
            if (currencyCode != null) {
                this.gui.selectNewCurrency(currencyCode);
            }
        } else {
        }
    }

    private Set<String> addCurrentJavaLocaleCurrency(Set<String> labels) {
        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        String defaultCurrencyCode = currency.getCurrencyCode();

        for (String label : labels) {
            String currencyCode = CurrencyUtils.CURRENCIES.get(label);
            if (currencyCode != null) {
                if (currencyCode.compareToIgnoreCase(defaultCurrencyCode) == 0) {
                    return labels;
                }
            }

        }

        Set<String> newLabels = new TreeSet<>();
        newLabels.addAll(labels);
        newLabels.add(defaultCurrencyCode);
        LOGGER.info("Added new currencyCode=" + defaultCurrencyCode);

        return newLabels;
    }
}