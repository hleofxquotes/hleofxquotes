package com.hungle.msmoney.gui.action;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.ImportStatus;
import com.hungle.msmoney.core.ofx.ImportUtils;

public class OpenUtils {
    private static final Logger LOGGER = Logger.getLogger(OpenUtils.class);

    public static ImportStatus open(Executor threadPool, File file) throws IOException {
        LOGGER.info("Opening file=" + file);
        return ImportUtils.doImport(threadPool, file);
    }
}
