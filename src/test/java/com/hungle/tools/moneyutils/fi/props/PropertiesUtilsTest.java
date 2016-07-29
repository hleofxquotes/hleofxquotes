package com.hungle.tools.moneyutils.fi.props;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesUtilsTest {
    @Test
    public void testIsNull() {
        Assert.assertTrue(PropertiesUtils.isNull(null));
        Assert.assertTrue(PropertiesUtils.isNull(""));

        Assert.assertFalse(PropertiesUtils.isNull(" "));
        Assert.assertFalse(PropertiesUtils.isNull("  "));

        Assert.assertFalse(PropertiesUtils.isNull("x"));
        Assert.assertFalse(PropertiesUtils.isNull(" x"));
        Assert.assertFalse(PropertiesUtils.isNull(" x "));
    }

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

    private void testSetOfxVersion(String prefix, String key, String versionValue) {
        Collection<String> keys = new ArrayList<String>();
        keys.add(key);

        Properties props = new Properties();
        if (PropertiesUtils.isNull(prefix)) {
            props.setProperty(key, versionValue);
        } else {
            props.setProperty(prefix + "." + key, versionValue);
        }

        BeanUtilsBean beanUtilsBean = PropertiesUtils.getDefaultBeanUtilsBean();
        OFX ofx = new OFX();
        PropertiesUtils.setProperties(prefix, keys, props, ofx, beanUtilsBean);

        Assert.assertEquals(versionValue, ofx.getVersion());
    }
}
