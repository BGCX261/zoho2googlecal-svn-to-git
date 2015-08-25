package com.incompanysolutions.ZohoSyncCalendar;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class BaseUtil {

	public String getError(Exception e){
		String str ="";
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		return writer.toString(); 
	}
	
}
