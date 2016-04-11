/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.role;

import java.util.List;

public interface FWRoleManager {

  List<String> getActions(String currentStatus);

  List<String> getActions(String currentStatus, String role);

  String checkAction(String preStatus, String postStatus) throws FWRoleException;

  String checkAction(String preStatus, String postStatus, String role) throws FWRoleException;

}
