package com.hungle.tools.moneyutils.fi;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.hungle.tools.moneyutils.fi.model.AccountGroup;

/**
 * @author lobas_av
 * 
 */
public class FinancialInstitutionDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final com.hungle.tools.moneyutils.fi.model.AccountGroup m_phoneGroup;
    private final List<String> m_names;
    private JPanel m_contentPane;
    private JButton m_buttonOk;
    private JComboBox m_comboBox;

    public FinancialInstitutionDialog(List<String> names, com.hungle.tools.moneyutils.fi.model.AccountGroup phoneGroup) {
        m_names = names;
        m_phoneGroup = phoneGroup;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle("Financial Institution");
        setBounds(100, 100, 432, 126);
        m_contentPane = new JPanel();
        m_contentPane.setBorder(new MatteBorder(5, 5, 5, 5, (Color) null));
        setContentPane(m_contentPane);
        //
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0E-4 };
        m_contentPane.setLayout(gridBagLayout);
        //
        {
            JLabel lblName = new JLabel("Name:");
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            m_contentPane.add(lblName, gbc);
        }
        {
            m_comboBox = new JComboBox();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 5, 0);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 1;
            gbc.gridy = 0;
            m_contentPane.add(m_comboBox, gbc);
        }
        {
            m_buttonOk = new JButton("OK");
            m_buttonOk.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = 1;
            gbc.gridy = 2;
            m_contentPane.add(m_buttonOk, gbc);
        }
        initDataBindings();
    }

    protected void initDataBindings() {
        JComboBoxBinding<String, List<String>, JComboBox> jComboBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ, m_names,
                m_comboBox);
        jComboBinding.bind();
        //
        BeanProperty<AccountGroup, String> phoneGroupBeanProperty = BeanProperty.create("name");
        BeanProperty<JComboBox, String> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<AccountGroup, String, JComboBox, String> autoBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_phoneGroup,
                phoneGroupBeanProperty, m_comboBox, jComboBoxBeanProperty);
        autoBinding.bind();
    }
}