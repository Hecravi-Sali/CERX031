package me.IAGO.Item;

import java.util.Date;
import org.json.JSONObject;

public interface StoreDate_Intfc {
	public JSONObject toJSON();
	public Date GetBeginDate();
	public Date GetEndDate();
}
