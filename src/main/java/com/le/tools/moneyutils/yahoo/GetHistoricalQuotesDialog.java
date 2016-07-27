package com.le.tools.moneyutils.yahoo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GetHistoricalQuotesDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(GetHistoricalQuotesDialog.class);

    private final JPanel contentPanel = new JPanel();
    // private JTextField textField;
    // private JTextField textField_1;

    private Date fromDate;
    private Date toDate;
    private int days = -30;
    private Boolean limitToFriday = false;
    private Boolean limitToEOM = false;
    
    private JXDatePicker fromDatePicker;

    private JXDatePicker toDatePicker;
    private JCheckBox limitToFriday_1;
    private JCheckBox limitToEOM_1;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            GetHistoricalQuotesDialog dialog = new GetHistoricalQuotesDialog();
            dialog.showDialog();
            log.info("isCanceled=" + dialog.isCanceled());
            log.info("fromDate=" + dialog.getFromDate());
            log.info("toDate=" + dialog.getToDate());
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public boolean isCanceled() {
        return (getFromDate() == null) || (getToDate() == null);
    }

    public void showDialog() {
        showDialog(null);
    }

    public void showDialog(Component relativeComponent) {
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(relativeComponent);
        this.setVisible(true);
    }

    /**
     * Create the dialog.
     */
    public GetHistoricalQuotesDialog(Date fromDate, Date toDate, String symbol) {
        if (toDate == null) {
            toDate = new Date();
        }
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            cal.add(Calendar.DATE, days);
            fromDate = cal.getTime();
        }
        this.setFromDate(fromDate);
        this.setToDate(toDate);

        if (symbol != null) {
            setTitle("\"" + symbol + "\"");
        } else {
            setTitle("Enter date range");
        }
        // setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.UNRELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.UNRELATED_GAP_COLSPEC,},
            new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),}));
        {
            JLabel lblNewLabel = new JLabel("From date");
            contentPanel.add(lblNewLabel, "2, 2, right, default");
        }
        {
            fromDatePicker = new JXDatePicker(getFromDate());
            // datePicker.setEditable(false);
            // datePicker.getEditor().setEditable(false);
            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Date date = fromDatePicker.getDate();
                    log.info("fromDate=" + date);
                    setFromDate(date);
                }
            };
            fromDatePicker.addActionListener(listener);
            // fromDatePicker.getEditor().addActionListener(listener);
            // textField = new JTextField();
            // contentPanel.add(textField, "4, 2, fill, default");
            // textField.setColumns(10);
            contentPanel.add(fromDatePicker, "4, 2, fill, default");
        }
        {
            JLabel lblNewLabel_1 = new JLabel("To date");
            contentPanel.add(lblNewLabel_1, "2, 4, right, default");
        }
        {
            toDatePicker = new JXDatePicker(getToDate());
            // datePicker.setEditable(false);
            // datePicker.getEditor().setEditable(false);
            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Date date = toDatePicker.getDate();
                    log.info("toDate=" + date);
                    setToDate(date);
                }
            };
            toDatePicker.addActionListener(listener);
            // toDatePicker.getEditor().addActionListener(listener);
            // textField_1 = new JTextField();
            // contentPanel.add(textField_1, "4, 4, fill, default");
            // textField_1.setColumns(10);
            contentPanel.add(toDatePicker, "4, 4, fill, default");
        }
        {
            JLabel lblNewLabel_2 = new JLabel("Limit to");
            lblNewLabel_2.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblNewLabel_2, "2, 6");
        }
        {
            JPanel panel = new JPanel();
            contentPanel.add(panel, "4, 6, fill, fill");
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            {
                limitToFriday_1 = new JCheckBox("Friday");
                panel.add(limitToFriday_1);
                limitToEOM_1 = new JCheckBox("End-of-month");
                panel.add(limitToEOM_1);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            fromDatePicker.commitEdit();
                            toDatePicker.commitEdit();
                            setLimitToFriday(limitToFriday_1.isSelected());
                            setLimitToEOM(limitToEOM_1.isSelected());
                        } catch (ParseException e1) {
                            log.warn(e);
                        }
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setFromDate(null);
                        setToDate(null);
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        initDataBindings();
    }

    public GetHistoricalQuotesDialog(String symbol) {
        this(null, new Date(), symbol);
    }

    public GetHistoricalQuotesDialog() {
        this(null);
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getLimitToFriday() {
        return limitToFriday;
    }

    public void setLimitToFriday(Boolean limitToFriday) {
        this.limitToFriday = limitToFriday;
    }

    public Boolean getLimitToEOM() {
        return limitToEOM;
    }

    public void setLimitToEOM(Boolean limitToEOM) {
        this.limitToEOM = limitToEOM;
    }
    protected void initDataBindings() {
    }
}
