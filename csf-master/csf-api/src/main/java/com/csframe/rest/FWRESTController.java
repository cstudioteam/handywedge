/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.rest;

import javax.enterprise.context.RequestScoped;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWException;
import com.csframe.user.auth.FWLoginManager;

// TODO jackson依存のコードになっているのをどうするか
// インターフェースのdefault実装ではなく抽象クラスとする
/**
 * RESTインターフェースで公開するための抽象クラスです。<br>
 * HTTPのGET、POST、PUT、DELETEメソッドをサポートします。<br>
 * それぞれdoXXXメソッドをオーバーライドし処理を実装して下さい。<br>
 * 
 * GET以外は引数にJSONをアンマーシャルしたDTOクラスが渡されます。<br>
 * メソッドにFWRESTRequestClassアノテーションを修飾し、JSONをマッピングするFWRESTRequestを継承したクラスを指定します。<br>
 * 
 * GETメソッドについてはURLのパスパラメータが渡されます。<br>
 * 
 * 各メソッドとも、戻り値はFWRESTResponseを継承したFWRESTResponseを継承したクラスを返却するとJSONにマーシャルしてクライアントに応答します。<br>
 * RESTインターフェースを使用する場合はweb.xmlにフィルターの設定が必要です。<br>
 * 
 * <pre>
 * {@code 
 * <filter-mapping>
 *   <filter-name>csf_rest_filter</filter-name>
 *   <url-pattern>/csf/rest/*</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 *
 * RESTインターフェースではAPIトークン認証が行われます。<br>
 * クライアントは、予めFWLoginManagerでトークンを発行し、リクエスト時にHTTPヘッダー(Authorization)でトークンを送信する必要があります。<br>
 * {@code Authorization: Bearer APIトークン}
 * 
 * @see FWLoginManager
 */
@RequestScoped
public abstract class FWRESTController {

  /**
   * HTTPのPOSTメソッドのリクエストで実行されます。
   * 
   * @param request JSONをアンマーシャルしたDTO
   * @return クライアントに応答するJSONにマーシャルするためのDTO
   */
  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doPost(FWRESTRequest request) {
    return createError();
  }

  /**
   * HTTPのGETメソッドのリクエストで実行されます。
   * 
   * @param param URLのパスパラメータ。
   * @return クライアントに応答するJSONにマーシャルするためのDTO
   */
  public FWRESTResponse doGet(String param) {
    return createError();
  }

  /**
   * HTTPのPUTメソッドのリクエストで実行されます。
   * 
   * @param request JSONをアンマーシャルしたDTO
   * @return クライアントに応答するJSONにマーシャルするためのDTO
   */
  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doPut(FWRESTRequest request) {
    return createError();
  }

  /**
   * HTTPのDELETEメソッドのリクエストで実行されます。
   * 
   * @param request JSONをアンマーシャルしたDTO
   * @return クライアントに応答するJSONにマーシャルするためのDTO
   */
  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doDelete(FWRESTRequest request) {
    return createError();
  }

  private FWRESTResponse createError() {
    FWException e = new FWException(String.valueOf(FWConstantCode.FW_REST_UNSUPPORTED));
    FWRESTResponse res = new FWRESTErrorResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_UNSUPPORTED);
    res.setReturn_msg(e.getMessage());
    return res;
  }
}
