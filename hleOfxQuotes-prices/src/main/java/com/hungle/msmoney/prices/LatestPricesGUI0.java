package com.hungle.msmoney.prices;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.hungle.sunriise.prices.GetLatestSecurityPrices.Result;

public class LatestPricesGUI0 extends AbstractLatestPricesGUI {
    private static final Logger LOGGER = Logger.getLogger(LatestPricesGUI0.class);
    protected JTable table;

    public LatestPricesGUI0() {
        super();
    }

    @Override
    protected void initPrefs() {
        super.initPrefs();
        this.prefs = Preferences.userNodeForPackage(LatestPricesGUI0.class);
    }

    protected Component initTableView(JTextField filterEdit) {
        SimpleTableView tableView = new SimpleTableView();
        this.table = tableView.getTable();
        return tableView;
    }

    protected void refreshView(final List<Result> results) {
        TableModel dataModel = new LatestPricesTableModel(results);
        table.setModel(dataModel);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LatestPricesGUI0 window = new LatestPricesGUI0();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
        });
    }

}