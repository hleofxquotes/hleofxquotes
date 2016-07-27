package com.le.tools.moneyutils.ofx.quotes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

public class ImportUtils {
    private static final Logger log = Logger.getLogger(ImportUtils.class);

    public static int doImport(Executor threadPool, List<File> ofxFiles) {
        if (ofxFiles == null) {
            return 0;
        }
        int count = 0;
        for (File ofxFile : ofxFiles) {
            if (doImport(threadPool, ofxFile)) {
                count++;
            }
        }
        return count;
    }

    private static boolean doImport(Executor threadPool, File ofxFile) {
        if (ofxFile == null) {
            log.warn("No OFX output file");
            return false;
        }
        if (!ofxFile.exists()) {
            log.warn("File ofxFile=" + ofxFile + " does not exist.");
            return false;
        }

        boolean returnCode = false;
        Runtime rt = Runtime.getRuntime();
        try {
            String command = "rundll32 SHELL32.DLL,ShellExec_RunDLL " + ofxFile.getAbsolutePath();
            if (log.isDebugEnabled()) {
                log.info("Import command=" + command);
            }
            Process proc = rt.exec(command);
            final InputStream stdout = proc.getInputStream();
            threadPool.execute(new StreamConsumer(stdout, "stdout"));
            final InputStream stderr = proc.getErrorStream();
            threadPool.execute(new StreamConsumer(stderr, "stderr"));

            if (log.isDebugEnabled()) {
                log.debug("pre proc.waitFor()");
            }
            int status = proc.waitFor();
            if (status != 0) {
                log.warn("Import command failed with exit status=" + status);
                log.warn("  command=" + command);
                returnCode = false;
            } else {
                returnCode = true;
            }
        } catch (IOException e) {
            log.error(e);
        } catch (InterruptedException e) {
            log.error(e);
        }

        return returnCode;
    }

}
