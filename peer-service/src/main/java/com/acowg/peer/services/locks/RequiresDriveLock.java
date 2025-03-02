package com.acowg.peer.services.locks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresDriveLock {
    /**
     * Name of the parameter containing the drive identifier.
     * If empty, drive will be extracted from path parameter(s).
     */
    String driveParamName() default "";

    /**
     * Name of the parameter containing the path or collection of paths.
     * If empty, the first suitable parameter will be auto-detected.
     */
    String pathParamName() default "";
}