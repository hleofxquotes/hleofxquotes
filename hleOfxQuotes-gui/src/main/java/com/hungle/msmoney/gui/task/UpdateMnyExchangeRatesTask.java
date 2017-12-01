package com.hungle.msmoney.gui.task;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.UpdateFx;
import com.hungle.msmoney.gui.GUI;

/**
 * The Class UpdateMnyExchangeRatesTask.
 */
public final class UpdateMnyExchangeRatesTask extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(UpdateMnyExchangeRatesTask.class);

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new update mny exchange rates task.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    public UpdateMnyExchangeRatesTask(GUI gui, String name) {
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
        // JOptionPane.showMessageDialog(GUI.this,
        // "Not yet implemented.");
        try {
            UpdateFx.invoke();
        } catch (Exception e) {
            LOGGER.error(e);

            StringBuilder message = new StringBuilder();
            message.append(e.toString() + "\n");
            // message.append("\n");
            // message.append("Please create a directory 'plugins' and\n");
            // message.append("add the sunriise*.jar file there.\n");
            message.append("Do you want me to download the sunriise plugin jar file and try again?");

            String title = "Error updating exchange rate";

            int n = JOptionPane.showOptionDialog(this.gui, message.toString(), title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (n == JOptionPane.YES_OPTION) {
                getJarFile(event);
            }
        }
    }

    /**
     * Gets the jar file.
     *
     * @param event
     *            the event
     * @return the jar file
     */
    private void getJarFile(final ActionEvent event) {
        final ProgressMonitor progressMonitor = new ProgressMonitor(this.gui, "Downloading sunriise plugin ...", "",
                0, 100);
        final String jarFileName = "sunriise-0.0.3-20111220.210334-1-jar-with-dependencies.jar";

        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String errorMessage = null;
                String uri = "http://sunriise.sourceforge.net/out/hleofxquotes/Build_20111220_64/" + jarFileName;
                File toJarFile = new File("plugins");
                if (!toJarFile.isDirectory()) {
                    toJarFile.mkdirs();
                }
                toJarFile = new File(toJarFile, jarFileName);
                try {
                    if (getJarFile(uri, toJarFile, progressMonitor)) {
                        actionPerformed(event);
                    } else {
                        LOGGER.warn("User cancel downloading!");
                        if (!toJarFile.delete()) {
                            LOGGER.warn("Failed to delete file=" + toJarFile);
                        } else {
                            LOGGER.info("Deleted file=" + toJarFile);
                        }
                    }
                } catch (IOException e) {
                    LOGGER.warn(e, e);
                    errorMessage = e.toString();
                } finally {
                    if (progressMonitor != null) {
                        progressMonitor.close();
                    }
                    if (errorMessage != null) {
                        errorMessage += "\nFailed to download jar file\n" + jarFileName;
                        JOptionPane.showMessageDialog(UpdateMnyExchangeRatesTask.this.gui, errorMessage, "Error downloading " + jarFileName,
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                return errorMessage;
            }
        };

        Future<String> future = this.gui.getThreadPool().submit(task);
    }

    /**
     * Gets the jar file.
     *
     * @param uri
     *            the uri
     * @param toJarFile
     *            the to jar file
     * @param progressMonitor
     *            the progress monitor
     * @return the jar file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private boolean getJarFile(String uri, File toJarFile, final ProgressMonitor progressMonitor)
            throws IOException {
        boolean canceled = false;
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(uri);
        LOGGER.info("GET " + uri);
        HttpResponse httpResponse = client.execute(httpGet);

        Long contentLength = -1L;
        Header header = httpResponse.getFirstHeader("Content-Length");
        if (header != null) {
            String value = header.getValue();
            LOGGER.info(header.getName() + ": " + value);
            if ((value != null) && (value.length() > 0)) {
                try {
                    contentLength = Long.valueOf(value);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e);
                }
            }
        }
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        LOGGER.info("  statusCode=" + statusCode);
        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity entity = httpResponse.getEntity();
            BufferedInputStream in = null;
            BufferedOutputStream out = null;

            try {
                out = new BufferedOutputStream(new FileOutputStream(toJarFile));
                LOGGER.info("  ... saving to " + toJarFile);
                in = new BufferedInputStream(entity.getContent());
                long total = 0L;
                long percentageDone = 0L;
                int n;
                byte[] buffer = new byte[2048];
                while ((n = in.read(buffer)) != -1) {
                    if (progressMonitor.isCanceled()) {
                        canceled = true;
                        notifyCanceled(httpGet, entity, progressMonitor);
                        break;
                    }

                    out.write(buffer, 0, n);

                    if (progressMonitor.isCanceled()) {
                        canceled = true;
                        notifyCanceled(httpGet, entity, progressMonitor);
                        break;
                    }

                    total += n;
                    if (contentLength > 0L) {
                        long done = (total * 100) / contentLength;
                        if (done > percentageDone) {
                            percentageDone = done;
                            if (percentageDone >= 99) {
                                percentageDone = 99;
                            }
                            final String message = updateProgressMonitor(percentageDone, progressMonitor);

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug(total + "/" + contentLength + ", " + message);
                            }
                        }
                    }
                    if (progressMonitor.isCanceled()) {
                        canceled = true;
                        notifyCanceled(httpGet, entity, progressMonitor);
                        break;
                    }
                }
                percentageDone = 100;
                updateProgressMonitor(percentageDone, progressMonitor);
            } finally {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("  > DONE saving");
                }

                if (in != null) {
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("  calling in.close()");
                        }
                        in.close();
                    } finally {
                        in = null;
                    }
                }
                if (out != null) {
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("  calling out.close()");
                        }
                        out.close();
                    } finally {
                        out = null;
                    }
                }
                LOGGER.info("  > DONE saving (post-closing)");
            }
        } else {
            throw new IOException(statusLine.toString());
        }

        LOGGER.info("canceled=" + canceled);

        return !canceled;
    }

    /**
     * Update progress monitor.
     *
     * @param percentageDone
     *            the percentage done
     * @param progressMonitor
     *            the progress monitor
     * @return the string
     */
    private String updateProgressMonitor(long percentageDone, final ProgressMonitor progressMonitor) {
        final String message = String.format("Completed %d%%.\n", percentageDone);
        final int progress = (int) percentageDone;
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                progressMonitor.setNote(message);
                progressMonitor.setProgress(progress);
            }
        };
        SwingUtilities.invokeLater(doRun);
        return message;
    }

    /**
     * Notify canceled.
     *
     * @param httpGet
     *            the http get
     * @param entity
     *            the entity
     * @param progressMonitor
     *            the progress monitor
     */
    private void notifyCanceled(HttpGet httpGet, HttpEntity entity, final ProgressMonitor progressMonitor) {
        LOGGER.warn("progressMonitor.isCanceled()=" + progressMonitor.isCanceled());
        if (httpGet != null) {
            httpGet.abort();
        }
        if (entity != null) {
            LOGGER.info("  calling entity.consumeContent()");
            try {
                entity.consumeContent();
            } catch (IOException e) {
                LOGGER.warn("Failed to entity.consumeContent(), " + e);
            }
        }
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                progressMonitor.setProgress(100);
                progressMonitor.close();
            }
        };
        SwingUtilities.invokeLater(doRun);
    }
}