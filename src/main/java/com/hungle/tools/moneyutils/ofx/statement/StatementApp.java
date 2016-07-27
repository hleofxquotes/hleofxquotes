package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.VelocityUtils;

public class StatementApp extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(StatementApp.class);

    private StatementPanel downloadPanel;

    public StatementApp(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension preferredSize = new Dimension(600, 600);

        setPreferredSize(preferredSize);

        getContentPane().add(createMainView());
    }

    private Component createMainView() {
        this.downloadPanel = new StatementPanel();
        JPanel view = this.downloadPanel;
        return view;
    }

    private void showMainView() {
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        if (this.downloadPanel != null) {
            this.downloadPanel.refreshFiDir();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        VelocityUtils.initVelocity();

        String title = "OFX Statement Downloader";

        StatementApp download = new StatementApp(title);
        download.showMainView();
    }

}
