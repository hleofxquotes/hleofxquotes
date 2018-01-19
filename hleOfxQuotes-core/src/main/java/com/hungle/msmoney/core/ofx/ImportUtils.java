package com.hungle.msmoney.core.ofx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ImportUtils.
 */
public class ImportUtils {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(ImportUtils.class);

    /**
     * Do import.
     *
     * @param threadPool the thread pool
     * @param ofxFiles the ofx files
     * @return the int
     * @throws IOException 
     */
    public static int doImport(Executor threadPool, List<File> ofxFiles) throws IOException {
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

    /**
     * Do import.
     *
     * @param threadPool the thread pool
     * @param ofxFile the ofx file
     * @return true, if successful
     * @throws IOException 
     */
    public static boolean doImport(Executor threadPool, File ofxFile) throws IOException {
        if (ofxFile == null) {
            LOGGER.warn("No OFX output file");
            return false;
        }
        if (!ofxFile.exists()) {
            LOGGER.warn("File ofxFile=" + ofxFile + " does not exist.");
            return false;
        }

        boolean returnCode = false;
        Runtime rt = Runtime.getRuntime();
        try {
            String command = "rundll32 SHELL32.DLL,ShellExec_RunDLL " + ofxFile.getAbsolutePath();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Import command=" + command);
            }
            Process proc = rt.exec(command);
            final InputStream stdout = proc.getInputStream();
            threadPool.execute(new StreamConsumer(stdout, "stdout"));
            final InputStream stderr = proc.getErrorStream();
            threadPool.execute(new StreamConsumer(stderr, "stderr"));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("pre proc.waitFor()");
            }
            int status = proc.waitFor();
            if (status != 0) {
                LOGGER.warn("Import command failed with exit status=" + status);
                LOGGER.warn("  command=" + command);
                returnCode = false;
            } else {
                returnCode = true;
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        return returnCode;
    }

    public static final File renameToOfxFile(File source) throws IOException {
        File dest = File.createTempFile("import", ".ofx");
        
        CopyOption options = StandardCopyOption.REPLACE_EXISTING;
        Files.copy(source.toPath(), dest.toPath(), options);
        dest.deleteOnExit();
        
        return dest;
    }

}
