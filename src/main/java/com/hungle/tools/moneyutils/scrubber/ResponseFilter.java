package com.le.tools.moneyutils.scrubber;

import java.io.File;

import org.apache.velocity.VelocityContext;

public interface ResponseFilter {

    void filter(File respFile, VelocityContext context);

}
