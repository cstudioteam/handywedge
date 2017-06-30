package com.handywedge.mail;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWStringUtil;
import com.handywedge.config.FWMessageResources;
import com.handywedge.log.FWLogger;
import com.handywedge.mail.FWAttachmentDataSource;
import com.handywedge.mail.FWMailCharacterEncoding;
import com.handywedge.mail.FWMailMessage;
import com.handywedge.mail.FWMailSendException;
import com.handywedge.mail.FWMailSender;

@RequestScoped
public class FWMailSenderImpl implements FWMailSender {

  @Inject
  private FWLogger logger;

  @Inject
  private FWMessageResources config;

  @Override
  public void send(String hostName, int port, FWMailMessage message) throws FWMailSendException {

    long startTime = logger.perfStart("send");
    try {
      Properties prop = new Properties();
      // SMTPサーバを設定
      prop.put("mail.smtp.host", hostName);
      prop.put("mail.smtp.port", port);
      prop.put("mail.transport.protocol", "smtp");
      prop.put("mail.smtp.starttls.enable",
          FWStringUtil.replaceNullString(config.get("fw.mail.starttls.enable"), "false"));
      prop.put("mail.smtp.starttls.required",
          FWStringUtil.replaceNullString(config.get("fw.mail.starttls.required"), "false"));

      logger.debug(prop.toString());
      Session session = Session.getInstance(prop);
      MimeMessage mimeMessage = new MimeMessage(session);
      String charCd = message.getCharacterEncoding().getCharacterEncoding();
      boolean convertFlag =
          charCd.equals(FWMailCharacterEncoding.ISO_2022_JP.getCharacterEncoding());
      // FROMアドレス
      if (!FWStringUtil.isEmpty(message.getFromAddress())) {
        mimeMessage.setFrom(new InternetAddress(message.getFromAddress()));
      }
      // TOアドレス
      InternetAddress[] toAddress = new InternetAddress[message.getToAddress().length];
      for (int i = 0; i < message.getToAddress().length; i++) {
        toAddress[i] = new InternetAddress(message.getToAddress()[i]);
      }
      mimeMessage.setRecipients(Message.RecipientType.TO, toAddress);
      // CCアドレス
      if ((message.getCcAddress() != null) && (message.getCcAddress().length > 0)) {
        InternetAddress[] ccAddress = new InternetAddress[message.getCcAddress().length];
        for (int i = 0; i < message.getCcAddress().length; i++) {
          ccAddress[i] = new InternetAddress(message.getCcAddress()[i]);
        }
        mimeMessage.setRecipients(Message.RecipientType.CC, ccAddress);
      }
      // BCCアドレス
      if ((message.getBccAddress() != null) && (message.getBccAddress().length > 0)) {
        InternetAddress[] bccAddress = new InternetAddress[message.getBccAddress().length];
        for (int i = 0; i < message.getBccAddress().length; i++) {
          bccAddress[i] = new InternetAddress(message.getBccAddress()[i]);
        }
        mimeMessage.setRecipients(Message.RecipientType.BCC, bccAddress);
      }
      mimeMessage.setSubject(
          MimeUtility.encodeText(convertIso2022jp(message.getSubject(), convertFlag), charCd, "B"));
      // 重要度を設定する。（X-Priorityの2,4は非対応）
      mimeMessage.setHeader("Priority", message.getPriority().getPriority());
      mimeMessage.setHeader("X-Priority", message.getPriority().getXPriority());
      mimeMessage.setHeader("X-MSMail-Priority", message.getPriority().getXMSMailPriority());
      mimeMessage.setHeader("Importance", message.getPriority().getImportance());
      if (message.getAttachmentFiles().isEmpty()) {
        // 添付ファイルなしの場合
        if (message.isHtmlFlg()) {
          // HTML形式で送信
          mimeMessage.setContent(convertIso2022jp(message.getBody(), convertFlag),
              "text/html; charset=" + charCd);
        } else {
          // TEXT形式で送信
          mimeMessage.setText(convertIso2022jp(message.getBody(), convertFlag), charCd);
        }
      } else {
        // 添付ファイルありの場合
        MimeMultipart content = new MimeMultipart();
        MimeBodyPart contentText = new MimeBodyPart();
        if (message.isHtmlFlg()) {
          contentText.setContent(convertIso2022jp(message.getBody(), convertFlag),
              "text/html; charset=" + charCd);
        } else {
          contentText.setText(convertIso2022jp(message.getBody(), convertFlag), charCd);
        }
        contentText.setHeader("Content-Transfer-Encoding", "7bit");
        content.addBodyPart(contentText);
        Iterator<FWAttachmentDataSource> ite = message.getAttachmentFiles().iterator();
        while (ite.hasNext()) {
          MimeBodyPart contentFile = new MimeBodyPart();
          DataSource source = ite.next();
          contentFile.setFileName(
              MimeUtility.encodeText(convertIso2022jp(source.getName(), convertFlag), charCd, "B"));
          DataHandler handler = new DataHandler(source);
          contentFile.setDataHandler(handler);
          content.addBodyPart(contentFile);
        }
        mimeMessage.setContent(content);
      }
      try {
        String user = config.get("fw.mail.user");
        String password = config.get("fw.mail.password");
        Transport.send(mimeMessage, user, password);
      } catch (SendFailedException se) {
        // 正常に送信できなかったアドレス
        Address[] invalidAdds = se.getInvalidAddresses();
        if (invalidAdds != null) {
          for (Address invalidAdd : invalidAdds) {
            logger.debug("Invalid Addresses : " + invalidAdd.toString());
          }
        }
        // 正常に送信できたアドレス
        Address[] validAdds = se.getValidSentAddresses();
        if (validAdds != null) {
          for (Address validAdd : validAdds) {
            logger.debug("Valid Sent Addresses : " + validAdd.toString());
          }
        }
        // 有効であるが送信できなかったアドレス
        Address[] validUnsentAdds = se.getValidUnsentAddresses();
        if (validUnsentAdds != null) {
          for (Address validUnsentAdd : validUnsentAdds) {
            logger.debug("Valid Unsent Addresses : " + validUnsentAdd.toString());
          }
        }
        throw new FWMailSendException(FWConstantCode.MAIL_SEND_FAIL, se);
      }
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new FWMailSendException(FWConstantCode.MAIL_SEND_FAIL, e);
    }
    logger.perfEnd("send", startTime);
  }

  /**
   * iso-2022-jpに変換した際に文字化する文字列をユニコードで指定する。<br>
   * iso-2022-jpに変換した際に文字化する文字列 ～,||,－,￠,￡,￢,―
   *
   * @param msg 変換対象文字列
   * @param convertFlag 変換フラグ
   * @return 変換文字列
   */
  private String convertIso2022jp(String msg, boolean convertFlag) {

    if (convertFlag) {
      StringBuilder sb = new StringBuilder();
      if (msg != null) {
        for (int i = 0; i < msg.length(); i++) {
          char ch = msg.charAt(i);
          switch (ch) {
            case 0xff5e: // ～
              ch = 0x301c;
              break;
            case 0x2225: // ||
              ch = 0x2016;
              break;
            case 0xff0d: // －
              ch = 0x2212;
              break;
            case 0xffe0: // ￠
              ch = 0x00a2;
              break;
            case 0xffe1: // ￡
              ch = 0x00a3;
              break;
            case 0xffe2: // ￢
              ch = 0x00ac;
              break;
            case 0x2015: // ―
              ch = 0x2014;
              break;
            default:
              break;
          }
          sb.append(ch);
        }
        msg = sb.toString();
      }
    }
    return msg;
  }
}
