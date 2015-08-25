package com.incompany.test;

import java.util.Calendar;
import java.util.Date;

import com.google.gdata.data.Link;
import com.incompany.ZohoSyncCalendar.Vo.EventVO;

public class TestEventGenerator {

	public static String regularEvent="regularEvent";
	public static String deleteEvent="deleteEvent";
	
	public static EventVO GetEvent(String type){
		EventVO e = new EventVO();
		if(type.equals(TestEventGenerator.regularEvent)){
			regularNew(e);
		}
		if(type.equals(TestEventGenerator.deleteEvent)){
			delete(e);
		}
		
		return e;
	}
	
	private static void regularNew(EventVO e){
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 9, 5, 10,30);
		Date start = cal.getTime();
		cal.set(2010, 9, 5, 11,30);
		Date end = cal.getTime();
		
		e.googleId = "test";
		e.zohoId="test";
		e.GoogleEditUrl = new Link();
		e.GoogleEditUrl.setHref("test");
		e.startDate = start;
		e.endDate=end;
		e.title="Test Event "+ Math.random();
		e.content="Test Event "+ Math.random();
	}
	 
	
	private static void delete(EventVO e){
		Calendar cal = Calendar.getInstance();
		Date d = Calendar.getInstance().getTime();
		cal.set(2010, d.getMonth() , 6, 10,30);
		Date start = cal.getTime();
		cal.set(2010, d.getMonth(), 6, 11,30);
		Date end = cal.getTime();
		e.googleId = "test";
		e.zohoId="test";
		e.GoogleEditUrl = new Link();
		e.GoogleEditUrl.setHref("test");
		e.googleId = "test";
		e.zohoId = "test";
		e.startDate = start;
		e.endDate=end;
		e.title="Delete Event "+ Math.random();
		e.content="Test Event "+ Math.random();
	}
	 
	
	
}
