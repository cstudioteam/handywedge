/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import lombok.Getter;

public enum ErrorClassification {

  // TODO 意味のわかる定数名を定義する
  GCS_CREDENTIALS_READ_FAIL("BS0001"), BUCKET_NAME_INVALID("BS0002"), BS0003("BS0003"), UPLOAD_FAIL(
      "BS0004"), DELETE_FAIL("BS0005"), GET_FAIL("BS0006"), LIST_FAIL("BS0007"), NOT_FOUND(
          "BS0008"), UPLOAD_TOO_LARGE("BS0009"), CREATE_BINARY_FAIL("BS5001"), OUT_OF_RESOURCE(
              "CM0002"), DISK_IO_ERROR("CM0003"), RUNTIME_ERROR("BS9999");


  @Getter
  private String errorCode;

  private ErrorClassification(String errorCode) {
    this.errorCode = errorCode;
  }

}
