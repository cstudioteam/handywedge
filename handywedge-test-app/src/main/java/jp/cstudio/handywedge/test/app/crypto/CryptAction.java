package jp.cstudio.handywedge.test.app.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.RandomStringUtils;

import com.handywedge.common.FWStringUtil;
import com.handywedge.crypto.FWCipher;

import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class CryptAction {

  @Getter
  @Setter
  private String src;

  @Getter
  @Setter
  private String data;

  @Getter
  @Setter
  private String outputKey;

  @Getter
  @Setter
  private String inputKey;

  @Getter
  @Setter
  private boolean genKey;

  @Inject
  private FWCipher cipher;

  public String encrypt() {
    byte[] encData;
    if (genKey) {
      outputKey = RandomStringUtils.randomAlphanumeric(16);
      encData = cipher.encrypt(outputKey, src.getBytes(StandardCharsets.UTF_8));
    } else {
      encData = cipher.encrypt(src.getBytes(StandardCharsets.UTF_8));
    }
    data = Base64.getEncoder().encodeToString(encData);

    return "";
  }

  public String decrypt() {
    byte[] encData = Base64.getDecoder().decode(src);
    if (FWStringUtil.isEmpty(inputKey)) {
      data = new String(cipher.decrypt(encData), StandardCharsets.UTF_8);
    } else {
      data = new String(cipher.decrypt(inputKey, encData), StandardCharsets.UTF_8);

    }
    return "";
  }

}
