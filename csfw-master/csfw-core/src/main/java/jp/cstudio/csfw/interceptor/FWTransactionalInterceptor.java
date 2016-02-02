package jp.cstudio.csfw.interceptor;

import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import jp.cstudio.csfw.db.FWFullConnection;
import jp.cstudio.csfw.db.FWFullConnectionManager;
import jp.cstudio.csfw.db.FWTransactional;
import jp.cstudio.csfw.db.FWTransactional.FWTxType;
import jp.cstudio.csfw.log.FWLogger;
import jp.cstudio.csfw.util.FWThreadLocal;

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
          } finally {
            SQLWarning sw = connection.getWarnings();
            while (sw != null) {
              logger.warn("FWTransactionalInterceptor SQLWarning.", sw);
              sw = sw.getNextWarning();
            }
          }
        } catch (SQLException e) {
          logger.error("FWTransactionalInterceptor SQLException.", e);
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
        logger.error("FWTransactionalInterceptor Exception.", t);

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
        } else {
          connection.commit();
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
