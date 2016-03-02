package jp.cstudio.csframe.test.app.rest;

import com.csframe.rest.FWRESTRequest;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RESTKeyValueRequest extends FWRESTRequest {

  private String key;
  private String value;
}
