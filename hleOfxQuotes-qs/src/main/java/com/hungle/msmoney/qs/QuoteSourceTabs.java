package com.hungle.msmoney.qs;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class QuoteSourceTabs.
 */
public class QuoteSourceTabs extends JTabbedPane {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(QuoteSourceTabs.class);

    /**
     * Instantiates a new quote source tabs.
     */
    public QuoteSourceTabs() {
        super();
        initView();
    }

    /**
     * Inits the view.
     */
    private void initView() {
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                int selectedQuoteSource = p.getSelectedIndex();
                log.info("selectedQuoteSource=" + selectedQuoteSource);
                Component component = p.getSelectedComponent();
                QuoteSource quoteSource = null;
                // stockPricesLookupStarted(quoteSource);
            }
        });
        // JPopupMenu popup = new JPopupMenu();
        // popup.addPopupMenuListener(new PopupMenuListener() {
        // public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // int selected = tabbedPane.getSelectedIndex();
        // boolean enabled = tabbedPane.isEnabledAt(selected);
        // log.info("selected=" + selected + ", enabled=" + enabled);
        // }
        //
        // public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // }
        //
        // public void popupMenuCanceled(PopupMenuEvent e) {
        // }
        // });
        // popup.add(new AbstractAction("TODO") {
        // public void actionPerformed(ActionEvent e) {
        // }
        // });
        // tabbedPane.addMouseListener(new PopupListener(popup));

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooSourceView");
        }
        // tabbedPane.addTab("Yahoo", createYahooSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooApiSourceView");
        }
        // tabbedPane.addTab("Yahoo Options", createYahooApiSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createFtDotComSourceView");
        }
        // tabbedPane.addTab("ft.com", createFtDotComSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createGoogleSourceView");
        }
        // tabbedPane.addTab("Google", createGoogleSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooHistoricalSourceView");
        }
        // tabbedPane.addTab("Yahoo Historical",
        // createYahooHistoricalSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createBloombergSourceView");
        }
        // tabbedPane.addTab("Bloomberg", createBloombergSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createTIAACREFQuoteSourceView");
        }
        // tabbedPane.addTab("Scholarshare", createTIAACREFQuoteSourceView());

//        this.setSelectedIndex(0);
    }

}
