/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * クライアントに応答するためのJSONをマーシャルするためのクラスです。<br>
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BinaryStoreServiceResponse {

  private int return_cd;
  private String return_msg;

  /**
   * ロギングのためレスポンス内容を文字列で表現するtoStringを実装します。
   */
  @Override
  public abstract String toString();

}
