package com.incompanysolutions.ZohoSyncCalendar;


import java.beans.MethodDescriptor;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

 

import com.google.gdata.client.Query;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.Source;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.EventEntry;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Reminder.Method;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.VersionConflictException;
import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.UserVO;
 

public class GoogleUtil extends BaseUtil{
	 
	public static   URL defaultFeed;
    public static   URL allCalendars;
    public static   URL ownCalendars;
    
     
    
    private CalendarFeed calendarFeed;
		//The base URL for a user's calendar metafeed (needs a username appended).
	private   final String METAFEED_URL_BASE = 
	    "http://www.google.com/calendar/feeds/";
	
	// The string to add to the user's metafeedUrl to access the event feed for
	// their primary calendar.
	private   final String EVENT_FEED_URL_SUFFIX = "/private/full";
	// The URL for the metafeed of the specified user.
	// (e.g. http://www.google.com/feeds/calendar/jdoe@gmail.com)
	private   URL metafeedUrl = null;
	// The URL for the event feed of the specified user's primary calendar.
	// (e.g. http://www.googe.com/feeds/calendar/jdoe@gmail.com/private/full)
	private   URL eventFeedUrl = null;	
	 

	public CalendarService myService;
     
	public boolean serviceLoaded=false;
	
	public DataModel dm = DataModel.getModel();
	
	public GoogleUtil(String token) throws Exception{
		try{ 	
		defaultFeed  = new URL("http://www.google.com/calendar/feeds/default");
		    allCalendars = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
		    ownCalendars = new URL("http://www.google.com/calendar/feeds/default/owncalendars/full");
		    myService = new CalendarService("Eventos");
		    myService.setAuthSubToken(token);
		    LogManager.getLogManager().setLog("Log In to Google with token " + token);
		    getAllEvents();
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error update user mysql " + getError(ee));
			throw ee;
		}
	}
	
	
	public void getAllEvents() throws Exception{
		
		CalendarEntry cal = getMainCalendar();
		    eventFeedUrl = new URL(this.METAFEED_URL_BASE+ dm.username + EVENT_FEED_URL_SUFFIX);
		    CalendarEventFeed myFeed = myService.getFeed(eventFeedUrl, CalendarEventFeed.class);
		    List<CalendarEventEntry> events = myFeed.getEntries();
		    LogManager.getLogManager().setLog("Reading " + events.size() + " Events From Main Calendar");
		    for(CalendarEventEntry rawEvent : events){
		    	EventVO event = new EventVO();
		    	event.readFromGoogle(rawEvent);
		    	if(event.recursiveEvent==false){
		    		dm.gEvents.put(event.googleId, event);
		    	}
		    }
	}
	
	public CalendarEntry getMainCalendar() throws Exception{
		
		CalendarEntry cal = new CalendarEntry(); 
		 
		LogManager.getLogManager().setLog("Getting Main Calendar");
		 CalendarFeed resultFeed = myService.getFeed(ownCalendars, CalendarFeed.class);
		// CommonUtils.dump(resultFeed, System.out);
	      
	     List<CalendarEntry> l = resultFeed.getEntries();
	     if(l.size() > 0){
	    	 cal = l.get(0);
	     }
	    
	     LogManager.getLogManager().setLog("Selected Calendar: " + cal.getTitle().getPlainText());
	
	     return cal;
	        
	}
	

	public CalendarEventEntry insertUrl(EventVO e) throws Exception{		
		try{
		CalendarEventEntry insertedEntry = new CalendarEventEntry();
		
		LogManager.getLogManager().setLog("Adding new Event to Google Calendar " + e.title);
		String author = dm.username;
		
		URL postUrl =
			new URL("http://www.google.com/calendar/feeds/" + author + "/private/full");
			
			// Send the request and receive the response:
			CalendarEventEntry insert= myService.insert(postUrl, e.toGoogleEvent());
			e.googleId = insert.getId();
			e.GoogleEditUrl = insert.getEditLink();
			return insert;
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error Insert Google " + getError(ee));
			throw ee;
		}
	}
	 
	public CalendarEventEntry updateUrl(EventVO e) throws Exception{	
		try{
			LogManager.getLogManager().setLog("updating new Event to Google Calendar " + e.title);
			URL url = new URL(e.GoogleEditUrl.getHref());
			 
		  
			CalendarEventEntry update= myService.update(url,e.toGoogleEvent());
			e.googleId = update.getId();
			e.GoogleEditUrl = update.getEditLink();
			return update;
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error update Google " + getError(ee));
			throw ee;
		}
	}
	
	public void deleteUrl(EventVO e) throws Exception{		
		try{
		LogManager.getLogManager().setLog("Deleting Event to Google Calendar " + e.title);
		
		URL entryUrl = new URL(e.GoogleEditUrl.getHref());
		EventEntry retrievedEntry = myService.getEntry(entryUrl, EventEntry.class);

		URL url = new URL(retrievedEntry.getEditLink().getHref());
			
			
		   myService.delete(url);
			
		}
		catch(Exception ee){
				//ev.readFromGoogle(ee.)
				LogManager.getLogManager().setLog("Error delete Google " + getError(ee));
				throw ee;
			
			
		}
	}
	 
	 

	
}
