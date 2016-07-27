package com.le.tools.moneyutils.ofx.quotes;

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
import javax.swing.table.TableCellRenderer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.le.tools.moneyutils.bloomberg.BloombergQuoteSourcePanel;
import com.le.tools.moneyutils.data.SymbolMapper;
import com.le.tools.moneyutils.fi.UpdateFiDir;
import com.le.tools.moneyutils.fi.VelocityUtils;
import com.le.tools.moneyutils.ft.FtDotComQuoteSourcePanel;
import com.le.tools.moneyutils.fx.UpdateFx;
import com.le.tools.moneyutils.jna.ImportDialogAutoClickService;
import com.le.tools.moneyutils.misc.BuildNumber;
import com.le.tools.moneyutils.ofx.statement.StatementPanel;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxPriceInfo;
import com.le.tools.moneyutils.ofx.xmlbeans.OfxSaveParameter;
import com.le.tools.moneyutils.scholarshare.TIAACREFQuoteSourcePanel;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;
import com.le.tools.moneyutils.stockprice.Price;
import com.le.tools.moneyutils.stockprice.StockPrice;
import com.le.tools.moneyutils.yahoo.CurrencyUtils;
import com.le.tools.moneyutils.yahoo.GetYahooQuotes;
import com.le.tools.moneyutils.yahoo.YahooApiQuoteSourcePanel;
import com.le.tools.moneyutils.yahoo.YahooHistoricalSourcePanel;
import com.le.tools.moneyutils.yahoo.YahooQuoteSourcePanel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import net.ofx.types.x2003.x04.CurrencyEnum;

