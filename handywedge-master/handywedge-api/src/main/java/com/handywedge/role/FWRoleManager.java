/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.role;

import java.util.List;

public interface FWRoleManager {

  /**
   * ログイン中のユーザーが現在のステータスに対して起こせるアクション（遷移後のステータス）のリストを返します。
   * 
   * @param currentStatus 現在のデータステータス
   * @return 遷移させる事が可能なステータス
   */
  List<String> getActions(String currentStatus);

  /**
   * 指定されたロールのユーザーが現在のステータスに対して起こせるアクション（遷移後のステータス）のリストを返します。
   * 
   * @param currentStatus 現在のデータステータス
   * @param role ロール
   * @return 遷移させる事が可能なステータス
   */
  List<String> getActions(String currentStatus, String role);

  /**
   * 指定されたロールのユーザーが指定されたアクション（ステータス遷移）を実行できるか判定します。
   * 権限を持つ場合は該当アクションのアクションコードとアクション名が設定されたDTOを返します。
   * 権限がない場合は例外がスローされます。
   * 
   * @param preStatus 遷移前のステータス
   * @param postStatus 遷移後のステータス
   * @return アクションコードとアクション名のDTO
   * @throws FWRoleException 権限がない場合にスロー
   */
  FWAction checkAction(String preStatus, String postStatus) throws FWRoleException;

  /**
   * ログイン中のユーザーが指定されたアクション（ステータス遷移）を実行できるか判定します。
   * 権限を持つ場合は該当アクションのアクションコードとアクション名が設定されたDTOを返します。
   * 権限がない場合は例外がスローされます。
   * 
   * @param preStatus 遷移前のステータス
   * @param postStatus 遷移後のステータス
   * @param role ロール
   * @return アクションコードとアクション名のDTO
   * @throws FWRoleException 権限がない場合にスロー
   */
  FWAction checkAction(String preStatus, String postStatus, String role) throws FWRoleException;

  /**
   * ログイン中のユーザーがリクエストされたURLに対してアクセスする権限を持つか判定します。
   * 判定にはロール別ACLテーブルに登録されたデータが使用されます。（データが1行もない場合は常にtrueとなります）
   * 
   * @return 権限を持つ場合はtrue
   */
  boolean isAccessAllow();

}
