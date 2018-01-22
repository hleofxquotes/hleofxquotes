package com.hungle.msmoney.core.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class ResourceUtils {
    private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class);

    /**
     * Gets the resource.
     *
     * @param name the name
     * @return the resource
     */
    public static URL getResource(String name) {
        return ResourceUtils.getResource(name, name);
    }

    /**
     * Gets the resource.
     *
     * @param name the name
     * @param obj the obj
     * @return the resource
     */
    public static URL getResource(String name, Object obj) {
        if (obj == null) {
            obj = name;
        }
    
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(name);
        if (url == null) {
            cl = obj.getClass().getClassLoader();
            if (cl != null) {
                url = cl.getResource(name);
            }
        }
    
        if (url == null) {
            url = obj.getClass().getResource(name);
        }
        return url;
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        int bufSize = 1024;
        int n = 0;
        byte[] buffer = new byte[bufSize];
        while ((n = in.read(buffer, 0, bufSize)) != -1) {
            out.write(buffer, 0, n);
        }
    }

}
