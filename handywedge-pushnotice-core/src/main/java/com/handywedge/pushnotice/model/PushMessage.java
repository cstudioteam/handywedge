package com.handywedge.pushnotice.model;

public class PushMessage {

  protected String accesskey;
  protected String userId;
  protected String text;


  public String getAccesskey() {

    return accesskey;
  }

  public void setAccesskey(String accesskey) {

    this.accesskey = accesskey;
  }

  public String getUserId() {

    return userId;
  }

  public void setUserId(String userId) {

    this.userId = userId;
  }

  public String getText() {

    return text;
  }

  public void setText(String text) {

    this.text = text;
  }
}
