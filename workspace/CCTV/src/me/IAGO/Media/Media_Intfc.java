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
	public boolean PullMediaData(JSONObject date, MediaDataWatcher watcher) throws ParseException, JSONException;
	
	public boolean PushMediaData(Byte data);
	public boolean StartMediaForward(MediaDataWatcher watcher);
	public boolean StopMediaForward(MediaDataWatcher watcher);
}
