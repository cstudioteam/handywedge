/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.common;

import org.mindrot.jbcrypt.BCrypt;

public class FWPasswordUtil {

  public static String createPasswordHash(String password) {

    String salt = BCrypt.gensalt();
    return BCrypt.hashpw(password, salt);
  }

  public static boolean checkPassword(String password, String passwordHash) {

    return BCrypt.checkpw(password, passwordHash);
  }
}
