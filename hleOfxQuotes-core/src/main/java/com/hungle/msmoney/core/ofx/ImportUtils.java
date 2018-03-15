package com.hungle.msmoney.core.ofx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
     * @param threadPool
     *            the thread pool
     * @param files
     *            the ofx files
     * @return the int
     * @throws IOException
     */
    public static int doImport(Executor threadPool, List<File> files) throws IOException {
        if (files == null) {
            return 0;
        }
        int count = 0;
        for (File file : files) {
            ImportStatus importStatus = doImport(threadPool, file);
            if (importStatus.getStatusCode() == 0) {
                count++;
            } else {
                LOGGER.warn("Import statusCode=" + importStatus.getStatusCode());
            }
        }
        return count;
    }

    /**
     * Do import.
     *
     * @param threadPool
     *            the thread pool
     * @param file
     *            the ofx file
     * @return true, if successful
     * @throws IOException
     */
    public static ImportStatus doImport(Executor threadPool, File file) throws IOException {
        ImportStatus importStatus = new ImportStatus();
        
        if (file == null) {
            String message = "Cannot open file=" + file;
            LOGGER.warn(message);
            
            importStatus.setStatusCode(-1);
            List<String> lines = new ArrayList<String>();
            lines.add(message);
            importStatus.setStderrLines(lines);
            return importStatus;
        }
        if (!file.exists()) {
            String message = "File=" + file + " does not exist.";
            LOGGER.warn(message);
            
            importStatus.setStatusCode(-1);
            List<String> lines = new ArrayList<String>();
            lines.add(message);
            importStatus.setStderrLines(lines);
            return importStatus;
        }

//        boolean returnCode = false;
        Runtime runtime = Runtime.getRuntime();
        try {
            String command = getOpenCommand() + " " + file.getAbsolutePath();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Import command=" + command);
            }
            Process process = runtime.exec(command);
            
            final InputStream stdout = process.getInputStream();
            StreamConsumer stdoutStreamConsumer = new StreamConsumer(stdout, "stdout");
            threadPool.execute(stdoutStreamConsumer);
            
            final InputStream stderr = process.getErrorStream();
            StreamConsumer stderrStreamConsumer = new StreamConsumer(stderr, "stderr");
            threadPool.execute(stderrStreamConsumer);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("pre proc.waitFor()");
            }
            int status = process.waitFor();
            if (status != 0) {
                LOGGER.warn("Import command failed with exit status=" + status);
                LOGGER.warn("  command=" + command);
            }
            
            importStatus.setStatusCode(status);
            importStatus.setStdoutLines(stdoutStreamConsumer.getLines());
            importStatus.setStderrLines(stderrStreamConsumer.getLines());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        return importStatus;
    }

    private static String getOpenCommand() {
        String command = null;

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            command = "open";
        } else if (osName.startsWith("Windows")) {
            command = "rundll32 SHELL32.DLL,ShellExec_RunDLL";
        } else {
            command = "xdg-open";
        }

        return command;
    }

    public static final File renameToOfxFile(File source) throws IOException {
        File dest = File.createTempFile("import", ".ofx");

        CopyOption options = StandardCopyOption.REPLACE_EXISTING;
        Files.copy(source.toPath(), dest.toPath(), options);
        dest.deleteOnExit();

        return dest;
    }

}
