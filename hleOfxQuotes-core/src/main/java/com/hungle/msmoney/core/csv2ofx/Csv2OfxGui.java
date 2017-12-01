package com.hungle.msmoney.core.csv2ofx;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Csv2OfxGui.
 */
public class Csv2OfxGui {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(Csv2OfxGui.class);

    /** The frame. */
    private JFrame frame;

    /**
     * Launch the application.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Csv2OfxGui window = new Csv2OfxGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.error(e, e);
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
