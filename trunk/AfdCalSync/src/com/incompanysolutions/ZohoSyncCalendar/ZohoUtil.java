package com.incompanysolutions.ZohoSyncCalendar;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

 
import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

 

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
 

 
import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.UserVO;


public class ZohoUtil  extends BaseUtil{

	public ArrayList<EventVO> events = new ArrayList<EventVO>();
	
	private String ticket ="";
	private String apikey="";
	public DataModel dm = DataModel.getModel();
	
	 
	public String update(EventVO e) throws Exception{
		PostMethod post = new PostMethod();
		String response="";
		
		try{
		LogManager.getLogManager().setLog("Updating Event in Zoho " + e.title );
		String targetURL = "http://crm.zoho.com/crm/private/xml/Events/updateRecords"; 
		
		post = new PostMethod(targetURL);
		post.setParameter("ticket",ticket);
		post.setParameter("apikey",apikey);
		post.setParameter("newFormat","1");
		post.setParameter("id",e.zohoId);
		post.setParameter("xmlData",e.getZohoUpdateXml(1));

		HttpClient httpclient = new HttpClient();
		dm.currentZohoApiCount+=1;
		Integer r = httpclient.executeMethod(post);
		response = post.getResponseBodyAsString();
		String resp = "";
		if(r>300){
			LogManager.getLogManager().setLog("Error en Update Zoho " + response);  
			Exception ee = new Exception();
			throw ee;
			
		}
		else{
			resp = processResponse(post.getResponseBodyAsString());
			e.zohoId = resp;
		}
	 
		post.releaseConnection();
		 
		return resp;
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error update Zoho "  + response + getError(ee));
			throw ee;
		}
	}
	
	public void delete(EventVO e) throws Exception{
		GetMethod post = new GetMethod();
		String response = "";
		try{
		LogManager.getLogManager().setLog("Deleting Event in Zoho " + e.title );
		String targetURL = "http://crm.zoho.com/crm/private/xml/Events/deleteRecords"; 
		 
		post = new GetMethod(targetURL);
		post.setQueryString("apikey="+apikey+"&ticket="+ticket+"&id="+e.zohoId);
		
		HttpClient httpclient = new HttpClient();
		dm.currentZohoApiCount+=1;
		Integer r = httpclient.executeMethod(post);
		response = post.getResponseBodyAsString();
		if(r>300){
			LogManager.getLogManager().setLog("Error en Update Zoho " + response);
			Exception ee = new Exception();
			throw ee;
			
		}
		
		post.releaseConnection();
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error delete Zoho "  + response + getError(ee));
			throw ee;
		} 
	}

	public String insert(EventVO e)  throws Exception{
		PostMethod post = new PostMethod();
		String response ="";
		try{
		LogManager.getLogManager().setLog("Inserting Event in Zoho " + e.title );
		String targetURL = "http://crm.zoho.com/crm/private/xml/Events/insertRecords"; 
		String xmlDataString = e.getZohoInsertXml(0);
		xmlDataString = "<Events>" + xmlDataString + "</Events>";
		post = new PostMethod(targetURL);
		post.setParameter("ticket",ticket);
		post.setParameter("apikey",apikey);
		post.setParameter("newFormat","1");
		post.setParameter("xmlData",xmlDataString);
	
		HttpClient httpclient = new HttpClient();
		dm.currentZohoApiCount+=1;
		Integer r = httpclient.executeMethod(post);
		 
		response = post.getResponseBodyAsString();
		String resp = "";
		if(r>300){
			LogManager.getLogManager().setLog("Error en Update Zoho " + response);
			Exception ee = new Exception();
			throw ee;
		}
		else{
			resp = processResponse(post.getResponseBodyAsString());
			e.zohoId = resp;
		}
	 
			post.releaseConnection();
			return resp;
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error insert Zoho "  + response + getError(ee));
			throw ee;
		}
	}
	
