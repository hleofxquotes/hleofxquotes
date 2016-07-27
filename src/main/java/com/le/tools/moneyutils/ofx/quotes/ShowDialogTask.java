/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.awt.Component;

import javax.swing.JOptionPane;

public class ShowDialogTask implements Runnable {
    /**
     * 
     */
    private final Component parentComponent;

    private String message;

    private String title;

    private int messageType;

    public ShowDialogTask(Component parentComponent, String message, String title, int messageType) {
        super();
        this.parentComponent = parentComponent;
        this.message = message;
        this.title = title;
        this.messageType = messageType;
    }

    public ShowDialogTask(Component parentComponent, Exception exception, int messageType) {
        this(parentComponent, exception.toString(), exception.getClass().getName(), messageType);
    }

    @Override
    public void run() {
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
    }
}