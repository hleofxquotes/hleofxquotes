package com.hungle.msmoney.core.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

public class PriceCellEditor extends DefaultCellEditor implements TableCellEditor {
    private static final Logger LOGGER = Logger.getLogger(PriceCellEditor.class);

    public PriceCellEditor(JTextField textField) {
        super(textField);
    }
}
