package com.csframe.user.auth;

public interface FWLoginManager {

  boolean login(String id, String password) throws FWAuthException;

  void logout();

  String publishAPIToken(String id);

  void removeAPIToken(String id);
}
