package com.hungle.msmoney.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.misc.Utils;
import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.stmt.fi.AbstractFiDir;

/**
 * The Class CreateNewFi.
 */
final class CreateNewFiAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(CreateNewFiAction.class);

    /**
     * 
     */
    private final GUI gui;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parent component. */
    private Component parentComponent = null;

    /**
     * Instantiates a new creates the new fi.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    CreateNewFiAction(GUI gui, String name) {
        super(name);
        this.gui = gui;
        this.parentComponent = CreateNewFiAction.this.gui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
     * ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        String fiName = JOptionPane.showInputDialog(parentComponent, "Enter a new 'Financial Institution' name");
        if (fiName == null) {
            // cancel
            return;
        }

        fiName = fiName.trim();
        if (fiName.length() <= 0) {
            return;
        }

        File topDir = getTopDir();
        if ((!topDir.exists()) && (!topDir.mkdirs())) {
            JOptionPane.showMessageDialog(parentComponent, "Cannot create dir\ndir=" + topDir.getAbsolutePath(),
                    "Error creating", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File fiDir = new File(topDir, fiName);
        if (fiDir.exists()) {
            JOptionPane.showMessageDialog(parentComponent, "Directory exist\ndir=" + fiDir.getAbsolutePath(),
                    "Error creating", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!fiDir.mkdirs()) {
            JOptionPane.showMessageDialog(parentComponent, "Cannot create dir\ndir=" + fiDir.getAbsolutePath(),
                    "Error creating", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LOGGER.info("Created new FI dir=" + fiDir.getAbsolutePath());

        String fiPropertiesFileName = AbstractFiDir.DEFAULT_PROPERTIES_FILENAME;
        String sampleFileName = "samples" + "/" + fiPropertiesFileName;
        URL url = OfxUtils.getResource(sampleFileName);
        if (url == null) {
            JOptionPane.showMessageDialog(parentComponent, "Cannot find sample file\nfile=" + sampleFileName,
                    "Error creating", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File fiPropertiesFile = null;
        try {
            fiPropertiesFile = new File(fiDir, fiPropertiesFileName);
            Utils.copyToFile(url, fiPropertiesFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, "Error creating " + fiPropertiesFileName,
                    "Error creating", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane
                .showMessageDialog(parentComponent,
                        "Succesfully created dirctory for fi=" + fiName + "\n" + "Please edit file\n"
                                + fiPropertiesFile.getAbsolutePath(),
                        "FI Created", JOptionPane.INFORMATION_MESSAGE);

        postCreated();
    }

    /**
     * Gets the top dir.
     *
     * @return the top dir
     */
    protected File getTopDir() {
        return this.gui.getFiDir();
    }

    /**
     * Post created.
     */
    protected void postCreated() {
        this.gui.downloadView.refreshFiDir();

        this.gui.mainTabbed.setSelectedIndex(1);
    }

}