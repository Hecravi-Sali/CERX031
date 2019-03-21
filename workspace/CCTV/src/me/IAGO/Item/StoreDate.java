package me.IAGO.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class StoreDate implements StoreDate_Intfc {
	private Date _startdate, _enddate;
	private SimpleDateFormat formatter = new SimpleDateFormat(Label.CONST_STORETIMEFORMAT.toString());

	public StoreDate(JSONObject json) 
	        throws ParseException, JSONException {
        _startdate = formatter.parse(json.getString(Label.FIELD_STOREDATESTART.toString()));
        _enddate = formatter.parse(json.getString(Label.FIELD_STOREDATEEND.toString()));
	}
	
	public StoreDate(Date start, Date end) {
	    _startdate = start;
	    _enddate = end;
	}
	
	@Override
	public JSONObject toJSON() {
	    JSONObject json = new JSONObject();
	    json.put(
	            Label.FIELD_STOREDATESTART.toString(),
	            formatter.format(_startdate));
        json.put(
                Label.FIELD_STOREDATEEND.toString(), 
                formatter.format(_enddate));
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
