package com.handywedge.o365.service;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;

public class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
    public boolean autodiscoverRedirectionUrlValidationCallback(
            String redirectionUrl) {
        return redirectionUrl.toLowerCase().startsWith("https://");
    }
}
