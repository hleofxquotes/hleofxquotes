package com.hungle.msmoney.prices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.hungle.sunriise.io.MnyDb;
import com.hungle.sunriise.prices.GetLatestSecurityPrices;
import com.hungle.sunriise.prices.GetLatestSecurityPrices.Result;
import com.hungle.sunriise.viewer.MnyViewer;
import com.hungle.sunriise.viewer.open.OpenDbAction;
import com.hungle.sunriise.viewer.open.OpenDbDialog;

public abstract class AbstractLatestPricesGUI {
    private static final Logger LOGGER = Logger.getLogger(AbstractLatestPricesGUI.class);

    protected Preferences prefs;

    protected JFrame frame;

    private MnyDb mnyDb = new MnyDb();

    protected boolean enableFiltering = false;

    private final class MnyViewerOpenDbAction extends OpenDbAction {

        /**
         * Instantiates a new mny viewer open db action.
         *
         * @param locationRelativeTo
         *            the location relative to
         * @param prefs
         *            the prefs
         * @param mnyDb
         *            the opened db
         */
        private MnyViewerOpenDbAction(Component locationRelativeTo, Preferences prefs, MnyDb mnyDb) {
            super(locationRelativeTo, prefs, mnyDb);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.hungle.sunriise.viewer.open.OpenDbAction#dbFileOpened(com.hungle.
         * sunriise.utils.OpenedDb,
         * com.hungle.sunriise.viewer.open.OpenDbDialog)
         */
        @Override
        public void dbFileOpened(MnyDb newOpenedDb, OpenDbDialog dialog) {
            boolean readOnly = dialog.getReadOnlyCheckBox().isSelected();

            openMnyDb(newOpenedDb, readOnly);
        }
    }

    /**
     * Create the application.
     */
    public AbstractLatestPricesGUI() {
        initPrefs();
        initialize();
    }

    protected void initPrefs() {
        this.prefs = Preferences.userNodeForPackage(AbstractLatestPricesGUI.class);
    }

    protected abstract Component initTableView(JTextField filterEdit);

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);

        JMenuItem mntmNewMenuItem_1 = new JMenuItem("Open");
        mntmNewMenuItem_1.addActionListener(new MnyViewerOpenDbAction(AbstractLatestPricesGUI.this.getFrame(), prefs, getMnyDb()));
        mnNewMenu.add(mntmNewMenuItem_1);

        JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
        mntmNewMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnNewMenu.add(mntmNewMenuItem);

        final Container contentPane = frame.getContentPane();

        JPanel mainView = new JPanel();
        mainView.setBorder(BorderFactory.createTitledBorder("Latest Prices"));
        mainView.setLayout(new BorderLayout());
        contentPane.add(mainView, BorderLayout.CENTER);

        JTextField filterEdit = new JTextField(10);
        Component tableView = initTableView(filterEdit);
        mainView.add(tableView, BorderLayout.CENTER);

        if (enableFiltering) {
            filterEdit.setEnabled(true);
            JPanel commandView = new JPanel();
            commandView.setLayout(new BoxLayout(commandView, BoxLayout.X_AXIS));
            commandView.add(new JLabel("Filter: "));
            commandView.add(filterEdit);
            mainView.add(commandView, BorderLayout.SOUTH);
        } else {
            filterEdit.setEnabled(false);
        }

    }

    protected void openMnyDb(MnyDb newMnyDb, boolean readOnly) {
        if (newMnyDb != null) {
            AbstractLatestPricesGUI.this.setMnyDb(newMnyDb);
        } else {
            LOGGER.warn("newMnyDb is null. Cannot open.");
            return;
        }

        refreshViewWith(getMnyDb());
    }

    protected void refreshViewWith(MnyDb mnyDb) {
        String title = MnyViewer.TITLE_NO_OPENED_DB;
        File dbFile = mnyDb.getDbFile();
        if (dbFile != null) {
            title = dbFile.getAbsolutePath();
        }
        getFrame().setTitle(title);

        try {
            GetLatestSecurityPrices getter = new GetLatestSecurityPrices();
            final List<Result> results = getter.getLatestSecurityPrices(mnyDb);
            refreshView(results);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

    protected abstract void refreshView(List<Result> results);

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public MnyDb getMnyDb() {
        return mnyDb;
    }

    public void setMnyDb(MnyDb mnyDb) {
        this.mnyDb = mnyDb;
    }
}
