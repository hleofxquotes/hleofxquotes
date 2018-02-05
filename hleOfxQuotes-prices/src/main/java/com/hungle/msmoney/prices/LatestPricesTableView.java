package com.hungle.msmoney.prices;

import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.gui.AbstractGlazedListTableView;
import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.msmoney.core.gui.StripedTableRenderer;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;

public class LatestPricesTableView<T extends LatestPriceBean> extends AbstractGlazedListTableView<T> {
    private static final Logger LOGGER = Logger.getLogger(LatestPricesTableView.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LatestPricesTableView(EventList<T> eventList, JTextField filterEdit, Class<T> baseClass, PriceTableViewOptions options) {
        super(eventList, filterEdit, baseClass, options);
    }

    @Override
    protected TextFilterator<T> createFilter() {
        return new TextFilterator<T>() {
            @Override
            public void getFilterStrings(List<String> list, T bean) {
                list.add(bean.getName());
                list.add(bean.getSymbol());
            }
        };
    }

    @Override
    protected Comparator<T> createComparator() {
        return new Comparator<T>() {
            @Override
            public int compare(T b1, T b2) {
                String str1 = b1.getSymbol();
                if (str1 == null) {
                    str1 = "";
                }
                String str2 = b2.getSymbol();
                if (str2 == null) {
                    str2 = "";
                }
                int rv = str1.compareTo(str2);
                if (rv == 0) {
                    str1 = b1.getName();
                    if (str1 == null) {
                        str1 = "";
                    }
                    str2 = b2.getName();
                    if (str2 == null) {
                        str2 = "";
                    }
                }
                return str1.compareTo(str2);
            }
        };
    }

    @Override
    protected void setPreferredWidth(JTable table) {
        // TODO Auto-generated method stub
    }

    @Override
    protected TableCellRenderer createStripeTableCellRenderer() {
        final StripedTableRenderer stripedTableRenderer = new StripedTableRenderer() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void setCellHorizontalAlignment(int column) {
                super.setCellHorizontalAlignment(column);
                if ((column == 0) || (column == 2) || (column == 3) || (column == 4)) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
            }    
        };
        return stripedTableRenderer;
    }

}
