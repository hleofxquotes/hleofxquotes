package com.hungle.msmoney.prices;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.hungle.sunriise.viewer.cell.MyTableCellRenderer;

public class SimpleTableView extends JScrollPane {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTable table;

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public SimpleTableView() {
        super();
        // TODO Auto-generated constructor stub
//        TitledBorder border = BorderFactory.createTitledBorder("Latest Prices");
//        this.setBorder(border);
        table = new JTable() {
            @Override
            public void setModel(TableModel dataModel) {
                super.setModel(dataModel);
                TableColumnModel columnModel = this.getColumnModel();
                int cols = columnModel.getColumnCount();
                for (int i = 0; i < cols; i++) {
                    TableColumn column = columnModel.getColumn(i);
                    MyTableCellRenderer renderer = new MyTableCellRenderer(column.getCellRenderer());
                    column.setCellRenderer(renderer);
                }
            }
        };
        table.setAutoCreateRowSorter(true);
        this.setViewportView(table);

    }

}
