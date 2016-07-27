package com.hungle.tools.moneyutils.ofx.statement;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.UpdateFiDir;
import com.hungle.tools.moneyutils.fi.props.FIBean;

public class FiBean {
    private static final Logger log = Logger.getLogger(FiBean.class);

    private String name;

    private FIBean fi;

    private UpdateFiDir updater;

    private String status;

    private Date lastDownloaded;

    private Date lastImported;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    private Exception exception;

    private boolean downloaded = false;
    
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        final String oldValue = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldValue, name);
    }

    public FIBean getFi() {
        return fi;
    }

    public void setFi(FIBean fi) {
        this.fi = fi;
    }

    public String getFiOrg() {
        return fi.getOrg();
    }

    public String getFiBrokerId() {
        return fi.getBrokerId();
    }

    public String getFiId() {
        return fi.getId();
    }

    public String getFiUrl() {
        return fi.getUrl();
    }

    public UpdateFiDir getUpdater() {
        return updater;
    }

    public void setUpdater(UpdateFiDir updater) {
        this.updater = updater;
        readDownloadData();
    }

    private boolean isNull(String str) {
        if (str == null) {
            return true;
        }
        if (str.length() <= 0) {
            return true;
        }
        return false;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        final String oldValue = this.status;
        this.status = status;
        this.support.firePropertyChange("status", oldValue, status);
        writeDownloadData();
    }

    public Date getLastDownloaded() {
        return lastDownloaded;
    }

    public void setLastDownloaded(Date lastDownloaded) {
        if (log.isDebugEnabled()) {
            log.debug("> setLastUpdated");
        }
        Date oldValue = this.lastDownloaded;
        this.lastDownloaded = lastDownloaded;
        this.support.firePropertyChange("lastDownloaded", oldValue, lastDownloaded);
        writeDownloadData();
    }

    private void readDownloadData() {
        Properties props = new Properties();
        Reader reader = null;
        try {
            File file = new File(updater.getDir(), "download.properties");
            if (!file.exists()) {
                return;
            }
            reader = new BufferedReader(new FileReader(file));
            props.load(reader);
            String value = null;
            value = props.getProperty("download.status");
            if (!isNull(value)) {
                setStatus(value);
            }
            value = props.getProperty("download.lastImported");
            if (!isNull(value)) {
                try {
                    setLastImported(dateFormatter.parse(value));
                } catch (ParseException e) {
                    log.warn(e);
                }
            }
            value = props.getProperty("download.lastDownloaded");
            if (!isNull(value)) {
                try {
                    setLastDownloaded(dateFormatter.parse(value));
                } catch (ParseException e) {
                    log.warn(e);
                }
            }
        } catch (IOException e) {
            log.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    private void writeDownloadData() {
        Properties props = new Properties();
        props.setProperty("download.status", (status != null) ? status : "");
        props.setProperty("download.lastImported", (lastImported != null) ? dateFormatter.format(lastImported) : "");
        props.setProperty("download.lastDownloaded", (lastDownloaded != null) ? dateFormatter.format(lastDownloaded) : "");
        PrintWriter writer = null;
        File file = new File(updater.getDir(), "download.properties");
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            props.list(writer);
        } catch (IOException e) {
            log.error(e);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    public Date getLastImported() {
        return lastImported;
    }

    public void setLastImported(Date lastImported) {
        final Date oldValue = this.lastImported;
        this.lastImported = lastImported;
        this.support.firePropertyChange("lastImported", oldValue, lastImported);
        writeDownloadData();
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
