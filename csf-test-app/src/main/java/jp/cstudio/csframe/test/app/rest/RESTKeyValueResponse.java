package jp.cstudio.csframe.test.app.rest;

import com.csframe.rest.FWRESTResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RESTKeyValueResponse extends FWRESTResponse {

  private String key;
  private String value;

}
