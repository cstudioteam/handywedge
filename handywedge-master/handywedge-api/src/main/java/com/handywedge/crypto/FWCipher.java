/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.crypto;

/**
 * 暗号化に関するライブラリクラスです。
 */
public interface FWCipher {

  /**
   * データを暗号化します。<br>
   * 鍵データはプロパティファイルの「fw.crypto.key」に設定された値を使用します。
   * 
   * @param rawData 暗号化するデータ
   * @return 暗号化したデータ
   */
  byte[] encrypt(byte[] rawData);

  /**
   * データを暗号化します。<br>
   * 指定したkeyを使用しデータを暗号化します。
   * 
   * @param key 128bit長の暗号化鍵
   * @param rawData 暗号化するデータ
   * @return 暗号化したデータ
   */
  byte[] encrypt(String key, byte[] rawData);

  /**
   * データを復号します。<br>
   * 鍵データはプロパティファイルの「fw.crypto.key」に設定された値を使用します。
   * 
   * @param encData 復号するデータ
   * @return 復号されたデータ
   */
  byte[] decrypt(byte[] encData);

  /**
   * データを復号します。<br>
   * 指定したkeyを使用しデータを復号します。
   * 
   * @param key 128bit長の暗号化鍵
   * @param encData 復号するデータ
   * @return 復号したデータ
   */
  byte[] decrypt(String key, byte[] encData);

  /**
   * プロパティファイルの「fw.crypto.key」に設定された鍵を返します。
   * 
   * @return プロパティファイルに設定された鍵
   */
  String getKey();

}
