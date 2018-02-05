package com.hungle.msmoney.core.gui;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.stockprice.Price;

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

public abstract class AbstractGlazedListTableView<T> extends JScrollPane {
    private static final Logger LOGGER = Logger.getLogger(AbstractGlazedListTableView.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The popup menu. */
    private final JPopupMenu popupMenu;

    /** The filter. */
    private TextFilterator<T> filter;

    /** The add stripe. */
    private boolean addStripe = true;

    /** The comparator. */
    private Comparator<T> comparator;

    private JTable table;

    private T selectedRow;

    public AbstractGlazedListTableView(EventList<T> eventList, JTextField filterEdit, Class<T> baseClass,
            PriceTableViewOptions options) {
        super();

        popupMenu = new JPopupMenu();

        this.comparator = createComparator();

        this.filter = createFilter();

        table = createViewportView(eventList, filterEdit, baseClass, options);
        setViewportView(table);
    }

    protected abstract TextFilterator<T> createFilter();

    protected abstract Comparator<T> createComparator();

    protected abstract void setPreferredWidth(JTable table);

    /**
     * Creates the price table.
     *
     * @param eventList
     *            the price list
     * @param comparator
     *            the comparator
     * @param filterEdit
     *            the filter edit
     * @param filter
     *            the filter
     * @param baseClass
     *            the base class
     * @param addStripe
     *            the add stripe
     * @return the j table
     */
    private JTable createTable(EventList<T> eventList, Comparator<T> comparator, JTextField filterEdit, TextFilterator<T> filter,
            Class<T> baseClass, PriceTableViewOptions options, boolean addStripe) {

        EventList<T> source = eventList;

        SortedList<T> sortedList = null;

        if (comparator != null) {
            sortedList = new SortedList<T>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            source = addFiltering(filterEdit, filter, source);
        }

        DefaultEventTableModel<T> tableModel = createTableModel(source, baseClass, options);

        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            addSorting(sortedList, table);
        }

        setSelectionModel(source, table);

        if (addStripe) {
            // TableCellRenderer tableCellRenderer =
            // createStripeTableCellRenderer();
            addStripeToTable(table);
        }

        setPreferredWidth(table);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Rows:");
                    for (int selectedRowIndex : table.getSelectedRows()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(String.format(" %d", selectedRowIndex));
                        }
                    }
                    LOGGER.info(". Columns:");
                    for (int selectedColumnIndex : table.getSelectedColumns()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(String.format(" %d", selectedColumnIndex));
                        }
                    }
                }

                for (int selectedRowIndex : table.getSelectedRows()) {
                    rowIndexSelected(selectedRowIndex);
                    T row = tableModel.getElementAt(selectedRowIndex);
                    rowSelected(row);
                }
            }
        });

        NumberFormat paymentFormat = NumberFormat.getCurrencyInstance();
        JTextField textField = new JFormattedTextField(paymentFormat);
        TableCellEditor editor = new PriceCellEditor(textField);
        
        table.setDefaultEditor(Price.class, editor);
        
        return table;
    }

    protected void rowSelected(T row) {
        LOGGER.info("row=" + row);
        this.selectedRow = row;

    }

    protected abstract TableCellRenderer createStripeTableCellRenderer();

    /**
     * Adds the filtering.
     *
     * @param <T>
     *            the generic type
     * @param filterEdit
     *            the filter edit
     * @param filter
     *            the filter
     * @param source
     *            the source
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
     * @param <T>
     *            the generic type
     * @param sortedList
     *            the sorted list
     * @param table
     *            the table
     */
    private static <T> void addSorting(SortedList<T> sortedList, JTable table) {
        @SuppressWarnings("unused")
        TableComparatorChooser<T> tableSorter = TableComparatorChooser.install(table, sortedList,
                AbstractTableComparatorChooser.SINGLE_COLUMN);
    }

    /**
     * Sets the selection model.
     *
     * @param <T>
     *            the generic type
     * @param source
     *            the source
     * @param table
     *            the table
     */
    private static <T> void setSelectionModel(EventList<T> source, JTable table) {
        DefaultEventSelectionModel<T> eventSelectionModel = createEventSelectionModel(source);
        table.setSelectionModel(eventSelectionModel);
    }

    /**
     * Creates the event selection model.
     *
     * @param <T>
     *            the generic type
     * @param source
     *            the source
     * @return the default event selection model
     */
    private static <T> DefaultEventSelectionModel<T> createEventSelectionModel(EventList<T> source) {
        DefaultEventSelectionModel<T> eventSelectionModel = new DefaultEventSelectionModel<T>(
                GlazedListsSwing.swingThreadProxyList(source));
        return eventSelectionModel;
    }

    /**
     * Adds the stripe to table.
     *
     * @param table
     *            the table
     */
    private void addStripeToTable(JTable table) {
        // TableCellRenderer renderer = null;
        int cols = table.getColumnModel().getColumnCount();
        for (int i = 0; i < cols; i++) {
            TableCellRenderer renderer = createStripeTableCellRenderer();

            // renderer = new StripedTableRenderer() {
            // /**
            // *
            // */
            // private static final long serialVersionUID = 1L;
            //
            // @Override
            // public void setCellHorizontalAlignment(int column) {
            // super.setCellHorizontalAlignment(column);
            // if ((column == 0) || (column == 2) || (column == 3)) {
            // setHorizontalAlignment(SwingConstants.RIGHT);
            // }
            // }
            // };
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    /**
     * Creates the viewport view.
     * 
     * @param eventList
     *            the price list
     * @param filterEdit
     *            the filter edit
     * @param baseClass
     *            the base class
     * @return the j table
     */
    private JTable createViewportView(EventList<T> eventList, JTextField filterEdit, Class<T> baseClass, PriceTableViewOptions options) {
        JTable table = createTable(eventList, comparator, filterEdit, filter, baseClass, options, addStripe);
        // table.setFillsViewportHeight(true);

        PopupListener popupListener = new PopupListener(popupMenu);

        // Add the listener to the JTable:
        table.addMouseListener(popupListener);
        // Add the listener specifically to the header:
        // table.getTableHeader().addMouseListener(popupListener);

        return table;
    }

    protected void rowIndexSelected(int selectedRowIndex) {
        LOGGER.info("selectedRowIndex=" + selectedRowIndex);

    }

    /**
     * Gets the popup menu.
     *
     * @return the popup menu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    protected DefaultEventTableModel<T> createTableModel(EventList<T> source, Class<T> baseClass, PriceTableViewOptions options) {
        final String[] propertyNames = options.getPropertyNames();
        final String[] columnLabels = options.getColumnLabels();
        final boolean[] editable = options.getEditable();
        LOGGER.info("propertyNames=" + Arrays.toString(propertyNames));
        LOGGER.info("columnLabels=" + Arrays.toString(columnLabels));
        LOGGER.info("editable=" + Arrays.toString(editable));
        BeanTableFormat<T> tableFormat = new BeanTableFormat<T>(baseClass, propertyNames, columnLabels, editable);
        DefaultEventTableModel<T> tableModel = new DefaultEventTableModel<T>(GlazedListsSwing.swingThreadProxyList(source),
                tableFormat);
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public T getSelectedRow() {
        return selectedRow;
    }

}