package com.le.tools.moneyutils.misc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

public class BuildNumber {
    private static final Logger log = Logger.getLogger(BuildNumber.class);

    public static String findBuilderNumber(String implementationVendorId, String resourceName, ClassLoader classLoader) {
        String buildNumber = null;
        if (log.isDebugEnabled()) {
            log.debug("> findBuilderNumber: resourceName=" + resourceName + ", classLoader=" + classLoader);
        }

        if (classLoader == null) {
            return null;
        }
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(resourceName);
            if (resources == null) {
                log.warn("classLoader.getResources return null");
                return null;
            }
        } catch (IOException e) {
            log.warn(e);
            return null;
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (log.isDebugEnabled()) {
                log.debug("  resource=" + resource);
            }
            if (resource == null) {
                break;
            }

            InputStream stream = null;
            try {
                stream = resource.openStream();
                if (stream == null) {
                    log.warn("  stream is null.");
                    break;
                }

                Manifest mf = null;
                try {
                    mf = new Manifest();
                    mf.read(stream);
                    Attributes attributes = mf.getMainAttributes();
                    // Implementation-Vendor-Id: com.le.tools.moneyutils
                    String id = attributes.getValue("Implementation-Vendor-Id");
                    if ((id == null) || (id.length() <= 0)) {
                        continue;
                    }
//                    String implementationVendorId = "com.le.tools.moneyutils";
                    if (id.compareTo(implementationVendorId) != 0) {
                        continue;
                    }
                    log.info("FOUND Manifest with id='" + implementationVendorId + "'");
                    String build = attributes.getValue("Implementation-Build");
                    if (build != null) {
                        // GUI.VERSION = build;
                        buildNumber = build;
                        break;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Manifest has no value for \"Implementation-Build\", classLoader=" + classLoader);
                            log.debug("START - Dumping Manifest, resource=" + resource);
                            for (Object key : attributes.keySet()) {
                                Object value = attributes.get(key);
                                log.debug("    " + key + ": " + value);
                            }
                            log.debug("END - Dumping Manifest, resource=" + resource);
                        }
                    }
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    if (mf != null) {
                        mf = null;
                    }
                }
            } catch (IOException e) {
                log.warn(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        log.warn(e);
                    } finally {
                        stream = null;
                    }
                }
            }
        }
        return buildNumber;
    }

    public static String findBuilderNumber(String implementationVendorId) {
        log.info("> findBuilderNumber: implementationVendorId=" + implementationVendorId);
        String buildNumber = null;
        String resourceName = "META-INF/MANIFEST.MF";
        ClassLoader classLoader = null;
        if (buildNumber == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            log.debug("> findBuilderNumber (contextClassLoader): resourceName=" + resourceName + ", classLoader=" + classLoader);
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        if (buildNumber == null) {
            classLoader = log.getClass().getClassLoader();
            log.debug("> findBuilderNumber (logger classLoader): resourceName=" + resourceName + ", classLoader=" + classLoader);
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        if (buildNumber == null) {
            classLoader = ClassLoader.getSystemClassLoader();
            log.debug("> findBuilderNumber (systemClassLoader): resourceName=" + resourceName + ", classLoader=" + classLoader);
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        return buildNumber;
    }

}
