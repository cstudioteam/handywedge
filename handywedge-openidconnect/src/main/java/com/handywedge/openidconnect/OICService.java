package com.handywedge.openidconnect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.openidconnect.entity.OICJwks;
import com.handywedge.openidconnect.entity.OICJwt;
import com.handywedge.openidconnect.entity.OICKey;
import com.handywedge.openidconnect.entity.OICProviderMetadata;
import com.handywedge.openidconnect.entity.OICUserInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * OpenID Connect サービス基底クラス
 *
 * @author takeuchi
 */
public abstract class OICService {

  private static final String SUPPORTED_RESPONSE_TYPE = "id_token";

  protected static Logger logger = LoggerFactory.getLogger(OICService.class);
  protected static String cachedKid = null;
  protected static PublicKey cachedPubKey = null;

  protected OICProviderMetadata config;

  /**
   * プロパティファイルからサービスクラスのインスタンスを生成する。
   *
   * @return OICServiceクラス
   * @throws OICException
   */
  public static OICService getInstance() throws OICException {

    logger.info("getInstance <start>");

    OICService service = null;
    String className = OICProperties.get(OICConst.SERVICE_CLASS);

    try {
      if (className.isEmpty()) {
        throw new OICException("Property [" + OICConst.SERVICE_CLASS + "] is required.");
      }

      service = (OICService) Class.forName(className).newInstance();
      logger.info("Service class=" + service);
    } catch (OICException e) {
      logger.error("Service class instantion error.", e);
      throw e;
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      logger.error("Service class instantion error.", e);
      throw new OICException(e);
    } finally {
      logger.info("getInstance <end>");
    }

    return service;
  }

  public void login(HttpServletRequest request, HttpServletResponse response) throws OICException {

    try {
      logger.info("login <start>");
      // 戻り先のURLをクエリパラメータから取得。
      String returnURL = null;
      String str = request.getParameter("url");
      if (str != null) {
        returnURL = URLDecoder.decode(str, StandardCharsets.UTF_8.toString());
      }

      // リクエスト毎にユニークな番号を持つstateを生成
      OICState state = OICStateManager.createState(returnURL);

      // OpenId プロバイダの認証エンドポイントへリダイレクト
      String url = config.getAuthorization_endpoint() + getAuthorizationQuery(state);
      response.setStatus(HttpServletResponse.SC_FOUND);
      response.setHeader("Location", url);
      response.setHeader("Access-Control-Allow-Origin", "*");

    } catch (UnsupportedEncodingException e) {
      logger.error("login method error.", e);
    } finally {
      logger.info("login <end>");
    }
  }

  public void auth(HttpServletRequest request, HttpServletResponse response) throws OICException {

    logger.info("auth <start>");

    try {
      String error = request.getParameter(OICConst.TAG_NAME_ERROR);
      if (error != null) {
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\n \"return_cd\": -9000,\n \"return_msg\": \"[" + error
            + "] " + request.getParameter(OICConst.TAG_NAME_ERROR_DESCRIPTION) + "\"\n}");
        return;
      }

      String stateId = request.getParameter("state");
      String id_token = request.getParameter("id_token");
      logger.trace("auth stateId=" + stateId);
      logger.trace("auth id_token=" + id_token);

      OICState state = OICStateManager.getState(stateId);
      if (state == null) {
        throw new OICException("State not found. [" + stateId + "]");
      }

      OICJwt jwt = new OICJwt(id_token);
      PublicKey pubKey = getPublicKey(jwt.getHeader().getParameter("kid"));
      if (!jwt.validate(pubKey, state.getNonce())) {
        throw new OICException("Invalid id_token. [" + id_token + "]");
      }
      OICUserInfo userInfo = getUserInfo(jwt);
      String token = handywedgeLogin(userInfo);
      String userId = userInfo.getId();

      String url = state.getReturnUrl();
      if (url == null || url.isEmpty()) {
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\n \"return_cd\": 0,\n \"token\": \"" + token + "\"\n}");
      } else {
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", url + "?" + "token=" + token + "&id=" + userId);
      }

