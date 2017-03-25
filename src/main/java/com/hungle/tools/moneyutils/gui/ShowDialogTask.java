/**
 * 
 */
package com.hungle.tools.moneyutils.gui;

import java.awt.Component;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class ShowDialogTask.
 */
public class ShowDialogTask implements Runnable {
    
    /** The parent component. */
    private final Component parentComponent;

    /** The message. */
    private String message;

    /** The title. */
    private String title;

    /** The message type. */
    private int messageType;

    /**
     * Instantiates a new show dialog task.
     *
     * @param parentComponent the parent component
     * @param message the message
     * @param title the title
     * @param messageType the message type
     */
    public ShowDialogTask(Component parentComponent, String message, String title, int messageType) {
        super();
        this.parentComponent = parentComponent;
        this.message = message;
        this.title = title;
        this.messageType = messageType;
    }

    /**
     * Instantiates a new show dialog task.
     *
     * @param parentComponent the parent component
     * @param exception the exception
     * @param messageType the message type
     */
    public ShowDialogTask(Component parentComponent, Exception exception, int messageType) {
        this(parentComponent, exception.toString(), exception.getClass().getName(), messageType);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
    }
}