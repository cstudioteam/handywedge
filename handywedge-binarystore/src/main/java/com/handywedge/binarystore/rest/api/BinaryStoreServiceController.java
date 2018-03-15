/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api;


import static java.nio.charset.StandardCharsets.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceDeleteRequest;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceDeleteResponse;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceGetRequest;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceGetResponse;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceListRequest;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceListResponse;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceUploadRequest;
import com.handywedge.binarystore.rest.api.model.BinaryStoreServiceUploadResponse;
import com.handywedge.binarystore.store.IStoreManager;
import com.handywedge.binarystore.store.common.BinaryInfo;
import com.handywedge.binarystore.store.common.ErrorClassification;
import com.handywedge.binarystore.store.common.StorageInfo;
import com.handywedge.binarystore.store.common.StoreException;
import com.handywedge.binarystore.util.CommonUtils;

@RequestScoped
@Path("/binary")
public class BinaryStoreServiceController {

  private Logger logger = LoggerFactory.getLogger(BinaryStoreServiceController.class);

  private IStoreManager manager = null;

  private StorageInfo storage = null;

  /**
   * コンストラクタ
   *
   * @throws StoreException
   */
  public BinaryStoreServiceController() throws StoreException {
    String curStorage = System.getenv("HW_STORAGE");
    logger.debug("storage={}", curStorage);
    if (curStorage == null) {
      curStorage = "";
    }
    switch (curStorage) {
      case "S3":
        storage = new StorageInfo("S3");
        manager = new com.handywedge.binarystore.store.aws.BinaryStoreManagerImpl();
        break;
      case "GCS":
        storage = new StorageInfo("GCS");
        manager = new com.handywedge.binarystore.store.gcs.BinaryStoreManagerImpl();
        break;
      case "ABS":
        storage = new StorageInfo("ABS");
        manager = new com.handywedge.binarystore.store.azure.BinaryStoreManagerImpl();
        break;
      default:
        break;
    }
  }

