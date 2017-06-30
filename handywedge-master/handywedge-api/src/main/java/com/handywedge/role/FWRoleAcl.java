/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.role;

import java.io.Serializable;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

public class FWRoleAcl implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private String role;

  @Getter
  @Setter
  private String urlPattern;

  @Getter
  private Pattern pattern;

  public FWRoleAcl(String role, String urlPattern) {
    this.role = role;
    this.urlPattern = urlPattern;
    this.pattern = Pattern.compile(urlPattern); // 毎回コンパイルするとコストが大きいのでキャッシュ時にコンパイル
  }
}
