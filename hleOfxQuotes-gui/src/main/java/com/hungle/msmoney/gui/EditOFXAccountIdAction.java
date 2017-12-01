package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * The Class EditOFXAccountIdAction.
 */
final class EditOFXAccountIdAction extends AbstractAction {

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new edits the OFX account id action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    EditOFXAccountIdAction(GUI gui, String name) {
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
                "Current: " + this.gui.getAccountId() + "\n" + "OFX Account Id:", "Set OFX Account Id", JOptionPane.PLAIN_MESSAGE,
                icon, possibilities, this.gui.getAccountId());

        // If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            String value = s;
            this.gui.selectNewAccountId(value);
        } else {
        }
    }
}