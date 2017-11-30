package com.hungle.tools.moneyutils.fi.props;

import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.Assert;
import org.junit.Test;

public class FIBeanTest {
    @Test
    public void testParseEmpty() {
        Properties props = new Properties();
        BeanUtilsBean beanUtilsBean = PropertiesUtils.getDefaultBeanUtilsBean();
        FIBean bean = FIBean.parse(props, beanUtilsBean);
        Assert.assertNotNull(bean);

        String name = bean.getName();
        Assert.assertNull(name);
        
        String org = bean.getOrg();
        Assert.assertNull(org);

        String id = bean.getId();
        Assert.assertNull(id);
        
        String brokerId = bean.getBrokerId();
        Assert.assertNull(brokerId);

        String url = bean.getUrl();
        Assert.assertNull(url);
    }
    
    @Test
    public void testParse() {
        Properties props = new Properties();
        
        String fiName = "FI Name";
        props.setProperty(FIBean.FI_PREFIX + "." + "name", fiName);
        
        String fiOrg = "FI Org";
        props.setProperty(FIBean.FI_PREFIX + "." + "org", fiOrg);
        
        String fiId = "FI ID";
        props.setProperty(FIBean.FI_PREFIX + "." + "id", fiId);

        String fiBrokerId = "FI Broker ID";
        props.setProperty(FIBean.FI_PREFIX + "." + "brokerId", fiBrokerId);

        String fiUrl = "FI URL";
        props.setProperty(FIBean.FI_PREFIX + "." + "url", fiUrl);

        BeanUtilsBean beanUtilsBean = PropertiesUtils.getDefaultBeanUtilsBean();
        FIBean bean = FIBean.parse(props, beanUtilsBean);
        Assert.assertNotNull(bean);

        String name = bean.getName();
        Assert.assertEquals(fiName, name);
        
        String org = bean.getOrg();
        Assert.assertEquals(fiOrg, org);

        String id = bean.getId();
        Assert.assertEquals(fiId, id);
        
        String brokerId = bean.getBrokerId();
        Assert.assertEquals(fiBrokerId, brokerId);

        String url = bean.getUrl();
        Assert.assertEquals(fiUrl, url);
    }
}
