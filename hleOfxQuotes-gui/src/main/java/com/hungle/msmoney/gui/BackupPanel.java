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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.backup.SaveBackups;
import com.hungle.msmoney.core.backup.SaveBackupsListener;
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
                    saveBackups.setSaveBackupsListener(BackupPanel.this);
                    LOGGER.info("> START BACKUP");
                    try {
                        String password = null;
                        saveBackups.saveBackups(fromDir, toDir, password);
                    } catch (IOException e) {
                        LOGGER.error(e, e);
                    } finally {
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

        setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.MIN_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));

        JLabel lblNewJgoodiesTitle = DefaultComponentFactory.getInstance().createTitle("Backup Organizer");
        lblNewJgoodiesTitle.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesTitle, "4, 2");

        JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("From dir");
        lblNewJgoodiesLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel, "4, 4, right, default");

        fromDirTextField = new JTextField();
        add(fromDirTextField, "6, 4, 5, 1, fill, default");
        fromDirTextField.setColumns(10);
        String pathName = GUI.PREFS.get(fromDirPrefKey, null);
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
                GUI.PREFS.put(fromDirPrefKey, path);
            }
        });
        add(fromDirButton, "12, 4");

        JLabel lblNewJgoodiesLabel_1 = DefaultComponentFactory.getInstance().createLabel("To dir");
        lblNewJgoodiesLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel_1, "4, 6, right, default");

        toDirTextField = new JTextField();
        add(toDirTextField, "6, 6, 5, 1, fill, default");
        toDirTextField.setColumns(10);
        pathName = GUI.PREFS.get(toDirPrefKey, null);
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
                GUI.PREFS.put(toDirPrefKey, path);
            }
        });
        add(toDirButton, "12, 6");

        backupButton = new JButton("Organize");
        backupButton.setEnabled(true);

        backupButton.addActionListener(new SaveBackupsAction());

        progressBar = new JProgressBar();
        add(progressBar, "6, 10, 5, 1");
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setString(null);
        add(backupButton, "12, 10");
        
        JTextPane txtpnThisToolWill = new JTextPane();
        txtpnThisToolWill.setEditable(false);
        txtpnThisToolWill.setText("This tool will copy backup files from ‘From dir’ to ‘To dir’ and put them into\nan hierarchical directory structure\nYYYY/MM/DD");
        add(txtpnThisToolWill, "4, 14, 9, 1, fill, fill");
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
                LOGGER.info("> notifyStartBackup");
                backupButton.setEnabled(false);
                progressBar.setValue(0);
                progressBar.setString(null);
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
    public void notifyStartCopyFile(File file, int size) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                int progress = (fileCount * 100) / size;
                LOGGER.info("> notifyStartCopyFile, progress=" + progress + ", fileCount=" + fileCount + ", size=" + size);

                progressBar.setValue(progress);
                progressBar.setString(fileCount + "/" + size);
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
                LOGGER.info("> notifyCopyFile, " + fromFile + " -> " + toFile);
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
    public void notifyDoneCopyFile(File file, int size) {
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                fileCount++;
                LOGGER.info("> notifyDoneCopyFile, fileCount= + fileCount");
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
                LOGGER.info("> notifyDoneBackup");

                progressBar.setValue(100);
                progressBar.setString(null);

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
