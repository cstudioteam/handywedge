/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.handywedge.binarystore.store.common.BinaryInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinaryStoreServiceGetRequest extends BinaryStoreServiceRequest {

  private String fileName;

  @Override
  public BinaryInfo transBinaryInfo() {
    BinaryInfo bi = new BinaryInfo();
    bi.setBucketName(getBucketName());
    bi.setFileName(fileName);
    return bi;
  }
}
