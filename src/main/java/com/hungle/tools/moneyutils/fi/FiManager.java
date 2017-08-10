package com.hungle.tools.moneyutils.fi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.hungle.tools.moneyutils.fi.model.Account;
import com.hungle.tools.moneyutils.fi.model.AccountGroup;
import com.hungle.tools.moneyutils.fi.model.AccountGroups;

// TODO: Auto-generated Javadoc
/**
 * The Class FiManager.
 *
 * @author lobas_av
 */
public class FiManager extends JFrame {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(FiManager.class);
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The m groups. */
    private AccountGroups m_groups = new AccountGroups();
    
    /** The m names. */
    private List<String> m_names = new ArrayList<String>();
    
    /** The m content pane. */
    private JSplitPane m_contentPane;
    
    /** The m group list. */
    private JList m_groupList;
    
    /** The group toolbar. */
    private JPanel groupToolbar;
    
    /** The m new group button. */
    private JButton m_newGroupButton;
    
    /** The m edit group button. */
    private JButton m_editGroupButton;
    
    /** The m delete group button. */
    private JButton m_deleteGroupButton;
    
    /** The m person table. */
    private JTable m_personTable;
    
    /** The m new person button. */
    private JButton m_newPersonButton;
    
    /** The m delete person button. */
    private JButton m_deletePersonButton;
    
    /** The m name text field. */
    private JTextField m_nameTextField;
    
    /** The m email text field. */
    private JTextField m_emailTextField;
    
    /** The m phone text field. */
    private JTextField m_phoneTextField;
    
    /** The m mobile phone 1 text field. */
    private JTextField m_mobilePhone1TextField;
    
