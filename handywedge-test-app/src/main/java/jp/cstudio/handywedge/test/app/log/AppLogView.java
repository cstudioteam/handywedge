package jp.cstudio.handywedge.test.app.log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/app_log")
public class AppLogView {

  static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

  // @Inject
  // private FWLogger logger;

  @OnMessage
  public void onMessage(String message) {

  }

  @OnOpen
  public void open(Session sess) {

    sessions.add(sess);
  }

  @OnClose
  public void close(Session sess) {

    sessions.remove(sess);
  }
}
