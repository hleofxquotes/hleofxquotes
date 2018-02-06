package com.hungle.msmoney.core.gui;

import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

public abstract class PriceCellEditor<T> extends DefaultCellEditor implements TableCellEditor {
    private static final Logger LOGGER = Logger.getLogger(PriceCellEditor.class);

    private AbstractGlazedListTableView<T> abstractGlazedListTableView;

    public PriceCellEditor(JTextField textField, AbstractGlazedListTableView<T> abstractGlazedListTableView) {
        super(textField);
        this.abstractGlazedListTableView = abstractGlazedListTableView;
        // setInputVerifier(textField);
    }

    private void setInputVerifier(JTextField textField) {
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
    public boolean stopCellEditing() {
        JTable table = (JTable) getComponent().getParent();

        try {
            // because we override getCellEditorValue to return a Price
            String editingValue = (String) super.getCellEditorValue();
            if (!validate(editingValue)) {
                JTextField textField = (JTextField) getComponent();
                textField.setBorder(new LineBorder(Color.red));
                textField.selectAll();
                textField.requestFocusInWindow();

                boolean showDialog = false;
                if (showDialog) {
                    JOptionPane.showMessageDialog(null, "Please enter valid number.", "Alert!",
                            JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        } catch (ClassCastException e) {
            LOGGER.warn(e);
            return false;
        }

        return super.stopCellEditing();
    }

    private boolean validate(String editingValue) {
        if (editingValue == null) {
            return false;
        }
        editingValue = editingValue.trim();
        if (editingValue.length() <= 0) {
            return false;
        }

        Double d = null;
        try {
            d = Double.valueOf(editingValue);
        } catch (NumberFormatException e) {
            LOGGER.warn(e);
        }

        return (d != null);
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