  public void init() throws StoreException {
    if (manager == null) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          ErrorClassification.RUNTIME_ERROR, "ストレージの種別が判別できませんでした。コンテナが正しく起動できていません。");
    }
  }

  /**
   * getBucketName
   *
   * @return
   * @throws StoreException
   */
  private String getBucketName(String requestId, String bucketName) throws StoreException {
    logger.info("getBucketName start.");
    logger.info(" パラメータ： requestId={}, bucketName={}", requestId, bucketName);

    String rtnBucketName = "";
    if (!CommonUtils.isNullOrEmpty(bucketName)) {
      rtnBucketName = bucketName;
    } else if (!CommonUtils.isNullOrEmpty(requestId)) {
      rtnBucketName = requestId;
    } else {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.BUCKET_NAME_INVALID,
          "null");
    }

    // バケット命名 バリデーションチェック
    // 参考： https://docs.aws.amazon.com/ja_jp/AmazonS3/latest/dev/BucketRestrictions.html
    String patternYes = "^[0-9a-z\\.\\-]+$";
    String patternNo = "(^[^0-9a-z])|([^0-9a-z]$)";
    if (CommonUtils.isNullOrEmpty(rtnBucketName)
        || (rtnBucketName.length() < 3 || rtnBucketName.length() > 63)
        || !CommonUtils.regxMatch(rtnBucketName, patternYes)
        || CommonUtils.regxMatch(rtnBucketName, patternNo)) {

      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.BUCKET_NAME_INVALID,
          rtnBucketName);
    }

    logger.info("getBucketName end. バケット名={}", rtnBucketName);
    return rtnBucketName;
  }


  @POST
  @Path("/upload")
  @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public Response upload(FormDataMultiPart multiPart) {
    MDC.put("method", "UPLOAD");
    logger.info("api start.");
    BinaryStoreServiceUploadRequest rqst = null;
    BinaryStoreServiceUploadResponse resp = new BinaryStoreServiceUploadResponse();
    InputStream is = null;

    // TODO 入力チェックをしっかりしないとis=nullになりそう
    try {
      init();
      // json パラメータ受取
      if (multiPart.getFields().containsKey("json")) {

        BodyPartEntity bodyPartEntity = (BodyPartEntity) multiPart.getField("json").getEntity();
        ObjectMapper mapper = new ObjectMapper();
        rqst = mapper.readValue(bodyPartEntity.getInputStream(),
            BinaryStoreServiceUploadRequest.class);
        logger.debug("Get JSON Parameter={}", rqst);
      }

      // バケット名設定
      if (null == rqst) {
        rqst = new BinaryStoreServiceUploadRequest();
      }
      rqst.setBucketName(getBucketName(rqst.getRequestId(), rqst.getBucketName()));
      logger.debug("Request Parameter={}", rqst);

      // file バイナリデータ受取
      List<BodyPart> bodyPartList = multiPart.getBodyParts();
      for (BodyPart bodyPart : bodyPartList) {
        BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyPart.getEntity();
        String name = bodyPart.getContentDisposition().getParameters().get("name");

        if ("file".equalsIgnoreCase(name)) {
          // MultiPartのみ文字コード変換が必要
          String filename = bodyPart.getContentDisposition().getFileName();
          filename = new String(filename.getBytes("iso-8859-1"), UTF_8);

          rqst.setFileName(filename);
          rqst.setContentType(bodyPart.getMediaType().toString());
          is = bodyPartEntity.getInputStream();
        }
      }

      // ストア 転送処理
      long start = System.currentTimeMillis();
      logger.info("Start upload process.");
      BinaryInfo outBinary = manager.upload(storage, rqst.transBinaryInfo(), is);
      long end = System.currentTimeMillis();
      logger.info("End upload process. [{}]ms", (end - start));
      List<BinaryInfo> outBinaries = new ArrayList<BinaryInfo>();
      outBinaries.add(outBinary);
      resp.setBinaryInfos(outBinaries);
    } catch (StoreException se) {
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    } catch (Exception e) {
      StoreException se = new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          ErrorClassification.RUNTIME_ERROR, e, e.getMessage());
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    }

    logger.info("api end. response={}", resp);
    return Response.ok(resp).build();
  }

  @POST
  @Path("/get")
  @Produces({MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON})
  public Response get(BinaryStoreServiceGetRequest rqst) {
    MDC.put("method", "GET");
    logger.info("api start. request={}", rqst);
    BinaryStoreServiceGetResponse resp = new BinaryStoreServiceGetResponse();

    try {
      init();
      // バケット名設定
      rqst.setBucketName(getBucketName(rqst.getRequestId(), rqst.getBucketName()));
      long start = System.currentTimeMillis();
      logger.info("Start get process.");
      BinaryInfo outBinary = manager.get(storage, rqst.transBinaryInfo());
      long end = System.currentTimeMillis();
      logger.info("End get process. [{}]ms", (end - start));
      List<BinaryInfo> outBinaries = new ArrayList<BinaryInfo>();
      outBinaries.add(outBinary);
      resp.setBinaryInfos(outBinaries);
    } catch (StoreException se) {
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    } catch (Exception e) {
      StoreException se = new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          ErrorClassification.RUNTIME_ERROR, e, e.getMessage());
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    }

    logger.info("api end. response={}", resp);
    return Response.ok(resp).build();
  }

  @POST
  @Path("/list")
  @Produces({MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_JSON})
  public Response list(BinaryStoreServiceListRequest rqst) {
    MDC.put("method", "LIST");
    logger.info("api start. request={}", rqst);
    BinaryStoreServiceListResponse resp = new BinaryStoreServiceListResponse();


    try {
      init();
      // バケット名設定
      rqst.setBucketName(getBucketName(rqst.getRequestId(), rqst.getBucketName()));

      long start = System.currentTimeMillis();
      logger.info("Start list process.");
      List<BinaryInfo> outBinaries = manager.list(storage, rqst.transBinaryInfo());
      long end = System.currentTimeMillis();
      logger.info("End list process. [{}]ms", (end - start));
      resp.setBinaryInfos(outBinaries);

    } catch (StoreException se) {
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    } catch (Exception e) {
      StoreException se = new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          ErrorClassification.RUNTIME_ERROR, e, e.getMessage());
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    }

    logger.info("api end. response={}", resp);
    return Response.ok(resp).build();
  }


  @POST
  @Path("/delete")
  @Produces({MediaType.APPLICATION_JSON})
  public Response delete(BinaryStoreServiceDeleteRequest rqst) {
    MDC.put("method", "DELETE");
    logger.info("api start. request={}", rqst);
    BinaryStoreServiceDeleteResponse resp = new BinaryStoreServiceDeleteResponse();

    try {
      init();
      // バケット名設定
      rqst.setBucketName(getBucketName(rqst.getRequestId(), rqst.getBucketName()));
      long start = System.currentTimeMillis();
      logger.info("Start delete process.");
      manager.delete(storage, rqst.transBinaryInfo());
      long end = System.currentTimeMillis();
      logger.info("End delete process. [{}]ms", (end - start));

    } catch (StoreException se) {
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    } catch (Exception e) {
      StoreException se = new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          ErrorClassification.RUNTIME_ERROR, e, e.getMessage());
      logger.error("api has Exception.", se);
      return Response.status(se.getHttpStatus()).entity(se.getMessage()).build();
    }

    logger.info("api end. response={}", resp);
    return Response.ok().build();

  }
}
