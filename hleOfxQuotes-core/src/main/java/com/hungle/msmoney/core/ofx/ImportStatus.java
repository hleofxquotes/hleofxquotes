package com.hungle.msmoney.core.ofx;

import java.util.List;

public class ImportStatus {
    private int statusCode = -1;
    private List<String> stdoutLines;
    private List<String> stderrLines;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getStdoutLines() {
        return stdoutLines;
    }

    public void setStdoutLines(List<String> stdoutLines) {
        this.stdoutLines = stdoutLines;
    }

    public List<String> getStderrLines() {
        return stderrLines;
    }

    public void setStderrLines(List<String> stderrLines) {
        this.stderrLines = stderrLines;
    }
}
