package com.hungle.msmoney.gui;

import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.hungle.msmoney.core.gui.AbstractGlazedListTableView;
import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.gui.StripedTableRenderer;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;

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

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private static final int COL_SYMBOL = 0;

    private static final int COL_NAME = 1;

    private static final int COL_PRICE = 2;

    private static final int COL_LAST_TRADE_DATE = 3;

    private static final int COL_LAST_TRADE_TIME = 4;

    public PriceTableView(EventList<T> priceList, JTextField filterEdit, Class<T> baseClass, PriceTableViewOptions options) {
        super(priceList, filterEdit, baseClass, options);
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
                if ((column == COL_SYMBOL) || (column == COL_PRICE) || (column == COL_LAST_TRADE_DATE)) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
            }
        };
        return renderer;
    }
}
