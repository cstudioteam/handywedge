package com.csframe.user.auth;

public interface FWLoginManager {

  boolean login(String id, String password);

  void logout();

  String publishAPIToken(String id);

  String getAPIToken(String id);

  void removeAPIToken(String id);

  boolean authAPIToken(String token);
}
