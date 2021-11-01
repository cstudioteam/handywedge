package com.handywedge.report.service.api.v1.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PdfRequest {

  private String jasperFileName;
  private Map<String, Object> parameters;
  private List<Map<String, Object>> fields;
}
