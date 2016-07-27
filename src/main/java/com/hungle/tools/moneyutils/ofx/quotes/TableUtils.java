package com.hungle.tools.moneyutils.ofx.quotes;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

// TODO: Auto-generated Javadoc
/**
 * The Class TableUtils.
 */
public class TableUtils {
    
    /**
     * Adjust column sizes.
     *
     * @param table the table
     * @param maxWidthValues the max width values
     */
    /*
     * This method picks good column sizes. If all column heads are wider than
     * the column's cells' contents, then you can just use
     * column.sizeWidthToFit().
     */
    public static void adjustColumnSizes(final JTable table, final Object[] maxWidthValues) {
        final TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        final TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < maxWidthValues.length; i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, maxWidthValues[i], false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            // XXX: Before Swing 1.1 Beta 2, use setMinWidth instead.
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    /**
     * Calculate max width values.
     *
     * @param values the values
     * @return the object[]
     */
    public static Object[] calculateMaxWidthValues(int[] values) {
        final int columns = values.length;
        final Object maxWidthValues[] = new Object[columns];
        int maxWidthValue = -1;
        for (int column = 0; column < columns; column++) {
            maxWidthValue = values[column];
            char chars[] = null;
            if (maxWidthValue > 0) {
                chars = new char[maxWidthValue];
            } else {
                chars = new char[1];
            }
            maxWidthValues[column] = new String(chars);
        }

        return maxWidthValues;
    }
}
