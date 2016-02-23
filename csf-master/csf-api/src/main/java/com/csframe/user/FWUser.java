package com.csframe.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Locale;

public interface FWUser extends Serializable {

  String getId();

  String getName();

  Locale getLanguage();

  void setLanguage(Locale language);

  Timestamp getLastLoginTime();

}
