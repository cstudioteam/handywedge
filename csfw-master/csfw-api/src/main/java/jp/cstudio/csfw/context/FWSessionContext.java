package jp.cstudio.csfw.context;

import java.io.Serializable;
import java.util.Date;

import jp.cstudio.csfw.user.FWFullUser;

/**
 * FW内部のみ使用します。
 */
public interface FWSessionContext extends Serializable {

    Date getLastAccessTime();

    void setLastAccessTime(Date lastAccessTime);

    FWFullUser getUser();

    void setUser(FWFullUser user);

}
