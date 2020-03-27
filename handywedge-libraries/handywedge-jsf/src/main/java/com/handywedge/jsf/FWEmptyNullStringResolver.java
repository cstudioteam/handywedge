/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.jsf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

/*
 * JSF2.2のjavax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULLパラメータが機能しない不具合をカバーするクラス。
 * faces-config.xmlに<el-resolver>com.handywedge.jsf.FWEmptyNullStringResolver</el-resolver>と記載する。
 * （web.xmlの上記パラメータは削除する）
 */
public class FWEmptyNullStringResolver extends ELResolver {

  @Override
  public Class<?> getCommonPropertyType(ELContext context, Object base) {
    return String.class;
  }

  @Override
  public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
    return null;
  }

  @Override
  public Class<?> getType(ELContext context, Object base, Object property) {
    return null;
  }

  @Override
  public Object getValue(ELContext context, Object base, Object property) {
    return null;
  }

  @Override
  public boolean isReadOnly(ELContext context, Object base, Object property) {
    return true;
  }

  @Override
  public void setValue(ELContext context, Object base, Object property, Object value) {}

  @Override
  public Object convertToType(ELContext context, Object obj, Class<?> targetType) {
    if (String.class.equals(targetType) && obj instanceof String
        && ((String) obj).trim().isEmpty()) {
      context.setPropertyResolved(true);
    }
    return null;
  }
}
