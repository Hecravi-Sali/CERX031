package me.IAGO.Communication;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import me.IAGO.Core.Core_Intfc;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Log.Log;
import me.IAGO.Log.Log_Intfc;

@Component
@ServerEndpoint(value = "/websocket")
public class WebsocketServer {
    private static Log_Intfc _logger = new Log();
    private static FileSystem_Intfc filesystem;
	private static ConcurrentHashMap<String, WebsocketServer> _collectionmap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Core_Intfc> _coremap = new ConcurrentHashMap<>();
	private Session _session;
	private String _uuid;
	
	@OnOpen
	public void onOpen(Session session) {
	    _uuid = UUID.randomUUID().toString();
	    _collectionmap.put(_uuid, this);
		_session = session;
	}
	
	@OnMessage
	public void onMessage(Session session, String message) {
		
	}

	@OnClose
	public void onClose(Session session) {
	    _collectionmap.remove(_uuid);
	}

	@OnError
	public void onError(Session session, Throwable error) {

	}
}
