package jp.cstudio.csframe.test.app.report.pdf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Seikyusho {

  private String itemName;
  private Integer price;
  private Integer quantity;

  public Integer getTotalPrice() {
    return price * quantity;
  }



}
