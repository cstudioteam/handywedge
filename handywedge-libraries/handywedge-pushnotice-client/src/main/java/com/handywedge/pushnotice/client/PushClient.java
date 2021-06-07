package com.handywedge.pushnotice.client;

import java.io.IOException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handywedge.pushnotice.PushServiceException;
import com.handywedge.pushnotice.model.LoginUsers;
import com.handywedge.pushnotice.model.PushMessage;

/**
 * Handywedgeプッシュ通知サービスのクライアント
 * 
 * @author takeuchi
 *
 */
public class PushClient {

  protected static final String USER_WS_PATH = "/hw-pushnotice/webapi/pushservice/loginusers";
  protected static final String PUSH_WS_PATH = "/hw-pushnotice/webapi/pushservice/notice";

  protected static final Logger logger = LogManager.getLogger("PushClient");


  public static LoginUsers getLoginUsers(String target, String accessKey)
      throws PushServiceException {

    LoginUsers users = null;

    long start = System.currentTimeMillis();
    logger.info("getLoginUsers <<Start>>");
    logger.debug("getLoginUsers target=" + target);

    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target(target);
    webTarget = webTarget.path(USER_WS_PATH);
    webTarget = webTarget.queryParam("accessKey", accessKey);
    Invocation.Builder invocationBuilder = webTarget.request();
    String json = invocationBuilder.get(String.class);
    logger.trace("getLoginUsers JSON=" + json);

    ObjectMapper mapper = new ObjectMapper();
    try {
      users = mapper.readValue(json, LoginUsers.class);
    } catch (IOException e) {
      logger.error("JSONへの変換エラーです。", e);
      throw new PushServiceException(e);
    }
    logger.info("getLoginUsers <<End>> (" + (System.currentTimeMillis() - start) + "{} ms)");

    return users;
  }

  public static void sendMessage(String target, String accessKey, String userId, String text)
      throws PushServiceException {

    long start = System.currentTimeMillis();
    logger.info("sendMessage <<Start>>");
    logger.debug("sendMessage target=" + target + " userId=" + userId);

    PushMessage message = new PushMessage();
    message.setAccesskey(accessKey);
    message.setUserId(userId);
    message.setText(text);

    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target(target);
    webTarget = webTarget.path(PUSH_WS_PATH);
    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
    Response response = invocationBuilder.post(Entity.entity(message, MediaType.APPLICATION_JSON));

    int status = response.getStatus();
    logger.info("sendMessage <<End>> status=" + status + " (" + (System.currentTimeMillis() - start)
        + " ms)");

    if (status >= 400) {
      throw new PushServiceException("プッシュ通知でエラーが返されました。 status=" + status);
    }
  }
}
