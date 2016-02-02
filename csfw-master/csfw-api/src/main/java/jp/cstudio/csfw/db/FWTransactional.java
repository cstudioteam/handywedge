package jp.cstudio.csfw.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface FWTransactional {

  @Nonbinding
  public FWTransactional.FWTxType value() default FWTxType.REQUIRED;

  /* 例外は基本的に全てロールバックさせ、例外的にロールバックさせたくないものをdontRollbackOnで指定させる */
  // @Nonbinding
  // public Class<? extends Exception>[] rollbackOn() default {};

  @Nonbinding
  public Class<? extends Exception>[] dontRollbackOn() default {};

  @Nonbinding
  public String dataSourceName() default "";

  public enum FWTxType {

    REQUIRED;
  }

}
