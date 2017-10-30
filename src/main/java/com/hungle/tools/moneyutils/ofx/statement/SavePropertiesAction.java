package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.hungle.tools.moneyutils.fi.AbstractFiDir;
import com.hungle.tools.moneyutils.fi.DefaultFiDir;

/**
 * The Class SavePropertiesAction.
 */
final class SavePropertiesAction extends AbstractAction {
    
    /**
     * 
     */
    private final StatementPanel statementPanel;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new save properties action.
     *
     * @param name the name
     * @param statementPanel TODO
     */
    SavePropertiesAction(StatementPanel statementPanel, String name) {
        super(name);
        this.statementPanel = statementPanel;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        StatementPanel.LOGGER.info("> saving " + this.statementPanel.fiPropertiesFile);

        JTextArea textArea = this.statementPanel.fiPropertiesTextArea;
        if (textArea == null) {
            return;
        }
        if (this.statementPanel.fiPropertiesFile == null) {
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.statementPanel.fiPropertiesFile));
            textArea.write(writer);
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    try {
                        File d = SavePropertiesAction.this.statementPanel.fiPropertiesFile.getAbsoluteFile().getParentFile();
                        FiBean bean = SavePropertiesAction.this.statementPanel.detailsForBean;
                        if (bean == null) {
                            return;
                        }
                        AbstractFiDir updater = new DefaultFiDir(d);
                        SavePropertiesAction.this.statementPanel.refreshBean(bean, updater);
                    } catch (IOException e) {
                        StatementPanel.LOGGER.error(e, e);
                    } finally {
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        } catch (IOException e) {
            StatementPanel.LOGGER.error(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    StatementPanel.LOGGER.warn(e);
                } finally {
                    writer = null;
                }
            }
            JOptionPane.showMessageDialog(this.statementPanel,
                    "File:\n" + this.statementPanel.fiPropertiesFile.getAbsolutePath() + " is saved.", "File saved",
                    JOptionPane.PLAIN_MESSAGE);
            StatementPanel.LOGGER.info("< DONE saving " + this.statementPanel.fiPropertiesFile);
        }
    }
}