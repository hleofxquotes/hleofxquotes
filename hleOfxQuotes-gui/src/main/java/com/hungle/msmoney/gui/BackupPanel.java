package com.hungle.msmoney.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.backup.SaveBackups;
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
public class BackupPanel extends JPanel {
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
    private String  toDirPrefKey = "backup.toDir";
    
    private JFileChooser fc;

    private ExecutorService threadPool;
    
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
                FormSpecs.DEFAULT_COLSPEC,
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
                FormSpecs.DEFAULT_ROWSPEC,}));
        
        JLabel lblNewJgoodiesTitle = DefaultComponentFactory.getInstance().createTitle("Backup Manager");
        lblNewJgoodiesTitle.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesTitle, "4, 2");
        
        JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("From dir");
        lblNewJgoodiesLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel, "4, 4, right, default");
        
        fromDirTextField = new JTextField();
        add(fromDirTextField, "6, 4, 5, 1, fill, default");
        fromDirTextField.setColumns(10);
        String pathName = GUI.PREFS.get(fromDirPrefKey, null);
        if (! CheckNullUtils.isNull(pathName)) {
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
                if ((! dir.exists()) || (! dir.isDirectory())) {
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
        if (! CheckNullUtils.isNull(pathName)) {
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
                if ((! dir.exists()) || (! dir.isDirectory())) {
                    return;
                }
                
                toDir = dir;
                String path = toDir.getAbsolutePath();
                toDirTextField.setText(path);
                GUI.PREFS.put(toDirPrefKey, path);
            }
        });
        add(toDirButton, "12, 6");
        
        JButton backupButton = new JButton("Backup");
        backupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (fromDir == null) {
                    return;
                }
                if (! fromDir.isDirectory()) {
                    return;
                }
                if (toDir == null) {
                    return;
                }
                if (! toDir.isDirectory()) {
                    return;
                }

                Runnable command = new Runnable() {
                    
                    @Override
                    public void run() {
                        LOGGER.info("BACKUP - fromDir=" + fromDir);
                        LOGGER.info("BACKUP - toDir=" + toDir);
                        SaveBackups cmd = new SaveBackups();
                        LOGGER.info("> START BACKUP");
                        try {
                            String password = null;
                            cmd.saveBackups(fromDir, toDir, password);
                        } catch (IOException e) {
                            LOGGER.error(e, e);
                        } finally {
                            LOGGER.info("< DONE BACKUP");
                        }                        
                    }
                };
                threadPool.execute(command);
            }
        });
        add(backupButton, "10, 10");
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

}
