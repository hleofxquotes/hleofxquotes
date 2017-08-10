package com.hungle.tools.moneyutils.fi;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckFi.
 */
public class CheckFi {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CheckFi.class);

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
                    CheckFi window = new CheckFi();
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
    public CheckFi() {
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
