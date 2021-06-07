/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.interceptor;

import java.sql.SQLException;
import java.sql.SQLWarning;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import com.handywedge.common.FWBusinessException;
import com.handywedge.db.FWFullConnection;
import com.handywedge.db.FWFullConnectionManager;
import com.handywedge.db.FWTransactional;
import com.handywedge.db.FWTransactional.FWTxType;
import com.handywedge.log.FWLogger;
import com.handywedge.util.FWThreadLocal;

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
      connection = connectionManager.getConnection(dataSourceName);
      switch (txType) {
        case REQUIRED:
          connection.setReadOnly(false); // プールから取得する場合は戻しておく必要がある
          connection.setAutoCommit(false);
          break;
        case NON_TRANSACTION:
          connection.setReadOnly(false);
          connection.setAutoCommit(true);
          break;
        case READ_ONLY:
          connection.setReadOnly(true);
          connection.setAutoCommit(true);
          break;
        default:
          connection.setReadOnly(false);
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
    boolean commitError = false;
    try {
      if (txMgr.isTopLayer()) {
        logger.info("transaction start.");
      }
      txMgr.incrementLayer();
      startTime = logger.perfStart(signature);
      returnVal = ctx.proceed();
      logger.perfEnd(signature, startTime);
      txMgr.decrementLayer();
      if (txMgr.isTopLayer() && !connection.getAutoCommit()) {
        try {
          try {
            connection.commit();
            logger.info("transaction commit.");
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
          commitError = true;
          throw e;
        }
      }
    } catch (Throwable t) {
      if (!commitError) {// コミットでのエラーはここでは何も処理せずにスローする
        logger.perfEnd(signature, startTime);
        txMgr.decrementLayer();

        // 最上位レイヤでロールバック判定処理
        if (txMgr.isTopLayer()) {
          FWThreadLocal.put(FWThreadLocal.INTERCEPTOR_ERROR, true); // フィルタで二重出力させないようにする

          FWBusinessException ke = t.getClass().getAnnotation(FWBusinessException.class);
          if (ke == null) {
            logger.error("Exception.", t);
          } else {
            logger.warn("BusinessLogic exception!", t);
          }

          if (!connection.getAutoCommit()) {
            // ロールバックはJTAライクではなく独自仕様にする。（例外は全てロールバック）
            boolean rollback = true;
            Class<? extends Exception>[] dontRollbackOn = tx.dontRollbackOn();
            for (Class<? extends Exception> c : dontRollbackOn) {
              if (isSubClass(c, t)) {
                rollback = false;
                break;
              }
            }

            try {
              if (rollback) {
                connection.rollback();
                logger.info("transaction rollback.");
              } else {
                connection.commit();
                logger.info("transaction commit.");
              }
            } catch (SQLException e) {
              // コミットロールバックの例外はログだけ出力して元の例外は上にスローする
              logger.error("SQLException.", e);
            }
          }
        }
      }
      throw t;
    } finally {
      if (txMgr.isTopLayer()) {
        logger.info("transaction end.");
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
