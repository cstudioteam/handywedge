package com.handywedge.pushnotice.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SessionManager {

	private static Logger logger = LogManager.getLogger("PushService");
	private static SessionManager instance = new SessionManager(); 

	private Map<String, Session> sessions = new HashMap<>();
	private Map<String, String> index = new HashMap<>();

	
	static public SessionManager getSessionManager() {
		
		return instance;
	}

	
	public void add(String userId, Session session) {

        logger.trace("Session add userId={} sessionId={} ", userId, session.getId());

        remove(userId);
		sessions.put(userId, session);
		index.put(session.getId(), userId);

		logger.trace("Session add sessions={} index={} ", sessions.size(), index.size());
	}
	
	public Session get(String userId) {

		return sessions.get(userId);
	}

	public void remove(String userId) {
		
		Session session = get(userId);
		
		if (session != null) {
			try {
				String json = "{ \"NoticeCode\": 8, \"Message\": { \"Reason\": \"同一アカウントでログインされました\"}}";
				session.getAsyncRemote().sendText(json);
				session.close();
			} catch (IOException e) {
			}
			sessions.remove(userId);
			index.remove(session.getId());
		}
		logger.trace("Session remove sessions={} index={} ", sessions.size(), index.size());
	}
	
	public void remove(Session session) {
		
		String userId = index.get(session.getId());
		
		if (userId != null) {
			try {
				session.close();
			} catch (IOException e) {
			}
			sessions.remove(userId);
			index.remove(session.getId());
		}
		logger.trace("Session remove sessions={} index={} ", sessions.size(), index.size());
	}
	
	public List<String> getLoginUsers() {
		
		List<String> list = new ArrayList<>();
		
		Iterator<String> i = sessions.keySet().iterator();
		while(i.hasNext()) {
			String s = i.next();
			list.add(s);
		}
		
		return list;
	}
}
