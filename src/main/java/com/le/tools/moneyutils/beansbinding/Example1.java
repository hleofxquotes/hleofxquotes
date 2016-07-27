package com.le.tools.moneyutils.beansbinding;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;

public class Example1 extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Example1(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().add(createMainView());
    }

    private Component createMainView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.setPreferredSize(new Dimension(600, 400));

        JTable table = new JTable();
        List<FinancialInstitution> financialInstitutions = new ArrayList<FinancialInstitution>();
        FinancialInstitution fi = null;

        fi = new FinancialInstitution();
        fi.setName("American Express");
        fi.setId("3101");
        fi.setOrg("AMEX");
        fi.setUrl("https://online.americanexpress.com/myca/ofxdl/desktop/desktopDownload.do?request_type=nl_ofxdownload");
        fi.setBrokerId("");
        financialInstitutions.add(fi);

        JTableBinding<FinancialInstitution, List<FinancialInstitution>, JTable> tableBinding = SwingBindings.createJTableBinding(
                AutoBinding.UpdateStrategy.READ_WRITE, financialInstitutions, table);

        Property<FinancialInstitution, ?> binding = null;
        ColumnBinding columnBinding = null;

        binding = ELProperty.create("${name}");
        columnBinding = tableBinding.addColumnBinding(binding);
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);

        binding = ELProperty.create("${id}");
        columnBinding = tableBinding.addColumnBinding(binding);
        columnBinding.setColumnName("ID");
        columnBinding.setColumnClass(String.class);

        binding = ELProperty.create("${org}");
        columnBinding = tableBinding.addColumnBinding(binding);
        columnBinding.setColumnName("Org");
        columnBinding.setColumnClass(String.class);

        binding = ELProperty.create("${url}");
        columnBinding = tableBinding.addColumnBinding(binding);
        columnBinding.setColumnName("URL");
        columnBinding.setColumnClass(String.class);

        binding = ELProperty.create("${brokerId}");
        columnBinding = tableBinding.addColumnBinding(binding);
        columnBinding.setColumnName("Broker Id");
        columnBinding.setColumnClass(String.class);

        BindingGroup bindingGroup = new BindingGroup();
        bindingGroup.addBinding(tableBinding);
        tableBinding.bind();

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(table);

        view.add(jScrollPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String title = "Example1";
        final Example1 mainFrame = new Example1(title);
        Runnable doRun = new Runnable() {

            @Override
            public void run() {
                mainFrame.showMainFrame();
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    private void showMainFrame() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
