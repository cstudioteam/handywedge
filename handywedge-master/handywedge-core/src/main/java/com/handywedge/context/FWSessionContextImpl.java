/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;

import com.handywedge.user.FWFullUser;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import lombok.Data;

@Data
@SessionScoped
public class FWSessionContextImpl implements FWSessionContext {

  private static final long serialVersionUID = 1L;

  private Date lastAccessTime;

  @Inject
  private FWFullUser user;

}
