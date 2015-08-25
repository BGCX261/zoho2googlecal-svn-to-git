package com.incompany.ZohoSyncCalendar.Vo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.incompanysolutions.ZohoSyncCalendar.BaseUtil;
import com.incompanysolutions.ZohoSyncCalendar.DataModel;
import com.incompanysolutions.ZohoSyncCalendar.LogManager;

public class IntegrationVO extends BaseUtil {

	public static String DELETE_IN_GOOGLE="deleteGoogle";
	public static String DELETE_IN_ZOHO="deleteZoho";
	
	public static String UPDATE_IN_GOOGLE="updateGoogle";
	public static String UPDATE_IN_ZOHO="updateZoho";
	
	public static String INSERT_IN_GOOGLE="insertGoogle";
	public static String INSERT_IN_ZOHO="insertZoho";
	
	
	
	private String type="";
	
	private EventVO evento;
	
	private DataModel dm = DataModel.getModel();
	
	public void doIntegration(String type,EventVO event){
		evento = event;
		this.type=type;
	}
	
	
	public void performAction() throws Exception{
		
		if(type.equals(IntegrationVO.INSERT_IN_GOOGLE)){
			try{
				insertingoogle();
				insertinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
		else if(type.equals(IntegrationVO.INSERT_IN_ZOHO)){
			try{
				insertinzoho();
				insertinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
		else if(type.equals(IntegrationVO.UPDATE_IN_GOOGLE)){
			try{
				updateingoogle();
				updateinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
		else if(type.equals(IntegrationVO.UPDATE_IN_ZOHO)){
			try{
				updateinzoho();
				updateinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
		else if(type.equals(IntegrationVO.DELETE_IN_GOOGLE)){
			try{
				deleteingoogle();
				deleteinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
		else if(type.equals(IntegrationVO.DELETE_IN_ZOHO)){
			try{
				deleteinzoho();
				deleteinsql();
			}
			catch(Exception e){
				undo(getError(e));
			}
		}
		
	}
	
	  
	private void undo(String error) throws Exception{
		if(type.equals(IntegrationVO.INSERT_IN_GOOGLE)){
			try {
				deleteinsql();
			} catch (Exception e) {
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error " +
						 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
			try{
				
				deleteingoogle();
				
			}
			catch(Exception e){
				
			}
		}
		
		else if(type.equals(IntegrationVO.INSERT_IN_ZOHO)){
			try {
				deleteinsql();
			} catch (Exception e) {
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error "+
						 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
			try{
				
				deleteinzoho();
				
			}
			catch(Exception e){
				
			}
		}
		
		else if(type.equals(IntegrationVO.UPDATE_IN_GOOGLE)){
			try{
				this.evento = evento.oldEvent;
				updateingoogle();
				updateinsql();
			}
			catch(Exception e){
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error "+
				 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
		}
		
		else if(type.equals(IntegrationVO.UPDATE_IN_ZOHO)){
			try{
				this.evento = evento.oldEvent;
				updateinzoho();
				updateinsql();
			}
			catch(Exception e){
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error "+
				 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
		}
		
		else if(type.equals(IntegrationVO.DELETE_IN_GOOGLE)){
			try{
				 
			}
			catch(Exception e){
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error "+
				 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
		}
		
		else if(type.equals(IntegrationVO.DELETE_IN_ZOHO)){
			try{
				 
			}
			catch(Exception e){
				LogManager.getLogManager().setLog("No se pudo revertir una accion con error "+
				 "Original Error " + error + " Actual " + getError(e));
				throw e;
			}
		}
	}
	
	
	private void deleteingoogle( ) throws Exception{
		dm.gUtil.deleteUrl(evento);
	}
	
	private void deleteinzoho() throws Exception{
		dm.zUtil.delete(evento);
	}

	private void updateingoogle() throws Exception{
		dm.gUtil.updateUrl(evento);
	}

	private void updateinzoho() throws Exception{
		dm.zUtil.update(evento);
	}

private void insertingoogle() throws Exception{
	dm.gUtil.insertUrl(evento);
}

private void insertinzoho() throws Exception{
	dm.zUtil.insert(evento);
}

private void updateinsql() throws Exception{
	dm.sqlUtil.updateEvent(evento);
}

private void insertinsql() throws Exception{
	dm.sqlUtil.insertEvent(evento);
}

private void deleteinsql() throws Exception{
	dm.sqlUtil.deleteEvent(evento);
}



}
