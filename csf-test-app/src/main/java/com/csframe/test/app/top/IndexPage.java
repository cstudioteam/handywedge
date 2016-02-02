package com.csframe.test.app.top;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.context.FWContext;
import com.csframe.user.FWUser;

@RequestScoped
@Named("topPage")
public class IndexPage {

  @Inject
  private FWContext ctx;

  public FWUser getUser() {

    return ctx.getUser();
  }

}
