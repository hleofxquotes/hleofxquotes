package com.hungle.msmoney.gui;

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

// TODO: Auto-generated Javadoc
/**
 * The Class GetHistoricalQuotesDialog.
 */
public class GetHistoricalQuotesDialog extends JDialog {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(GetHistoricalQuotesDialog.class);

    /** The content panel. */
    private final JPanel contentPanel = new JPanel();
    // private JTextField textField;
    // private JTextField textField_1;

    /** The from date. */
    private Date fromDate;
    
    /** The to date. */
    private Date toDate;
    
    /** The days. */
    private int days = -30;
    
    /** The limit to friday. */
    private Boolean limitToFriday = false;
    
    /** The limit to EOM. */
    private Boolean limitToEOM = false;
    
    /** The from date picker. */
    private JXDatePicker fromDatePicker;

    /** The to date picker. */
    private JXDatePicker toDatePicker;
    
    /** The limit to friday 1. */
    private JCheckBox limitToFriday_1;
    
    /** The limit to EO M 1. */
    private JCheckBox limitToEOM_1;

    /**
     * Launch the application.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            GetHistoricalQuotesDialog dialog = new GetHistoricalQuotesDialog();
            dialog.showDialog();
            LOGGER.info("isCanceled=" + dialog.isCanceled());
            LOGGER.info("fromDate=" + dialog.getFromDate());
            LOGGER.info("toDate=" + dialog.getToDate());
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Checks if is canceled.
     *
     * @return true, if is canceled
     */
    public boolean isCanceled() {
        return (getFromDate() == null) || (getToDate() == null);
    }

    /**
     * Show dialog.
     */
    public void showDialog() {
        showDialog(null);
    }

    /**
     * Show dialog.
     *
     * @param relativeComponent the relative component
     */
    public void showDialog(Component relativeComponent) {
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(relativeComponent);
        this.setVisible(true);
    }

    /**
     * Create the dialog.
     *
     * @param fromDate the from date
     * @param toDate the to date
     * @param symbol the symbol
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
                    LOGGER.info("fromDate=" + date);
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
                    LOGGER.info("toDate=" + date);
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
                            LOGGER.warn(e);
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

    /**
     * Instantiates a new gets the historical quotes dialog.
     *
     * @param symbol the symbol
     */
    public GetHistoricalQuotesDialog(String symbol) {
        this(null, new Date(), symbol);
    }

    /**
     * Instantiates a new gets the historical quotes dialog.
     */
    public GetHistoricalQuotesDialog() {
        this(null);
    }

    /**
     * Gets the from date.
     *
     * @return the from date
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the new from date
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the new to date
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets the limit to friday.
     *
     * @return the limit to friday
     */
    public Boolean getLimitToFriday() {
        return limitToFriday;
    }

    /**
     * Sets the limit to friday.
     *
     * @param limitToFriday the new limit to friday
     */
    public void setLimitToFriday(Boolean limitToFriday) {
        this.limitToFriday = limitToFriday;
    }

    /**
     * Gets the limit to EOM.
     *
     * @return the limit to EOM
     */
    public Boolean getLimitToEOM() {
        return limitToEOM;
    }

    /**
     * Sets the limit to EOM.
     *
     * @param limitToEOM the new limit to EOM
     */
    public void setLimitToEOM(Boolean limitToEOM) {
        this.limitToEOM = limitToEOM;
    }
    
    /**
     * Inits the data bindings.
     */
    protected void initDataBindings() {
    }
}
