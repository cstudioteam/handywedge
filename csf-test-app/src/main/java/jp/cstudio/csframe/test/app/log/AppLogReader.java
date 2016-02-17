package jp.cstudio.csframe.test.app.log;

import java.util.Set;

import javax.websocket.Session;

public class AppLogReader extends LogReader {

  public AppLogReader(int myInterval) {
    super("/opt/tomcat/logs/csf-test-app.log", myInterval);
  }

  @Override
  public Set<Session> getSession() {

    return AppLogView.sessions;
  }

}
