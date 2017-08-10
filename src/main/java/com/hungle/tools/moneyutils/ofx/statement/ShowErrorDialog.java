package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.AbstractUpdateFiDir;

// TODO: Auto-generated Javadoc
/**
 * The Class ShowErrorDialog.
 */
public class ShowErrorDialog extends JDialog {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(ShowErrorDialog.class);

    /** The bean. */
    private FiBean bean;

    /**
     * Instantiates a new show error dialog.
     *
     * @param frameForComponent the frame for component
     * @param bean the bean
     */
    public ShowErrorDialog(Frame frameForComponent, FiBean bean) {
        super(frameForComponent);
        this.bean = bean;
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.getContentPane().add(createMainView());
    }

    /**
     * Creates the main view.
     *
     * @return the component
     */
    private Component createMainView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setPreferredSize(new Dimension(400, 400));

        JPanel panel_1 = new JPanel();
        view.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.50);
        panel_1.add(splitPane);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        scrollPane.setBorder(BorderFactory.createTitledBorder("Last error:"));
        JTextArea txtrUpper = new JTextArea();
        txtrUpper.setWrapStyleWord(true);
        Exception exception = bean.getException();
        if (exception == null) {
            txtrUpper.setText("No detail error");
        } else {
            StackTraceElement[] stacks = exception.getStackTrace();
            txtrUpper.setText(exception.toString());
            txtrUpper.append("\n");
            if (stacks != null) {
                for (StackTraceElement stack : stacks) {
                    txtrUpper.append("\n");
                    txtrUpper.append(stack.toString());
                }
            }
        }
        txtrUpper.setCaretPosition(0);
        scrollPane.setViewportView(txtrUpper);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        scrollPane_1.setBorder(BorderFactory.createTitledBorder("OFX Response File:"));
        JTextArea txtrLower = new JTextArea();
        txtrLower.setWrapStyleWord(true);
        AbstractUpdateFiDir updater = bean.getUpdater();
        File respFile = updater.getRespFile();
        if (respFile == null) {
            respFile = new File(updater.getDir(), updater.getRespFileName());
        }
        if ((respFile == null) || (!respFile.exists())) {
            txtrLower.setText("No reponse file");
        } else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(respFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    txtrLower.append(line);
                    txtrLower.append("\n");
                }
            } catch (IOException e) {
                log.error(e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        log.warn(e1);
                    } finally {
                        reader = null;
                    }
                }
            }
        }
        txtrLower.setCaretPosition(0);
        scrollPane_1.setViewportView(txtrLower);

        JPanel panel = new JPanel();
        view.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        panel.add(Box.createHorizontalGlue());
        JButton btnNewButton = new JButton("OK");
        btnNewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(btnNewButton);
        panel.add(Box.createHorizontalStrut(5));

        return view;
    }

    /**
     * Show dialog.
     *
     * @param c the c
     */
    public void showDialog(Component c) {
        this.pack();
        this.setLocationRelativeTo(c);
        this.setVisible(true);
    }

}
