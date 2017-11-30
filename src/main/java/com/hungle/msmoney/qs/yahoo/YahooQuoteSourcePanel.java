package com.hungle.msmoney.qs.yahoo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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

import org.apache.http.HttpEntity;
import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxUtils;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.AbstractLoadStockSymbolsTask;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.gui.PopupListener;
import com.hungle.msmoney.gui.ShowDialogTask;
import com.hungle.msmoney.qs.DefaultQuoteSource;
import com.hungle.msmoney.qs.QuoteSource;
import com.hungle.msmoney.qs.QuoteSourceListener;
import com.hungle.msmoney.qs.net.AbstractHttpQuoteGetter;
import com.hungle.msmoney.qs.net.GetQuotesListener;

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
    private static final String QUOTE_SERVER_PREFS_KEY = "yahooQuoteServer";

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
    protected String quoteServer;

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
     * @param gui the gui
     * @param stockSymbolsPrefKey the stock symbols pref key
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
        this.quoteServer = prefs.get(YahooQuoteSourcePanel.QUOTE_SERVER_PREFS_KEY, YahooQuotesGetter.DEFAULT_HOST);
        quoteSourceListener = new QuoteSourceListener() {

            @Override
            public void stockPricesLookupStarted(QuoteSource quoteSource) {
                if (parentQuoteSourceListener != null) {
                    parentQuoteSourceListener.stockPricesLookupStarted(quoteSource);
                }
            }

            @Override
            public void stockSymbolsStringReceived(QuoteSource quoteSource, String lines) {
                stockSymbolsView.setText(lines);
                stockSymbolsView.setCaretPosition(0);
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
     * @param gui the gui
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
        String stockSymbols = OfxUtils.retrieveStockSymbols(prefs, stockSymbolsPrefKey);
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
        this.stockSymbolsView = textArea;

        view.add(scrollPane, BorderLayout.CENTER);

        view.add(createCommandView(), BorderLayout.SOUTH);
    }

    /**
     * Creates the samples menu.
     *
     * @return the j menu
     */
    private JMenu createSamplesMenu() {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Samples");

        menuItem = new JMenuItem(new AbstractLoadStockSymbolsTask("NYSE", quoteSourceListener, threadPool) {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected List<String> getStocks() throws IOException {
                return OfxUtils.getNYSEList();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractLoadStockSymbolsTask("NASDAQ", quoteSourceListener, threadPool) {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<String> getStocks() throws IOException {
                return OfxUtils.getNASDAQList();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractLoadStockSymbolsTask("US Mutual Funds", quoteSourceListener, threadPool) {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<String> getStocks() throws IOException {
                return OfxUtils.getUSMFList();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new AbstractLoadStockSymbolsTask("London Stock Exchange", quoteSourceListener, threadPool) {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<String> getStocks() throws IOException {
                return OfxUtils.getLSEList();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();
        // Dogs of the Dow
        menuItem = new JMenuItem(new AbstractLoadStockSymbolsTask("Dogs of the Dow", quoteSourceListener, threadPool) {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<String> getStocks() throws IOException {
                return OfxUtils.getList("dotd.txt");
            }
        });
        menu.add(menuItem);
        return menu;
    }

    /**
     * Creates the bookmarks menu.
     *
     * @return the j menu
     */
    private JMenu createBookmarksMenu() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createBookmarksMenu()");
        }

        File dir = new File("bookmarks");
        if (!dir.isDirectory()) {
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        if (files.length <= 0) {
            return null;
        }

        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Bookmarks");

        Arrays.sort(files);
        for (File file : files) {
            menuItem = new JMenuItem(new LoadBookmarkAction(file, quoteSourceListener, threadPool));
            menu.add(menuItem);
        }
        return menu;
    }

    /**
     * Adds the popup menu.
     *
     * @param textArea the text area
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
        menuItem = new JMenuItem(new OpenAction("Open", quoteSourceListener, threadPool));
        menu.add(menuItem);
        popup.add(menu);

        menuItem = new JMenuItem(new SaveAsAction("Save As"));
        menu.add(menuItem);
        popup.add(menu);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> EditYahoServerAction");
        }
        menu = new JMenu("Edit");
        menuItem = new JMenuItem(new AbstractAction("Cut") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Copy") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Paste") {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new AbstractAction("Clean up") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String currentText = textArea.getText();
				currentText = cleanupText(currentText);
				textArea.setText(currentText);
	            textArea.setCaretPosition(0);
			}

			private String cleanupText(String stocksString) {
				String cleanupText = stocksString;
	            try {
					List<String> stockSymbols = toStockSymbols(stocksString);
					TreeSet<String> cleanupStockSymbols = new TreeSet<String>();
					cleanupStockSymbols.addAll(stockSymbols);
					
					int count = 0;
					StringBuilder sb = new StringBuilder();
					for (String stockSymbol : cleanupStockSymbols) {
						if (count > 0) {
							sb.append("\r\n");
						}
						sb.append(stockSymbol);
						count++;
					}
					cleanupText = sb.toString();
				} catch (IOException e) {
					LOGGER.error(e);
				}
				return cleanupText;
			}
		});
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditYahoServerAction("Yahoo Server"));
        menu.add(menuItem);

        popup.add(menu);

        popup.addSeparator();

        menu = createBookmarksMenu();
        if (menu != null) {
            popup.add(menu);
        }
        menu = createSamplesMenu();
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

        JButton button = new JButton(new GetQuotesAction("Update prices"));
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
        
        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.DefaultQuoteSource#getExchangeRates()
         */
        @Override
        public List<AbstractStockPrice> getExchangeRates() {
            return fxSymbols;
        }
    }

    /**
     * The Class GetQuotesProgressMonitor.
     */
    protected final class GetQuotesProgressMonitor implements GetQuotesListener {
        
        /** The sub task size. */
        private AtomicInteger subTaskSize = new AtomicInteger(0);
        
        /** The completed tasks. */
        private AtomicInteger completedTasks = new AtomicInteger(0);
        
        /** The progress bar. */
        private JProgressBar progressBar = null;

        /**
         * Instantiates a new gets the quotes progress monitor.
         *
         * @param progressBar the progress bar
         */
        public GetQuotesProgressMonitor(JProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#started(java.util.List)
         */
        @Override
        public void started(List<String> stocks) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> STARTED stocks=" + stocks);
            }
        }

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#ended(java.util.List, java.util.List, long)
         */
        @Override
        public void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> ENDED stocks=" + stocks + ", delta=" + delta);
            }
            completedTasks.getAndIncrement();

            Runnable doRun = new Runnable() {

                @Override
                public void run() {
                    int percentage = (completedTasks.get() * 100) / subTaskSize.get();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("  progressBar % " + percentage);
                    }
                    if (progressBar != null) {
                        progressBar.setValue(percentage);
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        }

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#setSubTaskSize(int)
         */
        @Override
        public void setSubTaskSize(int size) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("setSubTaskSize=" + size);
            }
            this.subTaskSize.set(size);
            this.completedTasks.set(0);
        }

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.GetQuotesListener#httpEntityReceived(org.apache.http.HttpEntity)
         */
        @Override
        public void httpEntityReceived(HttpEntity entity) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * The Class OpenAction.
     */
    private final class OpenAction extends AbstractLoadStockSymbolsTask {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /** The fc. */
        private JFileChooser fc = null;
        
        /** The stocks. */
        private List<String> stocks = null;

        /**
         * Instantiates a new open action.
         *
         * @param name the name
         * @param listener the listener
         * @param threadPool the thread pool
         */
        public OpenAction(String name, QuoteSourceListener listener, ExecutorService threadPool) {
            super(name, listener, threadPool);
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

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if (fc == null) {
                initFileChooser();
            }
            if (fc.showOpenDialog(YahooQuoteSourcePanel.this) == JFileChooser.CANCEL_OPTION) {
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

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#getStocks()
         */
        @Override
        protected List<String> getStocks() throws IOException {
            return stocks;
        }
    }

    /**
     * The Class SaveAsAction.
     */
    private final class SaveAsAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /** The fc. */
        private JFileChooser fc = null;

        /**
         * Instantiates a new save as action.
         *
         * @param name the name
         */
        public SaveAsAction(String name) {
            super(name);
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

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if (fc == null) {
                initFileChooser();
            }
            if (fc.showSaveDialog(YahooQuoteSourcePanel.this) == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File outFile = fc.getSelectedFile();
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(outFile));
                writer.write(stockSymbolsView.getText());
                LOGGER.info("Save stock symbols to file=" + outFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(YahooQuoteSourcePanel.this, e.getMessage(), "Failed To Save To File", JOptionPane.ERROR_MESSAGE);
                LOGGER.error(e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } finally {
                        writer = null;
                    }
                }
            }
        }
    }

    /**
     * The Class EditYahoServerAction.
     */
    private final class EditYahoServerAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new edits the yaho server action.
         *
         * @param name the name
         */
        public EditYahoServerAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String> keys = YahooQuotesGetter.QUOTE_HOSTS.keySet();
            String[] possibilities = new String[keys.size()];
            int i = 0;
            for (String key : keys) {
                possibilities[i++] = key;
            }
            Icon icon = null;
            String s = (String) JOptionPane.showInputDialog(YahooQuoteSourcePanel.this, "Current: " + quoteServer + "\n" + "Available:",
                    "Set Yahoo Quote Server", JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

            // If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                String value = YahooQuotesGetter.QUOTE_HOSTS.get(s);
                LOGGER.info("Selected new Yahoo Quote Server: " + value);
                quoteServer = value;
                prefs.put(YahooQuoteSourcePanel.QUOTE_SERVER_PREFS_KEY, quoteServer);
            } else {
            }

        }
    }

    /**
     * The Class LoadBookmarkAction.
     */
    private final class LoadBookmarkAction extends AbstractLoadStockSymbolsTask {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /** The file. */
        private File file;

        /**
         * Instantiates a new load bookmark action.
         *
         * @param file the file
         * @param listener the listener
         * @param threadPool the thread pool
         */
        public LoadBookmarkAction(File file, QuoteSourceListener listener, ExecutorService threadPool) {
            super(file.getName(), listener, threadPool);
            this.file = file;
        }

        /* (non-Javadoc)
         * @see com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask#getStocks()
         */
        @Override
        protected List<String> getStocks() throws IOException {
            List<String> stocks = new ArrayList<String>();
            OfxUtils.addToList(file.toURI().toURL(), stocks);
            return stocks;
        }

    }

    /**
     * The Class GetQuotesAction.
     */
    private final class GetQuotesAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new gets the quotes action.
         *
         * @param name the name
         */
        GetQuotesAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            getQuotes();
        }
    }

    /**
     * Notify no stock request.
     */
    private void notifyNoStockRequest() {
        LOGGER.warn("No stocks requested.");
        JOptionPane.showMessageDialog(YahooQuoteSourcePanel.this, "Please enter stock symbols.", "Missing input", JOptionPane.WARNING_MESSAGE);
        updateButton.setEnabled(true);
        progressBar.setValue(100);
        setCursor(null);
    }

    /**
     * Stock prices lookup started.
     */
    private void stockPricesLookupStarted() {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesLookupStarted(quoteSource);
        }
    }

    /**
     * Stock prices received.
     *
     * @param stockPrices the stock prices
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
     * @param stockSymbols the stock symbols
     * @param stocksString the stocks string
     * @return the stock quotes and notify
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void getStockQuotesAndNotify(final List<String> stockSymbols, final String stocksString) throws IOException {
        stockPricesLookupStarted();
        List<AbstractStockPrice> stockPrices = null;
        try {
            stockPrices = getStockQuotes(stockSymbols);
        } finally {
            OfxUtils.storeStockSymbols(prefs, stockSymbolsPrefKey, stocksString);
            stockPricesReceived(stockPrices);
        }
    }

    /**
     * Gets the stock quotes.
     *
     * @param stockSymbols the stock symbols
     * @return the stock quotes
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        List<AbstractStockPrice> stockPrices;
        AbstractHttpQuoteGetter quoteGetter = getHttpQuoteGetter();
        if (quoteServer != null) {
            quoteGetter.setHost(quoteServer);
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
     * To stock symbols.
     *
     * @param stocksString the stocks string
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<String> toStockSymbols(String stocksString) throws IOException {
        List<String> stocks = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new StringReader(stocksString));
            OfxUtils.addToList(reader, stocks);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
        return stocks;
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
    protected void getQuotes() {
        LOGGER.info("> getQuotes");
        
        updateButton.setEnabled(false);
        progressBar.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String text = stockSymbolsView.getText();

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
            stockSymbols = toStockSymbols(stocksString);
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
}
