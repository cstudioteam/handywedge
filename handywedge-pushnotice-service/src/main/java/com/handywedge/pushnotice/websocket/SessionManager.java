package com.handywedge.pushnotice.websocket;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.pushnotice.util.Property;


public class SessionManager {

  private static Logger logger = LogManager.getLogger("PushService");
  private static SessionManager instance = new SessionManager();

  private Map<String, List<Session>> sessions = new HashMap<>();
  private Map<String, String> index = new HashMap<>();
  private Map<String, Instant> pong = new HashMap<>();


  static public SessionManager getSessionManager() {

    return instance;
  }


  public void add(String userId, Session session) {

    logger.trace("Session add userId={} sessionId={} ", userId, session.getId());
    boolean multiSession = Property.getBool("MULTI_SESSION");
    if (!multiSession) {
      remove(userId);
    }
    List<Session> userSessions = sessions.get(userId);
    if (userSessions == null) {
      userSessions = new ArrayList<>();
      sessions.put(userId, userSessions);
    }
    sessions.get(userId).add(session);
    index.put(session.getId(), userId);

    logger.trace("Session add sessions={} index={} ", sessions.size(), index.size());
  }

  public List<Session> get(String userId) {

    return sessions.get(userId);
  }

  public void remove(String userId) {

    List<Session> userSessions = get(userId);

    if (userSessions != null) {
      try {
        String json = "{ \"NoticeCode\": 8, \"Message\": { \"Reason\": \"同一アカウントでログインされました\"}}";
        for (Session session : userSessions) {
          session.getAsyncRemote().sendText(json);
          index.remove(session.getId());
          session.close();
        }
      } catch (IOException e) {
      }
      sessions.remove(userId);
    }
    logger.trace("Session remove sessions={} index={} ", sessions.size(), index.size());
  }

  public void remove(Session session) {

    pong.remove(session.getId());
    String userId = index.get(session.getId());

    if (userId != null) {
      try {
        session.close();
      } catch (IOException e) {
      }
      List<Session> userSession = sessions.get(userId);
      boolean remove = userSession.remove(session);
      logger.debug("User Session remove {}.", remove);
      if (userSession.isEmpty()) {
        sessions.remove(userId);
      }
      index.remove(session.getId());
    }
    logger.trace("Session remove sessions={} index={} ", sessions.size(), index.size());
  }

  public List<String> getLoginUsers() {

    List<String> list = new ArrayList<>();

    Iterator<String> i = sessions.keySet().iterator();
    while (i.hasNext()) {
      String s = i.next();
      list.add(s);
    }

    return list;
  }

  public void setPong(String sessionId) {
    pong.put(sessionId, Instant.now());
  }

  public Instant getPong(String sessionId) {
    return pong.get(sessionId);
  }
}