    /** The m mobile phone 2 text field. */
    private JTextField m_mobilePhone2TextField;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    FiManager frame = new FiManager();
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        });
    }

    /**
     * Sets the default values.
     */
    private void setDefaultValues() {
        AccountGroup group1 = new AccountGroup("Developer Team");
        m_groups.addGroup(group1);
        group1.addPerson(new Account("Konstantin Scheglov", "kosta@nospam.com", "1234567890", "", ""));
        group1.addPerson(new Account("Alexander Mitin", "mitin@nospam.com", "", "0987654321", ""));
        group1.addPerson(new Account("Alexander Lobas", "lobas@nospam.com", "", "", "111-222-333-00"));
        group1.addPerson(new Account("Andey Sablin", "sablin@nospam.com", "098765", "", "aaa-vvv-ddd"));
        //
        AccountGroup group2 = new AccountGroup("Management Team");
        m_groups.addGroup(group2);
        group2.addPerson(new Account("Mike Taylor", "taylor@instantiations.com", "503-598-4900", "", ""));
        group2.addPerson(new Account("Eric Clayberg", "clayberg@instantiations.com", "+1 (503) 598-4900", "", ""));
        group2.addPerson(new Account("Dan Rubel", "dan@instantiations.com", "503-598-4900", "", ""));
        //
        AccountGroup group3 = new AccountGroup("Support Team");
        m_groups.addGroup(group3);
        group3.addPerson(new Account("Gina Nebling", "support@instantiations.com", "800-808-3737", "", ""));
        //
        m_names.add("Developer Team");
        m_names.add("Management Team");
        m_names.add("Support Team");
        m_names.add("Test Group's");
        m_names.add("Sales department");
        m_names.add("PR department");
        m_names.add("Advertising department");
    }

    /**
     * Instantiates a new fi manager.
     */
    public FiManager() {
        // setDefaultValues();
        setTitle("FiManager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 965, 744);
        m_contentPane = new JSplitPane();
        m_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        m_contentPane.setResizeWeight(0.50);
        m_contentPane.setDividerLocation(0.50);
        setContentPane(m_contentPane);
        {
            JPanel leftPanel = new JPanel();
            leftPanel.setBorder(null);
            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[] { 0, 0 };
            gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
            gridBagLayout.columnWeights = new double[] { 1.0, 1.0E-4 };
            gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0E-4 };
            leftPanel.setLayout(gridBagLayout);
            m_contentPane.setLeftComponent(leftPanel);
            {
                groupToolbar = new JPanel();
                FlowLayout flowLayout = (FlowLayout) groupToolbar.getLayout();
                flowLayout.setAlignment(FlowLayout.LEFT);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.insets = new Insets(0, 0, 5, 0);
                gbc.gridx = 0;
                gbc.gridy = 0;
                leftPanel.add(groupToolbar, gbc);
                {
                    m_newGroupButton = new JButton("New...");
                    m_newGroupButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            AccountGroup group = new AccountGroup("???");
                            FinancialInstitutionDialog dialog = new FinancialInstitutionDialog(m_names, group);
                            dialog.setLocationRelativeTo(m_contentPane);
                            dialog.setVisible(true);
                            m_groups.addGroup(group);
                            m_groupList.repaint();
                        }
                    });
                    groupToolbar.add(m_newGroupButton);
                }
                {
                    m_editGroupButton = new JButton("Edit");
                    m_editGroupButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            AccountGroup group = m_groups.getGroups().get(m_groupList.getSelectedIndex());
                            FinancialInstitutionDialog dialog = new FinancialInstitutionDialog(m_names, group);
                            dialog.setLocationRelativeTo(m_contentPane);
                            dialog.setVisible(true);
                        }
                    });
                    groupToolbar.add(m_editGroupButton);
                }
                {
                    m_deleteGroupButton = new JButton("Delete");
                    m_deleteGroupButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            m_groups.removeGroup(m_groups.getGroups().get(m_groupList.getSelectedIndex()));
                            m_groupList.repaint();
                        }
                    });
                    groupToolbar.add(m_deleteGroupButton);
                }
            }
            {
                m_groupList = new JList();
                m_groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = 0;
                gbc.gridy = 1;
                leftPanel.add(m_groupList, gbc);
            }
        }
        {
            JSplitPane rightPanel = new JSplitPane();
            rightPanel.setBorder(null);
            rightPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
            m_contentPane.setRightComponent(rightPanel);
            {
                JPanel topPanel = new JPanel();
                GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWidths = new int[] { 0, 0 };
                gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
                gridBagLayout.columnWeights = new double[] { 1.0, 1.0E-4 };
                gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0E-4 };
                topPanel.setLayout(gridBagLayout);
                rightPanel.setLeftComponent(topPanel);
                {
                    JPanel personToolbar = new JPanel();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.NORTHWEST;
                    gbc.insets = new Insets(0, 0, 5, 0);
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    topPanel.add(personToolbar, gbc);
                    {
                        m_newPersonButton = new JButton("New...");
                        m_newPersonButton.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                List<AccountGroup> groups = m_groups.getGroups();
                                if (groups == null) {
                                    return;
                                }
                                if (groups.size() <= 0) {
                                    return;
                                }
                                AccountGroup group = groups.get(m_groupList.getSelectedIndex());
                                group.addPerson(new Account());
                                m_groupList.repaint();
                                m_personTable.repaint();
                            }
                        });
                        personToolbar.add(m_newPersonButton);
                    }
                    {
                        m_deletePersonButton = new JButton("Delete");
                        m_deletePersonButton.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                AccountGroup group = m_groups.getGroups().get(m_groupList.getSelectedIndex());
                                Account person = group.getPersons().get(m_personTable.getSelectedRow());
                                group.removePerson(person);
                                m_groupList.repaint();
                                m_personTable.repaint();
                            }
                        });
                        personToolbar.add(m_deletePersonButton);
                    }
                }
                {
                    m_personTable = new JTable();
                    m_personTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    JScrollPane scrollPane = new JScrollPane(m_personTable);
                    scrollPane.setPreferredSize(new Dimension(600, 400));
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    topPanel.add(scrollPane, gbc);
                }
            }
            {
                JPanel bottomPanel = new JPanel();
                // bottomPanel.setPreferredSize(new Dimension(600, 400));
                bottomPanel.setBorder(new MatteBorder(10, 10, 10, 10, (Color) null));
                GridBagLayout gridBagLayout = new GridBagLayout();
                gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
                gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
                gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
                gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
                bottomPanel.setLayout(gridBagLayout);
                rightPanel.setRightComponent(bottomPanel);
                {
                    JLabel lblName = new JLabel("Name:");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 5, 5);
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    bottomPanel.add(lblName, gbc);
                }
                {
                    m_nameTextField = new JTextField();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 5, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    bottomPanel.add(m_nameTextField, gbc);
                    m_nameTextField.setColumns(10);
                }
                {
                    JLabel lblEmail = new JLabel("E-Mail:");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(0, 0, 5, 5);
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    bottomPanel.add(lblEmail, gbc);
                }
                {
                    m_emailTextField = new JTextField();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 5, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    bottomPanel.add(m_emailTextField, gbc);
                    m_emailTextField.setColumns(10);
                }
                {
                    JLabel lblPhone = new JLabel("Phone:");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(0, 0, 5, 5);
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    bottomPanel.add(lblPhone, gbc);
                }
                {
                    m_phoneTextField = new JTextField();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 5, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 1;
                    gbc.gridy = 2;
                    bottomPanel.add(m_phoneTextField, gbc);
                    m_phoneTextField.setColumns(10);
                }
                {
                    JLabel lblMobilePhone1 = new JLabel("Mobile Phone 1:");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(0, 0, 5, 5);
                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    bottomPanel.add(lblMobilePhone1, gbc);
                }
                {
                    m_mobilePhone1TextField = new JTextField();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 5, 0);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 1;
                    gbc.gridy = 3;
                    bottomPanel.add(m_mobilePhone1TextField, gbc);
                    m_mobilePhone1TextField.setColumns(10);
                }
                {
                    JLabel lblMobilePhone2 = new JLabel("Mobile Phone 2:");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(0, 0, 0, 5);
                    gbc.gridx = 0;
                    gbc.gridy = 4;
                    bottomPanel.add(lblMobilePhone2, gbc);
                }
                {
                    m_mobilePhone2TextField = new JTextField();
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridx = 1;
                    gbc.gridy = 4;
                    bottomPanel.add(m_mobilePhone2TextField, gbc);
                    m_mobilePhone2TextField.setColumns(10);
                }
            }
        }
        initDataBindings();
    }

    /**
     * Inits the data bindings.
     */
    protected void initDataBindings() {
        BeanProperty<AccountGroups, List<AccountGroup>> phoneGroupsBeanProperty = BeanProperty.create("groups");
        JListBinding<AccountGroup, AccountGroups, JList> jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ, m_groups,
                phoneGroupsBeanProperty, m_groupList);
        //
        ELProperty<AccountGroup, Object> phoneGroupEvalutionProperty = ELProperty.create("${name} (${personCount})");
        jListBinding.setDetailBinding(phoneGroupEvalutionProperty);
        //
        jListBinding.bind();
        //
        BeanProperty<JList, List<Account>> jListPersonsBeanProperty = BeanProperty.create("selectedElement.persons");
        JTableBinding<Account, JList, JTable> jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ, m_groupList,
                jListPersonsBeanProperty, m_personTable);
        //
        BeanProperty<Account, String> personBeanProperty = BeanProperty.create("name");
        jTableBinding.addColumnBinding(personBeanProperty).setColumnName("Name");
        //
        BeanProperty<Account, String> personBeanProperty_1 = BeanProperty.create("email");
        jTableBinding.addColumnBinding(personBeanProperty_1).setColumnName("E-mail");
        //
        BeanProperty<Account, String> personBeanProperty_2 = BeanProperty.create("phone");
        jTableBinding.addColumnBinding(personBeanProperty_2).setColumnName("Phone");
        //
        BeanProperty<Account, String> personBeanProperty_3 = BeanProperty.create("mobilePhone1");
        jTableBinding.addColumnBinding(personBeanProperty_3).setColumnName("Mobile Phone 1");
        //
        BeanProperty<Account, String> personBeanProperty_4 = BeanProperty.create("mobilePhone2");
        jTableBinding.addColumnBinding(personBeanProperty_4).setColumnName("Mobile Phone 2");
        //
        jTableBinding.setEditable(false);
        jTableBinding.bind();
        //
        BeanProperty<JTable, String> jTableBeanProperty = BeanProperty.create("selectedElement.name");
        BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
        AutoBinding<JTable, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_personTable,
                jTableBeanProperty, m_nameTextField, jTextFieldBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<JTable, String> jTableBeanProperty_1 = BeanProperty.create("selectedElement.email");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
        AutoBinding<JTable, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_personTable,
                jTableBeanProperty_1, m_emailTextField, jTextFieldBeanProperty_1);
        autoBinding_2.bind();
        //
        BeanProperty<JTable, String> jTableBeanProperty_2 = BeanProperty.create("selectedElement.phone");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty.create("text");
        AutoBinding<JTable, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_personTable,
                jTableBeanProperty_2, m_phoneTextField, jTextFieldBeanProperty_2);
        autoBinding_3.bind();
        //
        BeanProperty<JTable, String> jTableBeanProperty_3 = BeanProperty.create("selectedElement.mobilePhone1");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty.create("text");
        AutoBinding<JTable, String, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_personTable,
                jTableBeanProperty_3, m_mobilePhone1TextField, jTextFieldBeanProperty_3);
        autoBinding_4.bind();
        //
        BeanProperty<JTable, String> jTableBeanProperty_4 = BeanProperty.create("selectedElement.mobilePhone2");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_4 = BeanProperty.create("text");
        AutoBinding<JTable, String, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, m_personTable,
                jTableBeanProperty_4, m_mobilePhone2TextField, jTextFieldBeanProperty_4);
        autoBinding_5.bind();
        //
        ELProperty<JList, Object> jListEvalutionProperty = ELProperty.create("${ selectedElement != null }");
        BeanProperty<JButton, Boolean> jButtonBeanProperty = BeanProperty.create("enabled");
        AutoBinding<JList, Object, JButton, Boolean> autoBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, m_groupList,
                jListEvalutionProperty, m_editGroupButton, jButtonBeanProperty);
        autoBinding.bind();
        //
        ELProperty<JList, Object> jListEvalutionProperty_1 = ELProperty.create("${ selectedElement != null }");
        BeanProperty<JButton, Boolean> jButtonBeanProperty_1 = BeanProperty.create("enabled");
        AutoBinding<JList, Object, JButton, Boolean> autoBinding_6 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, m_groupList,
                jListEvalutionProperty_1, m_deleteGroupButton, jButtonBeanProperty_1);
        autoBinding_6.bind();
        //
        ELProperty<JTable, Object> jTableEvalutionProperty = ELProperty.create("${ selectedElement != null }");
        BeanProperty<JButton, Boolean> jButtonBeanProperty_2 = BeanProperty.create("enabled");
        AutoBinding<JTable, Object, JButton, Boolean> autoBinding_7 = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, m_personTable,
                jTableEvalutionProperty, m_deletePersonButton, jButtonBeanProperty_2);
        autoBinding_7.bind();
    }
}