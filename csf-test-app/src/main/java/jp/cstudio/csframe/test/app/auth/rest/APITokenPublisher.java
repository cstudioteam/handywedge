package jp.cstudio.csframe.test.app.auth.rest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.log.FWLogger;
import com.csframe.user.FWUser;
import com.csframe.user.auth.FWLoginManager;

import lombok.Getter;

@RequestScoped
@Named("tokenPub")
public class APITokenPublisher {

  @Getter
  private String token;

  @Inject
  private FWLoginManager loginMgr;

  @Inject
  private FWLogger logger;

  @Inject
  private FWUser user;

  @PostConstruct
  public void init() {
    token = loginMgr.getAPIToken(user.getId());
  }

  public String publishToken() {

    // 画面からの発行なのでログイン認証済
    token = loginMgr.publishAPIToken(user.getId());
    logger.info("APIトークンを発行しました。");
    return "";
  }

  public String deleteToken() {

    loginMgr.removeAPIToken(user.getId());
    token = null;
    return "";
  }

}
