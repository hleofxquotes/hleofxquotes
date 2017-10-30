package com.hungle.tools.moneyutils.ofx.quotes.net;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.hungle.tools.moneyutils.fi.props.FIBean;

public class CheckOfxVersionTest {
    @Test
    public void testInit() {
        CheckOfxVersion checker = new CheckOfxVersion();
        Assert.assertNotNull(checker);
    }

    @Test
    public void testFiDir() {
        CheckOfxVersion checker = new CheckOfxVersion();

        File fiDir = new File(FIBean.getDefaultFiDir());

        File[] dirs = fiDir.listFiles();
        for (File dir : dirs) {
            if (!dir.isDirectory()) {
                continue;
            }
            if (dir.getName().compareToIgnoreCase(".") == 0) {
                continue;
            }
            if (dir.getName().compareToIgnoreCase("..") == 0) {
                continue;
            }
            checker.check(dir);
        }
    }
}
