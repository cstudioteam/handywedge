/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.rest.api.model;

import java.util.List;

import com.handywedge.binarystore.store.common.BinaryInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BinaryStoreServiceGetResponse extends BinaryStoreServiceResponse {

  private List<BinaryInfo> binaryInfos;

}
