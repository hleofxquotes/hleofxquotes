package com.hungle.tools.moneyutils.browser;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class BrowserLauncher {
    private static final Logger LOGGER = Logger.getLogger(BrowserLauncher.class);

    private static final String[] browsers = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
    private static final String errMsg = "Error attempting to launch web browser";

    public static void openURL(String urlString) {
        URI uri = URI.create(urlString);
        if (desktopBrowse(uri)) {
            return;
        }

        // library not available or failed
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] { String.class }).invoke(null, new Object[] { urlString });
            } else if (osName.startsWith("Windows"))
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + urlString);
            else {
                // assume Unix or Linux
                String browser = null;
                for (String b : browsers)
                    if (browser == null && Runtime.getRuntime().exec(new String[] { "which", b }).getInputStream().read() != -1)
                        Runtime.getRuntime().exec(new String[] { browser = b, urlString });
                if (browser == null)
                    throw new Exception(Arrays.toString(browsers));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, errMsg + "\n" + e.toString());
        }
    }

    public static boolean desktopBrowse(URI uri) {
        boolean rv = false;
        // attempt to use Desktop library from JDK 1.6+
        // java.awt.Desktop.getDesktop().browse()
        try {
            Class<?> clz = Class.forName("java.awt.Desktop");
            if (clz != null) {
                Method browseMethod = clz.getDeclaredMethod("browse", new Class[] { java.net.URI.class });
                if (browseMethod != null) {
                    Method getDesktopMethod = clz.getDeclaredMethod("getDesktop");
                    if (getDesktopMethod != null) {
                        Object desktop = getDesktopMethod.invoke(null);
                        if (desktop != null) {
                            browseMethod.invoke(desktop, new Object[] { uri });
                            rv = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e);
        }

        return rv;
    }
}
