package com.hungle.msmoney.core.ofx.net;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.hungle.msmoney.qs.net.CheckOfxVersion;
import com.hungle.msmoney.statements.fi.props.FIBean;

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
        if (fiDir == null) {
        	return;
        }
        
        File[] dirs = fiDir.listFiles();
        if (dirs == null) {
        	return;
        }
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
