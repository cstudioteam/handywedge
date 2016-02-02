package jp.cstudio.csfw.context;

import java.util.Date;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import jp.cstudio.csfw.context.FWSessionContext;
import jp.cstudio.csfw.user.FWFullUser;
import lombok.Data;

/**
 * FW内部のみ使用。
 */
@Data
@SessionScoped
public class FWSessionContextImpl implements FWSessionContext {

  private static final long serialVersionUID = 1L;

  private Date lastAccessTime;
  private Locale locale;

  @Inject
  private FWFullUser user;

}
