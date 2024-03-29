package jp.cstudio.handywedge.test.app.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("util")
public class TestAppUtil {

  public String getGlobalIP() throws IOException {
    URL ipCheck = new URL("http://checkip.amazonaws.com");
    try (BufferedReader in = new BufferedReader(new InputStreamReader(ipCheck.openStream()));) {
      String ip = in.readLine();
      return ip;
    }
  }

}
