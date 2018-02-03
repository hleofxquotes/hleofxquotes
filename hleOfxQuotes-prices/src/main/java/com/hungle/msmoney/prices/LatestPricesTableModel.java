package com.hungle.msmoney.prices;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.hungle.sunriise.mnyobject.EnumSecurityPriceSrc;
import com.hungle.sunriise.mnyobject.Security;
import com.hungle.sunriise.mnyobject.SecurityPrice;
import com.hungle.sunriise.prices.GetLatestSecurityPrices.Result;

public class LatestPricesTableModel extends AbstractTableModel {
    private static final String[] columnNames = { "Symbol", "Name", "Price", "Date", "Source" };
    private static final Class[] columnClasses = { String.class, String.class, Double.class, Date.class, EnumSecurityPriceSrc.class };

    private List<Result> results;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columnClasses[column];
    }

    public LatestPricesTableModel(List<Result> results) {
        this.results = results;
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        Result result = results.get(rowIndex);
        Security security = null;
        SecurityPrice price = null;
        switch (columnIndex) {
        case 0:
            security = result.getSecurity();
            value = security.getSymbol();
            break;
        case 1:
            security = result.getSecurity();
            value = security.getName();
            break;
        case 2:
            price = result.getPrice();
            value = price.getPrice();
            break;
        case 3:
            price = result.getPrice();
            value = price.getDate();
            break;
            
        case 4:
            price = result.getPrice();
            value = price.getSrc();
            break;
        default:
            break;
        }

        return value;
    }
}
