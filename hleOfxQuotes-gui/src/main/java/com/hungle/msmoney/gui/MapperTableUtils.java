package com.hungle.msmoney.gui;

import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.data.SymbolMapper;
import com.hungle.msmoney.core.data.SymbolMapperEntry;
import com.hungle.msmoney.core.gui.StripedTableRenderer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

public class MapperTableUtils {
    private static final Logger LOGGER = Logger.getLogger(MapperTableUtils.class);

    private MapperTableUtils() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates the mapper table.
     *
     * @param mapper
     *            the mapper
     * @param comparator
     *            the comparator
     * @param filterEdit
     *            the filter edit
     * @param filter
     *            the filter
     * @param addStripe
     *            the add stripe
     * @return the j table
     */
    static final JTable createMapperTable(EventList<SymbolMapperEntry> mapper,
            Comparator<? super SymbolMapperEntry> comparator, JTextField filterEdit,
            TextFilterator<SymbolMapperEntry> filter, boolean addStripe) {
        EventList<SymbolMapperEntry> source = mapper;

        SortedList<SymbolMapperEntry> sortedList = null;
        if (comparator != null) {
            sortedList = new SortedList<SymbolMapperEntry>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            FilterList<SymbolMapperEntry> filterList = null;
            MatcherEditor<SymbolMapperEntry> textMatcherEditor = new TextComponentMatcherEditor<SymbolMapperEntry>(
                    filterEdit, filter);
            filterList = new FilterList<SymbolMapperEntry>(source, textMatcherEditor);
            source = filterList;
        }

        Class beanClass = SymbolMapperEntry.class;
        String propertyNames[] = { "quotesSourceSymbol", "msMoneySymbol", "type" };
        String columnLabels[] = { "Symbol", "MSMoney Symbol", "Type" };
        AdvancedTableFormat tableFormat = new BeanTableFormat(beanClass, propertyNames, columnLabels);
        final TransformedList<SymbolMapperEntry, SymbolMapperEntry> sourceProxyList = GlazedListsSwing
                .swingThreadProxyList(source);
        DefaultEventTableModel<SymbolMapperEntry> tableModel = new DefaultEventTableModel<SymbolMapperEntry>(
                sourceProxyList, tableFormat);
        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        DefaultEventSelectionModel myDefaultEventSelectionModel = new DefaultEventSelectionModel(source);
        table.setSelectionModel(myDefaultEventSelectionModel);

        if (addStripe) {
            // TableCellRenderer striped = new StripedTableCellRenderer(new
            // Color(204, 255, 204), Color.WHITE);
            int cols = table.getColumnModel().getColumnCount();
            for (int i = 0; i < cols; i++) {
                StripedTableRenderer renderer = new StripedTableRenderer();
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }

        // ???
        final int columnCount = 3;
        int[] colWidths = new int[columnCount];
        for (int i = 0; i < colWidths.length; i++) {
            colWidths[i] = -1;
        }
        colWidths = null;
        if (colWidths != null) {
            LOGGER.info("colWidths=" + colWidths);
            Object[] maxWidthValues = TableUtils.calculateMaxWidthValues(colWidths);
            TableUtils.adjustColumnSizes(table, maxWidthValues);
        }

        return table;
    }

    static final void clearMapperTable(EventList<SymbolMapperEntry> mapper) {
        mapper.clear();
    }

    public static final void updateMapperTable(SymbolMapper symbolMapper, EventList<SymbolMapperEntry> list) {
        list.getReadWriteLock().writeLock().lock();
        try {
            list.clear();
            list.addAll(symbolMapper.getEntries());
        } finally {
            list.getReadWriteLock().writeLock().unlock();
        }
    }

}
