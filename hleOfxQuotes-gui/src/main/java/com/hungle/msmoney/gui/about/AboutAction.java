package com.hungle.msmoney.gui.about;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.AbstractAction;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.hungle.msmoney.gui.GUI;

public final class AboutAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(AboutAction.class);

    /**
     * 
     */
    private final GUI gui;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AboutAction(GUI gui, String name) {
        super(name);
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // showMessageDialog();
        StringBuilder sb = new StringBuilder();
        sb.append("Version: " + GUI.VERSION + "\n");
        sb.append("Home page: " + GUI.HOME_PAGE + "\n");
        sb.append("Wiki (documentation): http://code.google.com/p/hle-ofx-quotes/wiki" + "\n");
        sb.append("\n");
        sb.append("Home Directory: " + GUI.getHomeDirectory() + "\n");
        sb.append("Top Directory: " + GUI.getTopDirectory() + "\n");
        sb.append("Current Directory: " + GUI.getCurrentWorkingDirectory() + "\n");
        sb.append("Currency: " + this.gui.getDefaultCurrency() + "\n");
        sb.append("OFX Account Id: " + this.gui.getAccountId() + "\n");
        sb.append("Yahoo server: " + this.gui.getYahooQuoteServer() + "\n");
        File file = new File("hleOfxQuotes-log.txt");
        if (file.exists()) {
            sb.append("Log file: " + file.getAbsoluteFile().getAbsolutePath() + "\n");
        } else {
            sb.append("Log file: " + "NOT_FOUND" + "\n");
        }

        sb.append("\n");
        String[] keys = { "dateOffset", "randomizeShareCount", "forceGeneratingINVTRANLIST", "suspiciousPrice",
                "incrementallyIncreasedShareCount" };
        Arrays.sort(keys);
        for (String key : keys) {
            try {
                String value = BeanUtils.getProperty(this.gui, key);
                sb.append(key + "=" + value + "\n");
            } catch (Exception e) {
                LOGGER.warn(e);
            }
        }

        Component source = (Component) event.getSource();
        source = null;
        AboutDialog.showDialog(this.gui, source, "About", sb.toString());
    }
}