package com.handywedge.o365.resource;

import com.github.psamsotha.jersey.properties.Prop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertiesResource {
    @Prop("ews.autodiscover")
    private String isAutodiscover;

    @Prop("ews.uri")
    private String uri;

    @Prop("ews.auth.userid")
    private String authUser;

    @Prop("ews.auth.key")
    private String authKey;

    @Prop("ews.auth.iv")
    private String authIv;

    @Prop("ews.auth.passphase")
    private String passPhase;

    @Prop("ews.auth.passwd")
    private String passwd;
}
