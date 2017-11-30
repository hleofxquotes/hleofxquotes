package com.hungle.msmoney.statements.scrubber;

import java.io.File;

import org.apache.velocity.VelocityContext;

public interface ResponseFilter {

    void filter(File respFile, VelocityContext context);

}
