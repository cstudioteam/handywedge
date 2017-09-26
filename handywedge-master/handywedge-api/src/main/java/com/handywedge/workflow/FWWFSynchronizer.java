/*
 * Copyright (c) 2016ｰ2017 C Studio Co., Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

public interface FWWFSynchronizer {

	/**
	 * WFアクション実行後に呼び出されワークフローデータを同期する。
	 *
	 * @param wfId ワークフローID
	 */
	void doSynchronize(String wfId);
}
