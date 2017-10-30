package com.hungle.tools.moneyutils.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.bloomberg.BloombergQuoteSourcePanel;
import com.hungle.tools.moneyutils.data.SymbolMapper;
import com.hungle.tools.moneyutils.data.SymbolMapperEntry;
import com.hungle.tools.moneyutils.fi.AbstractFiDir;
import com.hungle.tools.moneyutils.fi.VelocityUtils;
import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;
import com.hungle.tools.moneyutils.ft.FtDotComQuoteSourcePanel;
import com.hungle.tools.moneyutils.fx.UpdateFx;
import com.hungle.tools.moneyutils.jna.ImportDialogAutoClickService;
import com.hungle.tools.moneyutils.misc.BuildNumber;
import com.hungle.tools.moneyutils.ofx.quotes.CurrencyUtils;
import com.hungle.tools.moneyutils.ofx.quotes.DefaultQuoteSource;
import com.hungle.tools.moneyutils.ofx.quotes.FxTable;
import com.hungle.tools.moneyutils.ofx.quotes.ImportUtils;
import com.hungle.tools.moneyutils.ofx.quotes.OfxUtils;
import com.hungle.tools.moneyutils.ofx.quotes.QifUtils;
import com.hungle.tools.moneyutils.ofx.quotes.QuoteSource;
import com.hungle.tools.moneyutils.ofx.quotes.QuoteSourceListener;
import com.hungle.tools.moneyutils.ofx.quotes.Utils;
import com.hungle.tools.moneyutils.ofx.statement.StatementPanel;
import com.hungle.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.hungle.tools.moneyutils.ofx.xmlbeans.OfxSaveParameter;
import com.hungle.tools.moneyutils.scholarshare.TIAACREFQuoteSourcePanel;
import com.hungle.tools.moneyutils.stockprice.AbstractStockPrice;
import com.hungle.tools.moneyutils.stockprice.Price;
import com.hungle.tools.moneyutils.stockprice.StockPrice;
import com.hungle.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel;
import com.hungle.tools.moneyutils.yahoo.YahooHistoricalSourcePanel;
import com.hungle.tools.moneyutils.yahoo.YahooQuoteSourcePanel;
import com.hungle.tools.moneyutils.yahoo.YahooQuotesGetter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

// TODO: Auto-generated Javadoc
/**
 * The Class GUI.
 */
