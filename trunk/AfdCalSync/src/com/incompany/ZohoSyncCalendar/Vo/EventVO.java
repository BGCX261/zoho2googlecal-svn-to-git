package com.incompany.ZohoSyncCalendar.Vo;

 
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.incompanysolutions.ZohoSyncCalendar.DataModel;
import com.incompanysolutions.ZohoSyncCalendar.MySqlUtil;

public class EventVO {

	//event details
	public String title="";
	public String content="";
	public Date startDate; 
	public Date endDate;
	 
	//sync details
	public Date createdTime;
	public Date modifiedTime;
	public String zohoId;
	public String userName;
	public String googleId;
	public Integer sqlId;
	public Link GoogleEditUrl;
	//SQL TimeStamp
	public Date LastModifiedDateTime;
	private boolean isFullDay=false;
	public EventVO oldEvent;
	
	public boolean recursiveEvent=false;
	
	public String getZohoInsertXml(int row){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return  "<row no=\""+ row +"\">" +
				 
		
				"<FL val=\"Subject\">"+title+"</FL>" +
				"<FL val=\"Start DateTime\">"  + sdf.format(startDate) + "</FL>" +
				"<FL val=\"End DateTime\">"+ sdf.format(endDate) +"</FL>" +
				"<FL val=\"Description\">"+content+"</FL>" +
				"</row>";
		 
	}
	
	public String getZohoUpdateXml(int row){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return  "<row no=\""+ row +"\">" +
		"<FL val=\"Subject\">"+title+"</FL>" +
		"<FL val=\"Start DateTime\">"  + sdf.format(startDate) + "</FL>" +
		"<FL val=\"End DateTime\">"+ sdf.format(endDate) +"</FL>" +
		"<FL val=\"Description\">"+content+"</FL>" +
		"</row>";
	}
	
	 
	
	 
 
	
	public CalendarEventEntry toGoogleEvent(){
		CalendarEventEntry myEntry = new CalendarEventEntry();
		
		//String type = add.source.getType();
		DateTime startTime;
		DateTime endTime;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-06:00'");
 			startTime =  DateTime.parseDateTime(sdf.format(startDate));
		endTime =  DateTime.parseDateTime(sdf.format(endDate));	
		myEntry.setTitle(new PlainTextConstruct(this.title));
 			myEntry.setContent(new PlainTextConstruct(this.content));
 		
 		Calendar cal = Calendar.getInstance();
 		cal.setTimeInMillis(startTime.getValue());
 		
 		When eventTimes = new When();
 		eventTimes.setStartTime(startTime);
 		eventTimes.setEndTime(endTime);
 		
 		myEntry.addTime(eventTimes);
 	 
 		return myEntry;
	}
	
	public boolean changed(EventVO otherEvent){
		boolean equals=false;
		
		if(!title.equals(otherEvent.title)){
			equals=true;
		}
		
		if(!content.equals(otherEvent.content)){
			equals=true;
		}
		
		if(!startDate.equals(otherEvent.startDate)){
			equals=true;
		}
		
		if(!endDate.equals(otherEvent.endDate)){
			equals=true;
		}
		
		return equals;
	}
	
	//Paste all but Id's
	public void pasteEvent(EventVO otherEvent){
		this.oldEvent = this.clone();
		this.title = otherEvent.title;
		this.content = otherEvent.content;
		this.startDate = otherEvent.startDate;
		this.endDate = otherEvent.endDate;
	}
	
	public EventVO clone(){
		EventVO o = new EventVO();
		o.title = title;
		o.content = content;
		o.startDate = startDate;
		o.endDate = endDate;
		return o;
	}
	
	public void readFromGoogle(CalendarEventEntry e){
		this.title = e.getTitle().getPlainText();
		this.GoogleEditUrl = e.getEditLink();
		
		TextContent t = (TextContent)e.getContent();
		this.content =  t.getContent().getPlainText();
		this.startDate = new Date();
		this.endDate = new Date();
		if(e.getTimes().size()>0){
			DateTime temp = e.getTimes().get(0).getStartTime();
			this.startDate.setTime(temp.getValue());
			temp = e.getTimes().get(0).getEndTime();
			this.endDate.setTime(temp.getValue());
		}
		else{
			this.recursiveEvent=true;
			System.out.println("Event with no times + " + this.title );
		}
		this.createdTime = new Date();
		this.modifiedTime = new Date();
		this.googleId = e.getId();
		this.modifiedTime.setTime(e.getUpdated().getValue());
		this.createdTime.setTime(e.getPublished().getValue());
		
		}
	
	public void parseString(Map<String,String> valuePair) throws Exception{
		String field = valuePair.get("val");
		String value = valuePair.get("content");
		SimpleDateFormat sdf;
		sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 
		
		if(field.equals("ACTIVITYID")){
			this.zohoId = value; 
		 }
		
		 if(field.equals("Subject")){
			this.title = value; 
		 }
		 
		 else  if(field.equals("Description")){
			 this.content= value;
		 }
		 
		 else  if(field.equals("Start DateTime")){
			 this.startDate = sdf.parse(value);
		 }
		
		 else  if(field.equals("End DateTime")){
			 this.endDate = sdf.parse(value);
		 }

		 else  if(field.equals("Created Time")){
			 this.createdTime = sdf.parse(value);
		 }

		 else  if(field.equals("Modified Time")){
			 this.modifiedTime = sdf.parse(value);
		 }
		 
		
		 
	}
	
	public void readFromSql(ResultSet r) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("y-M-d k:m:s");
		this.sqlId = Integer.parseInt(MySqlUtil.getMySqlValue(r, "Id"));
		this.content = MySqlUtil.getMySqlValue(r, "Content");
		 
		this.endDate = sdf.parse(MySqlUtil.getMySqlValue(r, "EndDate"));
		 
		this.startDate = sdf.parse(MySqlUtil.getMySqlValue(r, "StartDate"));
		this.title = MySqlUtil.getMySqlValue(r, "Title");
		this.userName = MySqlUtil.getMySqlValue(r, "Username");
		this.zohoId = MySqlUtil.getMySqlValue(r, "ZohoId");
		this.googleId = MySqlUtil.getMySqlValue(r, "GoogleId");
		this.LastModifiedDateTime = sdf.parse(MySqlUtil.getMySqlValue(r, "LastModifiedDateTime"));
		Link l = new Link();
		String editUrl = MySqlUtil.getMySqlValue(r, "GoogleEditUrl");
		if(editUrl!=null){
			l.setHref(editUrl);
			GoogleEditUrl=l;
		}
	}
	
	public List<String> toUpdateParams(){
		SimpleDateFormat sdf = new SimpleDateFormat("y-M-d k:m:s");
		List<String>  l = new ArrayList<String>();
		l.add(this.title);
		l.add(this.content);
		l.add(sdf.format(startDate));
		l.add(sdf.format(this.endDate));
		l.add(zohoId);
		l.add(googleId);
		l.add(this.GoogleEditUrl.getHref());
		l.add(this.sqlId.toString());
		return l;
	}
	
	public List<String> toInsertParams(){
		SimpleDateFormat sdf = new SimpleDateFormat("y-M-d k:m:s");
		List<String>  l = new ArrayList<String>();
		l.add(this.title);
		l.add(this.content);
		l.add(sdf.format(startDate));
		l.add(sdf.format(this.endDate));
		l.add(zohoId);
		l.add(googleId);
		l.add(this.GoogleEditUrl.getHref());
		l.add(DataModel.getModel().username);
		return l;
	}
	
}
