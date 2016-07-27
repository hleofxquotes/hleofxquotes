package com.le.tools.moneyutils.ft;

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

import com.le.tools.moneyutils.ofx.quotes.DefaultQuoteSource;
import com.le.tools.moneyutils.ofx.quotes.GUI;
import com.le.tools.moneyutils.ofx.quotes.QuoteSource;
import com.le.tools.moneyutils.ofx.quotes.QuoteSourceListener;
import com.le.tools.moneyutils.ofx.quotes.ShowDialogTask;
import com.le.tools.moneyutils.ofx.quotes.StopWatch;
import com.le.tools.moneyutils.stockprice.AbstractStockPrice;

public class FtDotComQuoteSourcePanel extends JPanel {
    private static final Logger log = Logger.getLogger(FtDotComQuoteSourcePanel.class);

    private static final long serialVersionUID = 1L;

    private static final String PREF_IMPORT_FT_COM_CSV_DIR = "importFtComCsvDir";
    private static final String PREF_FT_COM_USE_SOURCE_SHARE_COUNT = "ftComUseShareCount";

    private final Preferences prefs;
    private final QuoteSourceListener quoteSourceListener;
    private JCheckBox shareCountCheckBox;
    private List<AbstractStockPrice> stockPrices = null;
    private QuoteSource quoteSource = new DefaultQuoteSource();

    private final class ImportFtComCsvAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private JFileChooser fc = null;

        private ImportFtComCsvAction(String name) {
            super(name);
            // initFileChooser();
        }

        private void initFileChooser() {
            String dir = prefs.get(FtDotComQuoteSourcePanel.PREF_IMPORT_FT_COM_CSV_DIR, ".");
            log.info("> pre creating JFileChooser");
            this.fc = new JFileChooser(dir);
            log.info("> post creating JFileChooser");
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

        @Override
        public void actionPerformed(ActionEvent event) {
            if (fc == null) {
                initFileChooser();
            }
            if (fc.showOpenDialog(FtDotComQuoteSourcePanel.this) == JFileChooser.CANCEL_OPTION) {
                return;
            }

            Exception exception = null;
            File fromFile = fc.getSelectedFile();
            String dir = fromFile.getAbsoluteFile().getParentFile().getAbsolutePath();
            prefs.put(FtDotComQuoteSourcePanel.PREF_IMPORT_FT_COM_CSV_DIR, dir);
            StopWatch stopWatch = new StopWatch();
            AbstractCsvConverter ftCsv = new FtCsv();
            boolean useQuoteSourceShareCount = shareCountCheckBox.isSelected();
            ftCsv.setUseQuoteSourceShareCount(useQuoteSourceShareCount);
            try {
                stockPricesLookupStarted(quoteSource);
                stopWatch.click();
                stockPrices = ftCsv.convert(fromFile);
            } catch (IOException e) {
                log.error(e, e);
                exception = e;
            } finally {
                long delta = stopWatch.click();
                log.info("ft.com CSV conversion took " + delta + "ms");
                stockPricesReceived(quoteSource, stockPrices);
            }

            if (exception != null) {
                Runnable doRun = new ShowDialogTask(FtDotComQuoteSourcePanel.this, exception, JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(doRun);
            }

        }
    }

    public FtDotComQuoteSourcePanel(GUI gui) {
        super();
        this.prefs = GUI.getPrefs();
        this.quoteSourceListener = gui.getQuoteSourceListener();
        createMainView();
    }

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
                if (log.isDebugEnabled()) {
                    log.debug("useQuoteSourceShareCount=" + selected);
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

    private void stockPricesLookupStarted(QuoteSource quoteSource) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesLookupStarted(quoteSource);
        }
    }

    private void stockSymbolsStringReceived(QuoteSource quoteSource) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockSymbolsStringReceived(quoteSource, null);
        }
    }

    private void stockPricesReceived(QuoteSource quoteSource, List<AbstractStockPrice> stockPrices) {
        if (quoteSourceListener != null) {
            quoteSourceListener.stockPricesReceived(quoteSource, stockPrices);
        }
    }

}
