package jp.cstudio.csfw.context;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import jp.cstudio.csfw.context.FWApplicationContext;
import jp.cstudio.csfw.context.FWFullContext;
import jp.cstudio.csfw.context.FWRequestContext;
import jp.cstudio.csfw.context.FWSessionContext;
import jp.cstudio.csfw.user.FWFullUser;
import jp.cstudio.csfw.user.FWUser;

@RequestScoped
public class FWContextImpl implements FWFullContext {

    @Inject
    private FWApplicationContext applicationContext;

    @Inject
    private FWSessionContext sessionContext;

    @Inject
    private FWRequestContext requestContext;

    @Override
    public String getRequestId() {

        return requestContext.getRequestId();
    }

    @Override
    public void setRequestId(String requestId) {

        requestContext.setRequestId(requestId);
    }

    @Override
    public String getApplicationId() {

        return applicationContext.getApplicationId();
    }

    @Override
    public Date getLastAccessTime() {

        return sessionContext.getLastAccessTime();
    }

    @Override
    public void setLastAccessTime(Date lastAccessTime) {

        sessionContext.setLastAccessTime(lastAccessTime);
    }

    @Override
    public String getContextPath() {

        return requestContext.getContextPath();
    }

    @Override
    public void setContextPath(String contextPath) {

        requestContext.setContextPath(contextPath);
    }

    @Override
    public String getHostName() {

        return applicationContext.getHostName();
    }

    @Override
    public void setHostName(String hostName) {

        applicationContext.setHostName(hostName);
    }

    @Override
    public Date getRequestStartTime() {

        return requestContext.getRequestStartTime();
    }

    @Override
    public void setRequestStartTime(Date requestStartTime) {

        requestContext.setRequestStartTime(requestStartTime);
    }

    @Override
    public void setApplicationId(String applicationId) {

        applicationContext.setApplicationId(applicationId);
    }

    @Override
    public void setUser(FWFullUser user) {

        sessionContext.setUser(user);
    }

    @Override
    public FWUser getUser() {

        return sessionContext.getUser();
    }

}