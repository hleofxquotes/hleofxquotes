package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;

import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.gui.GUI;

/**
 * The Class ProfileSelectedAction.
 */
public final class ProfileSelectedAction extends AbstractAction {

    /**
     * 
     */
    private final GUI gui;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The props. */
    private Properties props;

    /**
     * Instantiates a new profile selected action.
     *
     * @param name
     *            the name
     * @param props
     *            the props
     * @param gui TODO
     */
    public ProfileSelectedAction(GUI gui, String name, Properties props) {
        super(name);
        this.gui = gui;
        this.props = props;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
     * ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (props == null) {
            return;
        }

        String accountId = props.getProperty("accountId");
        if (!CheckNullUtils.isEmpty(accountId)) {
            this.gui.selectNewAccountId(accountId);
        }
        String currency = props.getProperty("currency");
        if (!CheckNullUtils.isEmpty(currency)) {
            this.gui.selectNewCurrency(currency);
        }
    }
}