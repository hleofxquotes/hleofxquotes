package com.hungle.msmoney.gui.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;

public final class EditPasteAction extends AbstractAction {
    private final JTextArea textArea;
    private static final long serialVersionUID = 1L;

    public EditPasteAction(String name, JTextArea textArea) {
        super(name);
        this.textArea = textArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textArea.paste();
    }
}