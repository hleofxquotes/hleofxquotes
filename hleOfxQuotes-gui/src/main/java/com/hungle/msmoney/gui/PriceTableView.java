package com.hungle.msmoney.gui;

import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.github.lgooddatepicker.tableeditors.DateTableEditor;
import com.github.lgooddatepicker.tableeditors.TimeTableEditor;
import com.hungle.msmoney.core.gui.AbstractGlazedListTableView;
import com.hungle.msmoney.core.gui.PriceCellEditor;
import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.gui.StripedTableRenderer;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.core.stockprice.Price;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;

// TODO: Auto-generated Javadoc
/**
 * The Class PriceTableView.
 *
 * @param <T>
 *            the generic type
 */
public class PriceTableView<T extends AbstractStockPrice> extends AbstractGlazedListTableView<T> {
    private static final Logger LOGGER = Logger.getLogger(PriceCellEditor.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private static final int COL_SYMBOL = 0;

    private static final int COL_NAME = 1;

    private static final int COL_PRICE = 2;

    private static final int COL_LAST_TRADE_DATE = 3;

    private static final int COL_LAST_TRADE_TIME = 4;

    public PriceTableView(EventList<T> priceList, JTextField filterEdit, Class<T> baseClass,
            PriceTableViewOptions options) {
        super(priceList, filterEdit, baseClass, options);
    }

    @Override
    protected String getToolTipText(TableModel tableModel, MouseEvent mouseEvent, int rowIndex, int columnIndex, int realColumnIndex,
            String defaultToolTipText) {
        
        String toolTipText = defaultToolTipText;
        
//        if (realColumnIndex == COL_LAST_TRADE_TIME) { 
//            T stockPrice = getEventList().get(rowIndex);
//            if (stockPrice != null) {
//                String qsName = stockPrice.getQuoteSourceName();
//                if (qsName != null) {
//                    toolTipText = qsName;
//                }
//            }
//        }
        
        return toolTipText;
    }

    protected void setPreferredWidth(JTable table) {
        table.getColumnModel().getColumn(COL_SYMBOL).setPreferredWidth(25);
        table.getColumnModel().getColumn(COL_NAME).setPreferredWidth(90);
        table.getColumnModel().getColumn(COL_PRICE).setPreferredWidth(80);
        table.getColumnModel().getColumn(COL_LAST_TRADE_DATE).setPreferredWidth(50);
        table.getColumnModel().getColumn(COL_LAST_TRADE_TIME).setPreferredWidth(50);
    }

    protected TextFilterator<T> createFilter() {
        return new TextFilterator<T>() {
            @Override
            public void getFilterStrings(List<String> list, T bean) {
                list.add(bean.getStockName());
                list.add(bean.getStockSymbol());
            }
        };
    }

    protected Comparator<T> createComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T b1, T b2) {
                return b1.getStockSymbol().compareTo(b2.getStockSymbol());
            }
        };
    }

    @Override
    protected TableCellRenderer createStripeTableCellRenderer() {
        StripedTableRenderer renderer = new StripedTableRenderer() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void setCellHorizontalAlignment(int column) {
                super.setCellHorizontalAlignment(column);
                if (/*(column == COL_SYMBOL) || */ 
                        (column == COL_PRICE) 
                        || (column == COL_LAST_TRADE_DATE)
                        || (column == COL_LAST_TRADE_TIME)
                        ) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
            }
        };
        return renderer;
    }

    @Override
    protected void setDefaultEditor(JTable table) {
        super.setDefaultEditor(table);

        NumberFormat paymentFormat = NumberFormat.getCurrencyInstance();
        JTextField textField = new JFormattedTextField(paymentFormat);

        TableCellEditor priceEditor = new PriceCellEditor<T>(textField, this) {

            @Override
            protected Object convertRowToType(T row, Object value) {
                Price price = null;
                if (value instanceof String) {
                    try {
                        Double dPrice = Double.valueOf((String) value);
                        price = row.getLastPrice().clonePrice();
                        price.setPrice(dPrice);
                    } catch (NumberFormatException e) {
                        LOGGER.warn(e);
                    }
                } else {
                    LOGGER.warn("Cannot convert value=" + value + " into a Price.");
                }
                return price;
            }
        };
        table.setDefaultEditor(Price.class, priceEditor);

        table.setDefaultEditor(LocalDate.class, new DateTableEditor());
        table.setDefaultRenderer(LocalDate.class, new DateTableEditor());

        table.setDefaultEditor(LocalTime.class, new TimeTableEditor());
        table.setDefaultRenderer(LocalTime.class, new TimeTableEditor());

    }
}
