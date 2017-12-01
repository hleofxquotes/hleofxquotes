package com.hungle.msmoney.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Auto-generated Javadoc
/**
 * The Interface PropertyAnnotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface PropertyAnnotation {

    /**
     * Key.
     *
     * @return the string
     */
    String key();

    /**
     * Index.
     *
     * @return the int
     */
    int index();

}
