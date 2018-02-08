/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.notice;

import java.util.List;

/**
 * データベースからお知らせ情報を取得、または情報を登録するためのインターフェースです。
 */
public interface FWNoticeManager {

  /**
   * お知らせを登録します。
   * 
   * @param notice お知らせ情報
   */
  void register(FWNotice notice);

  /**
   * お知らせを取得します。該当するお知らせIDが存在しない場合はnullを返します。
   * 
   * @param id お知らせID
   * @return お知らせ情報
   */
  FWNotice get(int id);

  /**
   * お知らせの一覧を取得します。お知らせの登録が無い場合は空のリストを返します。
   * 
   * @return お知らせ一覧
   */
  List<FWNotice> list();

  /**
   * お知らせを更新します。該当するお知らせIDが存在しない場合は何もしません。
   * 
   * @param notice お知らせ情報
   * @return 更新件数
   */
  int update(FWNotice notice);

  /**
   * お知らせを削除します。
   * 
   * @param id お知らせID
   * @return 削除件数
   */
  int delete(int id);
}
