package com.csframe.rest;

public abstract class FWRESTResponse {

  private int return_cd;
  private String return_msg;



  public int getReturn_cd() {
    return return_cd;
  }



  public void setReturn_cd(int return_cd) {
    this.return_cd = return_cd;
  }



  public String getReturn_msg() {
    return return_msg;
  }



  public void setReturn_msg(String return_msg) {
    this.return_msg = return_msg;
  }



  @Override
  public abstract String toString();

}
