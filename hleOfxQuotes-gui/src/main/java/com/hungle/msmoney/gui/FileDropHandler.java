package com.hungle.msmoney.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

public abstract class FileDropHandler extends TransferHandler {
    private static final Logger LOGGER = Logger.getLogger(FileDropHandler.class);

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.isFlavorJavaFileListType()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("canImport=" + true);
                }
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("canImport=" + false);
        }
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!this.canImport(support)) {
            return false;
        }

        List<File> files = null;
        try {
            files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.error(e, e);
            return false;
        }

        for (File file : files) {
            handleFile(file);
        }
        return true;
    }

    public abstract void handleFile(File file);
}
