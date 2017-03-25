/**
 * 
 */
package com.hungle.tools.moneyutils.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.ImportUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveOfxAction.
 */
public final class SaveOfxAction extends AbstractAction {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(SaveOfxAction.class);

    /** The Constant PREF_SAVE_OFX_DIR. */
    static final String PREF_SAVE_OFX_DIR = "saveOfxDir";

    /** The Constant DEFAULT_OFX_OUTPUT_FILENAME. */
    private static final String DEFAULT_OFX_OUTPUT_FILENAME = "quotes.ofx";

    /** The gui. */
    private final GUI gui;

    /** The fc. */
    private JFileChooser fc = null;

    /** The prefs. */
    private Preferences prefs;

    /**
     * Instantiates a new save ofx action.
     *
     * @param gui the gui
     * @param name the name
     */
    public SaveOfxAction(GUI gui, String name) {
        super(name);
        this.gui = gui;
        this.prefs = GUI.getPrefs();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        List<File> files = this.gui.getOutputFiles();
        
        if ((files == null) || (files.size() <= 0)) {
            log.warn("No OFX content to save");
            return;
        }

        if (fc == null) {
            initFileChooser();
        }
        Component parent = this.gui;
        if (this.fc.getSelectedFile() == null) {
            this.fc.setSelectedFile(new File(DEFAULT_OFX_OUTPUT_FILENAME));
        }

        if (fc.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File fromFile = files.get(0);
        File toFile = fc.getSelectedFile();
        prefs.put(PREF_SAVE_OFX_DIR, toFile.getAbsoluteFile().getParentFile().getAbsolutePath());
        try {
            copyFile(fromFile, toFile);
            log.info("Save to outputFile=" + toFile);
            boolean checkForShiftKey = false;
            if (checkForShiftKey) {
                int modifiers = event.getModifiers();
                if ((modifiers & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
                    log.info("SHIFT key is enable. Will auto-import into Microsoft Money");
                    List<File> list = new ArrayList<File>();
                    list.add(fromFile);
                    ImportUtils.doImport(this.gui.getThreadPool(), list);
                }
            }
        } catch (IOException e) {
            log.error("Cannot save to file=" + toFile, e);
        }
    }

    /**
     * Inits the file chooser.
     */
    private void initFileChooser() {
        if (log.isDebugEnabled()) {
            log.debug("> creating FileChooser");
        }
        String key = PREF_SAVE_OFX_DIR;
        fc = new JFileChooser(prefs.get(key, "."));
        if (log.isDebugEnabled()) {
            log.debug("< creating FileChooser");
        }
    }

    /**
     * Copy file.
     *
     * @param fromFile the from file
     * @param toFile the to file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static final void copyFile(File fromFile, File toFile) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fromFile));
            writer = new BufferedWriter(new FileWriter(toFile));
            copy(reader, writer);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    writer = null;
                }
            }

        }
    }

    /**
     * Copy.
     *
     * @param reader the reader
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static final void copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[1024];
        int n = 0;

        try {
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            writer.flush();
        }
    }
}