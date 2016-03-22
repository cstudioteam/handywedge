/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * メール情報を保持するクラスです。
 */
@Getter
public class FWMailMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 送信者アドレス
   */
  @Setter
  private String fromAddress;

  /**
   * 宛先アドレス
   */
  @Setter
  private String[] toAddress;

  /**
   * CC送付先アドレス
   */
  @Setter
  private String[] ccAddress;

  /**
   * BCC送付先アドレス
   */
  @Setter
  private String[] bccAddress;

  /**
   * 件名
   */
  @Setter
  private String subject;

  /**
   * 本文
   */
  @Setter
  private String body;

  /**
   * 添付ファイル
   */
  private List<FWAttachmentDataSource> attachmentFiles;

  /**
   * 文字コード
   */
  @Setter
  private FWMailCharacterEncoding characterEncoding;

  /**
   * 重要度
   */
  @Setter
  private FWMaiPriority priority;

  /**
   * HTMLフラグ
   */
  @Setter
  private boolean htmlFlg;

  /**
   * コンストラクタ<br>
   * デフォルトのメール設定
   * <ul>
   * <li>文字コード: UTF-8</li>
   * <li>重要度 : NORMAL</li>
   * <li>HTMLフラグ: FALSE</li>
   * <li>添付ファイルの初期化</li>
   * </ul>
   */
  public FWMailMessage() {

    characterEncoding = FWMailCharacterEncoding.UTF_8;
    priority = FWMaiPriority.NORMAL;
    htmlFlg = false;
    attachmentFiles = new ArrayList<FWAttachmentDataSource>();
  }

  /**
   * ファイルを添付する。
   *
   * @param b ファイルのバイト配列型
   * @param fileName ファイル名
   */
  public void addAttachmentFile(byte[] b, String fileName) {

    FWAttachmentDataSource source = new FWAttachmentDataSource(b, fileName);
    attachmentFiles.add(source);
  }

  /**
   * 添付された全てのファイルをリストで取得する。
   *
   * @return 添付されたファイルのリスト
   */
  public List<FWAttachmentDataSource> getAttachmentFiles() {

    return this.attachmentFiles;
  }

  /**
   * 添付されたファイルを全て削除する。
   */
  public void resetAttachmentFile() {

    attachmentFiles = new ArrayList<FWAttachmentDataSource>();
  }


  @Override
  public String toString() {

    String br = System.getProperty("line.separator");
    StringBuilder sb = new StringBuilder();
    // Fromアドレス
    sb.append("[");
    sb.append("From = " + this.fromAddress + ", ");
    // Toアドレス
    if ((this.toAddress != null) && (this.toAddress.length != 0)) {
      sb.append("To = {" + this.toAddress[0]);
      for (int i = 1; i < this.toAddress.length; i++) {
        sb.append(", " + this.toAddress[i]);
      }
      sb.append("}, ");
    }
    // Ccアドレス
    if ((this.ccAddress != null) && (this.ccAddress.length != 0)) {
      sb.append("Cc = {" + this.ccAddress[0]);
      for (int i = 1; i < this.ccAddress.length; i++) {
        sb.append(", " + this.ccAddress[i]);
      }
      sb.append("}, ");
    }
    // Bccアドレス
    if ((this.bccAddress != null) && (this.bccAddress.length != 0)) {
      sb.append("Bcc = {" + this.bccAddress[0]);
      for (int i = 1; i < this.bccAddress.length; i++) {
        sb.append(", " + this.bccAddress[i]);
      }
      sb.append("}, ");
    }
    // 件名
    sb.append("Subject = " + this.subject + ", ");
    // 本文(最初と最後の行のみ出力)
    String[] lines = this.body.split(br);
    sb.append("Body = " + lines[0]);
    if (lines.length > 1) {
      sb.append(" ～ " + lines[lines.length - 1]);
    }
    sb.append(", ");
    // 文字コード
    sb.append("CharcterEncode = " + this.characterEncoding.getCharacterEncoding() + ", ");
    sb.append("Priority = {priority = " + this.priority.getPriority());
    sb.append(", xPriority = " + this.priority.getXPriority());
    sb.append(", xMSMailPriority = " + this.priority.getXMSMailPriority());
    sb.append(", importance = " + this.priority.getImportance() + "}, ");
    // Html送信
    sb.append("SendType = ");
    if (this.htmlFlg) {
      sb.append("HTML Mail");
    } else {
      sb.append("Text Mail");
    }
    // 添付ファイル
    if (this.attachmentFiles.size() != 0) {
      sb.append(", AttachmentFile = {" + attachmentFiles.get(0).getName());
      for (int i = 1; i < attachmentFiles.size(); i++) {
        sb.append(", " + attachmentFiles.get(i).getName());
      }
      sb.append("}");
    }
    sb.append("]");
    return sb.toString();
  }
}
