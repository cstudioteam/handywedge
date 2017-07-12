package jp.cstudio.handywedge.test.app.workflow2;

import java.io.Serializable;
import java.sql.Timestamp;

import com.handywedge.workflow.FWWFStatus;

import lombok.Data;

/*
 * ワークフローテーブルのデータモデルクラス。
 */
@Data
public class Workflow2 implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  private String subject;
  private String body;
  private String wfId;
  private FWWFStatus status;
  private Timestamp createDate;
  private Timestamp updateDate;
}
