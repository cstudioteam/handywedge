/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.aws;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.handywedge.binarystore.store.IStoreManager;
import com.handywedge.binarystore.store.common.BinaryInfo;
import com.handywedge.binarystore.store.common.ErrorClassification;
import com.handywedge.binarystore.store.common.StorageInfo;
import com.handywedge.binarystore.store.common.StoreException;
import com.handywedge.binarystore.util.CommonUtils;
import com.handywedge.binarystore.util.PropertiesUtil;

public class BinaryStoreManagerImpl implements IStoreManager {

  private static Logger logger = LoggerFactory.getLogger(BinaryStoreManagerImpl.class);

  // 転送のデフォルトパートサイズ
  private static final long BINARY_PART_SIZE_5MB = 5 * 1024 * 1024;

  // Defalut Region
  private final Regions DEFAULT_REGION = Regions.AP_NORTHEAST_1;

  /**
   * クライアント生成
   *
   * @param bucketName
   * @return s3client
   * @throws StoreException
   * @throws Exception
   */
  private AmazonS3 getS3Client(String bucketName) throws StoreException {
    logger.debug("get S3 Client start.");
    // 認証情報
    AWSCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();

    // クライアント設定
    ClientConfiguration clientConfig = new ClientConfiguration()

        // .withProtocol(Protocol.HTTPS) // Proxy設定
        // .withProxyHost("proxyHost")
        // .withProxyPort(80)
        // .withProxyUsername("proxyUsername")
        // .withProxyPassword("proxyPassword")

        .withConnectionTimeout(10000);

    // クライアント生成
    AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(provider)
        .withClientConfiguration(clientConfig).withRegion(DEFAULT_REGION)
        .withForceGlobalBucketAccessEnabled(true).build();

    logger.debug("Region={}", s3client.getRegion());

    try {
      // バケット作成
      if (!CommonUtils.isNullOrEmpty(bucketName) && !(s3client.doesBucketExistV2(bucketName))) {
        s3client.createBucket(new CreateBucketRequest(bucketName, DEFAULT_REGION.getName()));
      }
      // Get location.
      String bucketLocation = s3client.getBucketLocation(new GetBucketLocationRequest(bucketName));
      logger.info("bucket location={}", bucketLocation);
    } catch (AmazonClientException ace) {
      throw new StoreException(HttpStatus.SC_CONFLICT, ErrorClassification.BS0003, ace, "バケット");
    }

    logger.info("get S3 Client end.");
    return s3client;
  }

