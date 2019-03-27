package me.IAGO.Communication;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import me.IAGO.Core.Core;
import me.IAGO.Core.Core_Intfc;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.FileSystem_NoDatabase;
import me.IAGO.Item.Label;
import me.IAGO.Log.Log;
import me.IAGO.Log.Log_Intfc;

@Component
@ServerEndpoint(value = "/websocket")
public class WebsocketPort {
    private static Log_Intfc _logger = new Log();
    private static FileSystem_Intfc _filesystem = null;
	private static ConcurrentHashMap<String, WebsocketPort> _collectionmap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Core_Intfc> _coremap = new ConcurrentHashMap<>();
	
	private Session _session;
	private String _uuid;
	private String _corename = null;
	
	@OnOpen
	public void OnOpen(Session session) {
	    _uuid = UUID.randomUUID().toString();
	    _collectionmap.put(_uuid, this);
		_session = session;
	}
	
	@OnMessage
	public void OnMessage(String message) {
	    if(_filesystem == null) {
	        _filesystem = new FileSystem_NoDatabase(_logger);
	    }
	    try {
	        JSONObject json = new JSONObject(message);
	        switch(Label.fromString(json.getString(Label.PORT_CONNECTCORE.toString()))) {
	        case PORT_CONNECTCORE_OPEN :{
	            _corename = json.getString(Label.FIELD_COREOWNERNAME.toString());
	            Core_Intfc core = _coremap.get(_corename);
	            if(core == null) {
	                if(_filesystem.GetUserPassword(_corename) != null) {
	                    _coremap.put(_corename, new Core(_corename, _filesystem, _logger));
	                    core = _coremap.get(_corename);
	                }
	                else {
	                    _logger.Error("The connected core does not exist");
	                }
	            }
	            if(core != null) {
	                core.OnOpen(
	                        _uuid, 
	                        (String data) -> {
	                            boolean re = false;
	                            try {
	                                _collectionmap.get(_uuid).SendMessage(data);
	                                re = true;
	                            } catch (IOException e) {
	                                _logger.Error("An IO Error occurred during websocket send data");
	                            }
	                            return re;
	                        });
	                _collectionmap.get(_uuid).SendMessage("Core connect successful");
	            }
	            break;
	        }
	        case PORT_CONNECTCORE_STABLE : {
	            if(_corename != null) {
	                Core_Intfc core = _coremap.get(_corename);
	                if(core != null) {
	                    _collectionmap.get(_uuid).SendMessage(core.OnMessage(_uuid, new JSONObject(json.getString(Label.FIELD_COREMESSAGE.toString()))).toString());
	                }
	            }
	            break;
	        }
	        case PORT_CONNECTCORE_CLOSE : {
	            if(_corename != null) {
	                Core_Intfc core = _coremap.get(_corename);
	                if(core != null) {
	                    core.OnClose(_uuid);
	                }
	                _corename = null;
	            }
	            break;
	        }
	        default:
                _logger.Error("Service forwarding layer has an unresolved service request");
	            break;
	        }
	    } catch(JSONException e) {
	        _logger.Error("An error occurred in JSON parsing");
	    } catch(IOException e) {
	        _logger.Error("Port stable stage get send message error");
	    }
	}

	@OnClose
	public void OnClose(Session session) {
	    _coremap.forEach(
	            (String uuid, Core_Intfc allcore) -> {
	                allcore.OnClose(_uuid);
	            });
	    _collectionmap.remove(_uuid);
	}

	@OnError
	public void OnError(Session session, Throwable error) {
        _logger.Error("Websocket Get an error : " + error);
	}
	
	private synchronized void SendMessage(String message) throws IOException {
	    _session.getBasicRemote().sendText(message);
	}
}
