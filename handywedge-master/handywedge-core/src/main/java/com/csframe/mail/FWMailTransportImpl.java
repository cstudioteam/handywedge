package com.csframe.mail;

import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWStringUtil;
import com.csframe.config.FWMessageResources;
import com.csframe.log.FWLogger;

@RequestScoped
public class FWMailTransportImpl implements FWMailTransport {

  @Inject
  private FWLogger logger;

  @Inject
  private FWMailSender sender;

  @Inject
  private FWMessageResources config;

  @Override
  public void send(FWMailMessage message) throws FWMailException {

    long startTime = logger.perfStart("send");
    logger.debug(message.toString());

    String hostName = config.get("fw.mail.host");
    if (FWStringUtil.isEmpty(hostName)) {
      throw new FWMailException(FWConstantCode.MAIL_HOST_MISSING, "fw.mail.host");
    }

    String port = FWStringUtil.replaceNullString(config.get("fw.mail.port"), "25");

    // メール送信先が存在するかチェック
    if ((message.getToAddress() == null) || (message.getToAddress().length == 0)) {
      throw new FWMailException(FWConstantCode.MAIL_ADDR_MISSING);
    }
    int maxFileSize = Integer.parseInt(FWStringUtil
        .replaceNullString(config.get("fw.mail.max_filesize"), "0"));
    // 最大ファイルサイズが設定されいない、または0以下の場合はサイズチェックはしない。
    if (maxFileSize > 0) {
      List<FWAttachmentDataSource> fileList = message.getAttachmentFiles();
      if ((fileList != null) && !fileList.isEmpty()) {
        int allFileSize = 0;
        for (FWAttachmentDataSource ds : fileList) {
          allFileSize += ds.getSize();
        }
        if (allFileSize > maxFileSize) {
          throw new FWMailException(FWConstantCode.MAIL_FILE_SIZE_OVER,
              allFileSize, maxFileSize);
        }
      }
    }

    try {
      sender.send(hostName, Integer.parseInt(port), message);
    } finally {
      Iterator<FWAttachmentDataSource> ite = message.getAttachmentFiles().iterator();
      while (ite.hasNext()) {
        // 添付ファイルのclose処理
        FWAttachmentDataSource data = ite.next();
        data.close();
      }
    }
    logger.perfEnd("send", startTime);
  }
}
