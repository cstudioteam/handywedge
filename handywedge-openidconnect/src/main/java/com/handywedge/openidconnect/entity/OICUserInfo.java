package com.handywedge.openidconnect.entity;

/**
 * ユーザ情報を保持するクラス。
 * 
 * @author takeuchi
 */
public class OICUserInfo {
    
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
