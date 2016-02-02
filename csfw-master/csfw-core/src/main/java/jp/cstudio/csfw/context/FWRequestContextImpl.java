package jp.cstudio.csfw.context;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import jp.cstudio.csfw.context.FWRequestContext;
import lombok.Data;

@Data
@RequestScoped
public class FWRequestContextImpl implements FWRequestContext {

  private String requestId;
  private String contextPath;
  private Date requestStartTime;

}
