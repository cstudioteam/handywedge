/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.openidconnect.entity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.handywedge.openidconnect.OICException;
import com.handywedge.openidconnect.OICUtil;

/**
 * JWT(ID_TOKEN)を保持するクラス。
 *
 * @author takeuchi
 */
public class OICJwt {

  private JwtHeader header;
  private JwtPayload payload;
  private byte[] signature;

  public OICJwt(String token) throws OICException, IOException {

    String[] array = token.split(Pattern.quote("."));
    if (array == null || array.length != 3) {
      throw new OICException("Invalid JWT token:" + token);
    }

    this.header = new JwtHeader(array[0]);
    this.payload = new JwtPayload(array[1]);
    this.signature = OICUtil.b64UrlDecode(array[2]);

  }

  public JwtHeader getHeader() {
    return header;
  }

  public JwtPayload getPayload() {
    return payload;
  }

  public byte[] getSignature() {
    return signature;
  }

  public boolean validate(PublicKey pubKey, String nonce)
      throws InvalidKeyException, SignatureException, OICException, NoSuchAlgorithmException {

    boolean result = false;

    String non = payload.getClaim("nonce");
    if (non == null || non.isEmpty() || !non.equals(nonce)) {
      throw new OICException("Nonce not matched. Issue:[" + nonce + "], Claim;[" + non + "]");
    }

    try {
      Signature verifier = Signature.getInstance("SHA256withRSA");
      verifier.initVerify(pubKey);
      verifier.update((header.coded + "." + payload.coded).getBytes());
      result = verifier.verify(signature);
    } catch (NoSuchAlgorithmException e) {
      throw e;
    }

    String exp = payload.getClaim("exp");
    if (exp != null && !exp.isEmpty()) {
      long l = Long.parseLong(exp);
      if (l < System.currentTimeMillis() / 1000) {
        throw new OICException("Token expired." + exp);
      }
    }

    return result;
  }


  public class JwtHeader {

    private String coded; // Base64url coded JSON String.
    private JsonNode node; // JSON node.

    JwtHeader(String token) throws IOException {

      coded = token;
      ObjectMapper mapper = new ObjectMapper();

      try {
        node = mapper.readTree(OICUtil.b64UrlDecode(coded));
      } catch (IOException e) {
        throw e;
      }

    }

    public String getParameter(String key) {
      return node.get(key).asText();
    }

  }

  public class JwtPayload {

    private String coded; // Base64url coded JSON String.
    private JsonNode node; // JSON node.

    JwtPayload(String token) throws IOException {

      coded = token;
      ObjectMapper mapper = new ObjectMapper();

      try {
        node = mapper.readTree(OICUtil.b64UrlDecode(coded));
      } catch (IOException e) {
        throw e;
      }

    }

    public String getClaim(String key) {
      return node.get(key).asText();
    }
  }

}
