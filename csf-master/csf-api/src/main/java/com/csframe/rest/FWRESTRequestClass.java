package com.csframe.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = {ElementType.METHOD})
public @interface FWRESTRequestClass {

  Class<? extends FWRESTRequest> value();

}
