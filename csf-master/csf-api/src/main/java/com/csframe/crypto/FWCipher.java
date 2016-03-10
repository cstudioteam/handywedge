package com.csframe.crypto;

public interface FWCipher {

  byte[] encrypt(byte[] rawData);

  byte[] encrypt(String key, byte[] rawData);

  byte[] decrypt(byte[] encData);

  byte[] decrypt(String key, byte[] encData);

  String getKey();

}
