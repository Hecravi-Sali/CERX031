package me.IAGO.Core;
import org.json.JSONObject;

import me.IAGO.Media.Media_Intfc;

public interface Core_Intfc {
    public void OnOpen(String portid, Media_Intfc.MediaDataWatcher sendfunction);
    public JSONObject OnMessage(String portid, JSONObject message);
    public void OnClose(String portid);
    public void OnError(String portid, Throwable error);
}
