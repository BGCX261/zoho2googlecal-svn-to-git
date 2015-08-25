package com.incompanysolutions.ZohoSyncCalendar;

import java.util.Calendar;
import java.util.Date;

import com.incompany.ZohoSyncCalendar.Vo.UserVO;

public class CalendarEventLoader {
	private DataModel dm = DataModel.getModel();
	
	public CalendarEventLoader() throws Exception{
		try{
		LoginCfg login =LoginCfg.self;
		LogManager.getLogManager().setLog("Starting Afd Zoho Google Sync");
		dm.sqlAllEvents.clear();
		dm.sqlUserEvents.clear();
		dm.sqlUsers.clear();
		dm.sqlUtil = new  MySqlUtil(login.myuser,login.mypass,login.mySqlUrl);
		
		
		LogManager.getLogManager().setLog("" + dm.sqlUsers.size() + " SQL Events");
		
		for(UserVO user : dm.sqlUsers){
			if(protectApiOverUsage(user)){
				dm.username = user.username;
				dm.zEvents.clear();
				dm.gEvents.clear();
				dm.sqlUserEvents.clear();
				if(dm.sqlAllEvents.get(user.username)!=null){
					dm.sqlUserEvents = dm.sqlAllEvents.get(user.username);
				}
				dm.zUtil = new ZohoUtil(user.username,user.password,user.lastSync,user.zohoApi);
				dm.gUtil = new GoogleUtil(user.token);
				this.protectCurrentIntegrationApiOverUsage(user);
				dm.sqlUtil.updateUser(user, dm.currentZohoApiCount + user.count);
			}
		}
		}
		catch(Exception e){
			LogManager.getLogManager().setLog("Could not log in and get Events from all Services, stoping integration");
			LogManager.getLogManager().containsError=true;
			throw e;
			
		}
	}
	

	public void protectCurrentIntegrationApiOverUsage(UserVO u) throws Exception{
		
		if(dm.currentZohoApiCount - u.count > 20){
			 LogHelper.postMail("Afd2Google API Big Session","El API de " + u.username + " se usa en mas de 20 ocaciones en esta session." );
				
		}
	}
	
	public boolean protectApiOverUsage(UserVO u) throws Exception{
		LogManager.getLogManager().setLog("Start Api Over Usage Protection for " + u.username);
		boolean okToGo=true;
		dm.currentZohoApiCount=0;
		this.resetCount(u);
		
		if(u.count > 200){
			
			if(checkCurrentHour(u)){
				LogManager.getLogManager().setLog("Current API Usage above 200, will reduce to each hour integration");
				okToGo=false;
			}
		}
		else if(u.count > 240){
			LogManager.getLogManager().setLog("Current API Usage above 240, Integration WILL Failed until API Count is reset");
			LogHelper.postMail("Afd2Google API Over Usage","El API de " + u.username + " esta llegando a su limite se deben hacer ajustes." );
		}
		
		return okToGo;
	}

	private boolean checkCurrentHour(UserVO u){
		Date d = Calendar.getInstance().getTime();
		boolean isInHour=false;
		if(u.lastSync.getHours() == d.getHours() ){
			 isInHour=true;
		}
		return isInHour; 
	}

	private void resetCount(UserVO u){
		Date d = Calendar.getInstance().getTime();
		
		if(u.lastSync.getMonth() == d.getMonth() ){
			if(u.lastSync.getDate() != d.getDay() ){
				u.count =0;
			}
		}
		else{
			u.count =0;
		}

	}
	
}
