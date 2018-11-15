package com.handywedge.calendar.Office365.rest.requests;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * エラー情報
 */

public class ErrorResponse   {
  @JsonProperty("message")
  private String message = null;

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("field")
  private String field = null;

  @JsonProperty("exMessage")
  private String exMessage = null;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getExMessage() {
    return exMessage;
  }

  public void setExMessage(String exMessage) {
    this.exMessage = exMessage;
  }



  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse error = (ErrorResponse) o;
    return Objects.equals(this.message, error.getMessage()) &&
        Objects.equals(this.code, error.getCode()) &&
        Objects.equals(this.field, error.getField()) &&
        Objects.equals(this.exMessage, error.getExMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, code, field, exMessage);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    field: ").append(toIndentedString(field)).append("\n");
    sb.append("    exMessage: ").append(toIndentedString(exMessage)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
