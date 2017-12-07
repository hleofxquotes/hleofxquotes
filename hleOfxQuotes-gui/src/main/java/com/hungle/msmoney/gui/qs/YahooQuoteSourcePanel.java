package com.hungle.msmoney.gui.qs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.gui.PopupListener;
import com.hungle.msmoney.gui.action.GetQuotesAction;
import com.hungle.msmoney.gui.action.OpenAction;
import com.hungle.msmoney.gui.action.SaveAsAction;
import com.hungle.msmoney.gui.menu.EditCleanupAction;
import com.hungle.msmoney.gui.menu.EditCopyAction;
import com.hungle.msmoney.gui.menu.EditCutAction;
import com.hungle.msmoney.gui.menu.EditPasteAction;
import com.hungle.msmoney.gui.menu.EditYahoServerAction;
import com.hungle.msmoney.gui.menu.MenuUtils;
import com.hungle.msmoney.gui.task.ShowDialogTask;
import com.hungle.msmoney.qs.DefaultQuoteSource;
import com.hungle.msmoney.qs.QuoteSource;
import com.hungle.msmoney.qs.QuoteSourceListener;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.yahoo.YahooQuotesGetter;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooQuoteSourcePanel.
 */
public class YahooQuoteSourcePanel extends JPanel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(YahooQuoteSourcePanel.class);

    /** The Constant PREF_YAHOO_QUOTE_SERVER. */
    public static final String QUOTE_SERVER_PREFS_KEY = "yahooQuoteServer";

    /** The Constant STOCK_SYMBOLS_PREF_KEY. */
    private static final String STOCK_SYMBOLS_PREF_KEY = null;

    /** The prefs. */
    private final Preferences prefs;

    /** The parent quote source listener. */
    private final QuoteSourceListener parentQuoteSourceListener;

    /** The thread pool. */
    private final ExecutorService threadPool;

    /** The set tool tip text. */
    private boolean setToolTipText = false;

    /** The stock symbols view. */
    private JTextArea stockSymbolsView;

    /** The listener. */
    protected GetQuotesProgressMonitor listener;

    /** The progress bar. */
    private JProgressBar progressBar;

    /** The quote server. */
    private String quoteServer;

    /** The update button. */
    private JButton updateButton;

    /** The quote source listener. */
    private QuoteSourceListener quoteSourceListener;

    /** The stock symbols pref key. */
    private String stockSymbolsPrefKey = null;

    /** The fx symbols. */
    protected List<AbstractStockPrice> fxSymbols;

    /** The quote source. */
    private final DefaultQuoteSource quoteSource = new DefaultYahooQuoteSource();

    /**
     * Instantiates a new yahoo quote source panel.
     *
     * @param gui
     *            the gui
     * @param stockSymbolsPrefKey
     *            the stock symbols pref key
     * @wbp.parser.constructor
     */
    public YahooQuoteSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super();
        this.prefs = GUI.getPrefs();
        this.threadPool = gui.getThreadPool();
        this.parentQuoteSourceListener = gui.getQuoteSourceListener();
        if (this.threadPool == null) {
            LOGGER.warn("YahooQuoteSourcePanel is constructed with this.threadPool=null");
        }
        this.setQuoteServer(getPrefs().get(YahooQuoteSourcePanel.QUOTE_SERVER_PREFS_KEY, YahooQuotesGetter.DEFAULT_HOST));
        this.quoteSourceListener = new QuoteSourceListener() {

            @Override
            public void stockPricesLookupStarted(QuoteSource quoteSource, List<String> stockSymbols) {
                if (parentQuoteSourceListener != null) {
                    parentQuoteSourceListener.stockPricesLookupStarted(quoteSource, stockSymbols);
                }
            }

            @Override
            public void stockSymbolsStringReceived(QuoteSource quoteSource, String lines) {
                getStockSymbolsView().setText(lines);
                getStockSymbolsView().setCaretPosition(0);
                if (parentQuoteSourceListener != null) {
                    parentQuoteSourceListener.stockSymbolsStringReceived(quoteSource, lines);
                }
            }

            @Override
            public void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
                if (parentQuoteSourceListener != null) {
                    parentQuoteSourceListener.stockPricesReceived(quoteSource, stockPrices);
                }
            }
        };
        this.stockSymbolsPrefKey = stockSymbolsPrefKey;
        
        createView();
    }

    /**
     * Instantiates a new yahoo quote source panel.
     *
     * @param gui
     *            the gui
     */
    public YahooQuoteSourcePanel(GUI gui) {
        this(gui, STOCK_SYMBOLS_PREF_KEY);
    }

    /**
     * Creates the view.
     */
    private void createView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createView()");
        }

        JPanel view = this;
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JTextArea textArea = new JTextArea();
        textArea = new JTextArea();
        String stockSymbols = OfxUtils.retrieveStockSymbols(getPrefs(), stockSymbolsPrefKey);
        if ((stockSymbols != null) && (stockSymbols.length() > 0)) {
            textArea.setText(stockSymbols);
            textArea.setCaretPosition(0);
        }
        if (setToolTipText) {
            textArea.setToolTipText("Enter stock symbols (for example: IBM,CRM)");
        }

        addPopupMenu(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Symbols"));
        this.setStockSymbolsView(textArea);

        view.add(scrollPane, BorderLayout.CENTER);

        view.add(createCommandView(), BorderLayout.SOUTH);
    }

    /**
     * Adds the popup menu.
     *
     * @param textArea
     *            the text area
     * @return the j popup menu
     */
    protected JPopupMenu addPopupMenu(final JTextArea textArea) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> addPopupMenu()");
        }

        JPopupMenu popup = new JPopupMenu();
        JMenu menu = null;
        JMenuItem menuItem = null;

        menu = new JMenu("File");
        menuItem = new JMenuItem(new OpenAction(this, "Open", quoteSourceListener, threadPool));
        menu.add(menuItem);
        popup.add(menu);

        menuItem = new JMenuItem(new SaveAsAction(this, "Save As"));
        menu.add(menuItem);
        popup.add(menu);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> EditYahoServerAction");
        }
        menu = new JMenu("Edit");
        menuItem = new JMenuItem(new EditCutAction("Cut", textArea));
        menu.add(menuItem);
        menuItem = new JMenuItem(new EditCopyAction("Copy", textArea));
        menu.add(menuItem);
        menuItem = new JMenuItem(new EditPasteAction("Paste", textArea));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditCleanupAction(this, "Clean up", textArea));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditYahoServerAction(this, "Yahoo Server"));
        menu.add(menuItem);

        popup.add(menu);

        popup.addSeparator();

        menu = MenuUtils.createBookmarksMenu(quoteSourceListener, threadPool);
        if (menu != null) {
            popup.add(menu);
        }
        menu = MenuUtils.createSamplesMenu(quoteSourceListener, threadPool);
        if (menu != null) {
            popup.add(menu);
        }

        // Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener(popup);
        textArea.addMouseListener(popupListener);

        return popup;
    }

    /**
     * Creates the command view.
     *
     * @return the component
     */
    private Component createCommandView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createCommandView()");
        }

        JPanel view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.LINE_AXIS));
        // view.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JButton button = new JButton(new GetQuotesAction(this, "Update prices"));
        if (setToolTipText) {
            button.setToolTipText("Update stock prices");
        }
        view.add(button);
        InputMap im = button.getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
        this.updateButton = button;

        view.add(Box.createHorizontalStrut(3));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        view.add(progressBar);
        this.progressBar = progressBar;

        this.listener = new GetQuotesProgressMonitor(this.progressBar);

        return view;
    }

    /**
     * The Class DefaultYahooQuoteSource.
     */
    protected class DefaultYahooQuoteSource extends DefaultQuoteSource {

        /*
         * (non-Javadoc)
         * 
         * @see com.le.tools.moneyutils.ofx.quotes.DefaultQuoteSource#
         * getExchangeRates()
         */
        @Override
        public List<AbstractStockPrice> getExchangeRates() {
            return fxSymbols;
        }
    }

    /**
     * Notify no stock request.
     */
    private void notifyNoStockRequest() {
        LOGGER.warn("No stocks requested.");
        JOptionPane.showMessageDialog(YahooQuoteSourcePanel.this, "Please enter stock symbols.", "Missing input",
                JOptionPane.WARNING_MESSAGE);
        updateButton.setEnabled(true);
        progressBar.setValue(100);
        setCursor(null);
    }

    /**
     * Stock prices lookup started.
     * @param stockSymbols 
     */
    private void stockPricesLookupStarted(List<String> stockSymbols) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesLookupStarted(quoteSource, stockSymbols);
        }
    }

    /**
     * Stock prices received.
     *
     * @param stockPrices
     *            the stock prices
     */
    private void stockPricesReceived(List<AbstractStockPrice> stockPrices) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> stockPricesReceived");
        }
        this.quoteSourceListener.stockPricesReceived(quoteSource, stockPrices);
    }

    /**
     * Gets the quote server.
     *
     * @return the quote server
     */
    public String getQuoteServer() {
        return quoteServer;
    }

    /**
     * Gets the stock quotes and notify.
     *
     * @param stockSymbols
     *            the stock symbols
     * @param stocksString
     *            the stocks string
     * @return the stock quotes and notify
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void getStockQuotesAndNotify(final List<String> stockSymbols, final String stocksString) throws IOException {
        stockPricesLookupStarted(stockSymbols);
        List<AbstractStockPrice> stockPrices = null;
        try {
            stockPrices = getStockQuotes(stockSymbols);
        } finally {
            OfxUtils.storeStockSymbols(getPrefs(), stockSymbolsPrefKey, stocksString);
            stockPricesReceived(stockPrices);
        }
    }

    /**
     * Gets the stock quotes.
     *
     * @param stockSymbols
     *            the stock symbols
     * @return the stock quotes
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        List<AbstractStockPrice> stockPrices;
        AbstractHttpQuoteGetter quoteGetter = getHttpQuoteGetter();
        if (getQuoteServer() != null) {
            quoteGetter.setHost(getQuoteServer());
        }
        try {
            stockPrices = quoteGetter.getQuotes(stockSymbols, listener);
            this.fxSymbols = quoteGetter.getFxSymbols();
        } finally {

        }
        return stockPrices;
    }

    protected AbstractHttpQuoteGetter getHttpQuoteGetter() {
        return new YahooQuotesGetter();
    }

    /**
     * Gets the fx symbols.
     *
     * @return the fx symbols
     */
    public List<AbstractStockPrice> getFxSymbols() {
        return fxSymbols;
    }

    /**
     * Gets the quote source.
     *
     * @return the quote source
     */
    public DefaultQuoteSource getQuoteSource() {
        return quoteSource;
    }

    /**
     * Gets the quotes.
     *
     * @return the quotes
     */
    public void getQuotes() {
        LOGGER.info("> getQuotes");

        updateButton.setEnabled(false);
        progressBar.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String text = getStockSymbolsView().getText();

        if (text == null) {
            notifyNoStockRequest();
            return;
        }

        text = text.trim();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stocks=" + text);
        }
        if (text.length() <= 0) {
            notifyNoStockRequest();
            return;
        }

        final String stocksString = text;
        List<String> stockSymbols = null;
        try {
            stockSymbols = QuoteSourceUtils.toStockSymbols(stocksString);
        } catch (IOException e) {
            Runnable doRun = new ShowDialogTask(YahooQuoteSourcePanel.this, e, JOptionPane.ERROR_MESSAGE);
            doRun.run();
            updateButton.setEnabled(true);
            progressBar.setValue(100);
            setCursor(null);
            return;
        }

        if ((stockSymbols == null) || (stockSymbols.size() <= 0)) {
            notifyNoStockRequest();
            return;
        }

        final List<String> stockSymbols2 = stockSymbols;
        Runnable command = new Runnable() {

            @Override
            public void run() {
                try {
                    Exception exception = null;

                    try {
                        getStockQuotesAndNotify(stockSymbols2, stocksString);
                    } catch (IOException e) {
                        LOGGER.warn(e);
                        exception = e;
                    }

                    if (exception != null) {
                        Runnable doRun = new ShowDialogTask(YahooQuoteSourcePanel.this, exception, JOptionPane.ERROR_MESSAGE);
                        SwingUtilities.invokeLater(doRun);
                    }
                } finally {
                    Runnable doRun = new Runnable() {

                        @Override
                        public void run() {
                            updateButton.setEnabled(true);
                            progressBar.setValue(100);
                            setCursor(null);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }
            }
        };
        threadPool.execute(command);
    }

    public void setQuoteServer(String quoteServer) {
        this.quoteServer = quoteServer;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public JTextArea getStockSymbolsView() {
        return stockSymbolsView;
    }

    public void setStockSymbolsView(JTextArea stockSymbolsView) {
        this.stockSymbolsView = stockSymbolsView;
    }
}
