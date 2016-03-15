/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.interceptor;

import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.csframe.db.FWFullConnection;
import com.csframe.db.FWFullConnectionManager;
import com.csframe.db.FWTransactional;
import com.csframe.db.FWTransactional.FWTxType;
import com.csframe.log.FWLogger;
import com.csframe.util.FWThreadLocal;

@Dependent
@FWTransactional
@Interceptor
public class FWTransactionalInterceptor {

  @Inject
  private FWLogger logger;

  @Inject
  private FWTransactionManager txMgr;

  @Inject
  private FWFullConnectionManager connectionManager;

  public FWTransactionalInterceptor() {}

  // TODO DBコネクションはセッションフィルターで閉じる？二重でもいいかも。
  @AroundInvoke
  public Object intercept(InvocationContext ctx) throws Throwable {

    FWTransactional tx = ctx.getMethod().getAnnotation(FWTransactional.class);
    FWTxType txType = tx.value();
    String dataSourceName = tx.dataSourceName();

    FWFullConnection connection = null;
    if (txMgr.isTopLayer()) {
      logger.debug("toplayer. txType={}, dataSourceName={}", txType, dataSourceName);
      switch (txType) {
        case REQUIRED:
          connection = connectionManager.getConnection(dataSourceName);
          connection.setAutoCommit(false);
          break;
        default:
          connection = connectionManager.getConnection(dataSourceName);
          connection.setAutoCommit(false);
          break;
      }
    } else {
      // 今のところREQUIRED_NEWなどはサポートしていないので処理はない
    }

    String signature =
        ctx.getMethod().getDeclaringClass().getSimpleName() + "#" + ctx.getMethod().getName();
    Object returnVal = null;
    long startTime = 0L;
    try {
      txMgr.incrementLayer();
      startTime = logger.perfStart(signature);
      returnVal = ctx.proceed();
      logger.perfEnd(signature, startTime);
      txMgr.decrementLayer();
      if (txMgr.isTopLayer()) {
        try {
          try {
            connection.commit();
            logger.debug("transaction commit.");
          } finally {
            SQLWarning sw = connection.getWarnings();
            while (sw != null) {
              logger.warn("SQLWarning.", sw);
              sw = sw.getNextWarning();
            }
          }
        } catch (SQLException e) {
          logger.error("SQLException.", e);
          FWThreadLocal.put(FWThreadLocal.INTERCEPTOR_ERROR, true);
          throw e;
        }
      }
    } catch (Throwable t) {
      txMgr.decrementLayer();
      logger.perfEnd(signature, startTime);

      Boolean errorFlg = FWThreadLocal.get(FWThreadLocal.INTERCEPTOR_ERROR);
      // 最上位レイヤでロールバック判定処理
      if (txMgr.isTopLayer() && errorFlg == null) {
        FWThreadLocal.put(FWThreadLocal.INTERCEPTOR_ERROR, true); // フィルタで二重出力させないようにする

        // TODO 業務例外みたいなものを作る？
        logger.error("Exception.", t);

        // ロールバックはJTAライクではなく独自仕様にする。（例外は全てロールバック）
        boolean rollback = true;
        Class<? extends Exception>[] dontRollbackOn = tx.dontRollbackOn();
        for (Class<? extends Exception> c : dontRollbackOn) {
          if (isSubClass(c, t)) {
            rollback = false;
            break;
          }
        }

        if (rollback) {
          connection.rollback();
          logger.debug("transaction rollback.");
        } else {
          connection.commit();
          logger.debug("transaction commit.");
        }
      }
      throw t;
    } finally {
      if (txMgr.isTopLayer()) {
        connectionManager.close(); // closeはマネージャに委譲
      }
    }

    return returnVal;
  }

  private boolean isSubClass(Class<?> expected, Object actual) {

    boolean retVal = false;
    Class<?> superClazz = actual.getClass();
    while (superClazz != null) {
      if (superClazz == expected) {
        retVal = true;
        break;
      }
      superClazz = superClazz.getSuperclass();
    }
    return retVal;
  }

}
