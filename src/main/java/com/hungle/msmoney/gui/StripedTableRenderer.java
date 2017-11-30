/**
 * 
 */
package com.hungle.msmoney.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// TODO: Auto-generated Javadoc
/**
 * The Class StripedTableRenderer.
 */
public class StripedTableRenderer extends DefaultTableCellRenderer {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The even rows color. */
    private final Color evenRowsColor;
    
    /** The odd rows color. */
    private final Color oddRowsColor;

    /**
     * Instantiates a new striped table renderer.
     *
     * @param evenRowsColor the even rows color
     * @param oddRowsColor the odd rows color
     */
    private StripedTableRenderer(Color evenRowsColor, Color oddRowsColor) {
        this.evenRowsColor = evenRowsColor;
        this.oddRowsColor = oddRowsColor;
    }

    /**
     * Instantiates a new striped table renderer.
     */
    public StripedTableRenderer() {
        this(new Color(204, 255, 204), Color.WHITE);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
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

    /**
     * Sets the cell horizontal alignment.
     *
     * @param column the new cell horizontal alignment
     */
    protected void setCellHorizontalAlignment(int column) {
    }
}