package jp.cstudio.csframe.test.app.log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/error_log")
public class ErrorLogView {

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