public class GUI extends JFrame {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant prefs. */
    // TODO: le.com.tools.moneyutils.ofx.quotes.GUI
    private static final Preferences PREFS = Preferences
            .userNodeForPackage(le.com.tools.moneyutils.ofx.quotes.GUI.class);

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GUI.class);

    /** The Constant DEFAULT_FI_DIR. */
    public static final String DEFAULT_FI_DIR = "fi";

    /** The Constant VERSION_PREFIX. */
    private static final String VERSION_PREFIX = "Build";
    // private static final String VERSION_PREFIX = "SNAPSHOT";

    /** The Constant VERSION_SUFFIX. */
    private static final String VERSION_SUFFIX = "01";

    /** The version. */
    // Build_20110706_31
    public static String VERSION = VERSION_PREFIX + "_" + "20111104" + "_" + VERSION_SUFFIX;

    /** The Constant PREF_DEFAULT_CURRENCY. */
    private static final String PREF_DEFAULT_CURRENCY = "defaultCurrency";
    
    /** The Constant PREF_LAST_KNOWN_IMPORT_STRING. */
    private static final String PREF_LAST_KNOWN_IMPORT_STRING = "lastKnownImportString";

    /** The Constant PREF_RANDOMIZE_SHARE_COUNT. */
    private static final String PREF_RANDOMIZE_SHARE_COUNT = "randomizeShareCount";

    /** The Constant PREF_FORCE_GENERATING_INVTRANLIST. */
    private static final String PREF_FORCE_GENERATING_INVTRANLIST = "forceGeneratingINVTRANLIST";

    /** The Constant HOME_PAGE. */
    protected static final String HOME_PAGE = "http://code.google.com/p/hle-ofx-quotes/";

    /** The Constant PREF_DATE_OFFSET. */
    private static final String PREF_DATE_OFFSET = "dateOffset";

    /** The Constant PREF_SUSPICIOUS_PRICE. */
    private static final String PREF_SUSPICIOUS_PRICE = "suspiciousPrice";

    /** The Constant PREF_INCREMENTALLY_INCREASED_SHARE_COUNT. */
    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT = "incrementallyIncreasedShareCount";

    /** The Constant PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE. */
    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE = "incrementallyIncreasedShareCountValue";

    /** The Constant PREF_ACCOUNT_ID. */
    private static final String PREF_ACCOUNT_ID = "accountId";

    /** The Constant PREF_IMPORT_DIALOG_AUTO_CLICK. */
    private static final String PREF_IMPORT_DIALOG_AUTO_CLICK = "importDialogAutoClick";

    /** The thread pool. */
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /** The output files. */
    private List<File> outputFiles;

    /** The result view. */
    private JTextPane resultView;

    /** The price list. */
    private EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
    
    /** The exchange rates. */
    private EventList<AbstractStockPrice> exchangeRates = new BasicEventList<AbstractStockPrice>();
    
    /** The mapper. */
    private EventList<SymbolMapperEntry> mapper = new BasicEventList<SymbolMapperEntry>();

    /** The price filter edit. */
    private JTextField priceFilterEdit;

    /** The defaul currency label. */
    private JLabel defaulCurrencyLabel;
    
    /** The clock label. */
    private JLabel clockLabel;

    /** The clock formatters. */
    private SimpleDateFormat[] clockFormatters;

    /** The import to money button. */
    private JButton importToMoneyButton;

    /** The last known import. */
    private JLabel lastKnownImport;

    /** The last known import string. */
    private String lastKnownImportString;

    /** The update exchange rate button. */
    private JButton updateExchangeRateButton;

    /** The save ofx button. */
    private JButton saveOfxButton;

    /** The default currency. */
    private String defaultCurrency = PREFS.get(PREF_DEFAULT_CURRENCY, com.hungle.tools.moneyutils.ofx.xmlbeans.CurrencyUtils.getDefaultCurrency());

    /** The randomize share count. */
    private Boolean randomizeShareCount = Boolean.valueOf(PREFS.get(PREF_RANDOMIZE_SHARE_COUNT, "False"));
    
    /** The random. */
    private Random random = new Random();

    /** The incrementally increased share count. */
    private Boolean incrementallyIncreasedShareCount = Boolean
            .valueOf(PREFS.get(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT, "False"));

    /** The bottom tabs. */
    private JTabbedPane bottomTabs;

    /** The yahoo quote source view. */
    private YahooQuoteSourcePanel yahooQuoteSourceView;

    /** The yahoo api quote source panel. */
    private YahooApiQuoteSourcePanel yahooApiQuoteSourcePanel;

    /** The ft dot com quote source panel. */
    private FtDotComQuoteSourcePanel ftDotComQuoteSourcePanel;

    /** The yahoo historical quote source view. */
    private YahooQuoteSourcePanel yahooHistoricalQuoteSourceView;

    /** The quote source listener. */
    private QuoteSourceListener quoteSourceListener;

    /** The force generating INVTRANLIST. */
    private Boolean forceGeneratingINVTRANLIST = Boolean.valueOf(PREFS.get(PREF_FORCE_GENERATING_INVTRANLIST, "False"));

    /** The date offset. */
    private Integer dateOffset = Integer.valueOf(PREFS.get(PREF_DATE_OFFSET, "0"));

    /** The suspicious price. */
    private Integer suspiciousPrice = Integer.valueOf(PREFS.get(PREF_SUSPICIOUS_PRICE, "10000"));

    /** The account id. */
    private String accountId = PREFS.get(PREF_ACCOUNT_ID, OfxPriceInfo.DEFAULT_ACCOUNT_ID);

    /** The account id label. */
    private JLabel accountIdLabel;

    /** The download view. */
    private StatementPanel downloadView;

    /** The main tabbed. */
    private JTabbedPane mainTabbed;

    /** The selected quote source. */
    private int selectedQuoteSource;

    /** The import dialog auto click service. */
    private ImportDialogAutoClickService importDialogAutoClickService;

    /** The price formatter. */
    private NumberFormat priceFormatter;

    /** The bloomberg quote source panel. */
    private YahooApiQuoteSourcePanel bloombergQuoteSourcePanel;

    /** The t IAACREF quote source panel. */
    private TIAACREFQuoteSourcePanel tIAACREFQuoteSourcePanel;

    /** The backup view. */
    private BackupPanel backupView;

    /** The fi dir. */
    // TODO_FI
    private File fiDir = new File(System.getProperty("fi.dir", getDefaultFiDir()));

    /**
     * The Class EditRandomizeShareCountAction.
     */
    private final class EditRandomizeShareCountAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new edits the randomize share count action.
         *
         * @param name the name
         */
        private EditRandomizeShareCountAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] possibilities = { "true", "false" };
            Icon icon = null;
            String s = (String) JOptionPane.showInputDialog(GUI.this,
                    "Current: " + randomizeShareCount + "\n" + "Choices:", "Set Randomize Share Count",
                    JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

            // If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                String value = s;
                LOGGER.info("Selected new 'Randomize Share Count': " + value);
                Boolean newValue = Boolean.valueOf(value);
                if (newValue.compareTo(randomizeShareCount) != 0) {
                    randomizeShareCount = newValue;
                    PREFS.put(PREF_RANDOMIZE_SHARE_COUNT, randomizeShareCount.toString());
                    // to clear the pricing table
                    QuoteSource quoteSource = null;
                    stockSymbolsStringReceived(quoteSource, null);
                }
            } else {
            }
        }
    }

    /**
     * The Class EditWarnSuspiciousPriceAction.
     */
    private final class EditWarnSuspiciousPriceAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new edits the warn suspicious price action.
         *
         * @param name the name
         */
        private EditWarnSuspiciousPriceAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            String[] possibilities = null;
            Icon icon = null;
            String s = (String) JOptionPane.showInputDialog(GUI.this,
                    "To guard against bad price from quote source,\n"
                            + "you can set a value above which will trigger a warning dialog.\n"
                            + "To disable: set to -1.\n" + "\n" + "Current: " + suspiciousPrice + "\n" + "Price:",
                    "Set a price", JOptionPane.PLAIN_MESSAGE, icon, possibilities, suspiciousPrice.toString());

            // If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                String value = s;
                LOGGER.info("Selected new 'Warn Suspicious Price': " + value);
                try {
                    Integer newValue = Integer.valueOf(value);
                    if (newValue.compareTo(suspiciousPrice) != 0) {
                        suspiciousPrice = newValue;
                        PREFS.put(PREF_SUSPICIOUS_PRICE, suspiciousPrice.toString());
                        // to clear the pricing table
                        // stockSymbolsStringReceived(null);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a valid number - " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
            }
        }
    }

    /**
     * The Class EditOFXAccountIdAction.
     */
    private final class EditOFXAccountIdAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new edits the OFX account id action.
         *
         * @param name the name
         */
        private EditOFXAccountIdAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            String[] possibilities = null;
            Icon icon = null;
            String s = (String) JOptionPane.showInputDialog(GUI.this,
                    "Current: " + accountId + "\n" + "OFX Account Id:", "Set OFX Account Id", JOptionPane.PLAIN_MESSAGE,
                    icon, possibilities, accountId);

            // If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                String value = s;
                selectNewAccountId(value);
            } else {
            }
        }
    }

    /**
     * The Class EditCurrencyAction.
     */
    private final class EditCurrencyAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new edits the currency action.
         *
         * @param name the name
         */
        private EditCurrencyAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String> keys = CurrencyUtils.CURRENCIES.keySet();
            String[] possibilities = new String[keys.size()];
            int i = 0;
            for (String key : keys) {
                possibilities[i++] = key;
            }
            Icon icon = null;
            String s = (String) JOptionPane.showInputDialog(GUI.this,
                    "Current: " + defaultCurrency + "\n" + "Available:", "Set Currency", JOptionPane.PLAIN_MESSAGE,
                    icon, possibilities, null);

            // If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                String value = CurrencyUtils.CURRENCIES.get(s);
                selectNewCurrency(value);
            } else {
            }
        }
    }

    /**
     * The Class UpdateMnyExchangeRatesTask.
     */
    private final class UpdateMnyExchangeRatesTask extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new update mny exchange rates task.
         *
         * @param name the name
         */
        private UpdateMnyExchangeRatesTask(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            // JOptionPane.showMessageDialog(GUI.this,
            // "Not yet implemented.");
            try {
                UpdateFx.invoke();
            } catch (Exception e) {
                LOGGER.error(e);

                StringBuilder message = new StringBuilder();
                message.append(e.toString() + "\n");
                // message.append("\n");
                // message.append("Please create a directory 'plugins' and\n");
                // message.append("add the sunriise*.jar file there.\n");
                message.append("Do you want me to download the sunriise plugin jar file and try again?");

                String title = "Error updating exchange rate";

                int n = JOptionPane.showOptionDialog(GUI.this, message.toString(), title, JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (n == JOptionPane.YES_OPTION) {
                    getJarFile(event);
                }
            }
        }

        /**
         * Gets the jar file.
         *
         * @param event the event
         * @return the jar file
         */
        private void getJarFile(final ActionEvent event) {
            final ProgressMonitor progressMonitor = new ProgressMonitor(GUI.this, "Downloading sunriise plugin ...", "",
                    0, 100);
            final String jarFileName = "sunriise-0.0.3-20111220.210334-1-jar-with-dependencies.jar";

            Callable<String> task = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String errorMessage = null;
                    String uri = "http://sunriise.sourceforge.net/out/hleofxquotes/Build_20111220_64/" + jarFileName;
                    File toJarFile = new File("plugins");
                    if (!toJarFile.isDirectory()) {
                        toJarFile.mkdirs();
                    }
                    toJarFile = new File(toJarFile, jarFileName);
                    try {
                        if (getJarFile(uri, toJarFile, progressMonitor)) {
                            actionPerformed(event);
                        } else {
                            LOGGER.warn("User cancel downloading!");
                            if (!toJarFile.delete()) {
                                LOGGER.warn("Failed to delete file=" + toJarFile);
                            } else {
                                LOGGER.info("Deleted file=" + toJarFile);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.warn(e, e);
                        errorMessage = e.toString();
                    } finally {
                        if (progressMonitor != null) {
                            progressMonitor.close();
                        }
                        if (errorMessage != null) {
                            errorMessage += "\nFailed to download jar file\n" + jarFileName;
                            JOptionPane.showMessageDialog(GUI.this, errorMessage, "Error downloading " + jarFileName,
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    return errorMessage;
                }
            };

            Future<String> future = threadPool.submit(task);
        }

        /**
         * Gets the jar file.
         *
         * @param uri the uri
         * @param toJarFile the to jar file
         * @param progressMonitor the progress monitor
         * @return the jar file
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private boolean getJarFile(String uri, File toJarFile, final ProgressMonitor progressMonitor)
                throws IOException {
            boolean canceled = false;
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(uri);
            LOGGER.info("GET " + uri);
            HttpResponse httpResponse = client.execute(httpGet);

            Long contentLength = -1L;
            Header header = httpResponse.getFirstHeader("Content-Length");
            if (header != null) {
                String value = header.getValue();
                LOGGER.info(header.getName() + ": " + value);
                if ((value != null) && (value.length() > 0)) {
                    try {
                        contentLength = Long.valueOf(value);
                    } catch (NumberFormatException e) {
                        LOGGER.warn(e);
                    }
                }
            }
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            LOGGER.info("  statusCode=" + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                BufferedInputStream in = null;
                BufferedOutputStream out = null;

                try {
                    out = new BufferedOutputStream(new FileOutputStream(toJarFile));
                    LOGGER.info("  ... saving to " + toJarFile);
                    in = new BufferedInputStream(entity.getContent());
                    long total = 0L;
                    long percentageDone = 0L;
                    int n;
                    byte[] buffer = new byte[2048];
                    while ((n = in.read(buffer)) != -1) {
                        if (progressMonitor.isCanceled()) {
                            canceled = true;
                            notifyCanceled(httpGet, entity, progressMonitor);
                            break;
                        }

                        out.write(buffer, 0, n);

                        if (progressMonitor.isCanceled()) {
                            canceled = true;
                            notifyCanceled(httpGet, entity, progressMonitor);
                            break;
                        }

                        total += n;
                        if (contentLength > 0L) {
                            long done = (total * 100) / contentLength;
                            if (done > percentageDone) {
                                percentageDone = done;
                                if (percentageDone >= 99) {
                                    percentageDone = 99;
                                }
                                final String message = updateProgressMonitor(percentageDone, progressMonitor);

                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug(total + "/" + contentLength + ", " + message);
                                }
                            }
                        }
                        if (progressMonitor.isCanceled()) {
                            canceled = true;
                            notifyCanceled(httpGet, entity, progressMonitor);
                            break;
                        }
                    }
                    percentageDone = 100;
                    updateProgressMonitor(percentageDone, progressMonitor);
                } finally {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("  > DONE saving");
                    }

                    if (in != null) {
                        try {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("  calling in.close()");
                            }
                            in.close();
                        } finally {
                            in = null;
                        }
                    }
                    if (out != null) {
                        try {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("  calling out.close()");
                            }
                            out.close();
                        } finally {
                            out = null;
                        }
                    }
                    LOGGER.info("  > DONE saving (post-closing)");
                }
            } else {
                throw new IOException(statusLine.toString());
            }

            LOGGER.info("canceled=" + canceled);

            return !canceled;
        }

        /**
         * Update progress monitor.
         *
         * @param percentageDone the percentage done
         * @param progressMonitor the progress monitor
         * @return the string
         */
        private String updateProgressMonitor(long percentageDone, final ProgressMonitor progressMonitor) {
            final String message = String.format("Completed %d%%.\n", percentageDone);
            final int progress = (int) percentageDone;
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    progressMonitor.setNote(message);
                    progressMonitor.setProgress(progress);
                }
            };
            SwingUtilities.invokeLater(doRun);
            return message;
        }

        /**
         * Notify canceled.
         *
         * @param httpGet the http get
         * @param entity the entity
         * @param progressMonitor the progress monitor
         */
        private void notifyCanceled(HttpGet httpGet, HttpEntity entity, final ProgressMonitor progressMonitor) {
            LOGGER.warn("progressMonitor.isCanceled()=" + progressMonitor.isCanceled());
            if (httpGet != null) {
                httpGet.abort();
            }
            if (entity != null) {
                LOGGER.info("  calling entity.consumeContent()");
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    LOGGER.warn("Failed to entity.consumeContent(), " + e);
                }
            }
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    progressMonitor.setProgress(100);
                    progressMonitor.close();
                }
            };
            SwingUtilities.invokeLater(doRun);
        }
    }

    /**
     * The listener interface for receiving closingWindow events.
     * The class that is interested in processing a closingWindow
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addClosingWindowListener<code> method. When
     * the closingWindow event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ClosingWindowEvent
     */
    private final class ClosingWindowListener implements WindowListener {
        
        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
         */
        @Override
        public void windowOpened(WindowEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
         */
        @Override
        public void windowIconified(WindowEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
         */
        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
         */
        @Override
        public void windowDeactivated(WindowEvent e) {
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
         */
        @Override
        public void windowClosing(WindowEvent event) {
            LOGGER.info("> windowClosing");
            try {
                shutdown();
            } finally {
                // TODO: see if this will help with JNA crash on the way out
                LOGGER.info("> Calling System.gc()");
                System.gc();
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
         */
        @Override
        public void windowClosed(WindowEvent event) {
            LOGGER.info("> windowClosed");
            try {
                shutdown();
            } finally {
                // TODO: see if this will help with JNA crash on the way out
                LOGGER.info("> Calling System.gc()");
                System.gc();
            }
        }

        /**
         * Shutdown.
         */
        private void shutdown() {
            if (threadPool != null) {
                List<Runnable> tasks = threadPool.shutdownNow();
                LOGGER.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
                long timeout = 1L;
                TimeUnit unit = TimeUnit.MINUTES;
                try {
                    LOGGER.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                    if (!threadPool.awaitTermination(timeout, unit)) {
                        LOGGER.warn("Timed-out waiting for threadPool.awaitTermination");
                    }
                } catch (InterruptedException e) {
                    LOGGER.error(e, e);
                } finally {
                    LOGGER.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                }
            }
            if (importDialogAutoClickService != null) {
                importDialogAutoClickService.setEnable(false);
                importDialogAutoClickService.shutdown();
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
         */
        @Override
        public void windowActivated(WindowEvent e) {
        }
    }

    /**
     * The Class StockPricesReceivedTask.
     */
    private final class StockPricesReceivedTask implements Runnable {
        
        /** The beans. */
        private final List<AbstractStockPrice> beans;
        
        /** The bad price. */
        private final Double badPrice;
        
        /** The fx table. */
        private final FxTable fxTable;
        
        /** The has wrapped share count. */
        private final boolean hasWrappedShareCount;
        
        /** The symbol mapper. */
        private final SymbolMapper symbolMapper;
        
        /** The quote source. */
        private final QuoteSource quoteSource;

        /**
         * Instantiates a new stock prices received task.
         *
         * @param beans the beans
         * @param badPrice the bad price
         * @param fxTable the fx table
         * @param hasWrappedShareCount the has wrapped share count
         * @param symbolMapper the symbol mapper
         * @param quoteSource the quote source
         */
        private StockPricesReceivedTask(List<AbstractStockPrice> beans, Double badPrice, FxTable fxTable,
                boolean hasWrappedShareCount, SymbolMapper symbolMapper, QuoteSource quoteSource) {
            this.beans = beans;
            this.badPrice = badPrice;
            this.fxTable = fxTable;
            this.hasWrappedShareCount = hasWrappedShareCount;
            this.symbolMapper = symbolMapper;
            this.quoteSource = quoteSource;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> stockPricesReceived, size=" + beans.size());
            }

            priceList.getReadWriteLock().writeLock().lock();
            try {
                priceList.clear();
                priceList.addAll(beans);
            } finally {
                priceList.getReadWriteLock().writeLock().unlock();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("< stockPricesReceived");
                }
            }

            exchangeRates.getReadWriteLock().writeLock().lock();
            try {
                exchangeRates.clear();
                if (quoteSource != null) {
                    List<AbstractStockPrice> newRates = quoteSource.getExchangeRates();
                    if (newRates != null) {
                        exchangeRates.addAll(newRates);
                    }
                }
            } finally {
                exchangeRates.getReadWriteLock().writeLock().unlock();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("< exchangeRatesReceived");
                }
            }

            try {
                boolean onePerFile = quoteSource.isHistoricalQuotes();
                List<File> ofxFiles = saveToOFX(beans, symbolMapper, fxTable, onePerFile);
                for (File ofxFile : ofxFiles) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("ofxFile=" + ofxFile);
                    }
                }

                File csvFile = saveToCsv(beans);
                LOGGER.info("csvFile=" + csvFile);

                mapper.getReadWriteLock().writeLock().lock();
                try {
                    mapper.clear();
                    mapper.addAll(symbolMapper.getEntries());
                } finally {
                    mapper.getReadWriteLock().writeLock().unlock();
                }
            } catch (IOException e) {
                LOGGER.warn(e);
            } finally {
                Runnable doRun = null;

                if (hasWrappedShareCount) {
                    doRun = new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Incrementally increased share count has wrapped around.\n"
                                            + "Next import will not update price(s).",
                                    "Share count has wrapped around", JOptionPane.WARNING_MESSAGE, null);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }

                if (badPrice != null) {
                    doRun = new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(GUI.this,
                                    "Incoming price from quote source has\n" + "suspicious price: " + badPrice,
                                    "Suspicious Price", JOptionPane.WARNING_MESSAGE, null);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }
                if (resultView != null) {
                    doRun = new UpdateResultViewTask();
                    SwingUtilities.invokeLater(doRun);
                }

                if (bottomTabs != null) {
                    doRun = new Runnable() {
                        @Override
                        public void run() {
                            bottomTabs.setSelectedIndex(0);
                        }
                    };
                    SwingUtilities.invokeLater(doRun);
                }
            }
        }
    }

    /**
     * The Class CreateNewFi.
     */
    private final class CreateNewFiAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        /** The parent component. */
        private Component parentComponent = GUI.this;

        /**
         * Instantiates a new creates the new fi.
         *
         * @param name the name
         */
        private CreateNewFiAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            String fiName = JOptionPane.showInputDialog(parentComponent, "Enter a new 'Financial Institution' name");
            if (fiName == null) {
                // cancel
                return;
            }

            fiName = fiName.trim();
            if (fiName.length() <= 0) {
                return;
            }
            
            File topDir = getTopDir();
            if ((!topDir.exists()) && (!topDir.mkdirs())) {
                JOptionPane.showMessageDialog(parentComponent, "Cannot create dir\ndir=" + topDir.getAbsolutePath(),
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File fiDir = new File(topDir, fiName);
            if (fiDir.exists()) {
                JOptionPane.showMessageDialog(parentComponent, "Directory exist\ndir=" + fiDir.getAbsolutePath(), "Error creating",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!fiDir.mkdirs()) {
                JOptionPane.showMessageDialog(parentComponent, "Cannot create dir\ndir=" + fiDir.getAbsolutePath(),
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LOGGER.info("Created new FI dir=" + fiDir.getAbsolutePath());
            
            String fiPropertiesFileName = AbstractFiDir.DEFAULT_PROPERTIES_FILENAME;
            String sampleFileName = "samples" + "/" + fiPropertiesFileName;
            URL url = OfxUtils.getResource(sampleFileName);
            if (url == null) {
                JOptionPane.showMessageDialog(parentComponent, "Cannot find sample file\nfile=" + sampleFileName,
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            File fiPropertiesFile = null;
            try {
                fiPropertiesFile = new File(fiDir, fiPropertiesFileName);
                Utils.copyToFile(url, fiPropertiesFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentComponent, "Error creating " + fiPropertiesFileName, "Error creating",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane
                    .showMessageDialog(parentComponent,
                            "Succesfully created dirctory for fi=" + fiName + "\n" + "Please edit file\n"
                                    + fiPropertiesFile.getAbsolutePath(),
                            "FI Created", JOptionPane.INFORMATION_MESSAGE);

            postCreated();
        }

        /**
         * Gets the top dir.
         *
         * @return the top dir
         */
        protected File getTopDir() {
            return getFiDir();
        }

        /**
         * Post created.
         */
        protected void postCreated() {
            downloadView.refreshFiDir();

            mainTabbed.setSelectedIndex(1);
        }

    }

    /**
     * The Class ProfileSelectedAction.
     */
    private final class ProfileSelectedAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        /** The props. */
        private Properties props;

        /**
         * Instantiates a new profile selected action.
         *
         * @param name the name
         * @param props the props
         */
        private ProfileSelectedAction(String name, Properties props) {
            super(name);
            this.props = props;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (props == null) {
                return;
            }

            String accountId = props.getProperty("accountId");
            if (!PropertiesUtils.isNull(accountId)) {
                selectNewAccountId(accountId);
            }
            String currency = props.getProperty("currency");
            if (!PropertiesUtils.isNull(currency)) {
                selectNewCurrency(currency);
            }
        }
    }

    /**
     * The Class ExitAction.
     */
    private final class ExitAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new exit action.
         *
         * @param name the name
         */
        private ExitAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            LOGGER.info("> ExitAction.actionPerformed");

            List<Runnable> tasks = threadPool.shutdownNow();
            LOGGER.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;
            try {
                LOGGER.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                if (!threadPool.awaitTermination(timeout, unit)) {
                    LOGGER.warn("Timed-out waiting for threadPool.awaitTermination");
                }
            } catch (InterruptedException e) {
                LOGGER.error(e, e);
            } finally {
                LOGGER.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            }

            if (importDialogAutoClickService != null) {
                importDialogAutoClickService.setEnable(false);
                importDialogAutoClickService.shutdown();
            }

            LOGGER.info("> Calling System.gc()");
            System.gc();
            LOGGER.info("> Calling System.exit(0)");
            System.exit(0);
        }
    }

    /**
     * The Class UpdateResultViewTask.
     */
    private final class UpdateResultViewTask implements Runnable {
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (resultView == null) {
                return;
            }

            List<File> files = getOutputFiles();
            if (files == null) {
                return;
            }
            if (files.size() <= 0) {
                return;
            }

            File outputFile = files.get(0);
            if (outputFile == null) {
                return;
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(outputFile));
                resultView.read(reader, outputFile.getName());
                resultView.setCaretPosition(0);
            } catch (IOException e) {
                LOGGER.warn(e);
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

        }
    }

    /**
     * The Class ImportAction.
     */
    private final class ImportAction extends AbstractAction {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        /**
         * Instantiates a new import action.
         *
         * @param name the name
         */
        private ImportAction(String name) {
            super(name);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            LOGGER.info("> Import action");
            Runnable command = new Runnable() {
                @Override
                public void run() {
                    try {
                        ImportUtils.doImport(threadPool, getOutputFiles());
                    } finally {
                        Runnable doRun = new Runnable() {
                            @Override
                            public void run() {
                                lastKnownImportString = (new Date()).toString();
                                if (lastKnownImport != null) {
                                    lastKnownImport.setText(lastKnownImportString);
                                }
                                PREFS.put(PREF_LAST_KNOWN_IMPORT_STRING, lastKnownImportString);
                            }
                        };
                        SwingUtilities.invokeLater(doRun);
                    }

                }

            };
            threadPool.execute(command);
        }
    }

    /**
     * Clear price table.
     */
    protected void clearPriceTable() {
        priceList.clear();
        exchangeRates.clear();
        if (priceFilterEdit != null) {
            priceFilterEdit.setText("");
        }
    }

    /**
     * Gets the default fi dir.
     *
     * @return the default fi dir
     */
    private String getDefaultFiDir() {
        File dir = FileSystemView.getFileSystemView().getDefaultDirectory();
        if ((dir == null) || (! dir.exists()) || (! dir.isDirectory())) {
            dir = new File(".");
        }
        File fiDir = new File(dir, DEFAULT_FI_DIR);
        return fiDir.getAbsolutePath();
    }

    /**
     * Clear mapper table.
     */
    protected void clearMapperTable() {
        mapper.clear();
    }

    /**
     * Stock prices received.
     *
     * @param quoteSource the quote source
     * @param stockPrices the stock prices
     */
    private void stockPricesReceived(final QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
        SymbolMapper symbolMapper = SymbolMapper.createDefaultSymbolMapper();

        final List<AbstractStockPrice> beans = (stockPrices != null) ? stockPrices
                : new ArrayList<AbstractStockPrice>();
        updateLastPriceCurrency(beans, defaultCurrency, symbolMapper);

        if (randomizeShareCount) {
            int randomInt = random.nextInt(998);
            randomInt = randomInt + 1;
            double value = randomInt / 1000.00;
            LOGGER.info("randomizeShareCount=" + randomizeShareCount + ", value=" + value);
            for (AbstractStockPrice bean : beans) {
                bean.setUnits(value);
            }
        }

        boolean hasWrappedShareCount = false;
        if (incrementallyIncreasedShareCount) {
            String key = PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE;
            double value = PREFS.getDouble(key, 0.000);
            if (value > 0.999) {
                value = 0.000;
                // TODO
                LOGGER.warn("incrementallyIncreasedShareCount, is wrapping back to " + value);
                hasWrappedShareCount = true;
            }
            value = value + 0.001;
            for (AbstractStockPrice bean : beans) {
                bean.setUnits(value);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("incrementallyIncreasedShareCount=" + incrementallyIncreasedShareCount + ", value=" + value);
            }
            PREFS.putDouble(key, value);
        }

        // for (StockPriceBean bean : beans) {
        // String currency = bean.getCurrency();
        // if (PropertiesUtils.isNull(currency)) {
        // String symbol = bean.getStockSymbol();
        // String overridingCurrency = getMapperCurrency(symbol, mapper);
        // log.info("symbol: " + symbol + ", overridingCurrency=" +
        // overridingCurrency);
        // if (!PropertiesUtils.isNull(overridingCurrency)) {
        // bean.setCurrency(overridingCurrency);
        // }
        // }
        // }

        Double badPrice = null;
        if (suspiciousPrice > -1L) {
            Double d = new Double(suspiciousPrice);
            for (AbstractStockPrice bean : beans) {
                String stockSymbol = bean.getStockSymbol();
                if ((stockSymbol != null) && (stockSymbol.startsWith("^"))) {
                    // index
                    continue;
                }
                Price price = bean.getLastPrice();
                if (price == null) {
                    continue;
                }
                if (price.getPrice().compareTo(d) > 0) {
                    badPrice = price.getPrice();
                    break;
                }
            }
        }
        FxTable fxTable = FxTable.createDefaultFxTable();
        Runnable stockPricesReceivedTask = new StockPricesReceivedTask(beans, badPrice, fxTable, hasWrappedShareCount,
                symbolMapper, quoteSource);
        // doRun.run();
        SwingUtilities.invokeLater(stockPricesReceivedTask);
    }

    /**
     * Save to OFX.
     *
     * @param stockPrices the stock prices
     * @param symbolMapper the symbol mapper
     * @param fxTable the fx table
     * @param onePerFile the one per file
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<File> saveToOFX(final List<AbstractStockPrice> stockPrices, SymbolMapper symbolMapper, FxTable fxTable,
            boolean onePerFile) throws IOException {
        // cleanup
        List<File> files = getOutputFiles();
        if (files != null) {
            for (File file : files) {
                deleteOutputFile(file);
            }
        }
        files = new ArrayList<File>();
        setOutputFiles(files);

        if (onePerFile) {
            for (AbstractStockPrice stockPrice : stockPrices) {
                List<AbstractStockPrice> list = new ArrayList<AbstractStockPrice>();
                list.add(stockPrice);
                File outputFile = saveToOFX(list, symbolMapper, fxTable);
                files.add(outputFile);
            }
        } else {
            File outputFile = saveToOFX(stockPrices, symbolMapper, fxTable);
            files.add(outputFile);
        }

        return files;
    }

    /**
     * Save to OFX.
     *
     * @param stockPrices the stock prices
     * @param symbolMapper the symbol mapper
     * @param fxTable the fx table
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File saveToOFX(final List<AbstractStockPrice> stockPrices, SymbolMapper symbolMapper, FxTable fxTable)
            throws IOException {
        File outputFile = File.createTempFile("quotes", ".ofx");
        outputFile.deleteOnExit();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("outputFile=" + outputFile.getAbsolutePath());
        }

        LOGGER.info("forceGeneratingINVTRANLIST=" + forceGeneratingINVTRANLIST);
        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(defaultCurrency);
        params.setAccountId(accountId);
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);
        params.setDateOffset(dateOffset);
        OfxPriceInfo.save(stockPrices, outputFile, params, symbolMapper, fxTable);
        return outputFile;
    }

    /**
     * Save to csv.
     *
     * @param beans the beans
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File saveToCsv(List<AbstractStockPrice> beans) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        File outFile = null;
        outFile = new File("quotes.csv");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            writer.println("symbol,name,price,date");
            writer.println("");
            for (AbstractStockPrice bean : beans) {
                writer.print(bean.getStockSymbol());

                writer.print(",");
                writer.print(bean.getStockName().replace(",", ""));

                writer.print(",");
                Double price = bean.getLastPrice().getPrice();
                // TODO: assume that portfolio currency is going to be
                // GBP
                String currency = bean.getCurrency();
                if (currency != null) {
                    if (currency.compareToIgnoreCase("GBX") == 0) {
                        price = price / 100.0;
                    }
                }
                writer.print(priceFormatter.format(price));

                writer.print(",");
                Date lastTrade = bean.getLastTrade();
                if (lastTrade == null) {
                    writer.print("");
                } else {
                    writer.print(formatter.format(lastTrade));
                }

                writer.println();
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
        return outFile;
    }

    /**
     * Gets the mapper currency.
     *
     * @param symbol the symbol
     * @param mapper the mapper
     * @param defaultValue the default value
     * @return the mapper currency
     */
    private String getMapperCurrency(String symbol, SymbolMapper mapper, String defaultValue) {
        String quoteSourceSymbol = null;
        String currency = defaultValue;
        for (SymbolMapperEntry entry : mapper.getEntries()) {
            quoteSourceSymbol = entry.getQuotesSourceSymbol();
            if (PropertiesUtils.isNull(quoteSourceSymbol)) {
                continue;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("s=" + quoteSourceSymbol + ", symbol=" + symbol);
            }
            if (quoteSourceSymbol.compareToIgnoreCase(symbol) != 0) {
                continue;
            }
            currency = entry.getQuotesSourceCurrency();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getMapperCurrency: s=" + quoteSourceSymbol + ", currency=" + currency);
            }
            if (!PropertiesUtils.isNull(currency)) {
                return currency;
            }
        }
        return currency;
    }

    /**
     * Update last price currency.
     *
     * @param stockPrices the stock prices
     * @param defaultCurrency the default currency
     * @param symbolMapper the symbol mapper
     */
    private void updateLastPriceCurrency(List<AbstractStockPrice> stockPrices, String defaultCurrency,
            SymbolMapper symbolMapper) {
        for (AbstractStockPrice stockPrice : stockPrices) {
            Price price = stockPrice.getLastPrice();
            if ((defaultCurrency != null) && (price.getCurrency() == null)) {
                price.setCurrency(defaultCurrency);
            }
            String currency = stockPrice.getCurrency();
            if (PropertiesUtils.isNull(currency)) {
                String symbol = stockPrice.getStockSymbol();
                String overridingCurrency = null;
                overridingCurrency = getMapperCurrency(symbol, symbolMapper, overridingCurrency);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("symbol: " + symbol + ", overridingCurrency=" + overridingCurrency);
                }
                if (!PropertiesUtils.isNull(overridingCurrency)) {
                    stockPrice.setCurrency(overridingCurrency);
                    stockPrice.updateLastPriceCurrency();
                }
            }
        }
    }

    /**
     * Gets the save ofx button.
     *
     * @return the save ofx button
     */
    public JButton getSaveOfxButton() {
        return saveOfxButton;
    }

    /**
     * Instantiates a new gui.
     *
     * @param title the title
     */
    public GUI(String title) {
        super(title);

        this.priceFormatter = NumberFormat.getNumberInstance();
        this.priceFormatter.setGroupingUsed(false);
        this.priceFormatter.setMinimumFractionDigits(2);
        this.priceFormatter.setMaximumFractionDigits(10);

        quoteSourceListener = new QuoteSourceListener() {
            @Override
            public void stockPricesLookupStarted(QuoteSource quoteSource) {
                GUI.this.stockPricesLookupStarted(quoteSource);
            }

            @Override
            public void stockSymbolsStringReceived(QuoteSource quoteSource, String lines) {
                GUI.this.stockSymbolsStringReceived(quoteSource, lines);
            }

            @Override
            public void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
                GUI.this.stockPricesReceived(quoteSource, stockPrices);
            }
        };

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        updateMainMenu();

        getContentPane().add(createMainView());

        if (importDialogAutoClickService != null) {
            importDialogAutoClickService.schedule();
        }

        WindowListener windowListener = new ClosingWindowListener();
        addWindowListener(windowListener);
    }

    /**
     * Stock prices lookup started.
     *
     * @param quoteSource the quote source
     */
    protected void stockPricesLookupStarted(QuoteSource quoteSource) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                clearPriceTable();
                clearMapperTable();
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update main menu.
     */
    private void updateMainMenu() {
        JMenuBar menubar = new JMenuBar();

        addFileMenu(menubar);

        addEditMenu(menubar);

        addToolsMenu(menubar);

        addHelpMenu(menubar);

        setJMenuBar(menubar);
    }

    /**
     * Adds the tools menu.
     *
     * @param menubar the menubar
     */
    private void addToolsMenu(JMenuBar menubar) {
        JMenu menu = null;
        // JMenuItem menuItem = null;

        menu = new JMenu("Tools");

        importDialogAutoClickService = new ImportDialogAutoClickService();

        JCheckBoxMenuItem importAutoClick = new JCheckBoxMenuItem("Import dialog auto-click");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();
                if (selected) {
                    importDialogAutoClickService.setEnable(true);
                    PREFS.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.TRUE);
                } else {
                    importDialogAutoClickService.setEnable(false);
                    PREFS.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
                }
            }
        };
        importAutoClick.addActionListener(listener);
        Boolean autoClick = PREFS.getBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
        importAutoClick.setSelected(autoClick);
        importDialogAutoClickService.setEnable(autoClick);
        menu.add(importAutoClick);

        menubar.add(menu);
    }

    /**
     * Adds the help menu.
     *
     * @param menubar the menubar
     */
    private void addHelpMenu(JMenuBar menubar) {
        JMenu menu = null;
        JMenuItem menuItem = null;

        menu = new JMenu("Help");

        menuItem = new JMenuItem(new AbstractAction("Home page") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Documentation (Wiki)") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/w/list";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Log a bug") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "http://code.google.com/p/hle-ofx-quotes/issues/list";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Discussion group") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String str = "https://groups.google.com/forum/?fromgroups#!forum/hleofxquotes";
                URI uri = URI.create(str);
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    LOGGER.error(e, e);
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("About") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                // showMessageDialog();
                StringBuilder sb = new StringBuilder();
                sb.append("Version: " + GUI.VERSION + "\n");
                sb.append("Home page: " + GUI.HOME_PAGE + "\n");
                sb.append("Wiki (documentation): http://code.google.com/p/hle-ofx-quotes/wiki" + "\n");
                sb.append("\n");
                sb.append("Directory: " + GUI.getCurrentWorkingDirectory() + "\n");
                sb.append("Currency: " + defaultCurrency + "\n");
                sb.append("OFX Account Id: " + accountId + "\n");
                sb.append("Yahoo server: " + getYahooQuoteServer() + "\n");
                File file = new File("hleOfxQuotes-log.txt");
                if (file.exists()) {
                    sb.append("Log file: " + file.getAbsoluteFile().getAbsolutePath() + "\n");
                } else {
                    sb.append("Log file: " + "NOT_FOUND" + "\n");
                }

                sb.append("\n");
                String[] keys = { "dateOffset", "randomizeShareCount", "forceGeneratingINVTRANLIST", "suspiciousPrice",
                        "incrementallyIncreasedShareCount" };
                Arrays.sort(keys);
                for (String key : keys) {
                    try {
                        String value = BeanUtils.getProperty(GUI.this, key);
                        sb.append(key + "=" + value + "\n");
                    } catch (Exception e) {
                        LOGGER.warn(e);
                    }
                }

                Component source = (Component) event.getSource();
                source = null;
                AboutDialog.showDialog(GUI.this, source, "About", sb.toString());
            }
        });
        menu.add(menuItem);

        menubar.add(menu);
    }

    /**
     * Adds the file menu.
     *
     * @param menubar the menubar
     */
    private void addFileMenu(JMenuBar menubar) {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("File");
        menubar.add(menu);

        JMenu newMenu = new JMenu("New");
        menu.add(newMenu);

        menuItem = new JMenuItem(new CreateNewFiAction("Financial Institution"));
        newMenu.add(menuItem);

        JMenu profilesMenu = new JMenu("Open Quotes Profiles");
        menu.add(profilesMenu);
        addProfilesToMenu(profilesMenu);

        menu.addSeparator();
        menuItem = new JMenuItem(new ExitAction("Exit"));
        menu.add(menuItem);
    }

    /**
     * Adds the profiles to menu.
     *
     * @param profilesMenu the profiles menu
     */
    private void addProfilesToMenu(JMenu profilesMenu) {
        File dir = new File("profiles");
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            LOGGER.info("file=" + file);
            if (file.isDirectory()) {
                continue;
            }
            if (!file.isFile()) {
                continue;
            }

            addProfileToMenu(profilesMenu, file);
        }
    }

    /**
     * Adds the profile to menu.
     *
     * @param profilesMenu the profiles menu
     * @param file the file
     */
    private void addProfileToMenu(JMenu profilesMenu, File file) {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            props.load(reader);
            String name = props.getProperty("name");
            if (PropertiesUtils.isNull(name)) {
                name = file.getName();
            }
            JMenuItem item = new JMenuItem(new ProfileSelectedAction(name, props));
            profilesMenu.add(item);
        } catch (IOException e) {
            LOGGER.warn(e);
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
    }

    /**
     * Adds the edit menu.
     *
     * @param menubar the menubar
     */
    private void addEditMenu(JMenuBar menubar) {
        JMenu menu;
        JMenuItem menuItem;

        JMenu editMenu = new JMenu("Edit");
        menubar.add(editMenu);

        menu = new JMenu("Quotes");
        // menubar.add(menu);
        editMenu.add(menu);

        // menu.addSeparator();
        menuItem = new JMenuItem(new EditCurrencyAction("Currency"));
        menu.add(menuItem);

        menuItem = new JMenuItem(new EditOFXAccountIdAction("OFX Account Id"));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditWarnSuspiciousPriceAction("Warn Suspicious Price"));
        menu.add(menuItem);

        menu.addSeparator();
        menuItem = new JMenuItem(new EditRandomizeShareCountAction("Randomize Share Count"));
        menu.add(menuItem);

        // incrementallyIncreasedShareCount
        menuItem = new JMenuItem(new AbstractAction("Incrementally Increased Share Count") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] possibilities = { "true", "false" };
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + incrementallyIncreasedShareCount + "\n" + "Choices:",
                        "Set Incrementally Increased Share Count", JOptionPane.PLAIN_MESSAGE, icon, possibilities,
                        null);

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Incrementally Increased Share Count': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(incrementallyIncreasedShareCount) != 0) {
                        incrementallyIncreasedShareCount = newValue;
                        PREFS.put(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT,
                                incrementallyIncreasedShareCount.toString());
                        // to clear the pricing table
                        QuoteSource quoteSource = null;
                        stockSymbolsStringReceived(quoteSource, null);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Force <INVTRANLIST>") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] possibilities = { "true", "false" };
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + forceGeneratingINVTRANLIST + "\n" + "Choices:", "Force generating <INVTRANLIST>",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, null);

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Force <INVTRANLIST>': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(forceGeneratingINVTRANLIST) != 0) {
                        forceGeneratingINVTRANLIST = newValue;
                        PREFS.put(PREF_FORCE_GENERATING_INVTRANLIST, forceGeneratingINVTRANLIST.toString());
                        // to clear the pricing table
                        QuoteSource quoteSource = null;
                        stockSymbolsStringReceived(quoteSource, null);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(new AbstractAction("Date Offset") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                String[] possibilities = null;
                Icon icon = null;
                String s = (String) JOptionPane.showInputDialog(GUI.this,
                        "Current: " + dateOffset + "\n" + "Number of days:", "Set number of date to offset",
                        JOptionPane.PLAIN_MESSAGE, icon, possibilities, dateOffset.toString());

                // If a string was returned, say so.
                if ((s != null) && (s.length() > 0)) {
                    String value = s;
                    LOGGER.info("Selected new 'Date Offset': " + value);
                    try {
                        Integer newValue = Integer.valueOf(value);
                        if (newValue.compareTo(dateOffset) != 0) {
                            dateOffset = newValue;
                            PREFS.put(PREF_DATE_OFFSET, dateOffset.toString());
                            // to clear the pricing table
                            QuoteSource quoteSource = null;
                            stockSymbolsStringReceived(quoteSource, null);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(GUI.this, "Not a valid number - " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                }
            }
        });
        menu.add(menuItem);
    }

    /**
     * Creates the main view.
     *
     * @return the component
     */
    private Component createMainView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        Dimension preferredSize = new Dimension(600, 600);
        view.setPreferredSize(preferredSize);

        mainTabbed = new JTabbedPane(SwingConstants.BOTTOM);

        mainTabbed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                LOGGER.info("selectedIndex=" + p.getSelectedIndex());
            }
        });
        // TAB: #1
        mainTabbed.addTab("Quotes", createQuotesView());

        // TAB: #2
        downloadView = new StatementPanel();
        downloadView.setFiDir(getFiDir());
        downloadView.refreshFiDir();
        mainTabbed.addTab("Statements", downloadView);

        // TAB: #3
        backupView = new BackupPanel();
        mainTabbed.addTab("Backup", backupView);

        view.add(mainTabbed, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the quotes view.
     *
     * @return the component
     */
    private Component createQuotesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        Component comp0 = createQuotesSourceTabView();
        Component comp1 = createResultView();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, comp0, comp1);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerLocation(0.33);

        view.add(splitPane, BorderLayout.CENTER);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("< createMainDataView");
        }

        return view;
    }

    /**
     * Creates the quotes source tab view.
     *
     * @return the component
     */
    private Component createQuotesSourceTabView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        view.add(p, BorderLayout.NORTH);

        JLabel label = new JLabel("Quote Sources");
        p.add(label);

        // final JTabbedPane tabbedPane = createQuoteSourceTabs();
        final JTabbedPane tabbedPane = createQuoteSourceTabsX();

        view.add(tabbedPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the quote source tabs X.
     *
     * @return the j tabbed pane
     */
    private JTabbedPane createQuoteSourceTabsX() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                selectedQuoteSource = p.getSelectedIndex();
                LOGGER.info("selectedQuoteSource=" + selectedQuoteSource);
                QuoteSource quoteSource = null;
                stockPricesLookupStarted(quoteSource);
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> creating createYahooSourceView");
        }
        tabbedPane.addTab("Yahoo", createYahooSourceView());

