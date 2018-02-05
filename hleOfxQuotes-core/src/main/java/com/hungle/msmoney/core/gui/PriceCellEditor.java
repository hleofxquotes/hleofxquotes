package com.hungle.msmoney.core.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

public abstract class PriceCellEditor<T> extends DefaultCellEditor implements TableCellEditor {
    private static final Logger LOGGER = Logger.getLogger(PriceCellEditor.class);
    private AbstractGlazedListTableView<T> abstractGlazedListTableView;

    public PriceCellEditor(JTextField textField, AbstractGlazedListTableView<T> abstractGlazedListTableView) {
        super(textField);
        this.abstractGlazedListTableView = abstractGlazedListTableView;
        final InputVerifier iv = new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField field = (JTextField) input;
                String text = field.getText();
                if (text == null) {
                    return false;
                }
                text = text.trim();
                if (text.length() < 0) {
                    return false;
                }
                Double value = null;
                try {
                    value = Double.valueOf(text);
                } catch (NumberFormatException e) {
                    LOGGER.warn(e);
                }

                return (value != null);
            }

            @Override
            public boolean shouldYieldFocus(JComponent input) {
                boolean valid = verify(input);
                if (!valid) {
                    JOptionPane.showMessageDialog(null, "invalid");
                }
                return valid;
            }

        };
        textField.setInputVerifier(iv);
    }

    @Override
    public Object getCellEditorValue() {
        Object value = super.getCellEditorValue();
        T row = abstractGlazedListTableView.getSelectedRow();
        if (row == null) {
            LOGGER.warn("No selected row");
            return value;
        }

        return convertRowToType(row, value);
    }

    protected abstract Object convertRowToType(T row, Object value);
}
