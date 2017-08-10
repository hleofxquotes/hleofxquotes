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

import com.hungle.tools.moneyutils.fi.AbstractUpdateFiDir;
import com.hungle.tools.moneyutils.fi.props.FIBean;
import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FiBean.
 */
public class FiBean {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(FiBean.class);

    /** The name. */
    private String name;

    /** The fi. */
    private FIBean fi;

    /** The updater. */
    private AbstractUpdateFiDir updater;

    /** The status. */
    private String status;

    /** The last downloaded. */
    private Date lastDownloaded;

    /** The last imported. */
    private Date lastImported;

    /** The date formatter. */
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    /** The exception. */
    private Exception exception;

    /** The downloaded. */
    private boolean downloaded = false;
    
    /** The support. */
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Adds the property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes the property change listener.
     *
     * @param listener the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        final String oldValue = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldValue, name);
    }

    /**
     * Gets the fi.
     *
     * @return the fi
     */
    public FIBean getFi() {
        return fi;
    }

    /**
     * Sets the fi.
     *
     * @param fi the new fi
     */
    public void setFi(FIBean fi) {
        this.fi = fi;
    }

    /**
     * Gets the fi org.
     *
     * @return the fi org
     */
    public String getFiOrg() {
        return fi.getOrg();
    }

    /**
     * Gets the fi broker id.
     *
     * @return the fi broker id
     */
    public String getFiBrokerId() {
        return fi.getBrokerId();
    }

    /**
     * Gets the fi id.
     *
     * @return the fi id
     */
    public String getFiId() {
        return fi.getId();
    }

    /**
     * Gets the fi url.
     *
     * @return the fi url
     */
    public String getFiUrl() {
        return fi.getUrl();
    }

    /**
     * Gets the updater.
     *
     * @return the updater
     */
    public AbstractUpdateFiDir getUpdater() {
        return updater;
    }

    /**
     * Sets the updater.
     *
     * @param updater the new updater
     */
    public void setUpdater(AbstractUpdateFiDir updater) {
        this.updater = updater;
        readDownloadData();
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        final String oldValue = this.status;
        this.status = status;
        this.support.firePropertyChange("status", oldValue, status);
        writeDownloadData();
    }

    /**
     * Gets the last downloaded.
     *
     * @return the last downloaded
     */
    public Date getLastDownloaded() {
        return lastDownloaded;
    }

    /**
     * Sets the last downloaded.
     *
     * @param lastDownloaded the new last downloaded
     */
    public void setLastDownloaded(Date lastDownloaded) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> setLastUpdated");
        }
        Date oldValue = this.lastDownloaded;
        this.lastDownloaded = lastDownloaded;
        this.support.firePropertyChange("lastDownloaded", oldValue, lastDownloaded);
        writeDownloadData();
    }

    /**
     * Read download data.
     */
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
            if (!PropertiesUtils.isNull(value)) {
                setStatus(value);
            }
            value = props.getProperty("download.lastImported");
            if (!PropertiesUtils.isNull(value)) {
                try {
                    setLastImported(dateFormatter.parse(value));
                } catch (ParseException e) {
                    LOGGER.warn(e);
                }
            }
            value = props.getProperty("download.lastDownloaded");
            if (!PropertiesUtils.isNull(value)) {
                try {
                    setLastDownloaded(dateFormatter.parse(value));
                } catch (ParseException e) {
                    LOGGER.warn(e);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Write download data.
     */
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
            LOGGER.error(e);
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }

    /**
     * Gets the last imported.
     *
     * @return the last imported
     */
    public Date getLastImported() {
        return lastImported;
    }

    /**
     * Sets the last imported.
     *
     * @param lastImported the new last imported
     */
    public void setLastImported(Date lastImported) {
        final Date oldValue = this.lastImported;
        this.lastImported = lastImported;
        this.support.firePropertyChange("lastImported", oldValue, lastImported);
        writeDownloadData();
    }

    /**
     * Gets the exception.
     *
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Sets the exception.
     *
     * @param exception the new exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * Checks if is downloaded.
     *
     * @return true, if is downloaded
     */
    public boolean isDownloaded() {
        return downloaded;
    }

    /**
     * Sets the downloaded.
     *
     * @param downloaded the new downloaded
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
