package com.handywedge.pushnotice.rest.api;


import java.util.List;

import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.pushnotice.model.LoginUsers;
import com.handywedge.pushnotice.model.PushMessage;
import com.handywedge.pushnotice.util.Property;
import com.handywedge.pushnotice.websocket.SessionManager;

@Path("pushservice")
public class PushResource {

  protected static final String ACCESS_KEY = Property.get("ACCESS_KEY");
  protected static final Logger logger = LogManager.getLogger("PushService");

  protected SessionManager sessionManager = SessionManager.getSessionManager();

  @POST
  @Path("/notice")
  @Consumes(MediaType.APPLICATION_JSON)
  public void notice(PushMessage message) {

    long start = System.currentTimeMillis();
    if (!ACCESS_KEY.equals(message.getAccesskey())) {
      logger.trace("ACCESS KEY=[{}]", message.getAccesskey());
      throw new WebApplicationException("アクセスキーが正しくありません。", Response.Status.FORBIDDEN);
    }

    String userId = message.getUserId();

    logger.debug("notice <<Start>> userId={} ({} byte)", userId, message.getText().length());
    logger.trace("notice userId={} text=[{}]", userId, message.getText());

    try {
      if (userId == null || userId.trim().length() == 0) {
        throw new WebApplicationException("送信先が正しくありません。", Response.Status.BAD_REQUEST);
      }

      List<Session> userSessions = sessionManager.get(userId);
      for (Session session : userSessions) {
        if (session == null) {
          throw new WebApplicationException("ユーザ [" + userId + "] はログインしていません。",
              Response.Status.BAD_REQUEST);
        }

        if (!session.isOpen()) {
          throw new WebApplicationException("セッションがクローズされています。", Response.Status.BAD_REQUEST);
        }

        synchronized (session) {
          String json = "{ \"NoticeCode\": 0, \"Message\": " + message.getText() + "}";
          session.getAsyncRemote().sendText(json);
        }
      }
    } catch (Exception e) {
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } finally {
      logger.debug("notice <<End>> userId={} ({} ms)", userId,
          (System.currentTimeMillis() - start));
    }

    return;
  }

  @GET
  @Path("/loginusers")
  @Produces(MediaType.APPLICATION_JSON)
  public LoginUsers getLoginUsers(@QueryParam("accessKey") String accessKey) {

    if (!ACCESS_KEY.equals(accessKey)) {
      throw new WebApplicationException("アクセスキーが正しくありません。", Response.Status.FORBIDDEN);
    }

    LoginUsers users = new LoginUsers();
    logger.debug("getLoginUsers <<Start>>");

    long start = System.currentTimeMillis();
    List<String> list = sessionManager.getLoginUsers();
    users.setLoginUsers(list);

    logger.debug("getLoginUsers <<End>> ({} ms)", (System.currentTimeMillis() - start));

    return users;
  }
}
