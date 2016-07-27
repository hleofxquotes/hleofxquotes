package com.le.tools.moneyutils.csv2ofx;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class Csv2OfxGui {
    private static final Logger log = Logger.getLogger(Csv2OfxGui.class);

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Csv2OfxGui window = new Csv2OfxGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Csv2OfxGui() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
