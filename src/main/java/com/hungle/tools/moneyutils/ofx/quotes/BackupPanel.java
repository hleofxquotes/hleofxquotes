package com.le.tools.moneyutils.ofx.quotes;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BackupPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private JTextField textField_1;
    public BackupPanel() {
        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,}));
        
        JLabel lblNewJgoodiesTitle = DefaultComponentFactory.getInstance().createTitle("Backup Manager");
        lblNewJgoodiesTitle.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesTitle, "4, 2");
        
        JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("From dir");
        lblNewJgoodiesLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel, "4, 4, right, default");
        
        textField = new JTextField();
        add(textField, "6, 4, fill, default");
        textField.setColumns(10);
        
        JButton btnNewButton = new JButton("Browse");
        add(btnNewButton, "8, 4");
        
        JLabel lblNewJgoodiesLabel_1 = DefaultComponentFactory.getInstance().createLabel("To dir");
        lblNewJgoodiesLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
        add(lblNewJgoodiesLabel_1, "4, 6, right, default");
        
        textField_1 = new JTextField();
        add(textField_1, "6, 6, fill, default");
        textField_1.setColumns(10);
        
        JButton btnNewButton_1 = new JButton("Browse");
        add(btnNewButton_1, "8, 6");
    }

}
