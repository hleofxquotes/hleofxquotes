package com.hungle.msmoney.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.ImportUtils;
import com.hungle.msmoney.gui.GUI;

/**
 * The Class ImportAction.
 */
public final class ImportAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(ImportAction.class);

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new import action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    public ImportAction(String name, GUI gui) {
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
        LOGGER.info("> Import action");
        final Component component = (Component) event.getSource();
        final String errorTitle = "Cannot import";
        
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    ImportUtils.doImport(ImportAction.this.gui.getThreadPool(), ImportAction.this.gui.getOutputFiles());
                } catch (IOException e) {
                    Runnable doRun = new Runnable() {
                        @Override
                        public void run() {
                            String message = e.getMessage();
                            JOptionPane.showMessageDialog(component, message, errorTitle, JOptionPane.ERROR_MESSAGE);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                } finally {
                    Runnable doRun = new Runnable() {
                        @Override
                        public void run() {
                            ImportAction.this.gui.setLastKnownImportString((new Date()).toString());
                            if (ImportAction.this.gui.getLastKnownImport() != null) {
                                ImportAction.this.gui.getLastKnownImport().setText(ImportAction.this.gui.getLastKnownImportString());
                            }
                            GUI.PREFS.put(GUI.PREF_LAST_KNOWN_IMPORT_STRING, ImportAction.this.gui.getLastKnownImportString());
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }

            }

        };
        this.gui.getThreadPool().execute(command);
    }

}