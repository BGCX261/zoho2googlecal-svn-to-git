package com.incompany.ZohoSyncCalendar.Vo;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.incompanysolutions.ZohoSyncCalendar.LogHelper;
import com.incompanysolutions.ZohoSyncCalendar.MySqlUtil;

public class UserVO {

	public String username="";
	public String password="";
	public String token="";
	public int count=0;
	public Date lastSync;
	public String zohoApi="";
	public int sqlId=0;
	public void readFromSql(ResultSet result) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("y-M-d");
		this.sqlId = Integer.parseInt(MySqlUtil.getMySqlValue(result, "Id"));
		username = MySqlUtil.getMySqlValue(result,"username");
		password = MySqlUtil.getMySqlValue(result,"password");
		token = MySqlUtil.getMySqlValue(result,"token");
		zohoApi = MySqlUtil.getMySqlValue(result,"zohoApiKey");
		lastSync = sdf.parse(MySqlUtil.getMySqlValue(result, "lastSync"));
		count = Integer.parseInt(MySqlUtil.getMySqlValue(result,"count"));
		
		
	}
		
	}
	
 
