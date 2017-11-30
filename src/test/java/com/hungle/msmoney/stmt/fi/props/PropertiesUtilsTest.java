package com.hungle.msmoney.stmt.fi.props;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.velocity.VelocityContext;
import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.core.misc.CheckNullUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertiesUtilsTest.
 */
public class PropertiesUtilsTest {

    /**
     * Test is null.
     */
    @Test
    public void testIsNull() {
        Assert.assertTrue(CheckNullUtils.isNull(null));
        Assert.assertTrue(CheckNullUtils.isNull(""));

        Assert.assertFalse(CheckNullUtils.isNull(" "));
        Assert.assertFalse(CheckNullUtils.isNull("  "));

        Assert.assertFalse(CheckNullUtils.isNull("x"));
        Assert.assertFalse(CheckNullUtils.isNull(" x"));
        Assert.assertFalse(CheckNullUtils.isNull(" x "));
    }

    /**
     * Test set properties.
     */
    @Test
    public void testSetProperties() {
        String prefix = null;
        String key = OFX.KEY_VERSION;
        String versionValue = "101";

        testSetOfxVersion(prefix, key, versionValue);

        // with prefix
        prefix = OFX.PREFIX;
        testSetOfxVersion(prefix, key, versionValue);

        // another value
        versionValue = "202";
        testSetOfxVersion(prefix, key, versionValue);
    }

    /**
     * Test set ofx version.
     *
     * @param prefix
     *            the prefix
     * @param key
     *            the key
     * @param versionValue
     *            the version value
     */
    private void testSetOfxVersion(String prefix, String key, String versionValue) {
        Collection<String> keys = new ArrayList<String>();
        keys.add(key);

        Properties props = new Properties();
        if (CheckNullUtils.isNull(prefix)) {
            props.setProperty(key, versionValue);
        } else {
            props.setProperty(prefix + "." + key, versionValue);
        }

        BeanUtilsBean beanUtilsBean = PropertiesUtils.getDefaultBeanUtilsBean();
        OFX ofx = new OFX();
        PropertiesUtils.setProperties(prefix, keys, props, ofx, beanUtilsBean);

        Assert.assertEquals(versionValue, ofx.getVersion());
    }

    @Test
    public void testCreateFromEmptyFile() throws IOException {
        File propsFile = File.createTempFile("test", ".props");
        propsFile.deleteOnExit();
        VelocityContext context = PropertiesUtils.createVelocityContext(propsFile);
        Assert.assertNotNull(context);
        
        FIBean fiBean = PropertiesUtils.getFiBean(context);
        Assert.assertNotNull(fiBean);
        
        OFX ofx = PropertiesUtils.getOfx(context);
        Assert.assertNotNull(ofx);
        
        HttpProperties httpProperties = PropertiesUtils.getHttpProperties(context);
        Assert.assertNotNull(httpProperties);
        
        String requestType = PropertiesUtils.getRequestType(context);
        Assert.assertNull(requestType);        
    }
}
