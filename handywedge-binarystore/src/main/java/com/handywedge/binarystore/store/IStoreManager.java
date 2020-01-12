/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.binarystore.store;

import java.io.InputStream;
import java.util.List;

import com.handywedge.binarystore.store.common.BinaryInfo;
import com.handywedge.binarystore.store.common.StorageInfo;
import com.handywedge.binarystore.store.common.StoreException;


/**
 * AWS S3からバイナリデータを登録、取得または削除するためのインターフェースです。
 */
public interface IStoreManager {

  /**
   * バイナリデータを登録(multipart)します。
   *
   * @param storage ストレージ情報
   * @param binary バイナリデータ情報
   * @param inStream
   * @return
   * @throws StoreException
   */
  public BinaryInfo upload(StorageInfo storage, BinaryInfo binary, InputStream inStream)
      throws StoreException;

  /**
   * バイナリデータを取得します。該当するバイナリデータIDが存在しない場合はnullを返します。
   *
   * @param storage ストレージ情報
   * @param binary バイナリデータ情報
   * @return バイナリデータ情報
   */
  public BinaryInfo get(StorageInfo storage, BinaryInfo binary) throws StoreException;

  /**
   * バイナリデータの一覧を取得します。バイナリデータの登録が無い場合は空のリストを返します。
   *
   * @param storage ストレージ情報
   * @return バイナリデータ一覧
   */
  public List<BinaryInfo> list(StorageInfo storage, BinaryInfo binary) throws StoreException;

  /**
   * バイナリデータを削除します。
   *
   * @param storage ストレージ情報
   * @param binary バイナリデータ情報
   * @return バイナリデータ情報
   */
  public void delete(StorageInfo storage, BinaryInfo binary) throws StoreException;

}
