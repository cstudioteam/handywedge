package jp.cstudio.csframe.test.app.db.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.Getter;

/*
 * ワークフローテーブルのデータモデルクラス。
 */
@Data
public class Workflow implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  private String subject;
  private String applyUserId;
  private Status status;
  private Date createDate;
  private Date updateDate;

  /*
   * ワークフローステータス定義
   */
  public enum Status {
    SHINKI("新規申請"), SHONIN1_MACHI("WF承認1待ち"), SHONIN2_MACHI("WF承認2待ち"), SHONIN_SUMI(
        "WF申請承認済み"), HININ_SUMI("WF申請否認済み");

    @Getter
    private String viewStatus;

    private Status(String viewStatus) {
      this.viewStatus = viewStatus;
    }

    @Override
    public String toString() {
      return viewStatus;
    }

    public static Status toStatus(String viewStatus) {
      for (Status s : values()) {
        if (s.toString().equals(viewStatus)) {
          return s;
        }
      }
      return null;
    }
  }
}
