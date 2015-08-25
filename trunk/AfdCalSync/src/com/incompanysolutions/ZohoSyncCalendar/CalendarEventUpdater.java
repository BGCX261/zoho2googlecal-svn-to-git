package com.incompanysolutions.ZohoSyncCalendar;

import java.net.URL;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.incompany.ZohoSyncCalendar.Vo.EventVO;

public class CalendarEventUpdater {

	
	public static void saveToGoogle(EventVO e) throws Exception{
		DataModel dm = DataModel.getModel();
		CalendarEventEntry entry= e.toGoogleEvent();
		if(e.googleId==null){
			//e.googleId = dm.gUtil.myService.insert(new URL(e.getGoogleUrl()), entry).getId();
			
		}
		else if (e.googleId!="-1"){
			//dm.gUtil.myService.update(new URL(e.getGoogleUrl()),entry);
		}
		else{
			//dm.gUtil.myService.delete(new URL(e.getGoogleUrl()));
		}
	}
	
	public static void saveToZoho(EventVO e){
		if(e.zohoId==null){
			//e.googleId = dm.gUtil.myService.insert(new URL(e.getGoogleUrl()), entry).getId();
			
		}
		else if (e.googleId!="-1"){
			//dm.gUtil.myService.update(new URL(e.getGoogleUrl()),entry);
		}
		else{
			//dm.gUtil.myService.delete(new URL(e.getGoogleUrl()));
		}
	}
	
}
