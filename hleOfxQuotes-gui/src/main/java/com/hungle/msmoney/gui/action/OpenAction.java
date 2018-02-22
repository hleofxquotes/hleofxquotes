package com.hungle.msmoney.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.gui.qs.QuoteSourcePanel;
import com.hungle.msmoney.qs.AbstractLoadStockSymbolsTask;
import com.hungle.msmoney.qs.QuoteSourceListener;

/**
 * The Class OpenAction.
 */
public final class OpenAction extends AbstractLoadStockSymbolsTask {
    private static final Logger LOGGER = Logger.getLogger(OpenAction.class);

    /**
     * 
     */
    private final QuoteSourcePanel yahooQuoteSourcePanel;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fc. */
    private JFileChooser fc = null;

    /** The stocks. */
    private List<String> stocks = null;

    /**
     * Instantiates a new open action.
     *
     * @param name
     *            the name
     * @param listener
     *            the listener
     * @param threadPool
     *            the thread pool
     * @param yahooQuoteSourcePanel
     *            TODO
     */
    public OpenAction(QuoteSourcePanel yahooQuoteSourcePanel, String name, QuoteSourceListener listener,
            ExecutorService threadPool) {
        super(name, listener, threadPool);
        this.yahooQuoteSourcePanel = yahooQuoteSourcePanel;
    }

    /**
     * Inits the file chooser.
     */
    private void initFileChooser() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> pre creating JFileChooser");
        }
        this.fc = new JFileChooser(".");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> post creating JFileChooser");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#
     * actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (fc == null) {
            initFileChooser();
        }
        if (fc.showOpenDialog(this.yahooQuoteSourcePanel) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File inFile = fc.getSelectedFile();
        stocks = new ArrayList<String>();
        try {
            OfxUtils.addToList(inFile.toURI().toURL(), stocks);
        } catch (MalformedURLException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        }

        super.actionPerformed(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#getStocks
     * ()
     */
    @Override
    protected List<String> getStocks() throws IOException {
        return stocks;
    }
}