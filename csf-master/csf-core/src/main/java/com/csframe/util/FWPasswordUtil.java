package com.csframe.util;

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
