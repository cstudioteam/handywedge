/*
 * Copyright (c) 2016ｰ2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.util.List;

/**
 *
 * @since0.5.0
 *
 */
public interface FWWFManager {

  /**
   * 指定されたWFIDの現在のステータスのDTOを返します。
   * ステータスが取得できなかった場合はNULLを返します。
   *
   * @param wfId ワークフローID
   * @return ステータスコードとステータス名のDTO
   */
  FWWFStatus getStatus(String wfId);

  /**
   * ログイン中のユーザーが実行可能な指定のアクションコードの承認アクションを（DTO）を返します。
   *
   * @param actionCode アクションコード
   * @return 実行可能な承認アクションDTO
   */
  FWWFAction getAction(String actionCode);

  /**
   * ログイン中のユーザーが現在のステータスに対して起こせる承認アクション、
   * 及び、否認アクション（前ステータスに戻す）のリスト（DTO）を返します。
   *
   * @param wfId ワークフローID
   * @return 実行可能な承認、否認アクションDTOリスト
   */
  List<FWWFAction> getActions(String wfId);

  /**
   * ログイン中のユーザーが現在のステータスに対して起こせる承認アクション（DTO）のリストを返します。
   *
   * @param wfId ワークフローID
   * @return 実行可能な承認アクションDTOリスト
   */
  List<FWWFAction> getGoActions(String wfId);

  /**
   * ログイン中のユーザーが現在のステータスに対して起こせる否認アクション（DTO）を返します。
   * 否認アクションは１つ前のステータスに戻すアクションを指す。
   *
   * @param wfId ワークフローID
   * @return 実行可能な否認系アクションDTOリスト
   */
  FWWFAction getRollBackAction(String wfId);

  /**
   * 指定されたロールのユーザーが実行可能な指定のアクションコードの承認アクションを（DTO）を返します。
   *
   * @param actionCode アクションコード
   * @param role ロール
   * @return 実行可能な承認アクションDTO
   */
  FWWFAction getActionInfo(String actionCode, String role);

  /**
   * 指定されたロールのユーザーが現在のステータスに対して起こせる承認アクション、
   * 及び、否認アクション（前ステータスに戻す）のリスト（DTO）を返します。
   *
   * @param wfId ワークフローID
   * @param role ロール
   * @return 実行可能な承認、否認アクションDTOリスト
   */
  List<FWWFAction> getActionInfos(String wfId, String role);

  /**
   * 指定されたロールのユーザーが現在のステータスに対して起こせる承認アクション（DTO）のリストを返します。
   *
   * @param wfId ワークフローID
   * @param role ロール
   * @return 実行可能な承認アクションDTOリスト
   */
  List<FWWFAction> getGoActionInfos(String wfId, String role);

  /**
   * 指定されたロールのユーザーが現在のステータスに対して起こせる否認アクション（DTO）を返します。
   * 否認アクションは１つ前のステータスに戻すアクションを指す。
   *
   * @param wfId ワークフローID
   * @param role ロール
   * @return 実行可能な否認系アクションDTOリスト
   */
  FWWFAction getRollBackActionInfo(String wfId, String role);

  /**
   * ログイン中のユーザーが指定されたアクションを実行できるか判定します。
   * 権限を持つ場合は該当アクションのアクションコードとアクション名が設定されたDTOを返します。
   * 権限がない場合は例外がスローされます。
   * ※ ワークフローID採番後のみ使用
   *
   * @param wfId ワークフローID
   * @param actionCode 実行対象のアクションコード
   * @return アクションコードとアクション名のDTO
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFAction checkAction(String wfId, String actionCode) throws FWWFException;

  /**
   * ログイン中のユーザーによる初期WFアクションを実行します。
   * 初期WFアクションとはワークフローの開始アクションを指し、WFIDの採番を行い返却します。
   * 以後のアクションはWFIDを指定することで一連のWFの紐付け実行が可能です。
   *
   * @param actionCode 実行対象のアクションコード
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doInitAction(String actionCode) throws FWWFException;

  /**
   * ログイン中のユーザーによる初期WFアクションを実行します。
   * 初期WFアクションとはワークフローの開始アクションを指し、WFIDの採番を行い返却します。
   * 以後のアクションはWFIDを指定することで一連のWFの紐付け実行が可能です。
   *
   * @param actionCode 実行対象のアクションコード
   * @param description ワークフロー履歴-備考
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doInitAction(String actionCode, String description) throws FWWFException;

  /**
   * ログイン中のユーザーによる初期WFアクションを実行します。
   * 初期WFアクションとはワークフローの開始アクションを指し、WFIDの採番を行い返却します。
   * 以後のアクションはWFIDを指定することで一連のWFの紐付け実行が可能です。
   *
   * @param wfAction 実行対象のアクションオブジェクト（actionCodeの指定必須）
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doInitAction(FWWFAction wfAction) throws FWWFException;

  /**
   * ログイン中のユーザーによる初期WFアクションを実行します。
   * 初期WFアクションとはワークフローの開始アクションを指し、WFIDの採番を行い返却します。
   * 以後のアクションはWFIDを指定することで一連のWFの紐付け実行が可能です。
   *
   * @param wfAction 実行対象のアクションオブジェクト（actionCodeの指定必須）
   * @param description ワークフロー履歴-備考
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doInitAction(FWWFAction wfAction, String description) throws FWWFException;

  /**
   * ログイン中のユーザーによるWFアクションを実行します。
   *
   * @param wfId ワークフローID
   * @param actionCode 実行対象のアクションコード
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doAction(String wfId, String actionCode) throws FWWFException;

  /**
   * ログイン中のユーザーによるWFアクションを実行します。
   *
   * @param wfId ワークフローID
   * @param actionCode 実行対象のアクションコード
   * @param description ワークフロー履歴-備考
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doAction(String wfId, String actionCode, String description) throws FWWFException;

  /**
   * ログイン中のユーザーによるWFアクションを実行します。
   *
   * @param wfAction 実行対象のアクションオブジェクト（wfId, actionCodeの指定必須）
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doAction(FWWFAction wfAction) throws FWWFException;

  /**
   * ログイン中のユーザーによるWFアクションを実行します。
   *
   * @param wfAction 実行対象のアクションオブジェクト（wfId, actionCodeの指定必須）
   * @param description ワークフロー履歴-備考
   * @return ワークフロー履歴のDTOリスト
   * @throws FWWFException 権限がない場合にスロー
   */
  FWWFLog doAction(FWWFAction wfAction, String description) throws FWWFException;

  /**
   * 指定されたWFIDのワークフロー履歴のDTOリストを返します。
   *
   * @param wfId ワークフローID
   * @return ワークフロー履歴のDTOリスト
   */
  List<FWWFLog> getWFLogs(String wfId);
}