//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("> creating createYahooApiSourceView");
//        }
//        tabbedPane.addTab("Yahoo Options", createYahooApiSourceView());
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("> creating createFtDotComSourceView");
//        }
//        tabbedPane.addTab("ft.com", createFtDotComSourceView());
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("> creating createYahooHistoricalSourceView");
//        }
//        tabbedPane.addTab("Yahoo Historical", createYahooHistoricalSourceView());
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("> creating createBloombergSourceView");
//        }
//        tabbedPane.addTab("Bloomberg", createBloombergSourceView());
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("> creating createTIAACREFQuoteSourceView");
//        }
//        tabbedPane.addTab("Scholarshare", createTIAACREFQuoteSourceView());

        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    /**
     * Creates the yahoo source view.
     *
     * @return the component
     */
    private Component createYahooSourceView() {
        final YahooQuoteSourcePanel view = new YahooQuoteSourcePanel(this);
        this.yahooQuoteSourceView = view;
        return view;
    }

    /**
     * Creates the yahoo api source view.
     *
     * @return the component
     */
    private Component createYahooApiSourceView() {
        final YahooApiQuoteSourcePanel view = new YahooApiQuoteSourcePanel(this);
        this.yahooApiQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the ft dot com source view.
     *
     * @return the component
     */
    private Component createFtDotComSourceView() {
        final FtDotComQuoteSourcePanel view = new FtDotComQuoteSourcePanel(this);
        this.ftDotComQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the yahoo historical source view.
     *
     * @return the component
     */
    private Component createYahooHistoricalSourceView() {
        final YahooQuoteSourcePanel view = new YahooHistoricalSourcePanel(this);
        this.yahooHistoricalQuoteSourceView = view;
        return view;
    }

    /**
     * Creates the bloomberg source view.
     *
     * @return the component
     */
    private Component createBloombergSourceView() {
        final YahooApiQuoteSourcePanel view = new BloombergQuoteSourcePanel(this);
        this.bloombergQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the TIAACREF quote source view.
     *
     * @return the component
     */
    private Component createTIAACREFQuoteSourceView() {
        final TIAACREFQuoteSourcePanel view = new TIAACREFQuoteSourcePanel(this);
        this.tIAACREFQuoteSourcePanel = view;
        return view;
    }

    /**
     * Creates the result top view.
     *
     * @return the component
     */
    private Component createResultTopView() {
        JPanel view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.PAGE_AXIS));
        view.add(createResultTopViewRow1());
        view.add(createResultTopViewRow2());
        return view;
    }

    /**
     * Creates the result top view row 2.
     *
     * @return the component
     */
    private Component createResultTopViewRow2() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
        accountIdLabel = new JLabel("OFX Account Id: " + accountId);
        view.add(accountIdLabel, BorderLayout.WEST);
        return view;
    }

    /**
     * Creates the result top view row 1.
     *
     * @return the component
     */
    private Component createResultTopViewRow1() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        defaulCurrencyLabel = new JLabel("Default currency: " + defaultCurrency);
        view.add(defaulCurrencyLabel, BorderLayout.WEST);

        TimeZone zone = null;
        String[] timeZoneIds = { "America/New_York",
                /* "Europe/London", */
        };
        clockFormatters = new SimpleDateFormat[timeZoneIds.length];
        for (int i = 0; i < timeZoneIds.length; i++) {
            clockFormatters[i] = new SimpleDateFormat("EEEEEEEEE, dd-MMM-yy HH:mm:ss z");
            zone = TimeZone.getTimeZone(timeZoneIds[i]);
            clockFormatters[i].setTimeZone(zone);
        }
        clockLabel = new JLabel(getClockDisplayString());
        scheduleClockUpdate();
        view.add(clockLabel, BorderLayout.EAST);

        return view;
    }

    /**
     * Schedule clock update.
     */
    private void scheduleClockUpdate() {
        Timer timer = new Timer();
        long delay = 1000L;
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                updateClockDisplay();
            }
        };
        long period = 1000L;
        timer.scheduleAtFixedRate(task, delay, period);
    }

    /**
     * Update clock display.
     */
    private void updateClockDisplay() {
        if (clockLabel == null) {
            return;
        }
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                String str = getClockDisplayString();
                clockLabel.setText(str);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Creates the result view.
     *
     * @return the component
     */
    private Component createResultView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        view.add(createResultTopView(), BorderLayout.NORTH);

        bottomTabs = new JTabbedPane();

        bottomTabs.add("Prices", createPricesView());

        bottomTabs.add("Exchange Rates", createExchangeRatesView());

        bottomTabs.add("Mapper", createMapperView());

        view.add(bottomTabs, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(new AbstractAction("Import *.csv file") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                QuoteSource quoteSource = new DefaultQuoteSource();
                List<AbstractStockPrice> stockPrices = new ArrayList<AbstractStockPrice>();
                getQuoteSourceListener().stockPricesReceived(quoteSource, stockPrices);
            }
        }));
        MouseListener listener = new PopupListener(popupMenu) {

            @Override
            protected void maybeShowPopup(MouseEvent e) {
                if (bottomTabs.getSelectedIndex() != 0) {
                    return;
                }
                super.maybeShowPopup(e);
            }
        };
        bottomTabs.addMouseListener(listener);
        return view;
    }

    /**
     * Creates the prices view.
     *
     * @return the component
     */
    private Component createPricesView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createPricesView");
        }
        final JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        priceFilterEdit = new JTextField(10);
        PriceTableView<AbstractStockPrice> priceScrollPane = new PriceTableView<AbstractStockPrice>(priceFilterEdit, priceList, AbstractStockPrice.class);
        view.add(priceScrollPane, BorderLayout.CENTER);

        JPanel commandView = new JPanel();
        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        commandView.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        AbstractAction action = null;

        action = new ImportAction("Import to MSMoney");
        importToMoneyButton = new JButton(action);
        importToMoneyButton.setEnabled(false);
        commandView.add(importToMoneyButton);
        commandView.add(Box.createHorizontalStrut(5));

        commandView.add(new JLabel("Last import on:"));
        commandView.add(Box.createHorizontalStrut(3));
        lastKnownImportString = PREFS.get(PREF_LAST_KNOWN_IMPORT_STRING, null);
        lastKnownImport = new JLabel(lastKnownImportString == null ? "Not known" : lastKnownImportString);
        commandView.add(lastKnownImport);

        // commandView.add(Box.createHorizontalStrut(5));

        /*
         * 
         * // view2.add(new JLabel("Filter:")); //
         * view2.add(Box.createHorizontalStrut(3)); //
         * view2.add(priceFilterEdit);
         */
        commandView.add(Box.createHorizontalGlue());
        action = new SaveOfxAction(this, "Save OFX");
        saveOfxButton = new JButton(action);
        saveOfxButton.setEnabled(false);
        commandView.add(saveOfxButton);
        view.add(commandView, BorderLayout.SOUTH);

        JMenu menu = null;
        // OFX
        menu = new JMenu("OFX");
        priceScrollPane.getPopupMenu().add(menu);
        action = new ImportAction("Open as *.ofx");
        menu.add(action);
        action = new SaveOfxAction(this, "Save");
        menu.add(action);

        // QIF
        menu = new JMenu("QIF");
        priceScrollPane.getPopupMenu().add(menu);

        // action = new AbstractAction("Open as *.qif") {
        // public void actionPerformed(ActionEvent event) {
        // try {
        // File file = null;
        //
        // file = File.createTempFile("hleofxquotes", ".qif");
        // file.deleteOnExit();
        // QifUtils.saveToQif(priceList, file);
        // log.info("file=" + file);
        //
        // } catch (IOException e) {
        // JOptionPane.showMessageDialog(view, e.getMessage(), "Error",
        // JOptionPane.ERROR_MESSAGE);
        // }
        // }
        //
        // };
        // menu.add(action);
        action = new AbstractAction("Save") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;
            private JFileChooser fc = null;

            @Override
            public void actionPerformed(ActionEvent event) {
                if (fc == null) {
                    initFileChooser();
                }
                Component parent = view;
                if (this.fc.getSelectedFile() == null) {
                    this.fc.setSelectedFile(new File("quotes.qif"));
                }

                if (fc.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
                    return;
                }
                File toFile = fc.getSelectedFile();
                PREFS.put(Action.ACCELERATOR_KEY, toFile.getAbsoluteFile().getParentFile().getAbsolutePath());
                try {
                    QifUtils.saveToQif(priceList, toFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(view, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            private void initFileChooser() {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("> creating FileChooser");
                }
                String key = Action.ACCELERATOR_KEY;
                fc = new JFileChooser(PREFS.get(key, "."));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("< creating FileChooser");
                }
            }
        };
        menu.add(action);

        return view;
    }

    /**
     * Creates the exchange rates view.
     *
     * @return the component
     */
    private Component createExchangeRatesView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createExchangeRatesView");
        }
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        PriceTableView<AbstractStockPrice> fxScrollPane = new PriceTableView<AbstractStockPrice>(filterEdit, exchangeRates, AbstractStockPrice.class);
        view.add(fxScrollPane, BorderLayout.CENTER);

        JPanel commandView = new JPanel();
        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        commandView.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        AbstractAction action = new UpdateMnyExchangeRatesTask("Update MsMoney");
        updateExchangeRateButton = new JButton(action);
        updateExchangeRateButton.setEnabled(false);
        commandView.add(updateExchangeRateButton);

        fxScrollPane.getPopupMenu().add(action);

        // commandView.add(Box.createHorizontalStrut(5));
        //
        // commandView.add(new JLabel("Last import on:"));
        // commandView.add(Box.createHorizontalStrut(3));
        // lastKnownImportString = prefs.get(PREF_LAST_KNOWN_IMPORT_STRING,
        // null);
        // lastKnownImport = new JLabel(lastKnownImportString == null ?
        // "Not known" : lastKnownImportString);
        // commandView.add(lastKnownImport);

        // commandView.add(Box.createHorizontalStrut(5));

        /*
         * 
         * // view2.add(new JLabel("Filter:")); //
         * view2.add(Box.createHorizontalStrut(3)); //
         * view2.add(priceFilterEdit);
         */
        // commandView.add(Box.createHorizontalGlue());
        // saveOfxButton = new JButton(new SaveOfxAction(this, "Save OFX"));
        // saveOfxButton.setEnabled(false);
        // commandView.add(saveOfxButton);
        //
        view.add(commandView, BorderLayout.SOUTH);

        return view;

    }

    /**
     * Creates the mapper view.
     *
     * @return the component
     */
    private Component createMapperView() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> createMapperView");
        }
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        JScrollPane scrollPane = createScrolledMapperTable(filterEdit);
        view.add(scrollPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the scrolled mapper table.
     *
     * @param filterEdit the filter edit
     * @return the j scroll pane
     */
    private JScrollPane createScrolledMapperTable(JTextField filterEdit) {
        Comparator<? super SymbolMapperEntry> comparator = new Comparator<SymbolMapperEntry>() {
            @Override
            public int compare(SymbolMapperEntry b1, SymbolMapperEntry b2) {
                return b1.getMsMoneySymbol().compareTo(b2.getMsMoneySymbol());
            }
        };
        // JTextField filterEdit = new JTextField(10);
        TextFilterator<SymbolMapperEntry> filter = null;
        // String propertyNames[] = { "stockName", "stockSymbol", };
        // filter = new BeanTextFilterator(propertyNames);
        filter = new TextFilterator<SymbolMapperEntry>() {
            @Override
            public void getFilterStrings(List<String> list, SymbolMapperEntry bean) {
                list.add(bean.getMsMoneySymbol());
                list.add(bean.getQuotesSourceSymbol());
            }
        };
        boolean addStripe = true;
        JTable table = createMapperTable(mapper, comparator, filterEdit, filter, addStripe);
        // table.setFillsViewportHeight(true);
        JScrollPane scrolledPane = new JScrollPane(table);

        return scrolledPane;
    }

    /**
     * Creates the mapper table.
     *
     * @param mapper the mapper
     * @param comparator the comparator
     * @param filterEdit the filter edit
     * @param filter the filter
     * @param addStripe the add stripe
     * @return the j table
     */
    private JTable createMapperTable(EventList<SymbolMapperEntry> mapper,
            Comparator<? super SymbolMapperEntry> comparator, JTextField filterEdit,
            TextFilterator<SymbolMapperEntry> filter, boolean addStripe) {
        EventList<SymbolMapperEntry> source = mapper;

        SortedList<SymbolMapperEntry> sortedList = null;
        if (comparator != null) {
            sortedList = new SortedList<SymbolMapperEntry>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            FilterList<SymbolMapperEntry> filterList = null;
            MatcherEditor<SymbolMapperEntry> textMatcherEditor = new TextComponentMatcherEditor<SymbolMapperEntry>(
                    filterEdit, filter);
            filterList = new FilterList<SymbolMapperEntry>(source, textMatcherEditor);
            source = filterList;
        }

        Class beanClass = SymbolMapperEntry.class;
        String propertyNames[] = { "quotesSourceSymbol", "msMoneySymbol", "type" };
        String columnLabels[] = { "Symbol", "MSMoney Symbol", "Type" };
        AdvancedTableFormat tableFormat = new BeanTableFormat(beanClass, propertyNames, columnLabels);
        final TransformedList<SymbolMapperEntry, SymbolMapperEntry> sourceProxyList = GlazedListsSwing.swingThreadProxyList(source);
        DefaultEventTableModel<SymbolMapperEntry> tableModel = new DefaultEventTableModel<SymbolMapperEntry>(sourceProxyList, tableFormat);
        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        DefaultEventSelectionModel myDefaultEventSelectionModel = new DefaultEventSelectionModel(source);
        table.setSelectionModel(myDefaultEventSelectionModel);

        if (addStripe) {
            // TableCellRenderer striped = new StripedTableCellRenderer(new
            // Color(204, 255, 204), Color.WHITE);
            int cols = table.getColumnModel().getColumnCount();
            for (int i = 0; i < cols; i++) {
                StripedTableRenderer renderer = new StripedTableRenderer();
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }

        // ???
        final int columnCount = 3;
        int[] colWidths = new int[columnCount];
        for (int i = 0; i < colWidths.length; i++) {
            colWidths[i] = -1;
        }
        colWidths = null;
        if (colWidths != null) {
            LOGGER.info("colWidths=" + colWidths);
            Object[] maxWidthValues = TableUtils.calculateMaxWidthValues(colWidths);
            TableUtils.adjustColumnSizes(table, maxWidthValues);
        }

        return table;
    }

    /**
     * Creates the price table.
     *
     * @param priceList the price list
     * @param comparator the comparator
     * @param filterEdit the filter edit
     * @param filter the filter
     * @param addStripe the add stripe
     * @return the j table
     */
    private static JTable createPriceTable(EventList<AbstractStockPrice> priceList,
            Comparator<? super AbstractStockPrice> comparator, JTextField filterEdit,
            TextFilterator<AbstractStockPrice> filter, boolean addStripe) {
        EventList<AbstractStockPrice> source = priceList;

        SortedList<AbstractStockPrice> sortedList = null;

        if (comparator != null) {
            sortedList = new SortedList<AbstractStockPrice>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            FilterList<AbstractStockPrice> filterList = null;
            MatcherEditor<AbstractStockPrice> textMatcherEditor = new TextComponentMatcherEditor<AbstractStockPrice>(
                    filterEdit, filter);
            filterList = new FilterList<AbstractStockPrice>(source, textMatcherEditor);
            source = filterList;
        }

        Class<StockPrice> beanClass = StockPrice.class;
        String propertyNames[] = { "stockSymbol", "stockName", "lastPrice", "lastTradeDate", "lastTradeTime" };
        String columnLabels[] = { "Symbol", "Name", "Price", "Last Trade Date", "Last Trade Time" };
        AdvancedTableFormat tableFormat = new BeanTableFormat(beanClass, propertyNames, columnLabels);
        TransformedList<AbstractStockPrice, AbstractStockPrice> sourceProxyList = GlazedListsSwing.swingThreadProxyList(source);
        DefaultEventTableModel<AbstractStockPrice> tableModel = new DefaultEventTableModel<AbstractStockPrice>(sourceProxyList, tableFormat);

        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        DefaultEventSelectionModel myDefaultEventSelectionModel = new DefaultEventSelectionModel(source);
        table.setSelectionModel(myDefaultEventSelectionModel);

        if (addStripe) {
            // final Color oddRowsColor = new Color(204, 255, 204);
            // final Color evenRowsColor = Color.WHITE;
            TableCellRenderer renderer = null;
            // renderer = new StripedTableCellRenderer(oddRowsColor,
            // evenRowsColor) {
            //
            // public Component getTableCellRendererComponent(JTable table,
            // Object value, boolean isSelected, boolean hasFocus, int row, int
            // column) {
            // Component rendererComponent = null;
            // rendererComponent = super.getTableCellRendererComponent(table,
            // value, isSelected, hasFocus, row, column);
            // if (column == 3) {
            // // ((JLabel)
            // rendererComponent).setHorizontalAlignment(JLabel.RIGHT);
            // // (javax.swing.JLabel)
            // rendererComponent).setHorizontalAlignment(JLabel.RIGHT);
            // rendererComponent.setEnabled(false);
            // }
            // return rendererComponent;
            // }
            //
            // };
            int cols = table.getColumnModel().getColumnCount();
            for (int i = 0; i < cols; i++) {
                renderer = new StripedTableRenderer() {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void setCellHorizontalAlignment(int column) {
                        super.setCellHorizontalAlignment(column);
                        if ((column == 0) || (column == 2) || (column == 3)) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                    }
                };
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
        // String dateFormat = "yyyyMMddHHMMSS";
        // DateTableCellRenderer renderer = new
        // DateTableCellRenderer(dateFormat);
        // table.getColumnModel().getColumn(2).setCellRenderer(renderer);
        //
        // final int columnCount = 3;
        // int[] colWidths = new int[columnCount];
        // for (int i = 0; i < colWidths.length; i++) {
        // colWidths[i] = -1;
        // }
        // colWidths = null;
        // if (colWidths != null) {
        // log.info("colWidths=" + colWidths);
        // Object[] maxWidthValues =
        // TableUtils.calculateMaxWidthValues(colWidths);
        // TableUtils.adjustColumnSizes(table, maxWidthValues);
        // }

        return table;
    }

    /**
     * Show main frame.
     */
    protected void showMainFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Gets the output files.
     *
     * @return the output files
     */
    public List<File> getOutputFiles() {
        return outputFiles;
    }

    /**
     * Sets the output files.
     *
     * @param outputFiles the new output files
     */
    public void setOutputFiles(List<File> outputFiles) {
        this.outputFiles = outputFiles;
        if (outputFiles != null) {
            Runnable doRun = new Runnable() {
                @Override
                public void run() {
                    if (saveOfxButton != null) {
                        saveOfxButton.setEnabled(true);
                    }
                    if (importToMoneyButton != null) {
                        importToMoneyButton.setEnabled(true);
                    }
                    if (updateExchangeRateButton != null) {
                        updateExchangeRateButton.setEnabled(true);
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);
        }
    }

    /**
     * Delete output file.
     *
     * @param outputFile the output file
     */
    private void deleteOutputFile(File outputFile) {
        if ((outputFile != null) && (outputFile.exists())) {
            if (!outputFile.delete()) {
                LOGGER.warn("Failed to delete outputFile=" + outputFile);
            }
        }
    }

    /**
     * Stock symbols string received.
     *
     * @param quoteSource the quote source
     * @param stockSymbolsString the stock symbols string
     */
    public void stockSymbolsStringReceived(QuoteSource quoteSource, String stockSymbolsString) {
        clearPriceTable();
        clearMapperTable();
        this.saveOfxButton.setEnabled(false);
        this.importToMoneyButton.setEnabled(false);
        if (this.updateExchangeRateButton != null) {
            this.updateExchangeRateButton.setEnabled(false);
        }
    }

    /**
     * Gets the thread pool.
     *
     * @return the thread pool
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Gets the clock display string.
     *
     * @return the clock display string
     */
    private String getClockDisplayString() {
        Date date = new Date();
        long time = date.getTime();
        // to seconds
        time = time / 1000;
        int i = (int) (time % clockFormatters.length);
        SimpleDateFormat formatter = clockFormatters[i];
        // String timeZoneDisplayName = "[" +
        // formatter.getTimeZone().getDisplayName() + "]";
        String str = formatter.format(date);
        return str;
    }

    /**
     * Gets the current working directory.
     *
     * @return the current working directory
     */
    public static String getCurrentWorkingDirectory() {
        return new File(".").getAbsoluteFile().getAbsolutePath();
    }

    /**
     * Gets the yahoo quote server.
     *
     * @return the yahoo quote server
     */
    private String getYahooQuoteServer() {
        String yahooQuoteServer = null;
        YahooQuoteSourcePanel quoteSourceView = getYahooQuoteSourceView();
        if (quoteSourceView != null) {
            yahooQuoteServer = quoteSourceView.getQuoteServer();
        } else {
            yahooQuoteServer = YahooQuotesGetter.DEFAULT_HOST;
        }
        return yahooQuoteServer;
    }

    /**
     * Gets the yahoo quote source view.
     *
     * @return the yahoo quote source view
     */
    public YahooQuoteSourcePanel getYahooQuoteSourceView() {
        return yahooQuoteSourceView;
    }

    /**
     * Gets the prefs.
     *
     * @return the prefs
     */
    public static Preferences getPrefs() {
        return PREFS;
    }

    /**
     * Gets the quote source listener.
     *
     * @return the quote source listener
     */
    public QuoteSourceListener getQuoteSourceListener() {
        return quoteSourceListener;
    }

    /**
     * Gets the date offset.
     *
     * @return the date offset
     */
    public Integer getDateOffset() {
        return dateOffset;
    }

    /**
     * Gets the force generating INVTRANLIST.
     *
     * @return the force generating INVTRANLIST
     */
    public Boolean getForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    /**
     * Gets the randomize share count.
     *
     * @return the randomize share count
     */
    public Boolean getRandomizeShareCount() {
        return randomizeShareCount;
    }

    /**
     * Gets the suspicious price.
     *
     * @return the suspicious price
     */
    public Integer getSuspiciousPrice() {
        return suspiciousPrice;
    }

    /**
     * Gets the incrementally increased share count.
     *
     * @return the incrementally increased share count
     */
    public Boolean getIncrementallyIncreasedShareCount() {
        return incrementallyIncreasedShareCount;
    }

    /**
     * Sets the incrementally increased share count.
     *
     * @param incrementallyIncreasedShareCount the new incrementally increased share count
     */
    public void setIncrementallyIncreasedShareCount(Boolean incrementallyIncreasedShareCount) {
        this.incrementallyIncreasedShareCount = incrementallyIncreasedShareCount;
    }

    /**
     * Select new currency.
     *
     * @param value the value
     */
    private void selectNewCurrency(String value) {
        LOGGER.info("Selected new currency: " + value);
        String newValue = value;
        if (newValue.compareTo(defaultCurrency) != 0) {
            defaultCurrency = value;
            PREFS.put(PREF_DEFAULT_CURRENCY, defaultCurrency);
            defaulCurrencyLabel.setText("Default currency: " + defaultCurrency);
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * Select new account id.
     *
     * @param value the value
     */
    private void selectNewAccountId(String value) {
        LOGGER.info("Selected new 'OFX Account Id': " + value);
        String newValue = value;
        if (newValue.compareTo(accountId) != 0) {
            accountId = newValue;
            PREFS.put(PREF_ACCOUNT_ID, accountId);
            accountIdLabel.setText("OFX Account Id: " + accountId);
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        VelocityUtils.initVelocity();

        String implementationVendorId = "com.le.tools.moneyutils";
        String buildNumber = BuildNumber.findBuilderNumber(implementationVendorId);
        if (buildNumber == null) {
            LOGGER.warn("Cannot find buildNumber from Manifest.");
            LOGGER.warn("Using built-in buildNumber which is likely to be wrong!");
        } else {
            GUI.VERSION = buildNumber;
        }

        String title = "OFX - Update stock prices - " + GUI.VERSION;
        LOGGER.info(title);

        String cwd = "Current directory is " + getCurrentWorkingDirectory();
        LOGGER.info(cwd);

        final GUI mainFrame = new GUI(title);
        LOGGER.info("Using quote server: " + mainFrame.getYahooQuoteServer());
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                mainFrame.showMainFrame();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("post mainFrame.showMainFrame()");
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Gets the fi dir.
     *
     * @return the fi dir
     */
    public File getFiDir() {
        return fiDir;
    }

    /**
     * Sets the fi dir.
     *
     * @param fiDir the new fi dir
     */
    public void setFiDir(File fiDir) {
        this.fiDir = fiDir;
    }

}
