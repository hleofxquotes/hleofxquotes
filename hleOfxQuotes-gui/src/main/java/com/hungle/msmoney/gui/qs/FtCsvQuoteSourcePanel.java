package com.hungle.msmoney.gui.qs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.csv2ofx.AbstractCsvConverter;
import com.hungle.msmoney.core.misc.StopWatch;
import com.hungle.msmoney.core.stockprice.AbstractStockPrice;
import com.hungle.msmoney.gui.GUI;
import com.hungle.msmoney.gui.task.ShowDialogTask;
import com.hungle.msmoney.qs.DefaultQuoteSource;
import com.hungle.msmoney.qs.QuoteSource;
import com.hungle.msmoney.qs.QuoteSourceListener;
import com.hungle.msmoney.qs.ft.FtCsv;

// TODO: Auto-generated Javadoc
/**
 * The Class FtDotComQuoteSourcePanel.
 */
public class FtCsvQuoteSourcePanel extends JPanel {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(FtCsvQuoteSourcePanel.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant PREF_IMPORT_FT_COM_CSV_DIR. */
    private static final String PREF_IMPORT_FT_COM_CSV_DIR = "importFtComCsvDir";
    
    /** The Constant PREF_FT_COM_USE_SOURCE_SHARE_COUNT. */
    private static final String PREF_FT_COM_USE_SOURCE_SHARE_COUNT = "ftComUseShareCount";

    /** The prefs. */
    private final Preferences prefs;
    
    /** The quote source listener. */
    private final QuoteSourceListener quoteSourceListener;
    
    /** The share count check box. */
    private JCheckBox shareCountCheckBox;
    
    /** The stock prices. */
    private List<AbstractStockPrice> stockPrices = null;
    
    /** The quote source. */
    private QuoteSource quoteSource = new DefaultQuoteSource();

    /**
     * The Class ImportFtComCsvAction.
     */
    private final class ImportFtComCsvAction extends AbstractAction {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        /** The fc. */
        private JFileChooser fc = null;

        /**
         * Instantiates a new import ft com csv action.
         *
         * @param name the name
         */
        private ImportFtComCsvAction(String name) {
            super(name);
            // initFileChooser();
        }

        /**
         * Inits the file chooser.
         */
        private void initFileChooser() {
            String dir = prefs.get(FtCsvQuoteSourcePanel.PREF_IMPORT_FT_COM_CSV_DIR, ".");
            LOGGER.info("> pre creating JFileChooser");
            this.fc = new JFileChooser(dir);
            LOGGER.info("> post creating JFileChooser");
            FileFilter filter = new FileFilter() {

                @Override
                public String getDescription() {
                    return "ft.com CSV";
                }

                @Override
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return true;
                    }
                    String name = file.getName();
                    return name.endsWith(".csv");
                }
            };
            this.fc.setFileFilter(filter);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if (fc == null) {
                initFileChooser();
            }
            if (fc.showOpenDialog(FtCsvQuoteSourcePanel.this) == JFileChooser.CANCEL_OPTION) {
                return;
            }

            Exception exception = null;
            File fromFile = fc.getSelectedFile();
            String dir = fromFile.getAbsoluteFile().getParentFile().getAbsolutePath();
            prefs.put(FtCsvQuoteSourcePanel.PREF_IMPORT_FT_COM_CSV_DIR, dir);
            StopWatch stopWatch = new StopWatch();
            AbstractCsvConverter ftCsv = new FtCsv();
            boolean useQuoteSourceShareCount = shareCountCheckBox.isSelected();
            ftCsv.setUseQuoteSourceShareCount(useQuoteSourceShareCount);
            try {
                stockPricesLookupStarted(quoteSource);
                stopWatch.click();
                stockPrices = ftCsv.convert(fromFile);
            } catch (IOException e) {
                LOGGER.error(e, e);
                exception = e;
            } finally {
                long delta = stopWatch.click();
                LOGGER.info("ft.com CSV conversion took " + delta + "ms");
                stockPricesReceived(quoteSource, stockPrices);
            }

            if (exception != null) {
                Runnable doRun = new ShowDialogTask(FtCsvQuoteSourcePanel.this, exception, JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(doRun);
            }

        }
    }

    /**
     * Instantiates a new ft dot com quote source panel.
     *
     * @param gui the gui
     */
    public FtCsvQuoteSourcePanel(GUI gui) {
        super();
        this.prefs = GUI.getPrefs();
        this.quoteSourceListener = gui.getQuoteSourceListener();
        createMainView();
    }

    /**
     * Creates the main view.
     *
     * @return the component
     */
    private Component createMainView() {
        JPanel view = this;
        view.setLayout(new BorderLayout());
        view.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        String text = "This tool supports importing a CSV file that was exported from ft.com (portfolio export)." + "\n\n" + "Login to your ft.com account. "
                + "\n" + "Select a ft.com portfolio, export to a *.csv file." + "\n"
                + "Then click on the 'Import CSV File' button below to select an exported *.csv file." + "\n\n"
                + "If you have funds, you will need to use a mapper.cvs to identify which symbols are fund." + "\n"
                + "Please see: http://code.google.com/p/hle-ofx-quotes/wiki/mapperDotCsv";
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        view.add(areaScrollPane, BorderLayout.CENTER);

        JPanel commandView = new JPanel();
        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        JButton button = new JButton(new ImportFtComCsvAction("Import CSV File"));
        commandView.add(button);
        InputMap im = button.getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");

        shareCountCheckBox = new JCheckBox("Keep Share Count");
        shareCountCheckBox.addActionListener(new AbstractAction() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton button = (AbstractButton) e.getSource();
                boolean selected = button.getModel().isSelected();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("useQuoteSourceShareCount=" + selected);
                }
                prefs.put(PREF_FT_COM_USE_SOURCE_SHARE_COUNT, Boolean.valueOf(selected).toString());
                // TODO: kind of a hack right now. We need to clear the price
                // table. It is no longer
                // valid
                stockSymbolsStringReceived(quoteSource);
                // JOptionPane.showMessageDialog(FtDotComQuoteSourcePanel.this,
                // "New setting causes current data to be invalid.\nPlease do another update.");
            }
        });
        String value = prefs.get(PREF_FT_COM_USE_SOURCE_SHARE_COUNT, Boolean.TRUE.toString());
        shareCountCheckBox.setSelected(Boolean.valueOf(value));
        commandView.add(Box.createHorizontalStrut(5));
        commandView.add(shareCountCheckBox);
        view.add(commandView, BorderLayout.SOUTH);

        return view;
    }

    /**
     * Stock prices lookup started.
     *
     * @param quoteSource the quote source
     */
    private void stockPricesLookupStarted(QuoteSource quoteSource) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesLookupStarted(quoteSource);
        }
    }

    /**
     * Stock symbols string received.
     *
     * @param quoteSource the quote source
     */
    private void stockSymbolsStringReceived(QuoteSource quoteSource) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockSymbolsStringReceived(quoteSource, null);
        }
    }

    /**
     * Stock prices received.
     *
     * @param quoteSource the quote source
     * @param stockPrices the stock prices
     */
    private void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesReceived(quoteSource, stockPrices);
        }
    }

}
