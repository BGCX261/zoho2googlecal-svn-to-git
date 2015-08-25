package com.incompanysolutions.ZohoSyncCalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.UserVO;

 

 

public class DataModel {

    static private DataModel DataModel = null;

    private DataModel() { }
	static public DataModel getModel() {

        if (DataModel == null) {
            DataModel = new DataModel();
        }
        return DataModel;
    }

	public int currentZohoApiCount=0;
	
	public String username;
	
	public MySqlUtil sqlUtil;
	
	public GoogleUtil gUtil;
	
	public ZohoUtil zUtil;
	
	public ArrayList<UserVO> sqlUsers = new ArrayList<UserVO>();
	
	public Map<String,List<EventVO>> sqlAllEvents = new HashMap<String,List<EventVO>>();
	
	public Map<String,EventVO>  zEvents = new HashMap<String,EventVO>();

	public Map<String,EventVO>  gEvents = new HashMap<String,EventVO>();

	public List<EventVO> sqlUserEvents = new ArrayList<EventVO>();
	
    
	
	
	
}