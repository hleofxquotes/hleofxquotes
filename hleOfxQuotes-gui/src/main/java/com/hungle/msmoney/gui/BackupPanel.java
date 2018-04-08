package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.backup.SaveBackups;
import com.hungle.msmoney.core.backup.SaveBackupsListener;
import com.hungle.msmoney.core.backup.SaveBackupsResult;
import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

// TODO: Auto-generated Javadoc
/**
 * The Class BackupPanel.
 */
public class BackupPanel extends JPanel implements SaveBackupsListener {
    private static final Logger LOGGER = Logger.getLogger(BackupPanel.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The text field. */
    private JTextField fromDirTextField;
    private File fromDir;
    private String fromDirPrefKey = "backup.fromDir";

    /** The text field 1. */
    private JTextField toDirTextField;
    private File toDir;
    private String toDirPrefKey = "backup.toDir";

    private JFileChooser fc;

    private ExecutorService threadPool;

    private JButton backupButton;

    private JProgressBar progressBar;

    private int fileCount;

    private StyledDocument outputDoc;

    private final class SaveBackupsAction implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            if (fromDir == null) {
                return;
            }
            if (!fromDir.isDirectory()) {
                return;
            }
            if (toDir == null) {
                return;
            }
            if (!toDir.isDirectory()) {
                return;
            }

            Runnable command = new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("BACKUP - fromDir=" + fromDir);
                    LOGGER.info("BACKUP - toDir=" + toDir);
                    SaveBackups saveBackups = new SaveBackups();
                    saveBackups.setListener(BackupPanel.this);
                    LOGGER.info("> START BACKUP");
                    SaveBackupsResult result = null;
                    try {
                        String password = null;
                        result = saveBackups.saveBackups(fromDir, toDir, password);
                    } catch (IOException e) {
                        LOGGER.error(e, e);
                    } finally {
                        LOGGER.info(result);
                        LOGGER.info("< DONE BACKUP");
                    }
                }
            };
            threadPool.execute(command);
        }
    }

    /**
     * Instantiates a new backup panel.
     */
    public BackupPanel() {
        this.fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setLayout(new FormLayout(
                new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.MIN_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
                        ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.RELATED_GAP_COLSPEC, },
                new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("max(79dlu;min)"), }));

        JLabel lblNewJgoodiesTitle = DefaultComponentFactory.getInstance().createTitle("Backup Organizer");
        lblNewJgoodiesTitle.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesTitle, "2, 2");

        JTextPane txtpnThisToolWill = new JTextPane();
        txtpnThisToolWill.setEditable(false);
        txtpnThisToolWill.setText(
                "This tool will copy backup files \r\nfrom ‘From dir’ to ‘To dir’ \r\nand put them into an hierarchical directory structure\r\nin format YYYY/MM/DD (for example: 2018/01/30)");
        add(txtpnThisToolWill, "4, 2, 3, 1, fill, fill");

        JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("From dir");
        lblNewJgoodiesLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel, "2, 4, right, default");

        fromDirTextField = new JTextField();
        add(fromDirTextField, "4, 4, fill, default");
        fromDirTextField.setColumns(10);
        String pathName = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(fromDirPrefKey, null);
        if (!CheckNullUtils.isEmpty(pathName)) {
            fromDirTextField.setText(pathName);
            fromDir = new File(pathName);
        }

        JButton fromDirButton = new JButton("Browse");
        fromDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (fc.showOpenDialog(fromDirButton) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File dir = fc.getSelectedFile();
                if ((!dir.exists()) || (!dir.isDirectory())) {
                    return;
                }

                fromDir = dir;
                String path = fromDir.getAbsolutePath();
                fromDirTextField.setText(path);
                le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(fromDirPrefKey, path);
            }
        });
        add(fromDirButton, "6, 4");

        JLabel lblNewJgoodiesLabel_1 = DefaultComponentFactory.getInstance().createLabel("To dir");
        lblNewJgoodiesLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel_1, "2, 6, right, default");

        toDirTextField = new JTextField();
        add(toDirTextField, "4, 6, fill, default");
        toDirTextField.setColumns(10);
        pathName = le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.get(toDirPrefKey, null);
        if (!CheckNullUtils.isEmpty(pathName)) {
            toDirTextField.setText(pathName);
            toDir = new File(pathName);
        }
        JButton toDirButton = new JButton("Browse");
        toDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                if (fc.showOpenDialog(fromDirButton) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File dir = fc.getSelectedFile();
                if ((!dir.exists()) || (!dir.isDirectory())) {
                    return;
                }

                toDir = dir;
                String path = toDir.getAbsolutePath();
                toDirTextField.setText(path);
                le.com.tools.moneyutils.ofx.quotes.GUI.PREFS.put(toDirPrefKey, path);
            }
        });
        add(toDirButton, "6, 6");

        backupButton = new JButton("Organize");
        backupButton.setEnabled(true);

        backupButton.addActionListener(new SaveBackupsAction());

        progressBar = new JProgressBar();
        add(progressBar, "4, 8");
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        // progressBar.setString(null);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(false);
        add(backupButton, "6, 8");

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "2, 10, 5, 1, fill, fill");

        JTextPane outputTextPane = new JTextPane();
        outputDoc = outputTextPane.getStyledDocument();

        scrollPane.setViewportView(outputTextPane);
        outputTextPane.setEditable(false);
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void notifyStartBackup() {
        fileCount = 0;

        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                String message = "> START" + "\r\n";
                try {
                    outputDoc.insertString(outputDoc.getLength(), message, null);
                } catch (BadLocationException e) {
                    LOGGER.warn(e);
                }

                backupButton.setEnabled(false);
                progressBar.setValue(0);
                // progressBar.setString(null);
            }
        };
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }

    @Override
    public void notifyStartCopyFile(File file, File dir, int size) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                int progress = (fileCount * 100) / size;

                progressBar.setValue(progress);
            }
        };
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }

    @Override
    public void notifyCopyFile(File fromFile, File toFile, String password) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
            }
        };
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }

    @Override
    public void notifyDoneCopyFile(File file, File dir, int size, final boolean copied) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                fileCount++;
                String message = null;
                if (copied) {
                    message = "COPIED file=" + file.getName() + " toDir=" + dir.getAbsolutePath() + "\r\n";
                } else {
                    message = "SKIP file=" + file.getName() + "\r\n";
                }
                if (copied) {
                    try {
                        outputDoc.insertString(outputDoc.getLength(), message, null);
                    } catch (BadLocationException e) {
                        LOGGER.warn(e);
                    }
                }
            }
        };
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }

    @Override
    public void notifyDoneBackup() {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                String message = "< DONE" + "\r\n";
                try {
                    outputDoc.insertString(outputDoc.getLength(), message, null);
                } catch (BadLocationException e) {
                    LOGGER.warn(e);
                }
                progressBar.setValue(100);
                // progressBar.setString(null);

                backupButton.setEnabled(true);
            }
        };
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (InvocationTargetException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }

}
