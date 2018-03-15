/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * バイナリ情報
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinaryInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param bucketName バケット名
   */
  public BinaryInfo(String bucketName) {
    this.bucketName = bucketName;
  }

  /**
   * コンストラクタ
   */
  public BinaryInfo() {}

  /**
   * バケット名。
   */
  private String bucketName;

  /**
   * キー。
   */
  private String fileName;

  /**
   * バイナリデータ（サイズ）
   */
  private long size;

  /**
   * バイナリデータ（base64 contents）
   */
  private String contents;

  /**
   * バイナリデータ（Content-Type）
   */
  private String contentType;

  /**
   * バイナリデータ（URL）
   */
  private String url;

  /**
   * バイナリデータ（PreSigned URL）
   */
  private String presignedUrl;
}
