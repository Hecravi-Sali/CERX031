package me.IAGO.Media;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import me.IAGO.Item.FileSystem_Intfc;
import me.IAGO.Item.StoreDate;
import me.IAGO.Item.StoreDate_Intfc;;

public class Media implements Media_Intfc {
	final String MEDIA_INDEXNUM = "media_indexnum";
	final String MEDIA_SAVE = "media_save";
	final String MEDIA_TIMELIMIT = "media_timelimit";

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
	
    private List<MediaDataWatcher> _list = new ArrayList<MediaDataWatcher>();
    private ScheduledExecutorService _timer = Executors.newScheduledThreadPool(2);
	private MediaDataBuffer _mediadatabuffer;
	private FileSystem_Intfc _filesystem;
	private String _username;
	
	Media(FileSystem_Intfc filesystem, String username) {
	    _filesystem = filesystem;
	    _mediadatabuffer = null;
	    _username = username;
	    StartMediaForward(new MediaDataWatcher() {
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
	
    @Override
    public boolean Config(JSONObject conf)
            throws JSONException {
        if(conf.getBoolean(MEDIA_SAVE)) {
            int timelimit = conf.getInt(MEDIA_TIMELIMIT);
            _timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Date enddate = new Date();
                    _filesystem.SaveUserFile(_mediadatabuffer.Removeout(), new StoreDate(_mediadatabuffer.startdate, enddate));
                    _mediadatabuffer.startdate = enddate;
                }
            }, 0, timelimit, TimeUnit.SECONDS);
        }
        else {
            if(_timer.isShutdown() == false) {
                try {
                    _timer.awaitTermination(1, TimeUnit.SECONDS);
                    Byte remainmediadata = _mediadatabuffer.Removeout();
                    if(remainmediadata.toString().isEmpty() == false) {
                        _filesystem.SaveUserFile(remainmediadata, new StoreDate(_mediadatabuffer.startdate, new Date()));
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
		if(SetFileSystemPath()) {
			List<StoreDate_Intfc> index = _filesystem.GetUserFileIndex();
			json.put(MEDIA_INDEXNUM, index.size());
			for(int key = 0; key < index.size(); key++) {
				json.put(String.valueOf(key), index.get(key).toString());
			}
		}
		return json;
	}

	@Override
	public boolean DelectMediaDate(JSONObject date)
	        throws ParseException, JSONException {
		if(SetFileSystemPath()) {
			for(int key = 0; key < date.getInt(MEDIA_INDEXNUM); key++) {
			    _filesystem.DeleteUserFile(new StoreDate(date.getJSONObject(String.valueOf(key))));
			}
		}
		return false;
	}

	@Override
	public boolean PullMediaData(JSONObject date, MediaDataWatcher callbackfunc)
	        throws ParseException, JSONException {
	    if(SetFileSystemPath()) {
	        StringBuffer mergemediadata = new StringBuffer();
	        for(int key = 0; key < date.getInt(MEDIA_INDEXNUM); key++) {
	            mergemediadata.append(_filesystem.GetUserFile(new StoreDate(date.getJSONObject(String.valueOf(key)))).toString());
	        }  
	        return callbackfunc.Push(new Byte(mergemediadata.toString()));
	    }
		return false;
	}

	@Override
	public boolean PushMediaData(Byte data) {
	    _list.forEach(watcher -> {
            watcher.Push(data);
        });
		return false;
	}

	@Override
	public boolean StartMediaForward(MediaDataWatcher watcher) {
        return _list.add(watcher);
	}

	@Override
	public boolean StopMediaForward(MediaDataWatcher watcher) {
        return _list.remove(watcher);
	}

	private boolean SetFileSystemPath()
	        throws JSONException{
		if(_filesystem.Available()) {
			return _filesystem.GotoUserPath(_username);
		}
		return false;
	}
}
