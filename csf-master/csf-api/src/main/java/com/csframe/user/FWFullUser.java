package com.csframe.user;

import java.sql.Timestamp;

public interface FWFullUser extends FWUser {

  void setId(String id);

  void setName(String name);

  void setLastLoginTime(Timestamp date);
}
