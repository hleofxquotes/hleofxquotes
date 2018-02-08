package com.hungle.msmoney.core.gui;

public class PriceTableViewOptions {
//    private static final String DEFAULT_PROPERTY_NAMES[] = { "stockSymbol", "stockName", "lastPrice", "lastTradeDate",
//            "lastTradeTime" };

    private static final String DEFAULT_PROPERTY_NAMES[] = { "stockSymbol", "stockName", "lastPrice",
            "lastTradeDateLocalDate", "lastTradeDateLocalTime" };
    
    private static String DEFAULT_COLUMN_LABELS[] = { "Symbol", "Name", "Price", "Last Trade Date", "Last Trade Time" };
    
    private static boolean DEFAULT_EDITABLES[] = { false, false, false, false, false };

    private boolean convertWhenExport;
    private boolean createImport;
    private boolean createMenu;
    private String[] propertyNames = DEFAULT_PROPERTY_NAMES;
    private String[] columnLabels = DEFAULT_COLUMN_LABELS;
    private boolean[] editable = DEFAULT_EDITABLES;

    public boolean isConvertWhenExport() {
        return convertWhenExport;
    }

    public void setConvertWhenExport(boolean convertWhenExport) {
        this.convertWhenExport = convertWhenExport;
    }

    public boolean isCreateImport() {
        return createImport;
    }

    public void setCreateImport(boolean createImportComponents) {
        this.createImport = createImportComponents;
    }

    public boolean isCreateMenu() {
        return createMenu;
    }

    public void setCreateMenu(boolean createMenu) {
        this.createMenu = createMenu;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyName) {
        this.propertyNames = propertyName;
    }

    public String[] getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(String[] columnLabels) {
        this.columnLabels = columnLabels;
    }

    public boolean[] getEditable() {
        return editable;
    }

    public void setEditable(boolean[] editable) {
        this.editable = editable;
    }

}
