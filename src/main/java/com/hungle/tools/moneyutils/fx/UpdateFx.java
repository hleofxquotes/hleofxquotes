package com.hungle.tools.moneyutils.fx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateFx.
 */
public class UpdateFx {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(UpdateFx.class);

    private static final String DEFAULT_CLASSNAME = "app.UpdateExchangeRatesGui";

    private static final String DEFAULT_PLUGINS_DIR = "plugins";

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        try {
            invoke(args);
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Invoke.
     *
     * @param args the args
     * @throws Exception the exception
     */
    public static void invoke(String[] args) throws Exception {
        String className = DEFAULT_CLASSNAME;
        invoke(className, args);
    }

    /**
     * Invoke.
     *
     * @param className the class name
     * @param args the args
     * @throws Exception the exception
     */
    public static void invoke(String className, String[] args) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                addDir(args[i], urls);
            }
        }
        if (urls.size() <= 0) {
            addDir(DEFAULT_PLUGINS_DIR, urls);
        }

        if (urls.size() <= 0) {
            throw new IOException("No plugins found.");
        }

        URL[] urlArray = new URL[0];
        URLClassLoader classLoader = null;
        try {
            urlArray = urls.toArray(urlArray);
            LOGGER.info(urlArray.length);
            classLoader = new URLClassLoader(urlArray);
            Class<?> clz = Class.forName(className, true, classLoader);
            Method method = clz.getMethod("main", new Class[] { String[].class });
            String mainArgs[] = {};
            method.invoke(null, new Object[] { mainArgs });
        } finally {
            classLoader = null;
        }
    }

    /**
     * Invoke.
     *
     * @throws Exception the exception
     */
    public static void invoke() throws Exception {
        String[] args = null;
        invoke(args);
    }

    /**
     * Adds the dir.
     *
     * @param dirName the dir name
     * @param urls the urls
     */
    private static void addDir(String dirName, List<URL> urls) {
        LOGGER.info("Adding dirName=" + dirName);

        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            LOGGER.warn("SKIP, not a directory=" + dirName);
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (name.equals(".")) {
                    continue;
                }
                if (name.equals("..")) {
                    continue;
                }
                if (!name.endsWith(".jar")) {
                    continue;
                }
                try {
                    URL url = file.toURI().toURL();
                    urls.add(url);
                    LOGGER.info("Added url=" + url);
                } catch (MalformedURLException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }
}
