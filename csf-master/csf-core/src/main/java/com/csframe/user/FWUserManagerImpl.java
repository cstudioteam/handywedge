/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.user;

import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;

@ApplicationScoped
public class FWUserManagerImpl implements FWUserManager {

  @Inject
  private FWUserService service;

  @Override
  public boolean register(String id, String password) {
    try {
      int result = service.insert(id, password);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public void update(FWUserData user) {
    throw new UnsupportedOperationException("未実装です。");

  }

  @Override
  public void delete(String id) {
    throw new UnsupportedOperationException("未実装です。");
  }

  @Override
  public boolean changePassword(String id, String password) {
    try {
      int result = service.changePassword(id, password);
      return result == 1;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

}
