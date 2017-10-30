package com.hungle.tools.moneyutils.ofx.statement;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import difflib.Delta;
import difflib.DiffUtils;

final class ShowDiffTask extends AbstractAction {
    /**
     * 
     */
    private final StatementPanel statementPanel;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    ShowDiffTask(StatementPanel statementPanel, String name) {
        super(name);
        this.statementPanel = statementPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        StatementPanel.LOGGER.info("###");
        StatementPanel.LOGGER.info("> Show Diffs: " + this.statementPanel.detailsForBean.getFiUrl());
        if (this.statementPanel.detailsForBean == null) {
            return;
        }

        File respFile = this.statementPanel.detailsForBean.getUpdater().getRespFile();
        File savedCertificates = new File(respFile.getAbsoluteFile().getParentFile(), StatementPanel.SAVED_CERTIFICATES_TXT);
        File currentCertificates = new File(respFile.getAbsoluteFile().getParentFile(),
                StatementPanel.CURRENT_CERTIFICATES_TXT);
        try {
            List<String> original = fileToLines(savedCertificates.getAbsolutePath());
            List<String> revised = fileToLines(currentCertificates.getAbsolutePath());
            difflib.Patch<String> patch = DiffUtils.diff(original, revised);
            for (Delta<?> delta : patch.getDeltas()) {
                StatementPanel.LOGGER.info(delta);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.statementPanel, e.toString(),
                    "Error generating diff for SSL Certificates", JOptionPane.ERROR_MESSAGE);
        }

    }

    public List<String> fileToLines(String filename) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}