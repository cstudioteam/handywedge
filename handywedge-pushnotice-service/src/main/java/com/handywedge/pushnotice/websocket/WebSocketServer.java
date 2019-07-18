package com.handywedge.pushnotice.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.pushnotice.util.Authorization;


// @ServerEndpoint(value = "/Ws/pushnotice/{token}", decoders = {MessageDecoder.class} )
@ServerEndpoint(value = "/Ws/pushnotice/{token}")
public class WebSocketServer {

	private static Logger logger = LogManager.getLogger("PushService");
	private SessionManager sessionManager = SessionManager.getSessionManager();

	
	@OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {

    	String userId = Authorization.getUserId(token);
    	if (userId != null) {
    		sessionManager.add(userId, session);
            logger.debug("onOpen sessionId={} userId={}", session.getId(), userId);
    	} else {
    		try {
				session.close();
			} catch (IOException e) {
			}
    	}
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    	logger.debug("onMessage ID={}, ", session.getId());
    }

    @OnError
    public void onError(Throwable th, Session session) {

    	logger.warn("onError ID=" + session.getId() , th);
    }

    @OnClose
    public void onClose(Session session) {

    	logger.debug("onClose ID={}", session.getId());
    	sessionManager.remove(session);
    }
}
