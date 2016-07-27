package com.le.tools.moneyutils.ofx.quotes;

import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.StockPrice;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

public class PriceTableView extends JScrollPane {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JPopupMenu popupMenu;

    public PriceTableView(JTextField filterEdit, EventList<AbstractStockPrice> priceList) {
        super();
        popupMenu = new JPopupMenu();
        setViewportView(createViewportView(filterEdit, priceList));
    }

    private JTable createViewportView(JTextField filterEdit, EventList<AbstractStockPrice> priceList) {
        Comparator<? super AbstractStockPrice> comparator = new Comparator<AbstractStockPrice>() {
            @Override
            public int compare(AbstractStockPrice b1, AbstractStockPrice b2) {
                return b1.getStockSymbol().compareTo(b2.getStockSymbol());
            }
        };
        // JTextField filterEdit = new JTextField(10);
        TextFilterator<AbstractStockPrice> filter = null;
        // String propertyNames[] = { "stockName", "stockSymbol", };
        // filter = new BeanTextFilterator(propertyNames);
        filter = new TextFilterator<AbstractStockPrice>() {
            @Override
            public void getFilterStrings(List<String> list, AbstractStockPrice bean) {
                list.add(bean.getStockName());
                list.add(bean.getStockSymbol());
            }
        };
        boolean addStripe = true;
        JTable table = createPriceTable(priceList, comparator, filterEdit, filter, addStripe);
        // table.setFillsViewportHeight(true);

        MouseListener popupListener = new PopupListener(popupMenu);
        // Add the listener to the JTable:
        table.addMouseListener(popupListener);
        // Add the listener specifically to the header:
//        table.getTableHeader().addMouseListener(popupListener);

        return table;
        // JScrollPane scrolledPane = new JScrollPane(table);
        //
        // return scrolledPane;
    }

    private JTable createPriceTable(EventList<AbstractStockPrice> priceList, Comparator<? super AbstractStockPrice> comparator, JTextField filterEdit,
            TextFilterator<AbstractStockPrice> filter, boolean addStripe) {
        EventList<AbstractStockPrice> source = priceList;

        SortedList<AbstractStockPrice> sortedList = null;

        if (comparator != null) {
            sortedList = new SortedList<AbstractStockPrice>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            FilterList<AbstractStockPrice> filterList = null;
            MatcherEditor<AbstractStockPrice> textMatcherEditor = new TextComponentMatcherEditor<AbstractStockPrice>(filterEdit, filter);
            filterList = new FilterList<AbstractStockPrice>(source, textMatcherEditor);
            source = filterList;
        }

        Class<StockPrice> beanClass = StockPrice.class;
        String propertyNames[] = { "stockSymbol", "stockName", "lastPrice", "lastTradeDate", "lastTradeTime" };
        String columnLabels[] = { "Symbol", "Name", "Price", "Last Trade Date", "Last Trade Time" };
        AdvancedTableFormat tableFormat = new BeanTableFormat(beanClass, propertyNames, columnLabels);
        EventTableModel<AbstractStockPrice> tableModel = new EventTableModel<AbstractStockPrice>(source, tableFormat);

        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList, AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        EventSelectionModel myEventSelectionModel = new EventSelectionModel(source);
        table.setSelectionModel(myEventSelectionModel);

        if (addStripe) {
            // final Color oddRowsColor = new Color(204, 255, 204);
            // final Color evenRowsColor = Color.WHITE;
            TableCellRenderer renderer = null;
            // renderer = new StripedTableCellRenderer(oddRowsColor,
            // evenRowsColor) {
            //
            // public Component getTableCellRendererComponent(JTable table,
            // Object value, boolean isSelected, boolean hasFocus, int row, int
            // column) {
            // Component rendererComponent = null;
            // rendererComponent = super.getTableCellRendererComponent(table,
            // value, isSelected, hasFocus, row, column);
            // if (column == 3) {
            // // ((JLabel)
            // rendererComponent).setHorizontalAlignment(JLabel.RIGHT);
            // // (javax.swing.JLabel)
            // rendererComponent).setHorizontalAlignment(JLabel.RIGHT);
            // rendererComponent.setEnabled(false);
            // }
            // return rendererComponent;
            // }
            //
            // };
            int cols = table.getColumnModel().getColumnCount();
            for (int i = 0; i < cols; i++) {
                renderer = new StripedTableRenderer() {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void setCellHorizontalAlignment(int column) {
                        super.setCellHorizontalAlignment(column);
                        if ((column == 0) || (column == 2) || (column == 3)) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                    }
                };
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
        // String dateFormat = "yyyyMMddHHMMSS";
        // DateTableCellRenderer renderer = new
        // DateTableCellRenderer(dateFormat);
        // table.getColumnModel().getColumn(2).setCellRenderer(renderer);
        //
        // final int columnCount = 3;
        // int[] colWidths = new int[columnCount];
        // for (int i = 0; i < colWidths.length; i++) {
        // colWidths[i] = -1;
        // }
        // colWidths = null;
        // if (colWidths != null) {
        // log.info("colWidths=" + colWidths);
        // Object[] maxWidthValues =
        // TableUtils.calculateMaxWidthValues(colWidths);
        // TableUtils.adjustColumnSizes(table, maxWidthValues);
        // }

        return table;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
}
