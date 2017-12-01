package com.hungle.msmoney.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The Class UpdateResultViewTask.
 */
final class UpdateResultViewTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(UpdateResultViewTask.class);

    /**
     * 
     */
    private final GUI gui;

    /**
     * @param gui
     */
    UpdateResultViewTask(GUI gui) {
        this.gui = gui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (this.gui.getResultView() == null) {
            return;
        }

        List<File> files = this.gui.getOutputFiles();
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
            this.gui.getResultView().read(reader, outputFile.getName());
            this.gui.getResultView().setCaretPosition(0);
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
}