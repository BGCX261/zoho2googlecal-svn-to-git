package com.incompanysolutions.ZohoSyncCalendar;

import java.util.ArrayList;
import java.util.List;

import com.incompany.ZohoSyncCalendar.Vo.EventVO;
import com.incompany.ZohoSyncCalendar.Vo.IntegrationVO;

public class CalendarEventIntegrator {

	private DataModel dm ;
	
	private List<IntegrationVO> ints = new ArrayList<IntegrationVO>();
	
	public CalendarEventIntegrator(){
		dm	= DataModel.getModel();
	}
	
	public void saveEvents() throws Exception{
		for(IntegrationVO inte : ints){
			inte.performAction();
		}
	}
	
	public void processEvents(){
		//Borrar Eventos del Otro que han sido borrados en Uno
		for(EventVO sEvent : dm.sqlUserEvents){
			EventVO zEvent = dm.zEvents.get(sEvent.zohoId);
			EventVO gEvent = dm.gEvents.get(sEvent.googleId);
			
			//Si no existe en cualquiera de los dos >> algun evento que esta en MySQL. Este se borra de Zoho , Google
			// y MYSQL
			if(zEvent==null && gEvent==null){
				 
			}
			else if(zEvent==null){
				IntegrationVO inte = new IntegrationVO();
				inte.doIntegration(IntegrationVO.DELETE_IN_GOOGLE, sEvent);
				ints.add(inte);
				dm.gEvents.remove(gEvent.googleId);
			}
			
			else if(gEvent==null){
				IntegrationVO inte = new IntegrationVO();
				inte.doIntegration(IntegrationVO.DELETE_IN_ZOHO, sEvent);
				ints.add(inte);
				dm.zEvents.remove(zEvent.zohoId);
				 
			}
			else{
			//Actualizar Eventos que cambiaron en algun de los dos
			boolean changeZoho = sEvent.changed(zEvent);
			boolean changeGoogle = sEvent.changed(gEvent);
			
			if( !changeZoho && !changeGoogle){
				dm.zEvents.remove(zEvent.zohoId);
				dm.gEvents.remove(gEvent.googleId);
			}
			
			else if( changeZoho && changeGoogle){
				LogManager.getLogManager().setLog("El Evento " + sEvent.title + " cambio en Zoho y Google no se puede integrar");
				LogManager.getLogManager().containsError=true;
				LogManager.getLogManager().needsAction=true;
				dm.zEvents.remove(zEvent.zohoId);
				dm.gEvents.remove(gEvent.googleId);
			}
			
			else if(changeZoho){
				sEvent.pasteEvent(zEvent);
				IntegrationVO inte = new IntegrationVO();
				inte.doIntegration(IntegrationVO.UPDATE_IN_GOOGLE, sEvent);
				ints.add(inte);
				dm.zEvents.remove(zEvent.zohoId);
				dm.gEvents.remove(gEvent.googleId);
			}
			
			else if(changeGoogle){
				sEvent.pasteEvent(gEvent);
				IntegrationVO inte = new IntegrationVO();
				inte.doIntegration(IntegrationVO.UPDATE_IN_ZOHO, sEvent);
				ints.add(inte);
				dm.zEvents.remove(zEvent.zohoId);
				dm.gEvents.remove(gEvent.googleId);
			}
			}
		}
		
		//Se repasan los eventos de Google buscando cual NO ESTA en MYSQL, se agrega en Zoho  
		// y se agrega en MYSQL con ambos ID's; 
		for(EventVO gEvent : dm.gEvents.values()){
			IntegrationVO inte = new IntegrationVO();
			inte.doIntegration(IntegrationVO.INSERT_IN_ZOHO, gEvent);
			ints.add(inte);
		}
		
		//Tambien se repasan los eventos de Zoho buscando CUAL NO ESTAN EN MYSQL y se agrega en Google y
		// en MYSQL con ZohoId y Google Id
		for(EventVO zEvent : dm.zEvents.values()){
			IntegrationVO inte = new IntegrationVO();
			inte.doIntegration(IntegrationVO.INSERT_IN_GOOGLE, zEvent);
			ints.add(inte);
		}	
	}	
}
