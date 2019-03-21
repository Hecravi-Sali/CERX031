package me.IAGO.Media;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import me.IAGO.Item.Label;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.StoreDate;
import me.IAGO.Item.StoreDate_Intfc;

public class Media implements Media_Intfc {
	private class MediaDataBuffer {
	    private StringBuffer [] _mediadata = new StringBuffer[2];
	    private int _bufferselect = 0;
	    public Date startdate = new Date();
	    
	    public void Savein(Byte data) {
	        _mediadata[ChangeBuffer(false)].append(data.toString());
	    }
	    
	    public Byte Removeout() {
	        return new Byte(_mediadata[ChangeBuffer(true)].toString());
	    }
	    
	    private synchronized int ChangeBuffer(boolean bufferswitch) {
	        if(bufferswitch) {
	            _bufferselect = 1 - _bufferselect;
	        }
	        return _bufferselect;
	    }
	}
	
	private Map<String, MediaDataWatcher> _list = new HashMap<String, MediaDataWatcher>();
    private ScheduledExecutorService _timer = Executors.newScheduledThreadPool(2);
	private MediaDataBuffer _mediadatabuffer;
	private FileSystem_Intfc _filesystem;
	private String _username;
	
	public Media(FileSystem_Intfc filesystem, String username) {
	    _filesystem = filesystem;
	    _mediadatabuffer = null;
	    _username = username;
	    StartMediaForward("MainSavein", new MediaDataWatcher() {
            @Override
            public boolean Push(Byte data) {
                if(_mediadatabuffer != null) {
                    _mediadatabuffer.Savein(data);
                    return true;
                }
                return false;
            }
	    });
	}
	
	public String OwnerName() {
	    return _username;
	}
	
    @Override
    public boolean Config(JSONObject conf)
            throws JSONException {
        if(conf.getBoolean(Label.FIELD_MEDIASAVE.toString())) {
            int timelimit = conf.getInt(Label.FIELD_MEDIATIMELIMIT.toString());
            _timer.scheduleAtFixedRate(
                    () -> {
                        Date enddate = new Date();
                        _filesystem.SaveUserFile(
                                _username,
                                _mediadatabuffer.Removeout(), 
                                new StoreDate(_mediadatabuffer.startdate, enddate));
                        _mediadatabuffer.startdate = enddate; },
                    0, timelimit, TimeUnit.SECONDS);
        }
        else {
            if(_timer.isShutdown() == false) {
                try {
                    if(_timer.awaitTermination(1, TimeUnit.SECONDS)) {
                        Byte remainmediadata = _mediadatabuffer.Removeout();
                        if(remainmediadata.toString().isEmpty() == false) {
                            _filesystem.SaveUserFile(
                                    _username,
                                    remainmediadata, 
                                    new StoreDate(_mediadatabuffer.startdate, new Date()));
                        } 
                    }
                    else {
                     // TODO 日志
                    }
                } catch (InterruptedException e) {
                    // TODO 日志
                }
            }
            _mediadatabuffer = null;
        }
        return true;
    }
    
	@Override
	public JSONObject GetMediaRecordDate()
	        throws JSONException {
		JSONObject json = new JSONObject();
		List<StoreDate_Intfc> index = _filesystem.GetUserFileIndex(_username);
        json.put(Label.FIELD_MEDIAINDEXNUM.toString(), index.size());
        for(int key = 0; key < index.size(); key++) {
            json.put(String.valueOf(key), index.get(key).toString());
        }
		return json;
	}

	@Override
	public boolean DelectMediaDate(JSONObject date)
	        throws ParseException, JSONException {
	    for(int key = 0; key < date.getInt(Label.FIELD_MEDIAINDEXNUM.toString()); key++) {
            _filesystem.DeleteUserFile(
                    _username,
                    new StoreDate(date.getJSONObject(String.valueOf(key))));
        }
		return true;
	}

	@Override
	public Byte PullMediaData(JSONObject date)
	        throws ParseException, JSONException {
	    StringBuffer mergemediadata = new StringBuffer();
        for(int key = 0; key < date.getInt(Label.FIELD_MEDIAINDEXNUM.toString()); key++) {
            mergemediadata.append(_filesystem.GetUserFile(
                    _username,
                    new StoreDate(date.getJSONObject(String.valueOf(key)))).toString());
        }  
        return new Byte(mergemediadata.toString());
	}

	@Override
	public void PushMediaData(Byte data) {
	    for (Map.Entry<String, MediaDataWatcher> entry : _list.entrySet()) {
	        entry.getValue().Push(data);
	    }
	}

	@Override
	public boolean StartMediaForward(String portid, MediaDataWatcher watcher) {
        _list.put(portid, watcher);
        return true;
	}

	@Override
	public boolean StopMediaForward(String portid) {    
        return _list.remove(portid) != null;
	}
}
