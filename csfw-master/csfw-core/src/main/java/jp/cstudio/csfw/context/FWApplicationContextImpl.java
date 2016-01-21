package jp.cstudio.csfw.context;

import javax.enterprise.context.ApplicationScoped;

import jp.cstudio.csfw.context.FWApplicationContext;
import lombok.Data;

@Data
@ApplicationScoped
public class FWApplicationContextImpl implements FWApplicationContext {

    private String hostName;
    private String applicationId;

}
