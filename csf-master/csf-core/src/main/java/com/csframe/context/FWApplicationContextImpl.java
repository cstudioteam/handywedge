/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import lombok.Data;

@Data
@ApplicationScoped
public class FWApplicationContextImpl implements FWApplicationContext {

  private String hostName;
  private String applicationId;
  private String contextPath;
  private Map<String, String> tokenMap = Collections.synchronizedMap(new HashMap<String, String>());

}