	public ZohoUtil(String user,String password,Date lastSync,String api) throws Exception{
	try	
	{
		//----------------------------Fetch Ticket ----------------------
		 
		try{
			ticket = getIAMTicket("ZohoCRM",user,password);
			
		}
		catch(Exception ee){
			LogManager.getLogManager().setLog("Error in Zoho Login " + getError(ee));
			throw ee;
		}
		apikey = api; //your API key
		 
		String newFormat = "1";
		String fromIndex = "1";
		String toIndex = "200";

		String targetURL = "http://crm.zoho.com/crm/private/json/Events/getMyRecords"; 
		String paramname = "content";
		PostMethod post = new PostMethod(targetURL);
		post.setParameter("ticket",ticket);
		post.setParameter("apikey",apikey);
		post.setParameter("newFormat",newFormat);
		post.setParameter("selectColumns","All");
		post.setParameter("fromIndex",fromIndex);
		post.setParameter("toIndex",toIndex);
		HttpClient httpclient = new HttpClient();
	 
		int count =200;
		while(count ==200){
			LogManager.getLogManager().setLog("Loading Zoho Events for " + user + " from "+ fromIndex+ " to " + toIndex );
			int result = httpclient.executeMethod(post);
			String body = post.getResponseBodyAsString();
			body = body.substring(22, body.length()-48);
			post.releaseConnection();
			count =this.processRows(body);
			LogManager.getLogManager().setLog("We got " + count + " Zoho Events");
			fromIndex+=200;
			toIndex+=201;
		}
		 
	}
	catch(Exception e)
	{
		LogManager.getLogManager().setLog("Error getting Events from Zoho"); 
		
		throw e;
	}	
}
	
	public String processResponse(String s) throws Exception{
		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         InputSource is = new InputSource();
         is.setCharacterStream(new StringReader(s));


         Document doc = docBuilder.parse(is);

         // normalize text representation
         doc.getDocumentElement ().normalize ();
         
         NodeList fls = doc.getElementsByTagName("FL");
        
        
        	 Element element = (Element) fls.item(0);
        	String str=element.getFirstChild().getNodeValue();
        	   
	

          return str;

        
         
	}
	
	public int processRows(String json) throws Exception{
		  
			int count =0;
			Object o = JSONParser.defaultJSONParser().parse(json);

			Object[] events = ((Map)o).values().toArray();
			
			//When there are no events there is an error
			try{
			Object[] temprows = ((Map)events[0]).values().toArray();
			ArrayList<Object> rows = new ArrayList<Object>();
			if(temprows.length>0){
				try {
					ArrayList<Object> temp = (ArrayList<Object>)temprows[0];
					rows = temp;
				} catch (Exception e) {
					rows.add(temprows[0]);
				}
			}
			
			for(Object row : (ArrayList<Object>)rows){
				Map map = (HashMap)row;
				ArrayList<Map> rawEvent = (ArrayList<Map>)map.get("FL");
				EventVO event = new EventVO();
				
				for(Map<String,String> valuePair : rawEvent){
					event.parseString(valuePair);
				}
				dm.zEvents.put(event.zohoId,event);
				count ++;
			}
			}
			catch (Exception e) {
			LogManager.getLogManager().setLog("No habian eventos en Zoho");
				// TODO: handle exception
			} 
			
			return count;
		 
	}
	
//-------------------------Get IAM Ticket ---------------------------------
public  String getIAMTicket(String serviceName, String loginId, String password)  throws Exception
{
	String strTicket = null;
	int code =0;;
	String response="";
	try {

		String iamUrl = "http://accounts.zoho.com/login?servicename="+serviceName+"&FROM_AGENT=true&LOGIN_ID="+loginId+"&PASSWORD="+password;
		URL u = new URL(iamUrl);
		HttpURLConnection c = (HttpURLConnection)u.openConnection();
		InputStream in = c.getInputStream();
		InputStreamReader ir=new InputStreamReader(in);
		BufferedReader br =new BufferedReader(ir);

		code = c.getResponseCode();
		response = c.getResponseMessage();
		String strLine=null;
		while ((strLine = br.readLine()) != null) {
			if(strLine != null && strLine.startsWith("TICKET")) {
				strTicket = strLine.substring(7);
			}
			else if(strLine != null && strLine.startsWith("CAUSE")) {
				response = response + " " + strLine;
			}
		}

		in.close();
		strTicket.length();
	}
	catch(Exception ee){
		LogManager.getLogManager().setLog("Error load Data zoho "+ code + " " + response + " " +  getError(ee));
		throw ee;
	}
	return strTicket;
}
	
}
