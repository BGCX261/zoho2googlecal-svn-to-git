package com.incompanysolutions.ZohoSyncCalendar;
 

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.UserVO;

 

public class MySqlUtil  extends BaseUtil{

	public Connection conn;
	
	public DataModel dm = DataModel.getModel();
	
	
	public MySqlUtil (String userName,String password,String url) throws Exception{
		try{
		this.createRemoteConnection(userName, password, url);
		ResultSet resultU = this.getDataFromMySql("select * from Users");
		 
		while(resultU.next()){
			UserVO u = new UserVO();
			u.readFromSql(resultU);
			dm.sqlUsers.add(u);	
		}
		
		resultU.close();
		
		ResultSet resultE = this.getDataFromMySql("select * from Events");
		
		int count =0;
		while(resultE.next()){
			EventVO u = new EventVO();
			u.readFromSql(resultE);
			
			if(dm.sqlAllEvents.containsKey(u.userName)){
				dm.sqlAllEvents.get(u.userName).add(u);
			}
			else{
				List<EventVO> l = new ArrayList<EventVO>();
				l.add(u);
				dm.sqlAllEvents.put(u.userName, l);
			}
			
			count++;
		}
		resultE.close();
		
		LogManager.getLogManager().setLog("We got " + count + " MySql Events");
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error Login MySql " + getError(ee));
			throw ee;
		}
	}

	public void updateEvent(EventVO e) throws Exception{
		try{
		String query = "update Events set Title='!',Content='!',StartDate='!',EndDate='!',ZohoId='!',GoogleId='!',GoogleEditUrl='!' where Id=!";
		String st = this.PrepareStatement(query,e.toUpdateParams());	
		//System.out.println(st);
		this.setDataFromMySql(st,false);
		
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error update Mysql " + getError(ee));
			throw ee;
		}
	}
	
	public void insertEvent(EventVO e) throws Exception{
		try{
		String query = "insert into Events (Title,Content,StartDate,EndDate,ZohoId,GoogleId,GoogleEditUrl,Username) values ('!','!','!','!','!','!','!','!')";
		String st = this.PrepareStatement(query,e.toInsertParams());
		//System.out.println(st);
		int id = this.setDataFromMySql(st,true);
		if(id==-1){
			Exception ee = new Exception();
			LogManager.getLogManager().setLog("Error insert Mysql ");
			throw ee;
		}
		else{
			e.sqlId=id;
		}
		
		
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error insert mysql " + getError(ee));
			throw ee;
		}
	}
	
	public void deleteEvent(EventVO e) throws Exception{
		try{
		String query = "delete from Events where Id =" + e.sqlId;
		this.setDataFromMySql(query,false);
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error delete mysql " + getError(ee));
			throw ee;
		}
	}
	
	public void updateUser(UserVO u,int count) throws Exception{
		try{
		String query = "update Users set count=" + count + " where Id=" + u.sqlId ;
		this.setDataFromMySql(query,false);
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error update user mysql " + getError(ee));
			throw ee;
		}
	}
	
	public String PrepareStatement(String query,List<String> params){
		int count = 0;
		while(count < params.size()){
			query = query.replaceFirst("!", params.get(count).trim());
			
			count++;
		}
		
		//LogManager.getLogManager().setLog(query);
		return query;
	}
	
	public ResultSet getDataFromMySql(String query) throws Exception{		 
		
		Statement s = conn.createStatement();
		s.executeQuery(query);
		ResultSet set = s.getResultSet();
		int count = getRowCount(set);
	//	log.info("Got " + count + "rows from " + set);
		return set;
	}
	
	public int setDataFromMySql(String query,boolean isInsert) throws Exception{		 	
	//	log.info("Getting Data from MYSQL " + query);
		//LogManager.getLogManager().setLog("Getting Data from MYSQL " + query);
		 
			Statement s = conn.createStatement();
			int lastInsertedId = -1;
			if(isInsert){
				s.execute(query, Statement.RETURN_GENERATED_KEYS);
				ResultSet rskey = s.getGeneratedKeys();

				if (rskey != null && rskey.next()) {
					   lastInsertedId = rskey.getInt(1);
				}
				return lastInsertedId;
				
			}
			else{
				return s.executeUpdate(query);
			}
		 
		 
	}
	
	public void createRemoteConnection(String userName,String password,String url) throws Exception {
	  try{
		// log.info("Creating MYSQL Connection");
	   Class.forName ("com.mysql.jdbc.Driver").newInstance ();
	   conn = DriverManager.getConnection (url, userName, password);
	  // log.info("MYSQL Connection established");
	  }
	  catch(Exception ee){
			LogManager.getLogManager().setLog("Error login mysql " + getError(ee));
			throw ee;
		}
	  }

	public static String getMySqlValue(ResultSet result ,String key) throws Exception{
		String value= result.getString(key);
		String returnValue="0";
		if(value == null ){ 

			
		}
		 
		else{
			returnValue= value;
		}
		if(returnValue.contains(" ")){
			returnValue = returnValue.trim();
		}
		return returnValue;
	}
	
	
	
	public int getRowCount(ResultSet res) throws Exception{
		int count=0;
		if(res !=null){
		res.last();
		count = res.getRow();
		res.beforeFirst();
		}
		
		return count;
	}
	
}
