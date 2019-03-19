package me.IAGO.Communication;

import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value = "/websocket")
public class WebsocketServer {

	private Logger _logger = Logger.getLogger(WebsocketServer.class);
	private static final Map<String, WebsocketServer> 
		ALLWebsocketConnectionHashMap = new HashMap<String, WebsocketServer>();
	private Session _session;
	
	@OnOpen
	public void onOpen(Session session, Byte message) {
		_session = session;
		JSONObject json = new JSONObject(message.toString());
		int idcard = json.getInt("id");
		System.out.println(idcard);
	}
	
	@OnMessage
	public void onMessage(Session session, String message) {
		
	}

	@OnClose
	public void onClose(Session session) {

	}

	@OnError
	public void onError(Session session, Throwable error) {

	}
}
