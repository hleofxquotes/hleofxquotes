package com.hungle.msmoney.gui;

import java.awt.event.MouseListener;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.hungle.msmoney.core.gui.StripedTableRenderer;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

// TODO: Auto-generated Javadoc
/**
 * The Class PriceTableView.
 *
 * @param <T> the generic type
 */
public class PriceTableView<T extends AbstractStockPrice> extends JScrollPane {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private static final int COL_SYMBOL = 0;

    private static final int COL_NAME = 1;

    private static final int COL_PRICE = 2;

    private static final int COL_LAST_TRADE_DATE = 3;

    private static final int COL_LAST_TRADE_TIME = 4;

    /** The popup menu. */
    private final JPopupMenu popupMenu;

    /** The filter. */
    private TextFilterator<T> filter;

    /** The add stripe. */
    private boolean addStripe = true;

    /** The comparator. */
    private Comparator<T> comparator;

    /**
     * Instantiates a new price table view.
     *
     * @param filterEdit            the filter edit
     * @param priceList            the price list
     * @param baseClass the base class
     */
    public PriceTableView(JTextField filterEdit, EventList<T> priceList, Class<T> baseClass) {
        super();

        popupMenu = new JPopupMenu();

        this.comparator = new Comparator<T>() {
            @Override
            public int compare(T b1, T b2) {
                return b1.getStockSymbol().compareTo(b2.getStockSymbol());
            }
        };

        this.filter = new TextFilterator<T>() {
            @Override
            public void getFilterStrings(List<String> list, T bean) {
                list.add(bean.getStockName());
                list.add(bean.getStockSymbol());
            }
        };

        setViewportView(createViewportView(filterEdit, priceList, baseClass));
    }

    /**
     * Creates the viewport view.
     *
     * @param filterEdit            the filter edit
     * @param priceList            the price list
     * @param baseClass the base class
     * @return the j table
     */
    private JTable createViewportView(JTextField filterEdit, EventList<T> priceList, Class<T> baseClass) {
        JTable table = createPriceTable(priceList, comparator, filterEdit, filter, addStripe, baseClass);
        // table.setFillsViewportHeight(true);

        MouseListener popupListener = new PopupListener(popupMenu);
        // Add the listener to the JTable:
        table.addMouseListener(popupListener);
        // Add the listener specifically to the header:
        // table.getTableHeader().addMouseListener(popupListener);

        return table;
    }

    /**
     * Creates the price table.
     *
     * @param priceList            the price list
     * @param comparator            the comparator
     * @param filterEdit            the filter edit
     * @param filter            the filter
     * @param addStripe            the add stripe
     * @param baseClass the base class
     * @return the j table
     */
    private JTable createPriceTable(EventList<T> priceList, Comparator<T> comparator, JTextField filterEdit,
            TextFilterator<T> filter, boolean addStripe, Class<T> baseClass) {

        EventList<T> source = priceList;

        SortedList<T> sortedList = null;

        if (comparator != null) {
            sortedList = new SortedList<T>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            source = addFiltering(filterEdit, filter, source);
        }

        DefaultEventTableModel<T> tableModel = createTableModel(source, baseClass);

        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            addSorting(sortedList, table);
        }

        setSelectionModel(source, table);

        if (addStripe) {
            addStripeToTable(table);
        }
        
        setPreferredWidth(table);

        return table;
    }

    /**
     * Adds the filtering.
     *
     * @param <T> the generic type
     * @param filterEdit the filter edit
     * @param filter the filter
     * @param source the source
     * @return the event list
     */
    private static <T> EventList<T> addFiltering(JTextField filterEdit, TextFilterator<T> filter, EventList<T> source) {
        FilterList<T> filterList = null;
        MatcherEditor<T> textMatcherEditor = new TextComponentMatcherEditor<T>(filterEdit, filter);
        filterList = new FilterList<T>(source, textMatcherEditor);
        source = filterList;
        return source;
    }

    /**
     * Adds the sorting.
     *
     * @param <T> the generic type
     * @param sortedList the sorted list
     * @param table the table
     */
    private static <T> void addSorting(SortedList<T> sortedList, JTable table) {
        @SuppressWarnings("unused")
        TableComparatorChooser<T> tableSorter = TableComparatorChooser.install(table, sortedList,
                AbstractTableComparatorChooser.SINGLE_COLUMN);
    }

    /**
     * Sets the selection model.
     *
     * @param <T> the generic type
     * @param source the source
     * @param table the table
     */
    private static <T> void setSelectionModel(EventList<T> source, JTable table) {
        DefaultEventSelectionModel<T> eventSelectionModel = createEventSelectionModel(source);
        table.setSelectionModel(eventSelectionModel);
    }

    /**
     * Adds the stripe to table.
     *
     * @param table the table
     */
    private static void addStripeToTable(JTable table) {
        TableCellRenderer renderer = null;
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

    /**
     * Creates the event selection model.
     *
     * @param <T> the generic type
     * @param source the source
     * @return the default event selection model
     */
    private static <T> DefaultEventSelectionModel<T> createEventSelectionModel(EventList<T> source) {
        DefaultEventSelectionModel<T> eventSelectionModel = new DefaultEventSelectionModel<T>(
                GlazedListsSwing.swingThreadProxyList(source));
        return eventSelectionModel;
    }

    /**
     * Creates the table model.
     *
     * @param <T> the generic type
     * @param source the source
     * @param baseClass the base class
     * @return the default event table model
     */
    private static <T> DefaultEventTableModel<T> createTableModel(EventList<T> source, Class<T> baseClass) {
        String propertyNames[] = { "stockSymbol", "stockName", "lastPrice", "lastTradeDate", "lastTradeTime" };
        String columnLabels[] = { "Symbol", "Name", "Price", "Last Trade Date", "Last Trade Time" };
        BeanTableFormat<T> tableFormat = new BeanTableFormat<T>(baseClass, propertyNames, columnLabels);
        DefaultEventTableModel<T> tableModel = new DefaultEventTableModel<T>(
                GlazedListsSwing.swingThreadProxyList(source), tableFormat);
        return tableModel;
    }

    private void setPreferredWidth(JTable table) {
        table.getColumnModel().getColumn(COL_SYMBOL).setPreferredWidth(25);
        table.getColumnModel().getColumn(COL_NAME).setPreferredWidth(90);
        table.getColumnModel().getColumn(COL_PRICE).setPreferredWidth(80);
        table.getColumnModel().getColumn(COL_LAST_TRADE_DATE).setPreferredWidth(50);
        table.getColumnModel().getColumn(COL_LAST_TRADE_TIME).setPreferredWidth(50);
    }

    /**
     * Gets the popup menu.
     *
     * @return the popup menu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
}
