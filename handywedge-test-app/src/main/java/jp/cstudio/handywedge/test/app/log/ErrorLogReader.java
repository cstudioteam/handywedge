package jp.cstudio.handywedge.test.app.log;

import java.util.Set;

import jakarta.websocket.Session;

public class ErrorLogReader extends LogReader {

  public ErrorLogReader(int myInterval) {
    super("/opt/tomcat/logs/handywedge_error.log", myInterval);
  }

  @Override
  public Set<Session> getSession() {

    return ErrorLogView.sessions;
  }

}
