package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseDetachmentActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;

public class CustomDetachmentActionCommand extends BaseDetachmentActionCommand {

	protected void execute() {
		super.execute();
	}
	
	protected void afterExecute(){
		//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "attachment", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		
		IAppObj currAppObj = this.formModel.getAppObj();
		IUIEnvironment currEnv = this.environment;
		String currID = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
		
		try{
			if(currAppObj.getObjectType().equals(ObjectType.CONTROL)){
				this.detachTestDef2Control(currAppObj, currEnv, currID);
				this.detachCETask2Control(currAppObj, currEnv, currID);
			}
			/*if(currAppObj.getObjectType().equals(ObjectType.CONTROLEXECUTIONTASK)){
				this.detachCETask2Control(currAppObj, currEnv, currID);
			}*/
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}

	}
	
	private void detachTestDef2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		
		try{
		
			while(ctrlIterator.hasNext()){
				
				IAppObj ctrlAppObj = ctrlIterator.next();
				IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
				IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
				
				String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
				
				if(ctrlID.equals(currID)){
										
					if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
						continue;
					
					//Replicando deleção da "Definição de Testes"
					ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).removeAllElements(this.getUserContext());
										
					//Salvando a replicação
					ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
					ctrlFacade.releaseLock(ctrlOVID);
					
				}
			}
			ctrlQuery.release();		
		
		}catch(Exception e){
			throw e;
		}
	}
	
	private void detachCETask2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		
		try{
		
			while(ctrlIterator.hasNext()){
				
				IAppObj ctrlAppObj = ctrlIterator.next();
				IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
				IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
				
				String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
				
				if(ctrlID.equals(currID)){
										
					if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
						continue;
					
					//Replicando deleção da "Definição de Testes"
					ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).removeAllElements(this.getUserContext());
										
					//Salvando a replicação
					ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
					ctrlFacade.releaseLock(ctrlOVID);
					
				}
			}
			ctrlQuery.release();		
		
		}catch(Exception e){
			throw e;
		}
	}	

}
