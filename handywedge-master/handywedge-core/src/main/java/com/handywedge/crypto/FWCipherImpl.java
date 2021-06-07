/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.handywedge.common.FWConstantCode;
import com.handywedge.config.FWMessageResources;
import com.handywedge.log.FWLogger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

@ApplicationScoped
public class FWCipherImpl implements FWCipher {

  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
  private static final String ALGORITHM = "AES";

  @Inject
  private FWMessageResources msg;

  @Inject
  private FWLogger logger;

  @Getter
  private String key;

  private String iv;

  @PostConstruct
  public void init() {
    key = msg.get(FWMessageResources.CRYPTO_KEY);
    iv = msg.get(FWMessageResources.CRYPTO_IV);
    logger.debug("Cipher init. key={}, iv={}", key, iv);
  }


  @Override
  public byte[] encrypt(byte[] rawData) {
    return encrypt(key, rawData);
  }

  @Override
  public byte[] encrypt(String key, byte[] rawData) {
    logger.debug("encrypt start.");
    try {
      byte[] encData = initCipher(key, MODE.ENCRYPT).doFinal(rawData);
      logger.debug("encrypt end.");
      return encData;
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | NoSuchAlgorithmException
        | NoSuchPaddingException e) {
      throw new FWCipherException(FWConstantCode.CIPHER_ENCRYPT_FAIL);
    }
  }

  @Override
  public byte[] decrypt(byte[] encData) {
    return decrypt(key, encData);
  }

  @Override
  public byte[] decrypt(String key, byte[] encData) {
    logger.debug("decrypt start.");
    try {
      byte[] rawData = initCipher(key, MODE.DECRYPT).doFinal(encData);
      logger.debug("decrypt end.");
      return rawData;
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | NoSuchAlgorithmException
        | NoSuchPaddingException e) {
      throw new FWCipherException(FWConstantCode.CIPHER_DECRYPT_FAIL);
    }
  }

  private Cipher initCipher(String key, MODE mode) throws InvalidKeyException,
      InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

    if (mode == MODE.ENCRYPT) {
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    } else {
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
    }

    return cipher;
  }

  private enum MODE {
    ENCRYPT, DECRYPT
  }

}
