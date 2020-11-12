package com.handywedge.converter.tosvg.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.JsonParseExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import com.handywedge.converter.tosvg.rest.resources.FWPDFToSVGJobService;

public class FWPDFToSVGApplication extends ResourceConfig {
  public FWPDFToSVGApplication() {
    packages("com.handywedge.converter.tosvg.rest.resources");
    register(JacksonJaxbJsonProvider.class);
    register(JsonParseExceptionMapper.class, 1);

    register(MultiPartFeature.class);

    register(FWPDFToSVGJobService.class);
    register(new AbstractBinder() {
      @Override
      protected void configure() {
        bindAsContract(FWPDFToSVGJobService.class).in(RequestScoped.class);
      }
    });
  }
}
