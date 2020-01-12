/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.azure;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.binarystore.store.IStoreManager;
import com.handywedge.binarystore.store.common.BinaryInfo;
import com.handywedge.binarystore.store.common.ErrorClassification;
import com.handywedge.binarystore.store.common.StorageInfo;
import com.handywedge.binarystore.store.common.StoreException;
import com.handywedge.binarystore.util.PropertiesUtil;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.BlockEntry;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;

@RequestScoped
public class BinaryStoreManagerImpl implements IStoreManager {

  private static Logger logger = LoggerFactory.getLogger(BinaryStoreManagerImpl.class);

  // 転送のデフォルトパートサイズ
  private static final long BINARY_PART_SIZE_5MB = 5 * 1024 * 1024;

  /**
   * ABSクライアント生成
   *
   * @param bucketName バケット名
   * @param isCreated バケット作成フラグ
   * @return
   * @throws StoreException
   */
  private CloudBlobClient getABSClient(String bucketName, boolean isCreated) throws StoreException {
    logger.debug("get ABS Client start.");

    CloudStorageAccount account = null;
    try {
      String accountName = PropertiesUtil.get("abs.credentials.accountname");
      String accountKey = PropertiesUtil.get("abs.credentials.accountkey");
      StorageCredentials creds = new StorageCredentialsAccountAndKey(accountName, accountKey);
      account = new CloudStorageAccount(creds, true);
    } catch (URISyntaxException ue) {
      // TODO BS0003の置換文字列がない
      throw new StoreException(HttpStatus.SC_UNAUTHORIZED, ErrorClassification.BS0003, ue);
    }

    CloudBlobClient bClient = account.createCloudBlobClient();

    try {
      CloudBlobContainer container = bClient.getContainerReference(bucketName);
      if (!container.exists()) {
        if (isCreated == false) {
          throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.NOT_FOUND, bucketName);
        }
        container.createIfNotExists();
      }

      // アップロード権限付与
      BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
      container.uploadPermissions(containerPermissions);

    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.BS0003, ue, "コンテナー");
    } catch (com.microsoft.azure.storage.StorageException se) {
      throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.BS0003, se, "コンテナー");
    }

    logger.info("get ABS Client end.");
    return bClient;
  }


  @SuppressWarnings("unused")
  @Override
  public BinaryInfo upload(StorageInfo storage, BinaryInfo binary, InputStream inStream)
      throws StoreException {
    logger.info("ABS update method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    CloudBlobClient bClient = getABSClient(binary.getBucketName(), true);

    BinaryInfo rtnBinary = new BinaryInfo();
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      long written = IOUtils.copyLarge(inStream, baos, 0, BINARY_PART_SIZE_5MB);

      byte[] data = baos.toByteArray();
      InputStream awsInputStream = new ByteArrayInputStream(data);
      CloudBlockBlob blob = bClient.getContainerReference(binary.getBucketName())
          .getBlockBlobReference(binary.getFileName());

      if (written < BINARY_PART_SIZE_5MB) {
        BlobOutputStream blobOutputStream = blob.openOutputStream();

        int next = awsInputStream.read();
        while (next != -1) {
          blobOutputStream.write(next);
          next = awsInputStream.read();
        }
        blobOutputStream.close();

        blob.downloadAttributes();
        BlobProperties properties = blob.getProperties();
        properties.setContentType(binary.getContentType());
        blob.uploadProperties();

      } else {
        int firstByte = 0;
        int partNumber = 1;
        Boolean isFirstChunck = true;
        Boolean overSizeLimit = false;
        List<BlockEntry> blockList = new ArrayList<BlockEntry>();
        InputStream firstChunck = new ByteArrayInputStream(data);
        PushbackInputStream chunckableInputStream = new PushbackInputStream(inStream, 1);

        while (-1 != (firstByte = chunckableInputStream.read())) {
          long partSize = 0;
          chunckableInputStream.unread(firstByte);
          File tempFile = File.createTempFile(
              UUID.randomUUID().toString().concat("-part").concat(String.valueOf(partNumber)),
              "tmp");
          tempFile.deleteOnExit();
          OutputStream os = null;
          try {
            os = new BufferedOutputStream(new FileOutputStream(tempFile.getAbsolutePath()));

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

          FileInputStream chunk = new FileInputStream(tempFile);
          Boolean isLastPart = -1 == (firstByte = chunckableInputStream.read());
          if (!isLastPart) {
            chunckableInputStream.unread(firstByte);
          }

          String blockId = Base64.encodeBase64String(
              String.format("BlockId%07d", partNumber).getBytes(StandardCharsets.UTF_8));
          BlockEntry block = new BlockEntry(blockId);
          blockList.add(block);
          blob.uploadBlock(blockId, chunk, partSize);

          partNumber++;
          chunk.close();
        }

        blob.commitBlockList(blockList);

        blob.downloadAttributes();
        BlobProperties properties = blob.getProperties();
        properties.setContentType(binary.getContentType());
        blob.uploadProperties();

        logger.debug("commitBlockList.");
      }

      if (blob.exists()) {
        rtnBinary = createReturnBinaryInfo(blob);
      } else {
        rtnBinary = binary;
      }

    } catch (StorageException se) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.UPLOAD_FAIL, se,
          binary.getFileName());
    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.UPLOAD_FAIL, ue,
          binary.getFileName());
    } catch (FileNotFoundException fe) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.UPLOAD_FAIL, fe,
          binary.getFileName());
    } catch (IOException ioe) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.UPLOAD_FAIL, ioe,
          binary.getFileName());
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (Exception e) {
        }
      }
    }

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("ABS update method: end.");
    return rtnBinary;
  }


  private BinaryInfo createReturnBinaryInfo(CloudBlockBlob blob) throws StoreException {
    BinaryInfo binary = new BinaryInfo();
    try {
      if (blob.exists()) {
        long milliSeconds = Long.parseLong(PropertiesUtil.get("abs.presignedurl.expiration"));

        binary.setBucketName(blob.getContainer().getName());
        binary.setFileName(blob.getName());
        binary.setContentType(blob.getProperties().getContentType());
        binary.setSize(blob.getProperties().getLength());
        binary.setUrl(blob.getUri().toString());

        // Policy
        SharedAccessBlobPolicy itemPolicy = new SharedAccessBlobPolicy();

        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        itemPolicy.setSharedAccessStartTime(calendar.getTime());

        calendar.add(Calendar.SECOND, (int) (milliSeconds / 1000));
        itemPolicy.setSharedAccessExpiryTime(calendar.getTime());

        itemPolicy.setPermissions(
            EnumSet.of(SharedAccessBlobPermissions.LIST, SharedAccessBlobPermissions.READ));

        String sasToken = blob.generateSharedAccessSignature(itemPolicy, null);
        Thread.sleep(1500);

        String sasUri = String.format("%s?%s", blob.getUri().toString(), sasToken);
        binary.setPresignedUrl(sasUri);

        logger.debug(" 署名なしURL: {}", binary.getUrl());
        logger.debug(" 署名付きURL: {}", binary.getPresignedUrl());

      } else {
        binary = null;
      }
    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.CREATE_BINARY_FAIL, ue,
          blob.getName());
    } catch (NumberFormatException ne) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.CREATE_BINARY_FAIL, ne,
          blob.getName());
    } catch (InvalidKeyException ie) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.CREATE_BINARY_FAIL, ie,
          blob.getName());
    } catch (InterruptedException ite) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.CREATE_BINARY_FAIL, ite,
          blob.getName());
    } catch (StorageException se) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.CREATE_BINARY_FAIL, se,
          blob.getName());
    }

    return binary;
  }

  @SuppressWarnings("unused")
  @Override
  public void delete(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("ABS delete method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    CloudBlobClient bClient = getABSClient(binary.getBucketName(), false);

    BinaryInfo rtnBinary = null;
    try {
      // 削除対象のバイナリ情報取得
      CloudBlockBlob blob = bClient.getContainerReference(binary.getBucketName())
          .getBlockBlobReference(binary.getFileName());
      if (!blob.exists()) {
        logger.info("The Binary has not exsit.content={}, binary={}", binary.getBucketName(),
            binary.getFileName());
        return;
      }

      boolean deleted = blob.deleteIfExists();
      if (!deleted) {
        throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.DELETE_FAIL, binary.getFileName());
      }

    } catch (StorageException se) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.DELETE_FAIL, se,
          binary.getFileName());
    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.DELETE_FAIL, ue,
          binary.getFileName());
    }

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("ABS delete method: end.");
    return;
  }

  @SuppressWarnings("unused")
  @Override
  public BinaryInfo get(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("ABS get method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    CloudBlobClient bClient = getABSClient(binary.getBucketName(), false);

    BinaryInfo rtnBinary = new BinaryInfo();
    try {
      logger.info("Get an binary");

      CloudBlockBlob blob = bClient.getContainerReference(binary.getBucketName())
          .getBlockBlobReference(binary.getFileName());
      if (!blob.exists()) {
        throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.NOT_FOUND, binary.getFileName());
      }
      rtnBinary = createReturnBinaryInfo(blob);

    } catch (com.microsoft.azure.storage.StorageException se) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, se,
          binary.getFileName());
    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, ue,
          binary.getFileName());
    }

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", binary.getFileName(), (endSingle - startSingle));

    logger.info("ABS get method: end.");
    return rtnBinary;
  }


  @SuppressWarnings("unused")
  @Override
  public List<BinaryInfo> list(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.info("ABS list method: start.");

    logger.debug("ストレージ情報：" + storage.toString());
    logger.debug("バイナリ情報：" + binary.toString());

    long startSingle = System.currentTimeMillis();

    List<BinaryInfo> objInfoList = new ArrayList<BinaryInfo>();
    CloudBlobClient bClient = getABSClient(binary.getBucketName(), false);

    try {
      logger.info("Listing binaries");

      Iterable<ListBlobItem> blobs =
          bClient.getContainerReference(binary.getBucketName()).listBlobs("", true);
      Iterator<ListBlobItem> blobIterator = blobs.iterator();
      while (blobIterator.hasNext()) {
        ListBlobItem blobItem = blobIterator.next();
        if (blobItem instanceof CloudBlockBlob) {
          CloudBlockBlob blob = (CloudBlockBlob) blobItem;
          objInfoList.add(createReturnBinaryInfo(blob));
        }
      }

    } catch (com.microsoft.azure.storage.StorageException se) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.LIST_FAIL, se,
          binary.getFileName());
    } catch (URISyntaxException ue) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.LIST_FAIL, ue,
          binary.getFileName());
    }

    logger.info("オブジェクト一覧 件数" + objInfoList.size());

    long endSingle = System.currentTimeMillis();
    logger.info("{} Geted : {} ms\n", objInfoList.size(), (endSingle - startSingle));

    logger.info("ABS list method: end.");
    return objInfoList;
  }
}
