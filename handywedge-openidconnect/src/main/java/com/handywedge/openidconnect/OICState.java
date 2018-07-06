package com.handywedge.openidconnect;

/**
 * 認証要求のステータス情報を保持する。
 * 
 * @author takeuchi
 */
public class OICState {

    private final String id;
    private final String nonce;
    private final String returnUrl;
    private final long createTime;
    
    OICState(String returnUrl) {
        
        id = OICUtil.getUUID();
        nonce = OICUtil.getUUID();
        this.returnUrl = returnUrl;
        createTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getNonce() {
        return nonce;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

}
