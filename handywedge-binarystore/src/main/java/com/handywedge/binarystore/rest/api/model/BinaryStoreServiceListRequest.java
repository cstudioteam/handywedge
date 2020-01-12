/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
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
public class BinaryStoreServiceListRequest extends BinaryStoreServiceRequest {

  @Override
  public BinaryInfo transBinaryInfo() {
    BinaryInfo bi = new BinaryInfo();
    bi.setBucketName(getBucketName());
    return bi;
  }
}
