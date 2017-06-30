package jp.cstudio.handywedge.test.app.workflow;

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
  private String statusName;

  public String getStatusName() {
    return status.toString();
  }
  /*
   * ワークフローステータス定義
   */
  public enum Status {
    STATUS001("新規作成"), STATUS002("上長承認待ち"), STATUS003("部門長承認待ち"), STATUS004("決済済み");

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
