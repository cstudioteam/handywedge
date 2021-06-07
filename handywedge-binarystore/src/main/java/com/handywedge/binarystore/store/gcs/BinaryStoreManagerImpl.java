/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.gcs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.RequestScoped;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Duration;

import com.google.api.gax.paging.Page;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.handywedge.binarystore.store.IStoreManager;
import com.handywedge.binarystore.store.common.BinaryInfo;
import com.handywedge.binarystore.store.common.ErrorClassification;
import com.handywedge.binarystore.store.common.StorageInfo;
import com.handywedge.binarystore.store.common.StoreException;
import com.handywedge.binarystore.util.CommonUtils;
import com.handywedge.binarystore.util.PropertiesUtil;

@RequestScoped
public class BinaryStoreManagerImpl implements IStoreManager {

  private static Logger logger = LoggerFactory.getLogger(BinaryStoreManagerImpl.class);

  // 転送のデフォルトパートサイズ
  private static final long BINARY_PART_SIZE_5MB = 5 * 1024 * 1024;

  private static final String DEFAULT_LOCATION = "asia-northeast1";

  private static final int DEFAULT_TIMEOUT = 60 * 1000;

  /**
   * GCSクライアント生成
   *
   * @param bucketName
   * @return
   * @throws StoreException
   * @throws Exception
   */
  private Storage getGCSClient(String bucketName, boolean isCreated) throws StoreException {
    logger.debug("get GCS Client start.");

    Storage gStorage = null;
    if (CommonUtils.isNullOrEmpty(PropertiesUtil.get("gcs.credentials.file.path"))) {
      gStorage = StorageOptions.getDefaultInstance().getService();
    } else {
      ServiceAccountCredentials credentials = null;
      try {
        credentials = ServiceAccountCredentials
            .fromStream(new FileInputStream(PropertiesUtil.get("gcs.credentials.file.path")));
      } catch (IOException e) {
        logger.error("認証キーファイル読み込みエラー。");
        throw new StoreException(HttpStatus.SC_UNAUTHORIZED, ErrorClassification.GCS_CREDENTIALS_READ_FAIL, e);
      }

      HttpTransportOptions transportOptions = StorageOptions.getDefaultHttpTransportOptions();
      transportOptions = transportOptions.toBuilder().setConnectTimeout(DEFAULT_TIMEOUT)
          .setReadTimeout(DEFAULT_TIMEOUT).build();

      gStorage = StorageOptions.newBuilder().setCredentials(credentials)
          .setTransportOptions(transportOptions).setRetrySettings(retrySettings()).build()
          .getService();
    }

    try {
      Bucket bucket = gStorage.get(bucketName);

      // バケット作成
      if (!CommonUtils.isNullOrEmpty(bucketName) && null == bucket) {
        if (isCreated == false) {
          throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.NOT_FOUND, bucketName);
        }

        BucketInfo bucketInfo = BucketInfo.newBuilder(bucketName)
            .setStorageClass(StorageClass.COLDLINE).setLocation(DEFAULT_LOCATION).build();
        bucket = gStorage.create(bucketInfo);
      }
      logger.info("bucket location = " + bucket.getLocation());
    } catch (StorageException gse) {
      throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.BS0003, gse, "バケット");
    }

    logger.info("get GCS Client end.");
    return gStorage;
  }


  private static RetrySettings retrySettings() {
    return RetrySettings.newBuilder().setMaxAttempts(10).setMaxRetryDelay(Duration.ofMillis(30000L))
        .setTotalTimeout(Duration.ofMillis(120000L)).setInitialRetryDelay(Duration.ofMillis(250L))
        .setRetryDelayMultiplier(1.0).setInitialRpcTimeout(Duration.ofMillis(120000L))
        .setRpcTimeoutMultiplier(1.0).setMaxRpcTimeout(Duration.ofMillis(120000L)).build();
  }

  @SuppressWarnings("unused")
  @Override
  public BinaryInfo upload(StorageInfo storage, BinaryInfo binary, InputStream inStream)
      throws StoreException {
    logger.info("GCS update method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    Storage gStorage = getGCSClient(binary.getBucketName(), true);

    File tempFile = null;
    logger.info("Uploading a new binary to GCS from a file\n");

    BinaryInfo rtnBinary = new BinaryInfo();
    BlobId blobId = BlobId.of(binary.getBucketName(), binary.getFileName());
    List<Acl> acls = new ArrayList<>();
    acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(binary.getContentType())
        .setStorageClass(StorageClass.COLDLINE).setAcl(acls).build();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    long written = -1L;
    try {
      written = IOUtils.copyLarge(inStream, baos, 0, BINARY_PART_SIZE_5MB);
    } catch (IOException e) {
      logger.error("IOUtils.copyLarge 処理エラー");
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.OUT_OF_RESOURCE, e);
    }

    byte[] data = baos.toByteArray();
    InputStream gcsInputStream = new ByteArrayInputStream(data);

    if (written < BINARY_PART_SIZE_5MB) {
      Blob blob = gStorage.create(blobInfo, data);
      rtnBinary = createReturnBinaryInfo(blob);
    } else {
      int firstByte = 0;
      int partNumber = 1;
      Boolean isFirstChunck = true;
      Boolean overSizeLimit = false;
      InputStream firstChunck = new ByteArrayInputStream(data);
      PushbackInputStream chunckableInputStream = new PushbackInputStream(inStream, 1);

      try {
        tempFile =
            File.createTempFile(UUID.randomUUID().toString().concat(binary.getFileName()), "tmp");
      } catch (IOException e) {
        logger.error("File.createTempFile 処理エラー。ファイル名={}",
            UUID.randomUUID().toString().concat(binary.getFileName()));
        throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.DISK_IO_ERROR, e,
            UUID.randomUUID().toString().concat(binary.getFileName()));
      }

      try {
        while (-1 != (firstByte = chunckableInputStream.read())) {
          long partSize = 0;
          chunckableInputStream.unread(firstByte);

          OutputStream os = null;
          try {
            os = new BufferedOutputStream(new FileOutputStream(tempFile.getAbsolutePath(), true));

            if (isFirstChunck == true) {
              partSize = IOUtils.copyLarge(firstChunck, os, 0, (BINARY_PART_SIZE_5MB));
              isFirstChunck = false;
            } else {
              partSize = IOUtils.copyLarge(chunckableInputStream, os, 0, (BINARY_PART_SIZE_5MB));
            }
            written += partSize;

            if (written > BINARY_PART_SIZE_5MB * 1024) { // 5GB以上
              overSizeLimit = true;
              logger.error("OVERSIZED FILE ({}). STARTING ABORT", written);
              break;
            }
          } finally {
            IOUtils.closeQuietly(os);
          }

          Boolean isLastPart = -1 == (firstByte = chunckableInputStream.read());
          if (!isLastPart) {
            chunckableInputStream.unread(firstByte);
          }
        }
      } catch (IOException e) {
        logger.error("ファイル書き込み処理エラー。ファイル名={}",
            UUID.randomUUID().toString().concat(binary.getFileName()));
        throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.DISK_IO_ERROR, e,
            UUID.randomUUID().toString().concat(binary.getFileName()));
      }

      try {
        WriteChannel writer = gStorage.writer(blobInfo);
        byte[] buffer = new byte[1024];
        InputStream input = new FileInputStream(tempFile);
        int limit;
        while ((limit = input.read(buffer)) >= 0) {
          try {
            writer.write(ByteBuffer.wrap(buffer, 0, limit));
          } catch (Exception ex) {
            logger.error("バッファ書き込み処理エラー。");
            throw ex;
          }
        }
      } catch (IOException e) {
        logger.error("ファイルUpload処理エラー。ファイル名={}", blobInfo.toString());
        throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.UPLOAD_FAIL, e,
            blobInfo.toString());
      }

      if (null != tempFile && tempFile.exists()) {
        tempFile.delete();
      }
    }

    Blob blob = gStorage.get(blobInfo.getBlobId());
    rtnBinary = createReturnBinaryInfo(blob);

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("GCS update method: end.");
    return binary;
  }

  private BinaryInfo createReturnBinaryInfo(Blob blob) throws StoreException {
    BinaryInfo binary = new BinaryInfo();
    if (blob.exists()) {
      long milliSeconds = Long.parseLong(PropertiesUtil.get("gcs.presignedurl.expiration"));

      binary.setBucketName(blob.getBucket().toString());
      binary.setFileName(blob.getName());
      binary.setContentType(blob.getContentType());
      binary.setSize(blob.getSize());
      binary.setUrl(blob.getMediaLink());

      ServiceAccountCredentials credentials = null;
      try {
        credentials = ServiceAccountCredentials
            .fromStream(new FileInputStream(PropertiesUtil.get("gcs.credentials.file.path")));
      } catch (IOException e) {
        logger.error("認証キーファイル読み込みエラー。");
        throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.GCS_CREDENTIALS_READ_FAIL, e);
      }
      binary
          .setPresignedUrl(blob
              .signUrl(milliSeconds, TimeUnit.MILLISECONDS,
                  SignUrlOption.httpMethod(HttpMethod.GET), SignUrlOption.signWith(credentials))
              .toString());

      logger.debug(" 署名なしURL: {}", binary.getUrl());
      logger.debug(" 署名付きURL: {}", binary.getPresignedUrl());

    } else {
      binary = null;
    }
    return binary;
  }

  @SuppressWarnings("unused")
  @Override
  public void delete(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("GCS delete method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    Storage gStorage = getGCSClient(binary.getBucketName(), false);

    BinaryInfo rtnBinary = null;
    try {
      // 削除対象のバイナリ情報取得
      Blob blob = gStorage.get(binary.getBucketName(), binary.getFileName());
      if (!blob.exists()) {
        logger.info("The Binary has not exsit.bucket={}, binary={}", binary.getBucketName(),
            binary.getFileName());
        return;
      }

      boolean deleted = gStorage.delete(blob.getBlobId());
      if (!deleted) {
        throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.DELETE_FAIL, binary.getFileName());
      }
    } catch (StorageException gse) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.DELETE_FAIL, gse,
          binary.getFileName());
    }
    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("GCS delete method: end.");
    return;
  }

  @SuppressWarnings("unused")
  @Override
  public BinaryInfo get(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("GCS get method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    Storage gStorage = getGCSClient(binary.getBucketName(), false);

    BinaryInfo rtnBinary = new BinaryInfo();
    try {
      logger.info("Get an binary");

      Blob blob = gStorage.get(binary.getBucketName(), binary.getFileName());

      if (null == blob) {
        throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.NOT_FOUND, binary.getFileName());
      }
      rtnBinary = createReturnBinaryInfo(blob);
    } catch (StorageException gse) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, gse,
          binary.getFileName());
    }
    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("GCS get method: end.");
    return rtnBinary;
  }


  @SuppressWarnings("unused")
  @Override
  public List<BinaryInfo> list(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("GCS list method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    List<BinaryInfo> objInfoList = new ArrayList<BinaryInfo>();
    Storage gStorage = getGCSClient(binary.getBucketName(), false);

    try {
      logger.info("Listing binaries");

      Page<Blob> blobs = gStorage.list(binary.getBucketName(), BlobListOption.pageSize(1000));
      Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
      while (blobIterator.hasNext()) {
        Blob blob = blobIterator.next();
        if (blob.getSize() != 0) {
          objInfoList.add(createReturnBinaryInfo(blob));
        }
      }
    } catch (StorageException gse) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.LIST_FAIL, gse,
          binary.getFileName());
    }
    logger.info("オブジェクト一覧 件数" + objInfoList.size());

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", objInfoList.size(), (endSingle - startSingle));

    logger.info("GCS list method: end.");
    return objInfoList;
  }

}
