package com.hungle.msmoney.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
        final Component parentComponent = (Component) event.getSource();
        final ExecutorService threadPool = this.getGui().getThreadPool();
        
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    ImportAction.this.getGui().saveToOFX();
                    final List<File> outputFiles = ImportAction.this.getGui().getOutputFiles();
                    ImportUtils.doImport(threadPool, outputFiles);
                } catch (IOException e) {
                    showErrorDialog(parentComponent, e);
                } finally {
                    updateLastKnownImportString();
                }
            }

        };
        threadPool.execute(command);
    }

    private GUI getGui() {
        return gui;
    }

    private void updateLastKnownImportString() {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                ImportAction.this.getGui().setLastKnownImportString((new Date()).toString());
                if (ImportAction.this.getGui().getLastKnownImport() != null) {
                    ImportAction.this.getGui().getLastKnownImport().setText(ImportAction.this.getGui().getLastKnownImportString());
                }
                le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(GUI.PREF_LAST_KNOWN_IMPORT_STRING, ImportAction.this.getGui().getLastKnownImportString());
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    private void showErrorDialog(final Component parentComponent, IOException e) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                final String errorTitle = "Cannot import";
                String message = e.getMessage();
                JOptionPane.showMessageDialog(parentComponent, message, errorTitle, JOptionPane.ERROR_MESSAGE);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

}