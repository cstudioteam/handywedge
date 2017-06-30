package jp.cstudio.handywedge.test.app.rest;

import java.sql.SQLException;

import javax.inject.Inject;

import com.handywedge.log.FWLogger;
import com.handywedge.rest.FWRESTController;
import com.handywedge.rest.FWRESTRequest;
import com.handywedge.rest.FWRESTRequestClass;
import com.handywedge.rest.FWRESTResponse;

import jp.cstudio.handywedge.test.app.db.DBMasterService;
import jp.cstudio.handywedge.test.app.db.dto.Test;

public class RESTKeyValueStore extends FWRESTController {

  @Inject
  private DBMasterService service;

  @Inject
  private FWLogger logger;

  @Override
  @FWRESTRequestClass(RESTKeyValueRequest.class)
  public FWRESTResponse doPost(FWRESTRequest request) {
    RESTKeyValueRequest req = (RESTKeyValueRequest) request;
    logger.info("doPost　データ登録。{}", req);
    Test test = new Test();
    test.setKey(req.getKey());
    test.setValue(req.getValue());
    try {
      service.insert(test);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    RESTKeyValueResponse res = new RESTKeyValueResponse();
    res.setReturn_cd(0);
    res.setReturn_msg("データを登録しました。");

    return res;
  }

  @Override
  @FWRESTRequestClass(RESTKeyValueRequest.class)
  public FWRESTResponse doPut(FWRESTRequest request) {
    RESTKeyValueRequest req = (RESTKeyValueRequest) request;
    logger.info("doPut　データ更新。{}", req);

    Test test = null;
    try {
      test = service.select(req.getKey());
    } catch (SQLException e1) {
      e1.printStackTrace();
    }
    if (test == null) {
      logger.debug("doPut insert.");
      test = new Test();
      test.setKey(req.getKey());
      test.setValue(req.getValue());
      try {
        service.insert(test);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else {
      logger.debug("doPut update.");
      test.setValue(req.getValue());
      try {
        service.update(test);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    RESTKeyValueResponse res = new RESTKeyValueResponse();
    res.setReturn_cd(0);
    res.setReturn_msg("データを更新しました。");

    return res;
  }

  @Override
  @FWRESTRequestClass(RESTKeyValueRequest.class)
  public FWRESTResponse doDelete(FWRESTRequest request) {
    logger.info("doDelete　データ削除。{}", request);
    RESTKeyValueRequest req = (RESTKeyValueRequest) request;
    try {
      service.delete(req.getKey());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    RESTKeyValueResponse res = new RESTKeyValueResponse();
    res.setReturn_cd(0);
    res.setReturn_msg("データを削除しました。");

    return res;
  }

  @Override
  public FWRESTResponse doGet(String param) {
    logger.info("doGet　データ取得。{}", param);
    Test test = null;
    try {
      test = service.select(param);
    } catch (SQLException e1) {
      e1.printStackTrace();
    }

    RESTKeyValueResponse res = new RESTKeyValueResponse();
    if (test == null) {
      res.setReturn_cd(-1);
      res.setReturn_msg("データがありません。");
    } else {
      res.setReturn_cd(0);
      res.setKey(test.getKey());
      res.setValue(test.getValue());
    }

    return res;
  }
}
