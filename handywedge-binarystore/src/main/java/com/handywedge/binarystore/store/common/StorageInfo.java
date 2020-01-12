/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ストレージ情報
 */
@Getter
@Setter
@ToString
public class StorageInfo implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * ストレージ種類定義
   *
   */
  public enum Type {
    S3(10, "S3", "Amazon Simple Storage Service"), GCS(20, "GCS", "Google Cloud Storage"), AZURE(30,
        "ABS", "Azure Storage"),;

    private final int id;
    private final String key;
    private final String name;

    private Type(final int id, final String key, final String name) {
      this.id = id;
      this.key = key;
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public String getKey() {
      return this.key;
    }

    public int getId() {
      return this.id;
    }
  }

  public String storageName;

  /**
   * コンストラクタ
   *
   * @param storageName ストレージ名
   */
  public StorageInfo(String storageName) {
    this.storageName = getStorageInfo(storageName).getName();
  }

  public StorageInfo() {
    this.storageName = getStorageInfo(10).getName();
  }

  public static Type getStorageInfo(final int id) {
    Type[] types = Type.values();
    for (Type type : types) {
      if (type.getId() == id) {
        return type;
      }
    }
    return null;
  }

  public static Type getStorageInfo(final String key) {
    Type[] types = Type.values();
    for (Type type : types) {
      if (type.getKey().equals(key.toUpperCase())) {
        return type;
      }
    }
    return null;
  }

}
