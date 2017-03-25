package com.hungle.tools.moneyutils.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

// TODO: Auto-generated Javadoc
/**
 * The Class AboutDialog.
 */
public class AboutDialog extends JDialog implements ActionListener {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The dialog. */
    private static AboutDialog dialog;

    /**
     * Show dialog.
     *
     * @param frameComp the frame comp
     * @param locationComp the location comp
     * @param title the title
     * @param info the info
     */
    public static void showDialog(Component frameComp, Component locationComp, String title, String info) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new AboutDialog(frame, locationComp, title, info);
        dialog.setVisible(true);
    }

    /**
     * Instantiates a new about dialog.
     *
     * @param frame the frame
     * @param locationComp the location comp
     * @param title the title
     * @param info the info
     */
    private AboutDialog(Frame frame, Component locationComp, String title, String info) {
        super(frame, title, true);

        final JButton setButton = new JButton("OK");
        setButton.setActionCommand("OK");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        JPanel infoPane = null;

        infoPane = new JPanel();
        infoPane.setLayout(new BorderLayout());
        infoPane.setBorder(new EmptyBorder(3, 3, 3, 3));
        JTextArea textArea = new JTextArea(10, 40);
        // textArea.setWrapStyleWord(true);
        // textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.append(info);
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        infoPane.add(scrollPane, BorderLayout.CENTER);

        // Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(setButton);

        // Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        if (infoPane != null) {
            contentPane.add(infoPane, BorderLayout.CENTER);
        }
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(locationComp);
    }

    // Handle clicks on the OK button.

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AboutDialog.dialog.setVisible(false);
    }
}
