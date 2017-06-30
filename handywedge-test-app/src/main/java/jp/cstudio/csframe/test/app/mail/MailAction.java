package jp.cstudio.csframe.test.app.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.csframe.common.FWException;
import com.csframe.common.FWStringUtil;
import com.csframe.log.FWLogger;
import com.csframe.mail.FWMaiPriority;
import com.csframe.mail.FWMailCharacterEncoding;
import com.csframe.mail.FWMailMessage;
import com.csframe.mail.FWMailTransport;

import lombok.Getter;
import lombok.Setter;

@ViewScoped
@Named
public class MailAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWLogger logger;

  @Inject
  private transient FWMailTransport mail;

  @Getter
  @Setter
  private String toAddress;

  @Getter
  @Setter
  private String fromAddress;

  @Getter
  @Setter
  private String ccAddress;

  @Getter
  @Setter
  private String bccAddress;

  @Getter
  @Setter
  private String body;

  @Getter
  @Setter
  private String subject;

  @Getter
  @Setter
  private int fileCount;

  @Getter
  @Setter
  private FWMaiPriority priority;

  @Getter
  @Setter
  private String format;

  @Getter
  @Setter
  private FWMailCharacterEncoding mailEncoding = FWMailCharacterEncoding.UTF_8;

  public void send() throws UnsupportedEncodingException, FWException {

    FWMailMessage mailMessage = new FWMailMessage();
    mailMessage.setToAddress(toAddress.split(",")); // TO
    mailMessage.setFromAddress(fromAddress); // FROM
    if (!FWStringUtil.isEmpty(ccAddress)) {
      mailMessage.setCcAddress(ccAddress.split(",")); // CC
    }
    if (!FWStringUtil.isEmpty(bccAddress)) {
      mailMessage.setBccAddress(bccAddress.split(",")); // BCC
    }
    mailMessage.setBody(body); // 本文
    mailMessage.setSubject(subject); // 件名
    if (priority != null) {
      mailMessage.setPriority(priority); // 重要度。デフォルトでNomalで設定済。
    }
    if (!FWStringUtil.isEmpty(format)) {
      mailMessage.setHtmlFlg("html".equals(format)); // デフォルトでTextで送信
    }
    mailMessage.setCharacterEncoding(mailEncoding);

    if (fileCount > 5) {
      fileCount = 5;
      logger.warn("ファイル数が多すぎるので5に設定します。");
    }

    for (int i = 0; i < fileCount; i++) {
      mailMessage.addAttachmentFile(UUID.randomUUID().toString().getBytes("UTF-8"),
          (i + 1) + ".txt");
    }

    mail.send(mailMessage);
  }

  public List<SelectItem> getPriorities() {

    ArrayList<SelectItem> items = new ArrayList<SelectItem>();
    items.add(new SelectItem(FWMaiPriority.HIGH, "高"));
    items.add(new SelectItem(FWMaiPriority.NORMAL, "中"));
    items.add(new SelectItem(FWMaiPriority.LOW, "低"));

    return items;
  }

  public List<SelectItem> getFormats() {

    ArrayList<SelectItem> items = new ArrayList<SelectItem>();
    items.add(new SelectItem("text", "テキスト"));
    items.add(new SelectItem("html", "HTML"));

    return items;
  }

  public List<SelectItem> getEncodings() {

    ArrayList<SelectItem> items = new ArrayList<SelectItem>();
    items.add(new SelectItem(FWMailCharacterEncoding.UTF_8, "UTF-8"));
    items.add(new SelectItem(FWMailCharacterEncoding.ISO_2022_JP, "ISO_2022_JP"));

    return items;
  }

}
