package com.handywedge.mail;

/**
 * メール重要度の列挙型です。
 */
public enum FWMaiPriority {

  HIGH("urgent", "1", "High", "High"), NORMAL("normal", "3", "Normal", "Normal"), LOW("non-urgent",
      "5", "Low", "Low");
  /** Priority */
  private String priority;
  /** X-Priority */
  private String xPriority;
  /** X-MSMail-Priority */
  private String xMSMailPriority;
  /** Importance */
  private String importance;

  /**
   * コンストラクタ<br>
   * 各ヘッダーに設定する値を設定する。
   *
   * @param priority Priorityヘッダー設定値
   * @param xPriority X-Priorityヘッダー設定値
   * @param xMSMailPriority X-MSMail-Priorityヘッダー設定値
   * @param importance Importanceヘッダー設定値
   */
  private FWMaiPriority(String priority, String xPriority, String xMSMailPriority,
      String importance) {

    this.priority = priority;
    this.xPriority = xPriority;
    this.xMSMailPriority = xMSMailPriority;
    this.importance = importance;
  }

  /**
   * Priorityヘッダー設定値を取得する。
   *
   * @return Priorityヘッダー設定値
   */
  public String getPriority() {

    return this.priority;
  }

  /**
   * X-Priorityヘッダー設定値を取得する。
   *
   * @return X-Priorityヘッダー設定値
   */
  public String getXPriority() {

    return this.xPriority;
  }

  /**
   * X-MSMail-Priorityヘッダー設定値を取得する。
   *
   * @return X-MSMail-Priorityヘッダー設定値
   */
  public String getXMSMailPriority() {

    return this.xMSMailPriority;
  }

  /**
   * Importanceヘッダー設定値を取得する。
   *
   * @return Importanceヘッダー設定値
   */
  public String getImportance() {

    return this.importance;
  }
}
