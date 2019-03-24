package me.IAGO.Media;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.druid.util.Base64;

import me.IAGO.Item.Label;
import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.StoreDate;
import me.IAGO.Item.StoreDate_Intfc;

public class Media implements Media_Intfc {
	private class ConnectBase64 {
	    private StringBuffer _mediadata = new StringBuffer();
	    public Date startdate = new Date();    

	    public void Savein(String data) {
	        synchronized(this) {
	            _mediadata.append(new String(Base64.base64ToByteArray(data)));
	        }
	    }
	    public String Removeout() {
	        String re;
	        synchronized(this) {
	            re = Base64.byteArrayToBase64(_mediadata.toString().getBytes());
	            _mediadata = new StringBuffer();
	        }
	        return re;
	    }
	}
	
	private Map<String, MediaDataWatcher> _list = new HashMap<String, MediaDataWatcher>();
    private ScheduledExecutorService _timer = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> runingthread = null;
	private ConnectBase64 _connectbase64 = null;
	private FileSystem_Intfc _filesystem;
	private String _username;
	
	public Media(FileSystem_Intfc filesystem, String username) {
	    _filesystem = filesystem;
	    _username = username;
	    StartMediaForward(
	            "MainSavein", 
	            (String data) -> {
	                if(_connectbase64 != null) {
	                    _connectbase64.Savein(data);
	                    return true;
	                }
	                return false;
	            });
	}
	
	public String OwnerName() {
	    return _username;
	}
	
    @Override
    public boolean Config(JSONObject conf)
            throws JSONException {
        boolean re = true;
        if(conf.getBoolean(Label.FIELD_MEDIASAVE.toString())) {
            if(runingthread == null) {
                int timelimit = conf.getInt(Label.FIELD_MEDIATIMELIMIT.toString());
                timelimit = timelimit > 1 ? timelimit : 2;
                _connectbase64 = new ConnectBase64();
                runingthread = _timer.scheduleAtFixedRate(
                        () -> {
                            Date enddate = new Date();
                            String data = _connectbase64.Removeout();
                            if(data.length() > 0) {
                                _filesystem.SaveUserFile(
                                        _username,
                                        data, 
                                        new StoreDate(_connectbase64.startdate, enddate));
                            }
                            _connectbase64.startdate = enddate; 
                        },
                        timelimit, timelimit, TimeUnit.SECONDS);
            }
            else {
                //  TODO log
                re = false;
            }
        }
        else {
            while(!runingthread.isDone()) {
                runingthread.cancel(false);
            }
            runingthread = null;
            String remainmediadata = _connectbase64.Removeout();
            if(remainmediadata.toString().isEmpty() == false) {
                _filesystem.SaveUserFile(
                        _username,
                        remainmediadata, 
                        new StoreDate(_connectbase64.startdate, new Date()));
            }
            _connectbase64 = null;
        }
        return re;
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
                    new StoreDate(new JSONObject(date.getString(String.valueOf(key)))));
        }
		return true;
	}

	@Override
	public String PullMediaData(JSONObject date)
	        throws ParseException, JSONException {
	    ConnectBase64 re = new ConnectBase64();
	    List<StoreDate> datesort = new ArrayList<>();
        for(int key = 0; key < date.getInt(Label.FIELD_MEDIAINDEXNUM.toString()); key++) {
            datesort.add(new StoreDate(new JSONObject(date.getString(String.valueOf(key)))));
        }
        datesort.sort(
                (StoreDate date1, StoreDate date2) -> {
                    return date1.GetBeginDate().compareTo(date2.GetBeginDate());
                });
        datesort.forEach(
                (oneoffile) -> {
                    re.Savein(
                            _filesystem.GetUserFile(
                                    _username,
                                    oneoffile));
                });
        return re.Removeout();
	}

	@Override
	public void PushMediaData(String data) {
	    for (Map.Entry<String, MediaDataWatcher> entry : _list.entrySet()) {
	        entry.getValue().Push(data);
	    }
	}

	@Override
	public boolean StartMediaForward(String portid, MediaDataWatcher watcher) {
	    boolean re = false;
	    if(_list.get(portid) == null) {
	        _list.put(portid, watcher);
	        re = true;
	    }
        return re;
	}

	@Override
	public boolean StopMediaForward(String portid) {    
        return _list.remove(portid) != null;
	}
}
