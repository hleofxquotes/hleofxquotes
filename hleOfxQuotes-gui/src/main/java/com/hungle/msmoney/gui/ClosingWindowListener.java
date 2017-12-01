package com.hungle.msmoney.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * The listener interface for receiving closingWindow events. The class that
 * is interested in processing a closingWindow event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's <code>addClosingWindowListener<code>
 * method. When the closingWindow event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ClosingWindowEvent
 */
final class ClosingWindowListener implements WindowListener {
    private static final Logger LOGGER = Logger.getLogger(ClosingWindowListener.class);

    /**
     * 
     */
    private final GUI gui;

    /**
     * @param gui
     */
    ClosingWindowListener(GUI gui) {
        this.gui = gui;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent event) {
        LOGGER.info("> windowClosing");
        try {
            shutdown();
        } finally {
            // TODO: see if this will help with JNA crash on the way out
            LOGGER.info("> Calling System.gc()");
            System.gc();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent event) {
        LOGGER.info("> windowClosed");
        try {
            shutdown();
        } finally {
            // TODO: see if this will help with JNA crash on the way out
            LOGGER.info("> Calling System.gc()");
            System.gc();
        }
    }

    /**
     * Shutdown.
     */
    private void shutdown() {
        if (this.gui.threadPool != null) {
            List<Runnable> tasks = this.gui.threadPool.shutdownNow();
            LOGGER.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;
            try {
                LOGGER.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                if (!this.gui.threadPool.awaitTermination(timeout, unit)) {
                    LOGGER.warn("Timed-out waiting for threadPool.awaitTermination");
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } finally {
                LOGGER.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            }
        }
        if (this.gui.getImportDialogAutoClickService() != null) {
            this.gui.getImportDialogAutoClickService().setEnable(false);
            this.gui.getImportDialogAutoClickService().shutdown();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.
     * WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent e) {
    }
}