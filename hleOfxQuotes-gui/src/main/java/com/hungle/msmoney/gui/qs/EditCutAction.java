package com.hungle.msmoney.gui.qs;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

final class EditCutAction extends AbstractAction {
    private final JTextArea textArea;
    private static final long serialVersionUID = 1L;

    EditCutAction(String name, JTextArea textArea) {
        super(name);
        this.textArea = textArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textArea.cut();
    }
}