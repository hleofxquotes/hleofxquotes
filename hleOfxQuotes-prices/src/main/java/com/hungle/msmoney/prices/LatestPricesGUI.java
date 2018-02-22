package com.hungle.msmoney.prices;

import java.awt.Component;
import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.gui.PriceTableViewOptions;
import com.hungle.sunriise.prices.GetLatestSecurityPrices.Result;

import ca.odell.glazedlists.BasicEventList;

public class LatestPricesGUI extends AbstractLatestPricesGUI {
    private static final Logger LOGGER = Logger.getLogger(LatestPricesGUI.class);
    
    private BasicEventList<LatestPriceBean> latestPriceBeans;

    public LatestPricesGUI() {
        super();
    }

    @Override
    protected void initPrefs() {
        super.initPrefs();
        this.prefs = Preferences.userNodeForPackage(LatestPricesGUI.class);

        this.enableFiltering = true;
    }

    protected Component initTableView(JTextField filterEdit) {
        // JTextField filterEdit = new JTextField(10);

        latestPriceBeans = new BasicEventList<LatestPriceBean>();
        Class<LatestPriceBean> baseClass = LatestPriceBean.class;
        PriceTableViewOptions priceTableViewOptions = new PriceTableViewOptions();
        final String propertyName[] = { "symbol", "name", "price", "date", "source" };
        priceTableViewOptions.setPropertyNames(propertyName);
        final String columnLabels[] = { "Symbol", "Name", "Price", "Date", "Source" };
        priceTableViewOptions.setColumnLabels(columnLabels);

        LatestPricesTableView<LatestPriceBean> tableView = new LatestPricesTableView<>(latestPriceBeans, filterEdit, baseClass, priceTableViewOptions);
        return tableView;
    }

    protected void refreshView(final List<Result> results) {
        LOGGER.info("> refresh");
        latestPriceBeans.getReadWriteLock().writeLock().lock();
        try {
            latestPriceBeans.clear();
            for (Result result : results) {
                LatestPriceBean bean = new LatestPriceBean();
                bean.setSymbol(result.getSecurity().getSymbol());
                bean.setName(result.getSecurity().getName());

                bean.setPrice(result.getPrice().getPrice());
                Date date = new Date(result.getPrice().getDate().getTime()) {
                    private SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

                    @Override
                    public String toString() {
                        String str = formatter.format(this);
                        return str;
                    }

                };
                bean.setDate(date);
                bean.setSource(result.getPrice().getSrc());

                latestPriceBeans.add(bean);
            }
        } finally {
            latestPriceBeans.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LatestPricesGUI window = new LatestPricesGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.error(e, e);
                }
            }
        });
    }

}