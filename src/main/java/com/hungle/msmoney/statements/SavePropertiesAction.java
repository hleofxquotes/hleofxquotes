package com.hungle.msmoney.statements;

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

import org.apache.log4j.Logger;

import com.hungle.msmoney.statements.fi.AbstractFiDir;
import com.hungle.msmoney.statements.fi.DefaultFiDir;

/**
 * The Class SavePropertiesAction.
 */
final class SavePropertiesAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(SavePropertiesAction.class);

    
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
        LOGGER.info("> saving " + this.statementPanel.fiPropertiesFile);

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
                        StatementPanel panel = SavePropertiesAction.this.statementPanel;
                        
                        File d = panel.fiPropertiesFile.getAbsoluteFile().getParentFile();
                        FiBean bean = panel.detailsForBean;
                        if (bean == null) {
                            return;
                        }
                        AbstractFiDir fiDir = new DefaultFiDir(d);
                        panel.refreshBean(bean, fiDir);
                    } catch (IOException e) {
                        LOGGER.error(e, e);
                    } finally {
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    writer = null;
                }
            }
            
            boolean showMessageDialog = true;
            if (showMessageDialog) {
                JOptionPane.showMessageDialog(this.statementPanel,
                        "File:\n" + this.statementPanel.fiPropertiesFile.getAbsolutePath() + " is saved.", "File saved",
                        JOptionPane.PLAIN_MESSAGE);
            }
            
            LOGGER.info("< DONE saving " + this.statementPanel.fiPropertiesFile);
        }
    }
}