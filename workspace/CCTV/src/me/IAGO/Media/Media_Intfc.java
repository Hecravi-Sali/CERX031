package me.IAGO.Media;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

public interface Media_Intfc {
	public interface MediaDataWatcher {
		boolean Push(Byte data);
	}
	public boolean Config(JSONObject conf) throws JSONException;
	
	public JSONObject GetMediaRecordDate() throws JSONException;
	public boolean DelectMediaDate(JSONObject date) throws ParseException, JSONException;
	public Byte PullMediaData(JSONObject date) throws ParseException, JSONException;
	
	public void PushMediaData(Byte data);
	public boolean StartMediaForward(String portid, MediaDataWatcher watcher);
	public boolean StopMediaForward(String portid);
}