public class GUI extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // TODO: le.com.tools.moneyutils.ofx.quotes.GUI
    private static final Preferences prefs = Preferences
            .userNodeForPackage(le.com.tools.moneyutils.ofx.quotes.GUI.class);

    private static final Logger log = Logger.getLogger(GUI.class);

    private static final String VERSION_PREFIX = "Build";
    // private static final String VERSION_PREFIX = "SNAPSHOT";

    private static final String VERSION_SUFFIX = "01";

    // Build_20110706_31
    public static String VERSION = VERSION_PREFIX + "_" + "20111104" + "_" + VERSION_SUFFIX;

    private static final String PREF_DEFAULT_CURRENCY = "defaultCurrency";
    private static final String PREF_LAST_KNOWN_IMPORT_STRING = "lastKnownImportString";

    private static final String PREF_RANDOMIZE_SHARE_COUNT = "randomizeShareCount";

    private static final String PREF_FORCE_GENERATING_INVTRANLIST = "forceGeneratingINVTRANLIST";

    protected static final String HOME_PAGE = "http://code.google.com/p/hle-ofx-quotes/";

    private static final String PREF_DATE_OFFSET = "dateOffset";

    private static final String PREF_SUSPICIOUS_PRICE = "suspiciousPrice";

    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT = "incrementallyIncreasedShareCount";

    private static final String PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE = "incrementallyIncreasedShareCountValue";

    private static final String PREF_ACCOUNT_ID = "accountId";

    private static final String PREF_IMPORT_DIALOG_AUTO_CLICK = "importDialogAutoClick";

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private List<File> outputFiles;

    private JTextPane resultView;

    private EventList<AbstractStockPrice> priceList = new BasicEventList<AbstractStockPrice>();
    private EventList<AbstractStockPrice> exchangeRates = new BasicEventList<AbstractStockPrice>();
    private EventList<SymbolMapperEntry> mapper = new BasicEventList<SymbolMapperEntry>();

    private JTextField priceFilterEdit;

    private JLabel defaulCurrencyLabel;
    private JLabel clockLabel;

    private SimpleDateFormat[] clockFormatters;

    private JButton importToMoneyButton;

    private JLabel lastKnownImport;

    private String lastKnownImportString;

    private JButton updateExchangeRateButton;

    private JButton saveOfxButton;

    private String defaultCurrency = prefs.get(PREF_DEFAULT_CURRENCY, CurrencyEnum.USD.toString());

    private Boolean randomizeShareCount = Boolean.valueOf(prefs.get(PREF_RANDOMIZE_SHARE_COUNT, "False"));
    private Random random = new Random();

    private Boolean incrementallyIncreasedShareCount = Boolean
            .valueOf(prefs.get(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT, "False"));

    private JTabbedPane bottomTabs;

    private YahooQuoteSourcePanel yahooQuoteSourceView;

    private YahooApiQuoteSourcePanel yahooApiQuoteSourcePanel;

    private FtDotComQuoteSourcePanel ftDotComQuoteSourcePanel;

    private YahooQuoteSourcePanel yahooHistoricalQuoteSourceView;

    private QuoteSourceListener quoteSourceListener;

    private Boolean forceGeneratingINVTRANLIST = Boolean.valueOf(prefs.get(PREF_FORCE_GENERATING_INVTRANLIST, "False"));

    private Integer dateOffset = Integer.valueOf(prefs.get(PREF_DATE_OFFSET, "0"));

    private Integer suspiciousPrice = Integer.valueOf(prefs.get(PREF_SUSPICIOUS_PRICE, "10000"));

    private String accountId = prefs.get(PREF_ACCOUNT_ID, OfxPriceInfo.DEFAULT_ACCOUNT_ID);

    private JLabel accountIdLabel;

    private StatementPanel downloadView;

    private JTabbedPane mainTabbed;

    private int selectedQuoteSource;

    private ImportDialogAutoClickService importDialogAutoClickService;

    private NumberFormat priceFormatter;

    private YahooApiQuoteSourcePanel bloombergQuoteSourcePanel;

    private TIAACREFQuoteSourcePanel tIAACREFQuoteSourcePanel;

    private BackupPanel backupView;

    // TODO_FI
    private File fiDir = new File("fi");

    private final class EditRandomizeShareCountAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private EditRandomizeShareCountAction(String name) {
            super(name);
        }

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
                log.info("Selected new 'Randomize Share Count': " + value);
                Boolean newValue = Boolean.valueOf(value);
                if (newValue.compareTo(randomizeShareCount) != 0) {
                    randomizeShareCount = newValue;
                    prefs.put(PREF_RANDOMIZE_SHARE_COUNT, randomizeShareCount.toString());
                    // to clear the pricing table
                    QuoteSource quoteSource = null;
                    stockSymbolsStringReceived(quoteSource, null);
                }
            } else {
            }
        }
    }

    private final class EditWarnSuspiciousPriceAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private EditWarnSuspiciousPriceAction(String name) {
            super(name);
        }

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
                log.info("Selected new 'Warn Suspicious Price': " + value);
                try {
                    Integer newValue = Integer.valueOf(value);
                    if (newValue.compareTo(suspiciousPrice) != 0) {
                        suspiciousPrice = newValue;
                        prefs.put(PREF_SUSPICIOUS_PRICE, suspiciousPrice.toString());
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

    private final class EditOFXAccountIdAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private EditOFXAccountIdAction(String name) {
            super(name);
        }

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

    private final class EditCurrencyAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private EditCurrencyAction(String name) {
            super(name);
        }

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

    private final class UpdateMnyExchangeRatesTask extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private UpdateMnyExchangeRatesTask(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            // JOptionPane.showMessageDialog(GUI.this,
            // "Not yet implemented.");
            try {
                UpdateFx.invoke();
            } catch (Exception e) {
                log.error(e);

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
                            log.warn("User cancel downloading!");
                            if (!toJarFile.delete()) {
                                log.warn("Failed to delete file=" + toJarFile);
                            } else {
                                log.info("Deleted file=" + toJarFile);
                            }
                        }
                    } catch (IOException e) {
                        log.warn(e, e);
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

        private boolean getJarFile(String uri, File toJarFile, final ProgressMonitor progressMonitor)
                throws IOException {
            boolean canceled = false;
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(uri);
            log.info("GET " + uri);
            HttpResponse httpResponse = client.execute(httpGet);

            Long contentLength = -1L;
            Header header = httpResponse.getFirstHeader("Content-Length");
            if (header != null) {
                String value = header.getValue();
                log.info(header.getName() + ": " + value);
                if ((value != null) && (value.length() > 0)) {
                    try {
                        contentLength = Long.valueOf(value);
                    } catch (NumberFormatException e) {
                        log.warn(e);
                    }
                }
            }
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            log.info("  statusCode=" + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                BufferedInputStream in = null;
                BufferedOutputStream out = null;

                try {
                    out = new BufferedOutputStream(new FileOutputStream(toJarFile));
                    log.info("  ... saving to " + toJarFile);
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

                                if (log.isDebugEnabled()) {
                                    log.debug(total + "/" + contentLength + ", " + message);
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
                    if (log.isDebugEnabled()) {
                        log.debug("  > DONE saving");
                    }

                    if (in != null) {
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("  calling in.close()");
                            }
                            in.close();
                        } finally {
                            in = null;
                        }
                    }
                    if (out != null) {
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("  calling out.close()");
                            }
                            out.close();
                        } finally {
                            out = null;
                        }
                    }
                    log.info("  > DONE saving (post-closing)");
                }
            } else {
                throw new IOException(statusLine.toString());
            }

            log.info("canceled=" + canceled);

            return !canceled;
        }

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

        private void notifyCanceled(HttpGet httpGet, HttpEntity entity, final ProgressMonitor progressMonitor) {
            log.warn("progressMonitor.isCanceled()=" + progressMonitor.isCanceled());
            if (httpGet != null) {
                httpGet.abort();
            }
            if (entity != null) {
                log.info("  calling entity.consumeContent()");
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    log.warn("Failed to entity.consumeContent(), " + e);
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

    private final class ClosingWindowListener implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent event) {
            log.info("> windowClosing");
            try {
                shutdown();
            } finally {
                // TODO: see if this will help with JNA crash on the way out
                log.info("> Calling System.gc()");
                System.gc();
            }
        }

        @Override
        public void windowClosed(WindowEvent event) {
            log.info("> windowClosed");
            try {
                shutdown();
            } finally {
                // TODO: see if this will help with JNA crash on the way out
                log.info("> Calling System.gc()");
                System.gc();
            }
        }

        private void shutdown() {
            if (threadPool != null) {
                List<Runnable> tasks = threadPool.shutdownNow();
                log.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
                long timeout = 1L;
                TimeUnit unit = TimeUnit.MINUTES;
                try {
                    log.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                    if (!threadPool.awaitTermination(timeout, unit)) {
                        log.warn("Timed-out waiting for threadPool.awaitTermination");
                    }
                } catch (InterruptedException e) {
                    log.error(e, e);
                } finally {
                    log.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                }
            }
            if (importDialogAutoClickService != null) {
                importDialogAutoClickService.setEnable(false);
                importDialogAutoClickService.shutdown();
            }
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }
    }

    private final class StockPricesReceivedTask implements Runnable {
        private final List<AbstractStockPrice> beans;
        private final Double badPrice;
        private final FxTable fxTable;
        private final boolean hasWrappedShareCount;
        private final SymbolMapper symbolMapper;
        private final QuoteSource quoteSource;

        private StockPricesReceivedTask(List<AbstractStockPrice> beans, Double badPrice, FxTable fxTable,
                boolean hasWrappedShareCount, SymbolMapper symbolMapper, QuoteSource quoteSource) {
            this.beans = beans;
            this.badPrice = badPrice;
            this.fxTable = fxTable;
            this.hasWrappedShareCount = hasWrappedShareCount;
            this.symbolMapper = symbolMapper;
            this.quoteSource = quoteSource;
        }

        @Override
        public void run() {
            if (log.isDebugEnabled()) {
                log.debug("> stockPricesReceived, size=" + beans.size());
            }

            priceList.getReadWriteLock().writeLock().lock();
            try {
                priceList.clear();
                priceList.addAll(beans);
            } finally {
                priceList.getReadWriteLock().writeLock().unlock();
                if (log.isDebugEnabled()) {
                    log.debug("< stockPricesReceived");
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
                if (log.isDebugEnabled()) {
                    log.debug("< exchangeRatesReceived");
                }
            }

            try {
                boolean onePerFile = quoteSource.isHistoricalQuotes();
                List<File> ofxFiles = saveToOFX(beans, symbolMapper, fxTable, onePerFile);
                for (File ofxFile : ofxFiles) {
                    if (log.isDebugEnabled()) {
                        log.debug("ofxFile=" + ofxFile);
                    }
                }

                File csvFile = saveToCsv(beans);
                log.info("csvFile=" + csvFile);

                mapper.getReadWriteLock().writeLock().lock();
                try {
                    mapper.clear();
                    mapper.addAll(symbolMapper.getEntries());
                } finally {
                    mapper.getReadWriteLock().writeLock().unlock();
                }
            } catch (IOException e) {
                log.warn(e);
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

    private final class CreateNewFi extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private CreateNewFi(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            String fiName = JOptionPane.showInputDialog(GUI.this, "Enter a new 'Financial Institution' name");
            if (fiName == null) {
                // cancel
                return;
            }

            fiName = fiName.trim();
            if (fiName.length() <= 0) {
                return;
            }
            // TODO_FI
            // File topDir = new File("fi");
            File topDir = getFiDir();
            if ((!topDir.exists()) && (!topDir.mkdirs())) {
                JOptionPane.showMessageDialog(GUI.this, "Cannot create dir\ndir=" + topDir.getAbsolutePath(),
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File d = new File(topDir, fiName);
            if (d.exists()) {
                JOptionPane.showMessageDialog(GUI.this, "Directory exist\ndir=" + d.getAbsolutePath(), "Error creating",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!d.mkdirs()) {
                JOptionPane.showMessageDialog(GUI.this, "Cannot create dir\ndir=" + d.getAbsolutePath(),
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Created new FI dir=" + d.getAbsolutePath());
            String fiPropertiesFileName = UpdateFiDir.DEFAULT_PROPERTIES_FILENAME;
            String sampleFileName = "samples" + "/" + fiPropertiesFileName;
            URL url = OfxUtils.getResource(sampleFileName);
            if (url == null) {
                JOptionPane.showMessageDialog(GUI.this, "Cannot find sample file\nfile=" + sampleFileName,
                        "Error creating", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File fiPropertiesFile = null;
            try {
                fiPropertiesFile = new File(d, fiPropertiesFileName);
                Utils.copyToFile(url, fiPropertiesFile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(GUI.this, "Error creating " + fiPropertiesFileName, "Error creating",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane
                    .showMessageDialog(GUI.this,
                            "Succesfully created dirctory for fi=" + fiName + "\n" + "Please edit file\n"
                                    + fiPropertiesFile.getAbsolutePath(),
                            "FI Created", JOptionPane.INFORMATION_MESSAGE);

            downloadView.refreshFiDir();

            mainTabbed.setSelectedIndex(1);
        }

    }

    private final class ProfileSelectedAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private Properties props;

        private ProfileSelectedAction(String name, Properties props) {
            super(name);
            this.props = props;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (props == null) {
                return;
            }

            String accountId = props.getProperty("accountId");
            if (!isNull(accountId)) {
                selectNewAccountId(accountId);
            }
            String currency = props.getProperty("currency");
            if (!isNull(currency)) {
                selectNewCurrency(currency);
            }
        }
    }

    private final class ExitAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private ExitAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            log.info("> ExitAction.actionPerformed");

            List<Runnable> tasks = threadPool.shutdownNow();
            log.info("Number of not-run tasks=" + ((tasks == null) ? 0 : tasks.size()));
            long timeout = 1L;
            TimeUnit unit = TimeUnit.MINUTES;
            try {
                log.info("WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
                if (!threadPool.awaitTermination(timeout, unit)) {
                    log.warn("Timed-out waiting for threadPool.awaitTermination");
                }
            } catch (InterruptedException e) {
                log.error(e, e);
            } finally {
                log.info("DONE WAITING - threadPool.awaitTermination: " + timeout + " " + unit.toString());
            }

            if (importDialogAutoClickService != null) {
                importDialogAutoClickService.setEnable(false);
                importDialogAutoClickService.shutdown();
            }

            log.info("> Calling System.gc()");
            System.gc();
            log.info("> Calling System.exit(0)");
            System.exit(0);
        }
    }

    private final class UpdateResultViewTask implements Runnable {
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
                log.warn(e);
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

        }
    }

    private final class ImportAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private ImportAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            log.info("> Import action");
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
                                prefs.put(PREF_LAST_KNOWN_IMPORT_STRING, lastKnownImportString);
                            }
                        };
                        SwingUtilities.invokeLater(doRun);
                    }

                }

            };
            threadPool.execute(command);
        }
    }

    protected void clearPriceTable() {
        priceList.clear();
        exchangeRates.clear();
        if (priceFilterEdit != null) {
            priceFilterEdit.setText("");
        }
    }

    protected void clearMapperTable() {
        mapper.clear();
    }

    private void stockPricesReceived(final QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
        SymbolMapper symbolMapper = SymbolMapper.createDefaultSymbolMapper();

        final List<AbstractStockPrice> beans = (stockPrices != null) ? stockPrices
                : new ArrayList<AbstractStockPrice>();
        updateLastPriceCurrency(beans, defaultCurrency, symbolMapper);

        if (randomizeShareCount) {
            int randomInt = random.nextInt(998);
            randomInt = randomInt + 1;
            double value = randomInt / 1000.00;
            log.info("randomizeShareCount=" + randomizeShareCount + ", value=" + value);
            for (AbstractStockPrice bean : beans) {
                bean.setUnits(value);
            }
        }

        boolean hasWrappedShareCount = false;
        if (incrementallyIncreasedShareCount) {
            String key = PREF_INCREMENTALLY_INCREASED_SHARE_COUNT_VALUE;
            double value = prefs.getDouble(key, 0.000);
            if (value > 0.999) {
                value = 0.000;
                // TODO
                log.warn("incrementallyIncreasedShareCount, is wrapping back to " + value);
                hasWrappedShareCount = true;
            }
            value = value + 0.001;
            for (AbstractStockPrice bean : beans) {
                bean.setUnits(value);
            }
            if (log.isDebugEnabled()) {
                log.debug("incrementallyIncreasedShareCount=" + incrementallyIncreasedShareCount + ", value=" + value);
            }
            prefs.putDouble(key, value);
        }

        // for (StockPriceBean bean : beans) {
        // String currency = bean.getCurrency();
        // if (isNull(currency)) {
        // String symbol = bean.getStockSymbol();
        // String overridingCurrency = getMapperCurrency(symbol, mapper);
        // log.info("symbol: " + symbol + ", overridingCurrency=" +
        // overridingCurrency);
        // if (!isNull(overridingCurrency)) {
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

    private File saveToOFX(final List<AbstractStockPrice> stockPrices, SymbolMapper symbolMapper, FxTable fxTable)
            throws IOException {
        File outputFile = File.createTempFile("quotes", ".ofx");
        outputFile.deleteOnExit();
        if (log.isDebugEnabled()) {
            log.debug("outputFile=" + outputFile.getAbsolutePath());
        }

        log.info("forceGeneratingINVTRANLIST=" + forceGeneratingINVTRANLIST);
        OfxSaveParameter params = new OfxSaveParameter();
        params.setDefaultCurrency(defaultCurrency);
        params.setAccountId(accountId);
        params.setForceGeneratingINVTRANLIST(forceGeneratingINVTRANLIST);
        params.setDateOffset(dateOffset);
        OfxPriceInfo.save(stockPrices, outputFile, params, symbolMapper, fxTable);
        return outputFile;
    }

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

    private String getMapperCurrency(String symbol, SymbolMapper mapper, String defaultValue) {
        String quoteSourceSymbol = null;
        String currency = defaultValue;
        for (SymbolMapperEntry entry : mapper.getEntries()) {
            quoteSourceSymbol = entry.getQuotesSourceSymbol();
            if (isNull(quoteSourceSymbol)) {
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("s=" + quoteSourceSymbol + ", symbol=" + symbol);
            }
            if (quoteSourceSymbol.compareToIgnoreCase(symbol) != 0) {
                continue;
            }
            currency = entry.getQuotesSourceCurrency();
            if (log.isDebugEnabled()) {
                log.debug("getMapperCurrency: s=" + quoteSourceSymbol + ", currency=" + currency);
            }
            if (!isNull(currency)) {
                return currency;
            }
        }
        return currency;
    }

    private void updateLastPriceCurrency(List<AbstractStockPrice> stockPrices, String defaultCurrency,
            SymbolMapper symbolMapper) {
        for (AbstractStockPrice stockPrice : stockPrices) {
            Price price = stockPrice.getLastPrice();
            if ((defaultCurrency != null) && (price.getCurrency() == null)) {
                price.setCurrency(defaultCurrency);
            }
            String currency = stockPrice.getCurrency();
            if (isNull(currency)) {
                String symbol = stockPrice.getStockSymbol();
                String overridingCurrency = null;
                overridingCurrency = getMapperCurrency(symbol, symbolMapper, overridingCurrency);
                if (log.isDebugEnabled()) {
                    log.info("symbol: " + symbol + ", overridingCurrency=" + overridingCurrency);
                }
                if (!isNull(overridingCurrency)) {
                    stockPrice.setCurrency(overridingCurrency);
                    stockPrice.updateLastPriceCurrency();
                }
            }
        }
    }

    public JButton getSaveOfxButton() {
        return saveOfxButton;
    }

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

    private void updateMainMenu() {
        JMenuBar menubar = new JMenuBar();

        addFileMenu(menubar);

        addEditMenu(menubar);

        addToolsMenu(menubar);

        addHelpMenu(menubar);

        setJMenuBar(menubar);
    }

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
                    prefs.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.TRUE);
                } else {
                    importDialogAutoClickService.setEnable(false);
                    prefs.putBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
                }
            }
        };
        importAutoClick.addActionListener(listener);
        Boolean autoClick = prefs.getBoolean(PREF_IMPORT_DIALOG_AUTO_CLICK, Boolean.FALSE);
        importAutoClick.setSelected(autoClick);
        importDialogAutoClickService.setEnable(autoClick);
        menu.add(importAutoClick);

        menubar.add(menu);
    }

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
                    log.error(e, e);
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
                    log.error(e, e);
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
                    log.error(e, e);
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
                    log.error(e, e);
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
                        log.warn(e);
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

    private void addFileMenu(JMenuBar menubar) {
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("File");
        menubar.add(menu);

        JMenu newMenu = new JMenu("New");
        menu.add(newMenu);

        menuItem = new JMenuItem(new CreateNewFi("Financial Institution"));
        newMenu.add(menuItem);

        JMenu profilesMenu = new JMenu("Open Quotes Profiles");
        menu.add(profilesMenu);
        addProfilesToMenu(profilesMenu);

        menu.addSeparator();
        menuItem = new JMenuItem(new ExitAction("Exit"));
        menu.add(menuItem);
    }

    private void addProfilesToMenu(JMenu profilesMenu) {
        File dir = new File("profiles");
        if (!dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            log.info("file=" + file);
            if (file.isDirectory()) {
                continue;
            }
            if (!file.isFile()) {
                continue;
            }

            addProfileToMenu(profilesMenu, file);
        }
    }

    private void addProfileToMenu(JMenu profilesMenu, File file) {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            props.load(reader);
            String name = props.getProperty("name");
            if (isNull(name)) {
                name = file.getName();
            }
            JMenuItem item = new JMenuItem(new ProfileSelectedAction(name, props));
            profilesMenu.add(item);
        } catch (IOException e) {
            log.warn(e);
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
    }

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
                    log.info("Selected new 'Incrementally Increased Share Count': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(incrementallyIncreasedShareCount) != 0) {
                        incrementallyIncreasedShareCount = newValue;
                        prefs.put(PREF_INCREMENTALLY_INCREASED_SHARE_COUNT,
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
                    log.info("Selected new 'Force <INVTRANLIST>': " + value);
                    Boolean newValue = Boolean.valueOf(value);
                    if (newValue.compareTo(forceGeneratingINVTRANLIST) != 0) {
                        forceGeneratingINVTRANLIST = newValue;
                        prefs.put(PREF_FORCE_GENERATING_INVTRANLIST, forceGeneratingINVTRANLIST.toString());
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
                    log.info("Selected new 'Date Offset': " + value);
                    try {
                        Integer newValue = Integer.valueOf(value);
                        if (newValue.compareTo(dateOffset) != 0) {
                            dateOffset = newValue;
                            prefs.put(PREF_DATE_OFFSET, dateOffset.toString());
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
                log.info("selectedIndex=" + p.getSelectedIndex());
            }
        });
        // TAB: #1
        mainTabbed.addTab("Quotes", createQuotesView());

        // TAB: #2
        downloadView = new StatementPanel();
        downloadView.refreshFiDir();
        mainTabbed.addTab("Statements", downloadView);

        // TAB: #3
        backupView = new BackupPanel();
        mainTabbed.addTab("Backup", backupView);

        view.add(mainTabbed, BorderLayout.CENTER);

        return view;
    }

    public static final boolean isNull(String str) {
        if (str == null) {
            return true;
        }

        if (str.length() <= 0) {
            return true;
        }

        return false;
    }

    private Component createQuotesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        Component comp0 = createQuotesSourceTabView();
        Component comp1 = createResultView();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, comp0, comp1);
        splitPane.setResizeWeight(0.33);
        splitPane.setDividerLocation(0.33);

        view.add(splitPane, BorderLayout.CENTER);

        if (log.isDebugEnabled()) {
            log.debug("< createMainDataView");
        }

        return view;
    }

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

    private JTabbedPane createQuoteSourceTabsX() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                JTabbedPane p = (JTabbedPane) event.getSource();
                selectedQuoteSource = p.getSelectedIndex();
                log.info("selectedQuoteSource=" + selectedQuoteSource);
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

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooSourceView");
        }
        tabbedPane.addTab("Yahoo", createYahooSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooApiSourceView");
        }
        tabbedPane.addTab("Yahoo Options", createYahooApiSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createFtDotComSourceView");
        }
        tabbedPane.addTab("ft.com", createFtDotComSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createYahooHistoricalSourceView");
        }
        tabbedPane.addTab("Yahoo Historical", createYahooHistoricalSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createBloombergSourceView");
        }
        tabbedPane.addTab("Bloomberg", createBloombergSourceView());

        if (log.isDebugEnabled()) {
            log.debug("> creating createTIAACREFQuoteSourceView");
        }
        tabbedPane.addTab("Scholarshare", createTIAACREFQuoteSourceView());

        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }

    private Component createYahooSourceView() {
        final YahooQuoteSourcePanel view = new YahooQuoteSourcePanel(this);
        this.yahooQuoteSourceView = view;
        return view;
    }

    private Component createYahooApiSourceView() {
        final YahooApiQuoteSourcePanel view = new YahooApiQuoteSourcePanel(this);
        this.yahooApiQuoteSourcePanel = view;
        return view;
    }

    private Component createFtDotComSourceView() {
        final FtDotComQuoteSourcePanel view = new FtDotComQuoteSourcePanel(this);
        this.ftDotComQuoteSourcePanel = view;
        return view;
    }

    private Component createYahooHistoricalSourceView() {
        final YahooQuoteSourcePanel view = new YahooHistoricalSourcePanel(this, "yahooHistoricalStockSymbols");
        this.yahooHistoricalQuoteSourceView = view;
        return view;
    }

    private Component createBloombergSourceView() {
        final YahooApiQuoteSourcePanel view = new BloombergQuoteSourcePanel(this);
        this.bloombergQuoteSourcePanel = view;
        return view;
    }

    private Component createTIAACREFQuoteSourceView() {
        final TIAACREFQuoteSourcePanel view = new TIAACREFQuoteSourcePanel(this);
        this.tIAACREFQuoteSourcePanel = view;
        return view;
    }

    private Component createResultTopView() {
        JPanel view = new JPanel();
        view.setLayout(new BoxLayout(view, BoxLayout.PAGE_AXIS));
        view.add(createResultTopViewRow1());
        view.add(createResultTopViewRow2());
        return view;
    }

    private Component createResultTopViewRow2() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
        accountIdLabel = new JLabel("OFX Account Id: " + accountId);
        view.add(accountIdLabel, BorderLayout.WEST);
        return view;
    }

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

    private Component createPricesView() {
        if (log.isDebugEnabled()) {
            log.debug("> createPricesView");
        }
        final JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        priceFilterEdit = new JTextField(10);
        PriceTableView priceScrollPane = new PriceTableView(priceFilterEdit, priceList);
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
        lastKnownImportString = prefs.get(PREF_LAST_KNOWN_IMPORT_STRING, null);
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
                prefs.put(SaveOfxAction.PREF_SAVE_OFX_DIR, toFile.getAbsoluteFile().getParentFile().getAbsolutePath());
                try {
                    QifUtils.saveToQif(priceList, toFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(view, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            private void initFileChooser() {
                if (log.isDebugEnabled()) {
                    log.debug("> creating FileChooser");
                }
                String key = SaveOfxAction.PREF_SAVE_OFX_DIR;
                fc = new JFileChooser(prefs.get(key, "."));
                if (log.isDebugEnabled()) {
                    log.debug("< creating FileChooser");
                }
            }
        };
        menu.add(action);

        return view;
    }

    private Component createExchangeRatesView() {
        if (log.isDebugEnabled()) {
            log.debug("> createExchangeRatesView");
        }
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        PriceTableView fxScrollPane = new PriceTableView(filterEdit, exchangeRates);
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

    private Component createMapperView() {
        if (log.isDebugEnabled()) {
            log.debug("> createMapperView");
        }
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        JScrollPane scrollPane = createScrolledMapperTable(filterEdit);
        view.add(scrollPane, BorderLayout.CENTER);

        return view;
    }

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
        EventTableModel<SymbolMapperEntry> tableModel = new EventTableModel<SymbolMapperEntry>(source, tableFormat);
        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        EventSelectionModel myEventSelectionModel = new EventSelectionModel(source);
        table.setSelectionModel(myEventSelectionModel);

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
            log.info("colWidths=" + colWidths);
            Object[] maxWidthValues = TableUtils.calculateMaxWidthValues(colWidths);
            TableUtils.adjustColumnSizes(table, maxWidthValues);
        }

        return table;
    }

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
        EventTableModel<AbstractStockPrice> tableModel = new EventTableModel<AbstractStockPrice>(source, tableFormat);

        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        EventSelectionModel myEventSelectionModel = new EventSelectionModel(source);
        table.setSelectionModel(myEventSelectionModel);

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

    protected void showMainFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public List<File> getOutputFiles() {
        return outputFiles;
    }

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

    private void deleteOutputFile(File outputFile) {
        if ((outputFile != null) && (outputFile.exists())) {
            if (!outputFile.delete()) {
                log.warn("Failed to delete outputFile=" + outputFile);
            }
        }
    }

    public void stockSymbolsStringReceived(QuoteSource quoteSource, String stockSymbolsString) {
        clearPriceTable();
        clearMapperTable();
        this.saveOfxButton.setEnabled(false);
        this.importToMoneyButton.setEnabled(false);
        if (this.updateExchangeRateButton != null) {
            this.updateExchangeRateButton.setEnabled(false);
        }
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

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

    public static String getCurrentWorkingDirectory() {
        return new File(".").getAbsoluteFile().getAbsolutePath();
    }

    private String getYahooQuoteServer() {
        String yahooQuoteServer = null;
        YahooQuoteSourcePanel quoteSourceView = getYahooQuoteSourceView();
        if (quoteSourceView != null) {
            yahooQuoteServer = quoteSourceView.getQuoteServer();
        } else {
            yahooQuoteServer = GetYahooQuotes.DEFAULT_HOST;
        }
        return yahooQuoteServer;
    }

    public YahooQuoteSourcePanel getYahooQuoteSourceView() {
        return yahooQuoteSourceView;
    }

    public static Preferences getPrefs() {
        return prefs;
    }

    public QuoteSourceListener getQuoteSourceListener() {
        return quoteSourceListener;
    }

    public Integer getDateOffset() {
        return dateOffset;
    }

    public Boolean getForceGeneratingINVTRANLIST() {
        return forceGeneratingINVTRANLIST;
    }

    public Boolean getRandomizeShareCount() {
        return randomizeShareCount;
    }

    public Integer getSuspiciousPrice() {
        return suspiciousPrice;
    }

    public Boolean getIncrementallyIncreasedShareCount() {
        return incrementallyIncreasedShareCount;
    }

    public void setIncrementallyIncreasedShareCount(Boolean incrementallyIncreasedShareCount) {
        this.incrementallyIncreasedShareCount = incrementallyIncreasedShareCount;
    }

    private void selectNewCurrency(String value) {
        log.info("Selected new currency: " + value);
        String newValue = value;
        if (newValue.compareTo(defaultCurrency) != 0) {
            defaultCurrency = value;
            prefs.put(PREF_DEFAULT_CURRENCY, defaultCurrency);
            defaulCurrencyLabel.setText("Default currency: " + defaultCurrency);
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    private void selectNewAccountId(String value) {
        log.info("Selected new 'OFX Account Id': " + value);
        String newValue = value;
        if (newValue.compareTo(accountId) != 0) {
            accountId = newValue;
            prefs.put(PREF_ACCOUNT_ID, accountId);
            accountIdLabel.setText("OFX Account Id: " + accountId);
            // to clear the pricing table
            QuoteSource quoteSource = null;
            stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        VelocityUtils.initVelocity();

        String implementationVendorId = "com.le.tools.moneyutils";
        String buildNumber = BuildNumber.findBuilderNumber(implementationVendorId);
        if (buildNumber == null) {
            log.warn("Cannot find buildNumber from Manifest.");
            log.warn("Using built-in buildNumber which is likely to be wrong!");
        } else {
            GUI.VERSION = buildNumber;
        }

        String title = "OFX - Update stock prices - " + GUI.VERSION;
        log.info(title);

        String cwd = "Current directory is " + getCurrentWorkingDirectory();
        log.info(cwd);

        final GUI mainFrame = new GUI(title);
        log.info("Using quote server: " + mainFrame.getYahooQuoteServer());
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                mainFrame.showMainFrame();
                if (log.isDebugEnabled()) {
                    log.debug("post mainFrame.showMainFrame()");
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    public File getFiDir() {
        return fiDir;
    }

    public void setFiDir(File fiDir) {
        this.fiDir = fiDir;
    }

}
