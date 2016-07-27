package com.le.tools.moneyutils.yahoo;

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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

import com.le.tools.moneyutils.ofx.quotes.AbstractLoadStockSymbolsTask;
import com.le.tools.moneyutils.ofx.quotes.DefaultQuoteSource;
import com.le.tools.moneyutils.ofx.quotes.GUI;
import com.le.tools.moneyutils.ofx.quotes.GetQuotesListener;
import com.le.tools.moneyutils.ofx.quotes.OfxUtils;
import com.le.tools.moneyutils.ofx.quotes.PopupListener;
import com.le.tools.moneyutils.ofx.quotes.QuoteSource;
import com.le.tools.moneyutils.ofx.quotes.QuoteSourceListener;
import com.le.tools.moneyutils.ofx.quotes.ShowDialogTask;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class YahooQuoteSourcePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(YahooQuoteSourcePanel.class);

    private static final String PREF_YAHOO_QUOTE_SERVER = "yahooQuoteServer";

    private final Preferences prefs;

    private final QuoteSourceListener parentQuoteSourceListener;

    private final ExecutorService threadPool;

    private boolean setToolTipText = false;

    private JTextArea stockSymbolsView;

    protected GetQuotesProgressMonitor listener;

    private JProgressBar progressBar;

    protected String quoteServer;

    private JButton updateButton;

    private QuoteSourceListener quoteSourceListener;

    private String stockSymbolsPrefKey = null;

    protected List<AbstractStockPrice> fxSymbols;

    private final DefaultQuoteSource quoteSource = new DefaultYahooQuoteSource();

    /**
     * @wbp.parser.constructor
     */
    public YahooQuoteSourcePanel(GUI gui, String stockSymbolsPrefKey) {
        super();
        this.prefs = GUI.getPrefs();
        this.threadPool = gui.getThreadPool();
        this.parentQuoteSourceListener = gui.getQuoteSourceListener();
        if (this.threadPool == null) {
            log.warn("YahooQuoteSourcePanel is constructed with this.threadPool=null");
        }
        this.quoteServer = prefs.get(YahooQuoteSourcePanel.PREF_YAHOO_QUOTE_SERVER, GetYahooQuotes.DEFAULT_HOST);
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

    public YahooQuoteSourcePanel(GUI gui) {
        this(gui, null);
    }

    private void createView() {
        if (log.isDebugEnabled()) {
            log.debug("> createView()");
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

    private JMenu createBookmarksMenu() {
        if (log.isDebugEnabled()) {
            log.debug("> createBookmarksMenu()");
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

    protected JPopupMenu addPopupMenu(final JTextArea textArea) {
        if (log.isDebugEnabled()) {
            log.debug("> addPopupMenu()");
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

        if (log.isDebugEnabled()) {
            log.debug("> EditYahoServerAction");
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

    private Component createCommandView() {
        if (log.isDebugEnabled()) {
            log.debug("> createCommandView()");
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

    protected class DefaultYahooQuoteSource extends DefaultQuoteSource {
        @Override
        public List<AbstractStockPrice> getExchangeRates() {
            return fxSymbols;
        }
    }

    protected final class GetQuotesProgressMonitor implements GetQuotesListener {
        private AtomicInteger subTaskSize = new AtomicInteger(0);
        private AtomicInteger completedTasks = new AtomicInteger(0);
        private JProgressBar progressBar = null;

        public GetQuotesProgressMonitor(JProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void started(List<String> stocks) {
            if (log.isDebugEnabled()) {
                log.debug("> STARTED stocks=" + stocks);
            }
        }

        @Override
        public void ended(List<String> stocks, List<AbstractStockPrice> beans, long delta) {
            if (log.isDebugEnabled()) {
                log.debug("> ENDED stocks=" + stocks + ", delta=" + delta);
            }
            completedTasks.getAndIncrement();

            Runnable doRun = new Runnable() {

                @Override
                public void run() {
                    int percentage = (completedTasks.get() * 100) / subTaskSize.get();
                    if (log.isDebugEnabled()) {
                        log.debug("  progressBar % " + percentage);
                    }
                    if (progressBar != null) {
                        progressBar.setValue(percentage);
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        }

        @Override
        public void setSubTaskSize(int size) {
            if (log.isDebugEnabled()) {
                log.debug("setSubTaskSize=" + size);
            }
            this.subTaskSize.set(size);
            this.completedTasks.set(0);
        }

        @Override
        public void httpEntityReceived(HttpEntity entity) {
            // TODO Auto-generated method stub

        }
    }

    private final class OpenAction extends AbstractLoadStockSymbolsTask {
        private static final long serialVersionUID = 1L;

        private JFileChooser fc = null;
        private List<String> stocks = null;

        public OpenAction(String name, QuoteSourceListener listener, ExecutorService threadPool) {
            super(name, listener, threadPool);
        }

        private void initFileChooser() {
            if (log.isDebugEnabled()) {
                log.debug("> pre creating JFileChooser");
            }
            this.fc = new JFileChooser(".");
            if (log.isDebugEnabled()) {
                log.debug("> post creating JFileChooser");
            }
        }

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
                log.error(e);
            } catch (IOException e) {
                log.error(e);
            }

            super.actionPerformed(event);
        }

        @Override
        protected List<String> getStocks() throws IOException {
            return stocks;
        }
    }

    private final class SaveAsAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        private JFileChooser fc = null;

        public SaveAsAction(String name) {
            super(name);
        }

        private void initFileChooser() {
            if (log.isDebugEnabled()) {
                log.debug("> pre creating JFileChooser");
            }
            this.fc = new JFileChooser(".");
            if (log.isDebugEnabled()) {
                log.debug("> post creating JFileChooser");
            }
        }

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
                log.info("Save stock symbols to file=" + outFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(YahooQuoteSourcePanel.this, e.getMessage(), "Failed To Save To File", JOptionPane.ERROR_MESSAGE);
                log.error(e);
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

    private final class EditYahoServerAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public EditYahoServerAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String> keys = GetYahooQuotes.QUOTE_HOSTS.keySet();
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
                String value = GetYahooQuotes.QUOTE_HOSTS.get(s);
                log.info("Selected new Yahoo Quote Server: " + value);
                quoteServer = value;
                prefs.put(YahooQuoteSourcePanel.PREF_YAHOO_QUOTE_SERVER, quoteServer);
            } else {
            }

        }
    }

    private final class LoadBookmarkAction extends AbstractLoadStockSymbolsTask {
        private static final long serialVersionUID = 1L;

        private File file;

        public LoadBookmarkAction(File file, QuoteSourceListener listener, ExecutorService threadPool) {
            super(file.getName(), listener, threadPool);
            this.file = file;
        }

        @Override
        protected List<String> getStocks() throws IOException {
            List<String> stocks = new ArrayList<String>();
            OfxUtils.addToList(file.toURI().toURL(), stocks);
            return stocks;
        }

    }

    private final class GetQuotesAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        GetQuotesAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            getQuotes();
        }
    }

    private void notifyNoStockRequest() {
        log.warn("No stocks requested.");
        JOptionPane.showMessageDialog(YahooQuoteSourcePanel.this, "Please enter stock symbols.", "Missing input", JOptionPane.WARNING_MESSAGE);
        updateButton.setEnabled(true);
        progressBar.setValue(100);
        setCursor(null);
    }

    private void stockPricesLookupStarted() {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesLookupStarted(quoteSource);
        }
    }

    private void stockPricesReceived(List<AbstractStockPrice> stockPrices) {
        if (log.isDebugEnabled()) {
            log.debug("> stockPricesReceived");
        }
        this.quoteSourceListener.stockPricesReceived(quoteSource, stockPrices);
    }

    public String getQuoteServer() {
        return quoteServer;
    }

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

    protected List<AbstractStockPrice> getStockQuotes(final List<String> stockSymbols) throws IOException {
        List<AbstractStockPrice> stockPrices;
        GetYahooQuotes quoteGetter = new GetYahooQuotes();
        if (quoteServer != null) {
            quoteGetter.setHost(quoteServer);
        }
        try {
            stockPrices = quoteGetter.getQuotes(stockSymbols, listener);
            this.fxSymbols = quoteGetter.getFxSymbols();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        return stockPrices;
    }

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
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
        return stocks;
    }

    public List<AbstractStockPrice> getFxSymbols() {
        return fxSymbols;
    }

    public DefaultQuoteSource getQuoteSource() {
        return quoteSource;
    }

    protected void getQuotes() {
        log.info("> getQuotes");
        
        updateButton.setEnabled(false);
        progressBar.setValue(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String text = stockSymbolsView.getText();

        if (text == null) {
            notifyNoStockRequest();
            return;
        }

        text = text.trim();
        if (log.isDebugEnabled()) {
            log.debug("stocks=" + text);
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
                        log.warn(e);
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
