package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;

/**
 * The Class ExitAction.
 */
public final class ExitAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(ExitAction.class);

    /**
     * 
     */
    private final GUI gui;
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new exit action.
     *
     * @param name
     *            the name
     * @param gui TODO
     */
    public ExitAction(GUI gui, String name) {
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
        LOGGER.info("> ExitAction.actionPerformed");

        List<Runnable> tasks = this.gui.getThreadPool().shutdownNow();
        LOGGER.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
        long timeout = 1L;
        TimeUnit unit = TimeUnit.MINUTES;
        try {
            LOGGER.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            if (!this.gui.getThreadPool().awaitTermination(timeout, unit)) {
                LOGGER.warn("Timed-out waiting for threadPool.awaitTermination");
            }
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
        }

        if (this.gui.getImportDialogAutoClickService() != null) {
            this.gui.getImportDialogAutoClickService().setEnable(false);
            this.gui.getImportDialogAutoClickService().shutdown();
        }

        LOGGER.info("> Calling System.gc()");
        System.gc();
        LOGGER.info("> Calling System.exit(0)");
        System.exit(0);
    }
}