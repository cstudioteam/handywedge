package com.handywedge.pushnotice.websocket;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Callable;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.pushnotice.util.Property;


public class PingSender  implements Callable<String> {

	protected static final int PING_INTERVAL_SEC = Property.getInt("PING_INTERVAL_SEC") * 1000;
	protected static final Logger logger = LogManager.getLogger("PushService");

	protected static boolean running =  (PING_INTERVAL_SEC != 0);

	@Override
	public String call() throws Exception {

		logger.info("PingSender Interval {} sec.",  PING_INTERVAL_SEC);
		
		while(running) {
			SessionManager manager = SessionManager.getSessionManager();
			List<String> list = manager.getLoginUsers();
			for(String userId: list) {
				Session session = manager.get(userId);
				try {
					if (session.isOpen()) {
						synchronized (session) {
							session.getBasicRemote().sendPing(ByteBuffer.wrap("ping".getBytes()));
							logger.trace("ping send. userId={} session={}", userId, session.getId());
						}
					}
				} catch(Exception e) {
				}
			}
			Thread.sleep(PING_INTERVAL_SEC);
		}
		
		return null;
	}
}
