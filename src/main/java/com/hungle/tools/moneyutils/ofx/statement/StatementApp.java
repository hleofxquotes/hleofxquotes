package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class StatementApp.
 */
public class StatementApp extends JFrame {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(StatementApp.class);

    /** The download panel. */
    private StatementPanel downloadPanel;

    /**
     * Instantiates a new statement app.
     *
     * @param title the title
     */
    public StatementApp(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension preferredSize = new Dimension(600, 600);

        setPreferredSize(preferredSize);

        getContentPane().add(createMainView());
    }

    /**
     * Creates the main view.
     *
     * @return the component
     */
    private Component createMainView() {
        this.downloadPanel = new StatementPanel();
        JPanel view = this.downloadPanel;
        return view;
    }

    /**
     * Show main view.
     */
    private void showMainView() {
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        if (this.downloadPanel != null) {
            this.downloadPanel.refreshFiDir();
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
//        VelocityUtils.initVelocity();

        String title = "OFX Statement Downloader";

        StatementApp download = new StatementApp(title);
        download.showMainView();
    }

}
