/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.handywedge.binarystore.store.common.BinaryInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * クライアントに応答するためのJSONをマーシャルするためのクラスです。<br>
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BinaryStoreServiceRequest {

  private String requestId; // リクエスト元識別ユニークID
  private String bucketName;

  /**
   * ロギングのためレスポンス内容を文字列で表現するtoStringを実装します。
   */
  @Override
  public abstract String toString();

  public abstract BinaryInfo transBinaryInfo();
}
