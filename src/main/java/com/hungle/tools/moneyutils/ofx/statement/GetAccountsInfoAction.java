package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.net.CheckOfxVersion;

import ca.odell.glazedlists.EventList;

/**
 * The Class GetAccountsInfoAction.
 */
final class GetAccountsInfoAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(GetAccountsInfoAction.class);

    /**
     * 
     */
    private final StatementPanel statementPanel;
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new gets the accounts info action.
     *
     * @param name the name
     * @param statementPanel TODO
     */
    GetAccountsInfoAction(StatementPanel statementPanel, String name) {
        super(name);
        this.statementPanel = statementPanel;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.info("> actionPerformed");
        
        CheckOfxVersion checkOfxVersion = new CheckOfxVersion();
        EventList<FiBean> beans = this.statementPanel.fiBeansSelectionModel.getSelected();
        for (FiBean bean : beans) {
            File dir = bean.getUpdater().getDir();
            checkOfxVersion.check(dir);
        }
    }
}