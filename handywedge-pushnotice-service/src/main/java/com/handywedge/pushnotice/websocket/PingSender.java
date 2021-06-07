package com.handywedge.pushnotice.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.pushnotice.util.Property;

import jakarta.websocket.Session;


public class PingSender implements Callable<String> {

  protected static final int PING_INTERVAL_SEC = Property.getInt("PING_INTERVAL_SEC") * 1000;
  protected static final Logger logger = LogManager.getLogger("PushService");

  protected static boolean running = (PING_INTERVAL_SEC != 0);

  @Override
  public String call() throws Exception {

    logger.info("PingSender Interval {} sec.", PING_INTERVAL_SEC);

    while (running) {

      SessionManager manager = SessionManager.getSessionManager();
      List<String> list = manager.getLoginUsers();
      for (String userId : list) {
        List<Session> userSessions = manager.get(userId);
        for (Session session : userSessions) {
          try {
            if (session.isOpen()) {
              synchronized (session) {
                session.getBasicRemote().sendPing(ByteBuffer.wrap("ping".getBytes()));
                logger.trace("websocket ping send sessiondId={}, userId={}", session.getId(),
                    userId);
                // pong応答のタイムアウト処理
                setTimeout(() -> {
                  Instant lastPong = manager.getPong(session.getId());
                  if (lastPong == null
                      || Instant.now().getEpochSecond() - lastPong.getEpochSecond() > 10) {
                    try {
                      logger.debug("websocket ping/pong timeout. sessionId={}", session.getId());
                      if (session.isOpen()) {
                        session.close();
                      }
                    } catch (IOException e) {
                      logger.error(e);
                    }
                  } else {
                    logger.trace("session healthy sessionId={}, lastPong={}", session.getId(),
                        lastPong);
                  }
                }, 10000);
              }
            }
          } catch (Exception e) {
          }
        }
      }
      Thread.sleep(PING_INTERVAL_SEC);
    }

    return null;
  }

  public static void setTimeout(Runnable runnable, int delay) {
    new Thread(() -> {
      try {
        Thread.sleep(delay);
        runnable.run();
      } catch (Exception e) {
      }
    }).start();
  }

}
