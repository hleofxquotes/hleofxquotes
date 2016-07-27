package com.le.tools.moneyutils.beansbinding;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.le.tools.moneyutils.fi.OfxPostClient;
import com.le.tools.moneyutils.fi.OfxPostClientParams;
import com.le.tools.moneyutils.fi.UpdateFiDir;
import com.le.tools.moneyutils.fi.Utils;
import com.le.tools.moneyutils.fi.VelocityUtils;
import com.le.tools.moneyutils.fi.model.bean.FiInfoBean;
import com.le.tools.moneyutils.fi.model.bean.TestResult;
import com.le.tools.moneyutils.fi.props.Authentication;
import com.le.tools.moneyutils.fi.props.OFX;
import com.le.tools.moneyutils.fi.props.PropertiesUtils;

public class FiInfoDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FiInfoDialog.class);
    private static final Executor threadPool = Executors.newCachedThreadPool();

    private final class TestAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            final Component source = (Component) event.getSource();
            source.setEnabled(false);

            Runnable command = new Runnable() {

                @Override
                public void run() {
                    try {
                        for (int i = 1; i <= 2; i++) {
                            int ofxVersion = i;
                            try {
                                log.info("> accountInquiry, ofxVersion=" + ofxVersion);
                                if (ofxVersion == 1) {
                                    Runnable doRun = new Runnable() {

                                        @Override
                                        public void run() {
                                            testResult.setV1("v1: Testing");
                                        }
                                    };
                                    SwingUtilities.invokeLater(doRun);
                                } else if (ofxVersion == 2) {
                                    Runnable doRun = new Runnable() {

                                        @Override
                                        public void run() {
                                            testResult.setV2("v2: Testing");
                                        }
                                    };
                                    SwingUtilities.invokeLater(doRun);
                                } else {
                                    log.warn("Unknown ofxVersion=" + ofxVersion);
                                }
                                accountInquiry(ofxVersion);
                            } catch (final Exception e) {
                                log.error(e, e);
                                if (ofxVersion == 1) {
                                    Runnable doRun = new Runnable() {

                                        @Override
                                        public void run() {
                                            testResult.setV1("v1: Error");
                                            testResult.setV1Message(e.getMessage());
                                        }
                                    };
                                    SwingUtilities.invokeLater(doRun);
                                } else if (ofxVersion == 2) {
                                    Runnable doRun = new Runnable() {

                                        @Override
                                        public void run() {
                                            testResult.setV2("v2: Error");
                                            testResult.setV2Message(e.getMessage());
                                        }
                                    };
                                    SwingUtilities.invokeLater(doRun);
                                } else {
                                    log.warn("Unknown ofxVersion=" + ofxVersion);
                                }
                            } finally {
                                log.info("< DONE");
                            }
                        }
                    } finally {
                        source.setEnabled(true);
                    }
                }
            };
            threadPool.execute(command);

        }

        private void accountInquiry(int ofxVersion) throws IOException {
            String requestType = "creditCard";
            long lastNDays = -30L;
            String dirName = "fi/americanExpress1";

            String url = getFiInfoBean().getUrl();
            if (PropertiesUtils.isNull(url)) {
                return;
            }
            VelocityContext context = new VelocityContext();

            context.put("fi", getFiInfoBean());

            OFX ofx = new OFX();
            ofx.setVersion("" + ofxVersion);
            context.put("ofx", ofx);

            context.put("requestType", requestType);

            context.put("startDate", PropertiesUtils.parseStartDate(new Long(lastNDays)));

            Authentication auth = new Authentication();
            auth.setId(getFiInfoBean().getLogin());
            auth.setPassword(getFiInfoBean().getPassword());
            context.put("auth", auth);

            Utils utils = new Utils();
            context.put("utils", utils);

            String templateType = "accountInquiry" + "-" + "v" + ofx.getVersion();
            String workingTemplate = "/templates/" + templateType + "." + "vm";

            File dir = new File(dirName);

            String requestFileName = templateType + "-" + "req" + "." + "txt";
            File reqFile = new File(dir, requestFileName);

            String responseFileName = templateType + "-" + "resp" + "." + "txt";
            File respFile = new File(dir, responseFileName);
            String encoding = OfxPostClient.DEFAULT_TEMPLATE_ENCODING;
            VelocityUtils.mergeTemplate(context, workingTemplate, encoding, reqFile);
            // TODO
            OfxPostClient.sendRequest(new OfxPostClientParams(url, reqFile, respFile, null));

            UpdateFiDir.checkVersionedRespFile(respFile, ofx);
            if (ofxVersion == 1) {
                Runnable doRun = new Runnable() {

                    @Override
                    public void run() {
                        testResult.setV1("v1: OK");
                        testResult.setV1Message(null);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            } else if (ofxVersion == 2) {
                Runnable doRun = new Runnable() {

                    @Override
                    public void run() {
                        testResult.setV2("v2: OK");
                        testResult.setV2Message(null);
                    }
                };
                SwingUtilities.invokeLater(doRun);
            } else {
                log.warn("Unknown ofxVersion=" + ofxVersion);
            }
        }
    }

    private final class CancelAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            setFiInfoBean(null);
            dispose();
        }
    }

    private final class OkAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private final JPanel contentPanel = new JPanel();
    private JTextField nameTextField;
    private JTextField idTextField;
    private JTextField orgTextField;
    private JTextField urlTextField;
    private JTextField brokerIdTextField;
    private FiInfoBean fiInfoBean;
    private JTextField textField;
    private JPasswordField passwordField;
    private String v1TestResult = "v1: NOT_TESTED";
    private String v2TestResult = "v2: NOT_TESTED";
    private TestResult testResult = new TestResult();
    private JLabel lblVNottested;
    private JLabel lblVNottested_1;

    /**
     * Create the dialog.
     * 
     * @throws CloneNotSupportedException
     */
    public FiInfoDialog(FiInfoBean fiInfoBean) throws CloneNotSupportedException {
        if (fiInfoBean == null) {
            fiInfoBean = new FiInfoBean();
        }
        this.fiInfoBean = fiInfoBean.cloneBean();
        setBounds(100, 100, 450, 380);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.DEFAULT_COLSPEC,
                ColumnSpec.decode("max(5dlu;default)"), ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
                new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
        {
            JLabel lblName = new JLabel("Name");
            lblName.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblName, "2, 4");
        }
        {
            nameTextField = new JTextField();
            contentPanel.add(nameTextField, "4, 4, fill, default");
            nameTextField.setColumns(10);
        }
        {
            JLabel lblId = new JLabel("ID");
            lblId.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblId, "2, 6");
        }
        {
            idTextField = new JTextField();
            contentPanel.add(idTextField, "4, 6, fill, default");
            idTextField.setColumns(10);
        }
        {
            JLabel lblOrg = new JLabel("Org");
            lblOrg.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblOrg, "2, 8");
        }
        {
            orgTextField = new JTextField();
            contentPanel.add(orgTextField, "4, 8, fill, default");
            orgTextField.setColumns(10);
        }
        {
            JLabel lblUrl = new JLabel("URL");
            lblUrl.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblUrl, "2, 10");
        }
        {
            urlTextField = new JTextField();
            contentPanel.add(urlTextField, "4, 10, fill, default");
            urlTextField.setColumns(10);
        }
        {
            JLabel lblBrokerId = new JLabel("Broker Id");
            lblBrokerId.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblBrokerId, "2, 12");
        }
        {
            brokerIdTextField = new JTextField();
            contentPanel.add(brokerIdTextField, "4, 12, fill, default");
            brokerIdTextField.setColumns(10);
        }
        {
            JSeparator separator = new JSeparator();
            contentPanel.add(separator, "4, 14");
        }
        {
            JLabel lblLogin = new JLabel("Login");
            lblLogin.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblLogin, "2, 16");
        }
        {
            textField = new JTextField();
            contentPanel.add(textField, "4, 16, fill, default");
            textField.setColumns(10);
        }
        {
            JLabel lblPassword = new JLabel("Password");
            lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
            contentPanel.add(lblPassword, "2, 18");
        }
        {
            passwordField = new JPasswordField();
            contentPanel.add(passwordField, "4, 18, fill, default");
        }
        {
            JButton btnTest = new JButton("Test");
            btnTest.addActionListener(new TestAction());
            contentPanel.add(btnTest, "2, 20");
        }
        {
            JPanel panel = new JPanel();
            contentPanel.add(panel, "4, 20, fill, fill");
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            {
                lblVNottested = new JLabel("v1 XXX");
                lblVNottested.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String message = testResult.getV1Message();
                        if (message == null) {
                            message = "No messasge";
                        }
                        JOptionPane.showMessageDialog(FiInfoDialog.this, message);
                    }
                });
                panel.add(lblVNottested);
            }
            {
                Component horizontalStrut = Box.createHorizontalStrut(20);
                panel.add(horizontalStrut);
            }
            {
                lblVNottested_1 = new JLabel("v2 XXX");
                lblVNottested_1.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String message = testResult.getV2Message();
                        if (message == null) {
                            message = "No messasge";
                        }
                        JOptionPane.showMessageDialog(FiInfoDialog.this, message);
                    }
                });
                panel.add(lblVNottested_1);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new OkAction());
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new CancelAction());
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        initDataBindings();
    }

    public FiInfoBean getFiInfoBean() {
        return fiInfoBean;
    }

    public void setFiInfoBean(FiInfoBean fiInfoBean) {
        this.fiInfoBean = fiInfoBean;
    }

    public String getV1TestResult() {
        return v1TestResult;
    }

    public void setV1TestResult(String v1TestResult) {
        this.v1TestResult = v1TestResult;
    }

    public String getV2TestResult() {
        return v2TestResult;
    }

    public void setV2TestResult(String v2TestResult) {
        this.v2TestResult = v2TestResult;
    }

    protected void initDataBindings() {
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty = BeanProperty.create("name");
        BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty, nameTextField, jTextFieldBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_1 = BeanProperty.create("id");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_1, idTextField, jTextFieldBeanProperty_1);
        autoBinding_1.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_2 = BeanProperty.create("org");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_2, orgTextField, jTextFieldBeanProperty_2);
        autoBinding_2.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_3 = BeanProperty.create("url");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_3, urlTextField, jTextFieldBeanProperty_3);
        autoBinding_3.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_4 = BeanProperty.create("brokerId");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_4 = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_4, brokerIdTextField, jTextFieldBeanProperty_4);
        autoBinding_4.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_5 = BeanProperty.create("login");
        BeanProperty<JTextField, String> jTextFieldBeanProperty_5 = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_5, textField, jTextFieldBeanProperty_5);
        autoBinding_5.bind();
        //
        BeanProperty<FiInfoBean, String> fiInfoBeanBeanProperty_6 = BeanProperty.create("password");
        BeanProperty<JPasswordField, String> jPasswordFieldBeanProperty = BeanProperty.create("text");
        AutoBinding<FiInfoBean, String, JPasswordField, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, fiInfoBean,
                fiInfoBeanBeanProperty_6, passwordField, jPasswordFieldBeanProperty);
        autoBinding_6.bind();
        //
        BeanProperty<TestResult, String> testResultBeanProperty = BeanProperty.create("v1");
        BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
        AutoBinding<TestResult, String, JLabel, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, testResult, testResultBeanProperty,
                lblVNottested, jLabelBeanProperty);
        autoBinding_9.bind();
        //
        BeanProperty<TestResult, String> testResultBeanProperty_1 = BeanProperty.create("v2");
        AutoBinding<TestResult, String, JLabel, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ, testResult, testResultBeanProperty_1,
                lblVNottested_1, jLabelBeanProperty);
        autoBinding_10.bind();
    }
}
