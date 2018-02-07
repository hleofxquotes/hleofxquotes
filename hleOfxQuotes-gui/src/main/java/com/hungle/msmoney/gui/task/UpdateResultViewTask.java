package com.hungle.msmoney.gui.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;

/**
 * The Class UpdateResultViewTask.
 */
public final class UpdateResultViewTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(UpdateResultViewTask.class);

    /**
     * 
     */
    private final GUI gui;

    /**
     * @param gui
     */
    public UpdateResultViewTask(GUI gui) {
        this.gui = gui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (this.getGui().getResultView() == null) {
            return;
        }
        
        try {
            this.getGui().saveToOFX();
        } catch (IOException e1) {
            LOGGER.error(e1, e1);
        }
        List<File> files = this.getGui().getOutputFiles();
        
        if (files == null) {
            return;
        }
        if (files.size() <= 0) {
            return;
        }

        File outputFile = files.get(0);
        if (outputFile == null) {
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(outputFile));
            this.getGui().getResultView().read(reader, outputFile.getName());
            this.getGui().getResultView().setCaretPosition(0);
        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }

    }

    private GUI getGui() {
        return gui;
    }
}