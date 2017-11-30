package com.hungle.msmoney.statements;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ca.odell.glazedlists.EventList;

/**
 * The Class ShowLastErrorAction.
 */
final class ShowLastErrorAction extends AbstractAction {
    
    /**
     * 
     */
    private final StatementPanel statementPanel;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The popup menu. */
    private final JPopupMenu popupMenu;

    /**
     * Instantiates a new show last error action.
     *
     * @param name the name
     * @param popupMenu the popup menu
     * @param statementPanel TODO
     */
    private ShowLastErrorAction(StatementPanel statementPanel, String name, JPopupMenu popupMenu) {
        super(name);
        this.statementPanel = statementPanel;
        this.popupMenu = popupMenu;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        EventList<FiBean> beans = this.statementPanel.fiBeansSelectionModel.getSelected();
        for (FiBean bean : beans) {
            ShowErrorDialog dialog = null;
            try {
                dialog = new ShowErrorDialog(JOptionPane.getFrameForComponent(this.statementPanel), bean);
                dialog.setTitle(bean.getName());
                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.showDialog(popupMenu);
            } finally {
                if (dialog != null) {
                    dialog.dispose();
                    dialog = null;
                }
            }
        }
    }
}