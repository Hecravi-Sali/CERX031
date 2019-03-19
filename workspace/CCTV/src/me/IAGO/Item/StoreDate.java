package me.IAGO.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class StoreDate implements StoreDate_Intfc {
    final public String STORE_STARTDATE = "storestartdate";
    final public String STORE_ENDDATE = "storeenddate";
    final public String STORE_TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    
	private Date _startdate, _enddate;
	private SimpleDateFormat formatter = new SimpleDateFormat(STORE_TIMEFORMAT);

	public StoreDate(JSONObject json) 
	        throws ParseException, JSONException {
        _startdate = formatter.parse(json.getString(STORE_STARTDATE));
        _enddate = formatter.parse(json.getString(STORE_ENDDATE));
	}
	
	public StoreDate(Date start, Date end) {
	    _startdate = start;
	    _enddate = end;
	}
	
	@Override
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put(STORE_STARTDATE, formatter.format(_startdate));
        json.put(STORE_ENDDATE, formatter.format(_enddate));
		return json;
	}

	@Override
	public Date GetBeginDate() {
		return _startdate;
	}

	@Override
	public Date GetEndDate() {
		return _enddate;
	}
}