  @Override
  public BinaryInfo upload(StorageInfo storage, BinaryInfo binary, InputStream inStream)
      throws StoreException {
    logger.debug("ストレージ情報={}", storage);
    logger.debug("バイナリ情報={}", binary);

    AmazonS3 s3client = getS3Client(binary.getBucketName());

    ObjectMetadata oMetadata = new ObjectMetadata();
    oMetadata.setContentType(binary.getContentType());

    // マルチパート初期化処理
    InitiateMultipartUploadRequest initRequest =
        new InitiateMultipartUploadRequest(binary.getBucketName(), binary.getFileName(), oMetadata);
    InitiateMultipartUploadResult initResponse = s3client.initiateMultipartUpload(initRequest);

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      long written = IOUtils.copyLarge(inStream, baos, 0, BINARY_PART_SIZE_5MB);

      byte[] data = baos.toByteArray();
      InputStream awsInputStream = new ByteArrayInputStream(data);

      if (written < BINARY_PART_SIZE_5MB) {
        oMetadata.setContentLength(written);
        s3client.putObject(binary.getBucketName(), binary.getFileName(), awsInputStream, oMetadata);
      } else {
        int firstByte = 0;
        int partNumber = 1;
        boolean isFirstChunck = true;
        boolean overSizeLimit = false;
        List<PartETag> partETags = new ArrayList<PartETag>();
        InputStream firstChunck = new ByteArrayInputStream(data);
        PushbackInputStream chunckableInputStream = new PushbackInputStream(inStream, 1);

        long maxSize = BINARY_PART_SIZE_5MB * 1024;
        String maxSizeStr = "5GB";
        String prefix = MDC.get("requestId");
        while (-1 != (firstByte = chunckableInputStream.read())) {
          long partSize = 0;
          chunckableInputStream.unread(firstByte);
          File tempFile =
              File.createTempFile(prefix.concat("-part").concat(String.valueOf(partNumber)), null);
          tempFile.deleteOnExit();
          try (OutputStream os =
              new BufferedOutputStream(new FileOutputStream(tempFile.getAbsolutePath()))) {

            if (isFirstChunck) {
              partSize = IOUtils.copyLarge(firstChunck, os, 0, (BINARY_PART_SIZE_5MB));
              isFirstChunck = false;
            } else {
              partSize = IOUtils.copyLarge(chunckableInputStream, os, 0, (BINARY_PART_SIZE_5MB));
            }
            written += partSize;

            if (written > maxSize) { // 5GB以上
              overSizeLimit = true;
              logger.warn("OVERSIZED FILE ({}). STARTING ABORT", written);
              break;
            }
          }

          FileInputStream chunk = new FileInputStream(tempFile);
          Boolean isLastPart = -1 == (firstByte = chunckableInputStream.read());
          if (!isLastPart) {
            chunckableInputStream.unread(firstByte);
          }

          oMetadata.setContentLength(partSize);

          UploadPartRequest uploadRequest =
              new UploadPartRequest().withBucketName(binary.getBucketName())
                  .withKey(binary.getFileName()).withUploadId(initResponse.getUploadId())
                  .withObjectMetadata(oMetadata).withInputStream(chunk).withPartSize(partSize)
                  .withPartNumber(partNumber).withLastPart(isLastPart);
          UploadPartResult result = s3client.uploadPart(uploadRequest);
          partETags.add(result.getPartETag());
          partNumber++;
        }

        if (overSizeLimit) {
          ListMultipartUploadsRequest listRequest =
              new ListMultipartUploadsRequest(binary.getBucketName());
          MultipartUploadListing listResult = s3client.listMultipartUploads(listRequest);

          int timesIterated = 20;
          // loop and abort all the multipart uploads
          while (listResult.getMultipartUploads().size() != 0 && timesIterated > 0) {
            s3client.abortMultipartUpload(new AbortMultipartUploadRequest(binary.getBucketName(),
                binary.getFileName(), initResponse.getUploadId()));
            Thread.sleep(1000);
            timesIterated--;
            listResult = s3client.listMultipartUploads(listRequest);
            logger.debug("Files that haven't been aborted are: {}",
                listResult.getMultipartUploads().listIterator().toString());
          }
          if (timesIterated == 0) {
            logger.warn("Files parts that couldn't be aborted in 20 seconds are:");
            Iterator<MultipartUpload> multipartUploadIterator =
                listResult.getMultipartUploads().iterator();
            while (multipartUploadIterator.hasNext()) {
              logger.warn(multipartUploadIterator.next().getKey());
            }
          }
          throw new StoreException(HttpStatus.SC_REQUEST_TOO_LONG,
              ErrorClassification.UPLOAD_TOO_LARGE, maxSizeStr);
        } else {
          CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
              binary.getBucketName(), binary.getFileName(), initResponse.getUploadId(), partETags);

          CompleteMultipartUploadResult comMPUResult =
              s3client.completeMultipartUpload(compRequest);
          logger.debug("CompleteMultipartUploadResult={}", comMPUResult);
        }
      }
    } catch (AmazonServiceException ase) {
      s3client.abortMultipartUpload(new AbortMultipartUploadRequest(binary.getBucketName(),
          binary.getFileName(), initResponse.getUploadId()));
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.UPLOAD_FAIL, ase,
          binary.toString());
    } catch (AmazonClientException ace) {
      s3client.abortMultipartUpload(new AbortMultipartUploadRequest(binary.getBucketName(),
          binary.getFileName(), initResponse.getUploadId()));
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.UPLOAD_FAIL, ace,
          binary.toString());
    } catch (IOException ioe) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.UPLOAD_FAIL,
          ioe, binary.toString());
    } catch (InterruptedException itre) {
      throw new StoreException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorClassification.UPLOAD_FAIL,
          itre, binary.toString());
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (Exception e) {
        }
      }
    }

    return getBinaryInfo(s3client, binary.getBucketName(), binary.getFileName());
  }


  @Override
  public void delete(StorageInfo storage, BinaryInfo binary) throws StoreException {

    logger.debug("ストレージ情報={}", storage);
    logger.debug("バイナリ情報={}", binary);

    AmazonS3 s3client = getS3Client(binary.getBucketName());

    if (!s3client.doesObjectExist(binary.getBucketName(), binary.getFileName())) {
      logger.info("The Binary has not exsit.bucket={}, binary={}", binary.getBucketName(),
          binary.getFileName());
      return;
    }

    try {
      s3client.deleteObject(new DeleteObjectRequest(binary.getBucketName(), binary.getFileName()));
    } catch (AmazonServiceException ase) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.DELETE_FAIL, ase,
          binary.getFileName());
    } catch (AmazonClientException ace) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.DELETE_FAIL, ace,
          binary.getFileName());
    }

    return;
  }


  private BinaryInfo getBinaryInfo(AmazonS3 s3client, String bucketName, String fileName)
      throws StoreException {
    logger.info("getBinaryInfo: start.");

    logger.debug("バケット名={}", bucketName);
    logger.debug("バイナリファイル名={}", fileName);

    BinaryInfo binary = new BinaryInfo(bucketName);
    try {
      S3Object s3binary = s3client.getObject(new GetObjectRequest(bucketName, fileName));

      if (null != s3binary) {
        binary.setFileName(fileName);
        binary.setContentType(s3binary.getObjectMetadata().getContentType());
        binary.setSize(s3binary.getObjectMetadata().getContentLength());
        binary.setUrl(s3client.getUrl(binary.getBucketName(), binary.getFileName()).toString());

        logger.debug("Generating pre-signed URL.");
        URL PresignedUrl = getPresignedUrl(s3client, binary.getBucketName(), binary.getFileName());
        binary.setPresignedUrl(PresignedUrl.toString());
        logger.debug("Pre-Signed URL = " + PresignedUrl.toString());
      }

    } catch (AmazonServiceException ase) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, ase,
          binary.getFileName());
    } catch (AmazonClientException ace) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, ace,
          binary.getFileName());
    }

    logger.info("getBinaryInfo: end.");
    return binary;
  }

  @Override
  public BinaryInfo get(StorageInfo storage, BinaryInfo binary) throws StoreException {

    logger.debug("ストレージ情報={}", storage);
    logger.debug("バイナリ情報={}", binary);

    AmazonS3 s3client = getS3Client(binary.getBucketName());

    try {
      logger.debug("Get an binary");
      if (!s3client.doesObjectExist(binary.getBucketName(), binary.getFileName())) {
        logger.info("The Binary has not exsit.bucket={}, binary={}", binary.getBucketName(),
            binary.getFileName());
        return null;
      }

      S3Object s3binary =
          s3client.getObject(new GetObjectRequest(binary.getBucketName(), binary.getFileName()));
      binary.setContentType(s3binary.getObjectMetadata().getContentType());
      binary.setSize(s3binary.getObjectMetadata().getContentLength());
      binary.setUrl(s3client.getUrl(binary.getBucketName(), binary.getFileName()).toString());

      logger.debug("Generating pre-signed URL.");
      URL PresignedUrl = getPresignedUrl(s3client, binary.getBucketName(), binary.getFileName());
      binary.setPresignedUrl(PresignedUrl.toString());
      logger.debug("Pre-Signed URL = " + PresignedUrl.toString());

    } catch (AmazonServiceException ase) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, ase,
          binary.getFileName());
    } catch (AmazonClientException ace) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.GET_FAIL, ace,
          binary.getFileName());
    }

    return binary;
  }

  @Override
  public List<BinaryInfo> list(StorageInfo storage, BinaryInfo binary) throws StoreException {
    logger.debug("ストレージ情報={}", storage);
    logger.debug("バイナリ情報={}", binary);

    List<BinaryInfo> objInfoList = new ArrayList<BinaryInfo>();

    AmazonS3 s3client = getS3Client(binary.getBucketName());

    try {
      logger.debug("Listing binaries");
      final ListObjectsV2Request req =
          new ListObjectsV2Request().withBucketName(binary.getBucketName()).withMaxKeys(2);
      ListObjectsV2Result result;
      do {
        result = s3client.listObjectsV2(req);
        for (S3ObjectSummary binarySummary : result.getObjectSummaries()) {
          logger.debug(" - {}(size={})", binarySummary.getKey(), binarySummary.getSize());
          if (binarySummary.getSize() != 0) {
            BinaryInfo objInfo = new BinaryInfo(binary.getBucketName());
            objInfo.setFileName(binarySummary.getKey());
            objInfo.setSize(binarySummary.getSize());
            S3Object s3Object = s3client
                .getObject(new GetObjectRequest(binary.getBucketName(), binarySummary.getKey()));
            objInfo.setContentType(s3Object.getObjectMetadata().getContentType());
            objInfo
                .setUrl(s3client.getUrl(binary.getBucketName(), binarySummary.getKey()).toString());

            logger.debug("Generating pre-signed URL.");
            URL PresignedUrl =
                getPresignedUrl(s3client, binary.getBucketName(), binarySummary.getKey());
            objInfo.setPresignedUrl(PresignedUrl.toString());
            logger.debug("Pre-Signed URL = " + PresignedUrl.toString());

            objInfoList.add(objInfo);
          }
        }
        logger.debug("Next Continuation Token : " + result.getNextContinuationToken());
        req.setContinuationToken(result.getNextContinuationToken());
      } while (result.isTruncated() == true);

    } catch (AmazonServiceException ase) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.LIST_FAIL, ase,
          binary.getFileName());
    } catch (AmazonClientException ace) {
      throw new StoreException(HttpStatus.SC_BAD_REQUEST, ErrorClassification.LIST_FAIL, ace,
          binary.getFileName());
    }

    logger.info("オブジェクト一覧 件数={}", objInfoList.size());
    return objInfoList;
  }

  /*
   * 署名づきURL取得
   */
  private URL getPresignedUrl(AmazonS3 s3client, String buckrtName, String key) {
    logger.info("getPresignedUrl start.");

    java.util.Date expiration = new java.util.Date();
    long milliSeconds = expiration.getTime();
    milliSeconds += Long.parseLong(PropertiesUtil.get("aws.presignedurl.expiration"));
    expiration.setTime(milliSeconds);

    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(buckrtName, key);
    generatePresignedUrlRequest.setMethod(HttpMethod.GET);
    generatePresignedUrlRequest.setExpiration(expiration);

    URL PresignedUrl = s3client.generatePresignedUrl(generatePresignedUrlRequest);

    logger.info("getPresignedUrl: end. url={}", PresignedUrl);
    return PresignedUrl;
  }
}
