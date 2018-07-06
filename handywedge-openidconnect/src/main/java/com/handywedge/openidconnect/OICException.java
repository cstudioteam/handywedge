package com.handywedge.openidconnect;

/**
 * OpenID Connect例外クラス。
 * 
 * @author takeuchi
 */
public class OICException extends Exception {

    public OICException() {
        super();
    }

    public OICException(String msg) {
        super(msg);
    }
    
    public OICException(String msg, Throwable t) {
        super(msg, t);
    }
    
    public OICException(Throwable t) {
        super(t);
    }
}