      OICStateManager.remove(stateId);
    } catch (InvalidKeyException | SignatureException | CertificateException
        | InvalidKeySpecException | IOException | NoSuchAlgorithmException e) {
      logger.error(e.getMessage(), e);
    } finally {
      logger.info("auth <end>");
    }
  }

  protected String getAuthorizationQuery(OICState state) throws OICException {

    StringBuilder sb = new StringBuilder();
    String responseType = OICProperties.get(OICConst.RESPONSE_TYPE);

    if (!SUPPORTED_RESPONSE_TYPE.equals(responseType)) {
    }
    switch (responseType) {
      case "id_token":
        String cliendId;
        if (System.getenv(OICConst.ENV_CLIENT_ID) != null) {
          cliendId = System.getenv(OICConst.ENV_CLIENT_ID);
        } else {
          cliendId = OICProperties.get(OICConst.CLIENT_ID);
        }
        sb.append("?").append("response_type=").append(responseType).append("&")
            .append("response_mode=").append(OICProperties.get(OICConst.RESPONSE_MODE)).append("&")
            .append("redirect_uri=")
            .append(getRedirectURL(OICProperties.get(OICConst.RP_AUTH_PATH))).append("&")
            .append("scope=").append(OICProperties.get(OICConst.SCOPE)).append("&").append("state=")
            .append(state.getId()).append("&").append("nonce=").append(state.getNonce()).append("&")
            .append("client_id=").append(cliendId);
        break;
      default:
        throw new OICException("Unsupport response_type. " + responseType);
    }

    logger.trace("getAuthorizationQuery:" + sb.toString());

    return sb.toString();
  }

  protected PublicKey getPublicKey(String kid)
      throws InvalidKeySpecException, CertificateException, OICException, NoSuchAlgorithmException {

    if (cachedKid != null && cachedKid.equals(kid)) {
      logger.debug("getPublicKey cache matched. kid=" + kid);
      return cachedPubKey;
    }

    PublicKey pubKey;

    OICJwks jwks = invokeGetAPI(config.getJwks_uri(), null, null, OICJwks.class);

    OICKey key = null;
    for (OICKey k : jwks.getKeys()) {
      if (k.getKid().equals(kid)) {
        key = k;
        break;
      }
    }

    if (key == null) {
      throw new OICException("Public key not matched in Jwks. kid=" + kid);
    }

    if (key.getX5c() == null || key.getX5c().length == 0) {
      try {
        KeyFactory kf = KeyFactory.getInstance(key.getKty());
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getN()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getE()));
        pubKey = kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        throw e;
      }
    } else {
      CertificateFactory factory = CertificateFactory.getInstance("X.509");
      String pk = "-----BEGIN CERTIFICATE-----\n" + key.getX5c()[0] + "\n-----END CERTIFICATE-----";
      X509Certificate cert =
          (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(pk.getBytes()));
      pubKey = (RSAPublicKey) cert.getPublicKey();
    }

    cachedKid = kid;
    cachedPubKey = pubKey;
    logger.debug("getPublicKey kid=" + kid + ", key=" + pubKey);

    return pubKey;
  }

  protected String getRedirectURL(String path) {

    String url = getBaseURL() + path;
    try {
      url = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      logger.error("URL Encode error.", e); // あり得ないのでログだけ出力。
    }

    return url;
  }

  protected String getBaseURL() {
    if (System.getenv(OICConst.ENV_RELYING_PARTY_URL) != null) {
      return System.getenv(OICConst.ENV_RELYING_PARTY_URL);
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(OICProperties.get(OICConst.RP_SCHEMA)).append("://")
          .append(OICProperties.get(OICConst.RP_HOST))
          .append(OICProperties.get(OICConst.RP_BASE_PATH));

      return sb.toString();
    }
  }

  protected String handywedgeLogin(OICUserInfo userInfo)
      throws OICException, IOException, InvalidKeyException, SignatureException,
      CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {

    String token = null;

    try {
      Map map = new HashMap();
      map.put("uid", userInfo.getId());
      map.put("name", userInfo.getName());
      String json = (String) invokePostAPI(OICProperties.get(OICConst.HW_SSO_LOGIN_ENDPOINT), null,
          map, String.class);

      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(json);
      token = node.get("token").asText();
    } catch (IOException e) {
      throw e;
    }

    return token;
  }


  protected synchronized void loadConfig() throws OICException {

    logger.info("loadConfig <start>");

    if (config == null) {
      config = invokeGetAPI(getConfigURL(), null, null, getConfigClass());
      logger.trace("Authorization_endpoint : " + config.getAuthorization_endpoint());
      if (logger.isTraceEnabled()) {
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          logger.trace(objectMapper.writeValueAsString(config));
        } catch (IOException e) {
        }
      }
    }

    logger.info("loadConfig <end>");
  }

  protected String getConfigURL() {
    if (System.getenv(OICConst.ENV_AZURE_TENANT_ID) != null) {
      String url = OICProperties.get(OICConst.OP_METADATA_DOC_URL);
      return String.format(url, System.getenv(OICConst.ENV_AZURE_TENANT_ID));
    } else {
      return OICProperties.get(OICConst.OP_METADATA_DOC_URL);
    }
  }

  private <T> T invokeGetAPI(String url, String path, String query, Class clazz)
      throws OICException {

    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    Client client = Client.create(clientConfig);

    WebResource resource = client.resource(url);
    if (path != null)
      resource.path(path);
    logger.debug("invokeGetAPI URL call : " + url);
    ClientResponse response = resource.get(ClientResponse.class);
    logger.debug("invokeGetAPI URL return : " + response.getStatus());

    if (response.getStatus() != 200) {
      throw new OICException(
          "API responce failed : " + response.getStatus() + " " + resource.getURI());
    }

    return (T) response.getEntity(clazz);
  }


  private <T> T invokePostAPI(String url, String path, Map<String, String> param, Class clazz)
      throws OICException {

    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    Client client = Client.create(clientConfig);

    WebResource resource = client.resource(OICProperties.get(OICConst.HW_SSO_LOGIN_ENDPOINT));
    if (path != null)
      resource.path(path);
    Iterator<String> i = param.keySet().iterator();
    while (i.hasNext()) {
      String key = i.next();
      resource = resource.queryParam(key, param.get(key));
      logger.trace("Token endpoint Parameter " + key + ":" + param.get(key));
    }
    logger.info("invokePostAPI URL call : " + url);
    ClientResponse response = resource.post(ClientResponse.class);
    logger.info("invokePostAPI URL return : " + response.getStatus());

    if (response.getStatus() != 200) {
      throw new OICException(
          "API responce failed : " + response.getStatus() + " " + resource.getURI());
    }

    return (T) response.getEntity(clazz);
  }

  protected abstract OICUserInfo getUserInfo(OICJwt jwt) throws OICException;

  protected abstract Class getConfigClass();
}
