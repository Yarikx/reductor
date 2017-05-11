package com.colorhaake.traveler.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by colorhaake on 2017/3/30.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {}
