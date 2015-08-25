package com.incompanysolutions;
import java.io.*;
import java.util.*;
import java.net.*;

import javax.xml.namespace.QName;

 
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.Log4jFactory;

import com.google.gdata.data.calendar.CalendarEventEntry;
import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.IntegrationVO;
import com.incompany.ZohoSyncCalendar.Vo.UserVO;
import com.incompany.test.TestEventGenerator;
import com.incompanysolutions.ZohoSyncCalendar.CalendarEventIntegrator;
import com.incompanysolutions.ZohoSyncCalendar.CalendarEventLoader;
import com.incompanysolutions.ZohoSyncCalendar.DataModel;
import com.incompanysolutions.ZohoSyncCalendar.GoogleUtil;
import com.incompanysolutions.ZohoSyncCalendar.LogHelper;
import com.incompanysolutions.ZohoSyncCalendar.LogManager;
import com.incompanysolutions.ZohoSyncCalendar.LoginCfg;
import com.incompanysolutions.ZohoSyncCalendar.MySqlUtil;
import com.incompanysolutions.ZohoSyncCalendar.ZohoUtil;
import com.mysql.jdbc.log.Log;
 

public class StartHere
{
	
	public static void integrateAll() throws Exception{
		CalendarEventLoader calSync = new CalendarEventLoader();
		CalendarEventIntegrator integrate = new CalendarEventIntegrator();
		integrate.processEvents();
		integrate.saveEvents();
		DataModel.getModel().sqlUtil.conn.close();
	}
	
	public static void main(String a[])
	{	
		try {
			//FlushCalendars();
			//fullTestEvents();
			//FlushCalendars();
			 // TestIntegration();
			integrateAll();
//			if(a.length==0){
//				
//			}
//			else if(a.length==1){
//				if(a[0].toLowerCase().equals("test")){
//					TestEvents();
//				}
//				else if(a[0].toLowerCase().equals("full test")){
//					fullTestEvents();
//				}
//				else if(a[0].toLowerCase().equals("flush")){
//					FlushCalendars();
//				}
//			}
//			
			
			
				
		} catch (Exception e) {
			//Exceptions that are no handled are sent to email to take action
			String str ="";
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			System.out.println(writer.toString()); 
			LogHelper.postMail("Cal Sync Error",writer.toString() + " " + LogManager.getLogManager().getLog());
		}
	}
		
	public static void TestIntegration() throws Exception{
		FlushCalendars();
		CalendarEventLoader calSync;
		DataModel dm  = DataModel.getModel();
		EventVO f1 = TestEventGenerator.GetEvent(TestEventGenerator.regularEvent);
		dm.gUtil.insertUrl(f1);
		integrateAll();
		
		calSync = new CalendarEventLoader();
		EventVO e = dm.sqlUserEvents.get(0);
		e.startDate.setHours(6);
		dm.zUtil.update(e);
		integrateAll();
		
		calSync = new CalendarEventLoader();
		EventVO e1 = dm.sqlUserEvents.get(0);
		
		e1.startDate.setHours(7);
		dm.gUtil.updateUrl(e1);
//		//si se cambia la hora se regresa, pero hay que hace update de google id
		e1.startDate.setHours(6);
		dm.sqlUtil.updateEvent(e1);
		integrateAll();
		
		calSync = new CalendarEventLoader();
		EventVO e2 = dm.sqlUserEvents.get(0);
		e2.startDate.setHours(9);
		dm.zUtil.update(e2);
		integrateAll();
		
		calSync = new CalendarEventLoader();
		EventVO e3 = dm.sqlUserEvents.get(0);
		dm.zUtil.delete(e3);
		integrateAll();
		
		
//		calSync = new CalendarEventLoader();
//		e = dm.sqlUserEvents.get(0);
//		e.startDate.setHours(11);
//		e.endDate.setHours(12);
//		dm.zUtil.update(e);
//		integrateAll();
//		
//		//calSync = new CalendarEventLoader();
//		dm.gUtil.deleteUrl(e);
//		integrateAll();
////		
	 
		
		//		EventVO f2 = TestEventGenerator.GetEvent(TestEventGenerator.regularEvent);
//		f2.startDate.setHours(15);
//		f2.endDate.setHours(20);
//		dm.zUtil.insert(f2);
//		integrateAll();
	}
	
	public static void FlushCalendars() throws Exception{
		CalendarEventLoader calSync = new CalendarEventLoader();
		DataModel dm  = DataModel.getModel();
		for(EventVO e : dm.zEvents.values()){
			dm.zUtil.delete(e);
		}
		for(EventVO e : dm.gEvents.values()){
			dm.gUtil.deleteUrl(e);
		}
		for(List<EventVO> l : dm.sqlAllEvents.values()){
			for(EventVO e : l){
			dm.sqlUtil.deleteEvent(e);
			}
		}
	}
	
	public static void TestEvents() throws Exception{
		CalendarEventLoader calSync = new CalendarEventLoader();
    	googleEvents(false);
    	zohoEvents(false);
    	sqlEvents(false);
    }
	
    public static void fullTestEvents() throws Exception{
    	CalendarEventLoader calSync = new CalendarEventLoader();
    	googleEvents(true);
    	zohoEvents(true);
    	sqlEvents(true);
    }
	
	public static void googleEvents(boolean delete) throws Exception{
		DataModel dm  = DataModel.getModel();
		EventVO f1 = TestEventGenerator.GetEvent(TestEventGenerator.regularEvent);
	 
		 dm.gUtil.insertUrl(f1);
		 
		f1.startDate.setDate(10);
		f1.endDate.setDate(10);
		dm.gUtil.updateUrl(f1);
		if(delete){
			dm.gUtil.deleteUrl(f1);
		}
	}
	
	public static void zohoEvents(boolean delete) throws Exception{
		DataModel dm  = DataModel.getModel();
		EventVO f1 = TestEventGenerator.GetEvent(TestEventGenerator.regularEvent);
	 dm.zUtil.insert(f1);
		 
		f1.startDate.setDate(13);
		f1.endDate.setDate(13);
		dm.zUtil.update(f1);
		if(delete){
			dm.zUtil.delete(f1);
		}
	}
	
	public static void sqlEvents(boolean delete) throws Exception{
		DataModel dm  = DataModel.getModel();
		EventVO f1 = TestEventGenerator.GetEvent(TestEventGenerator.regularEvent);
		f1.googleId = "323232";
		f1.zohoId = "dadadsa da";
		dm.sqlUtil.insertEvent(f1);
		f1.startDate.setDate(13);
		f1.endDate.setDate(13);
		dm.sqlUtil.updateEvent(f1);
		if(delete){
			dm.sqlUtil.deleteEvent(f1);
		}
	}
	
		
		
		
		
		
}
 