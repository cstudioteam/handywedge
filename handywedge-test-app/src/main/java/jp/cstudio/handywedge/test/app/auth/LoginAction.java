package jp.cstudio.handywedge.test.app.auth;

import java.io.IOException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWContext;
import com.handywedge.user.auth.FWLoginManager;

import lombok.Getter;
import lombok.Setter;

@RequestScoped
@Named("loginMgr")
public class LoginAction {

  @Setter
  @Getter
  private String id = "handywedge-test-app"; // デフォルトユーザ（入力を省略したい）

  @Setter
  @Getter
  private String password = "password";

  @Inject
  private FWContext ctx;

  @Inject
  private FWLoginManager loginMgr;

  public String login() throws IOException {

    boolean auth = loginMgr.login(id, password);
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext ec = fc.getExternalContext();
    if (auth) {
      ec.redirect(ctx.getContextPath());
    } else {
      fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "login failed.", null));
      ec.getFlash().setKeepMessages(true);
      ec.redirect(FWStringUtil.getLoginUrl());
    }
    fc.responseComplete();
    return null;
  }

  public String logout() {

    loginMgr.logout();

    return "/contents/auth/logout.xhtml";
  }
}
