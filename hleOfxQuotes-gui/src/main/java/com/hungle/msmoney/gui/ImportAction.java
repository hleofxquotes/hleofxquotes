package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.ImportUtils;

/**
 * The Class ImportAction.
 */
final class ImportAction extends AbstractAction {
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
    ImportAction(GUI gui, String name) {
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
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    ImportUtils.doImport(ImportAction.this.gui.threadPool, ImportAction.this.gui.getOutputFiles());
                } finally {
                    Runnable doRun = new Runnable() {
                        @Override
                        public void run() {
                            ImportAction.this.gui.lastKnownImportString = (new Date()).toString();
                            if (ImportAction.this.gui.lastKnownImport != null) {
                                ImportAction.this.gui.lastKnownImport.setText(ImportAction.this.gui.lastKnownImportString);
                            }
                            GUI.PREFS.put(GUI.PREF_LAST_KNOWN_IMPORT_STRING, ImportAction.this.gui.lastKnownImportString);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }

            }

        };
        this.gui.threadPool.execute(command);
    }

}