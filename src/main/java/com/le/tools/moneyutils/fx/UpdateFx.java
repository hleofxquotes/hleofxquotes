package com.le.tools.moneyutils.fx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class UpdateFx {
    private static final Logger log = Logger.getLogger(UpdateFx.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            invoke(args);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public static void invoke(String[] args) throws Exception {
        String className = "app.UpdateExchangeRatesGui";
        invoke(className, args);
    }

    public static void invoke(String className, String[] args) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                addDir(args[i], urls);
            }
        }
        if (urls.size() <= 0) {
            addDir("plugins", urls);
        }

        if (urls.size() <= 0) {
            throw new IOException("No plugins found.");
        }

        URL[] urlArray = new URL[0];
        URLClassLoader classLoader = null;
        try {
            urlArray = urls.toArray(urlArray);
            log.info(urlArray.length);
            classLoader = new URLClassLoader(urlArray);
            Class clz = Class.forName(className, true, classLoader);
            Method method = clz.getMethod("main", new Class[] { String[].class });
            String mainArgs[] = {};
            method.invoke(null, new Object[] { mainArgs });
        } finally {
            classLoader = null;
        }
    }

    public static void invoke() throws Exception {
        String[] args = null;
        invoke(args);
    }

    private static void addDir(String dirName, List<URL> urls) {
        log.info("Adding dirName=" + dirName);

        File d = new File(dirName);
        if (!d.isDirectory()) {
            log.warn("SKIP, not a directory=" + dirName);
        }

        File[] files = d.listFiles();
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
                    log.info("Added url=" + url);
                } catch (MalformedURLException e) {
                    log.warn(e);
                }
            }
        }
    }
}
