package jp.cstudio.handywedge.test.app.db.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Test implements Serializable {

  private static final long serialVersionUID = 1L;

  private String key;
  private String value;

}
