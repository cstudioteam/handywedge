/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

/**
 * メール送信インターフェースです。 使用方法は以下のとおりです。
 *
 * <PRE>
 *
 *   {@code  @Inject}
 * {@code    private FWMailTransport mailTansport;
 *     ・・・
 * 
 *     // 送信情報の設定
 *     FWMailMessage mailMessage = new FWMailMessage();
 *     mailMessage.setToAddress(new String[]{"aaa@bbb.co.jp"}); // TO
 *     mailMessage.setFromAddress("ccc@ddd.co.jp"); // FROM
 *     mailMessage.setCcAddress(new String[]{"eee@fff.co.jp"}); // CC
 *     mailMessage.setBody("This is Test."); // 本文
 *     mailMessage.setSubject("Is this Test?"); // 件名
 *     mailMessage.setCharacterEncoding(FWMailCharacterEncoding.ISO_2022_JP); // 文字コード。デフォルトでUTF-8に設定
 *     mailMessage.setPriority(FWMaiPriority.HIGH);  // 重要度。デフォルトでNomalで設定済。
 *     mailMessage.setHtmlMail(true); // HTML形式で送信。 デフォルトでTextで送信
 * 
 *     // 添付ファイルの設定
 *     byte[] file;
 *     String fileName;
 *     mailMessage.addAttachmentFile(file, fileName);
 * 
 *     // メール送信インスタンスを生成し、送信方法指定し、送信する。
 *     mailTansport.send(mailMessage);
 *     }
 * </PRE>
 */
public interface FWMailTransport {

  public void send(FWMailMessage message) throws FWMailException;

}
