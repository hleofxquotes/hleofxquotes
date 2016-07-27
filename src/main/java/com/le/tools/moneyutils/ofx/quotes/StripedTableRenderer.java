/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StripedTableRenderer extends DefaultTableCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Color evenRowsColor;
    private final Color oddRowsColor;

    private StripedTableRenderer(Color evenRowsColor, Color oddRowsColor) {
        this.evenRowsColor = evenRowsColor;
        this.oddRowsColor = oddRowsColor;
    }

    public StripedTableRenderer() {
        this(new Color(204, 255, 204), Color.WHITE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // striped
        if (!isSelected) {
            if (row % 2 == 0) {
                rendererComponent.setBackground(evenRowsColor);
            } else {
                rendererComponent.setBackground(oddRowsColor);
            }
        }
        setCellHorizontalAlignment(column);

        return rendererComponent;
    }

    protected void setCellHorizontalAlignment(int column) {
    }
}