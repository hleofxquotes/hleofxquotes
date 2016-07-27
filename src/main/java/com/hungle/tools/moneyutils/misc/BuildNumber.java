package com.hungle.tools.moneyutils.misc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class BuildNumber.
 */
public class BuildNumber {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(BuildNumber.class);

    /**
     * Find builder number.
     *
     * @param implementationVendorId
     *            the implementation vendor id
     * @param resourceName
     *            the resource name
     * @param classLoader
     *            the class loader
     * @return the string
     */
    public static String findBuilderNumber(String implementationVendorId, String resourceName,
            ClassLoader classLoader) {
        String buildNumber = null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> findBuilderNumber: resourceName=" + resourceName + ", classLoader=" + classLoader);
        }

        if (classLoader == null) {
            return null;
        }
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(resourceName);
            if (resources == null) {
                LOGGER.warn("classLoader.getResources return null");
                return null;
            }
        } catch (IOException e) {
            LOGGER.warn(e);
            return null;
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("  resource=" + resource);
            }
            if (resource == null) {
                break;
            }

            InputStream stream = null;
            try {
                stream = resource.openStream();
                if (stream == null) {
                    LOGGER.warn("  stream is null.");
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
                    // String implementationVendorId =
                    // "com.le.tools.moneyutils";
                    if (id.compareTo(implementationVendorId) != 0) {
                        continue;
                    }
                    LOGGER.info("FOUND Manifest with id='" + implementationVendorId + "'");
                    String build = attributes.getValue("Implementation-Build");
                    if (build != null) {
                        // GUI.VERSION = build;
                        buildNumber = build;
                        break;
                    } else {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(
                                    "Manifest has no value for \"Implementation-Build\", classLoader=" + classLoader);
                            LOGGER.debug("START - Dumping Manifest, resource=" + resource);
                            for (Object key : attributes.keySet()) {
                                Object value = attributes.get(key);
                                LOGGER.debug("    " + key + ": " + value);
                            }
                            LOGGER.debug("END - Dumping Manifest, resource=" + resource);
                        }
                    }
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    if (mf != null) {
                        mf = null;
                    }
                }
            } catch (IOException e) {
                LOGGER.warn(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        LOGGER.warn(e);
                    } finally {
                        stream = null;
                    }
                }
            }
        }
        return buildNumber;
    }

    /**
     * Find builder number.
     *
     * @param implementationVendorId
     *            the implementation vendor id
     * @return the string
     */
    public static String findBuilderNumber(String implementationVendorId) {
        LOGGER.info("> findBuilderNumber: implementationVendorId=" + implementationVendorId);
        String buildNumber = null;
        String resourceName = "META-INF/MANIFEST.MF";
        ClassLoader classLoader = null;
        if (buildNumber == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> findBuilderNumber (contextClassLoader): resourceName=" + resourceName + ", classLoader="
                        + classLoader);
            }
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        if (buildNumber == null) {
            classLoader = LOGGER.getClass().getClassLoader();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> findBuilderNumber (logger classLoader): resourceName=" + resourceName + ", classLoader="
                        + classLoader);
            }
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        if (buildNumber == null) {
            classLoader = ClassLoader.getSystemClassLoader();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("> findBuilderNumber (systemClassLoader): resourceName=" + resourceName + ", classLoader="
                        + classLoader);
            }
            buildNumber = findBuilderNumber(implementationVendorId, resourceName, classLoader);
        }
        return buildNumber;
    }

}
