package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;
import com.idsscheer.webapps.arcm.ui.framework.dialog.ForwardDialog;

public class CustomSaveControl extends ControlSaveActionCommand {

	protected void addForwardDialog() {
		super.addForwardDialog();
	}
	
	protected void addControlOption(IAppObj p_appObj, ForwardDialog p_forwardDialog) {
		super.addControlOption(p_appObj, p_forwardDialog);
	}
	
	protected void addTestdefinitionOption(IAppObj p_appObj, ForwardDialog p_forwardDialog) {
		super.addTestdefinitionOption(p_appObj, p_forwardDialog);
	}
	
	protected void addRiskOption(IAppObj p_appObj, ForwardDialog p_forwardDialog) {
		super.addRiskOption(p_appObj, p_forwardDialog);
	}
	
	protected void afterExecute(){
		//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "Curr ID: " + currID, new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		
		IAppObj currAppObj = this.formModel.getAppObj();
		IUIEnvironment currEnv = this.environment;
		String currID = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
		
		try{
			
			IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
			IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
			IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
			
			while(ctrlIterator.hasNext()){
				
				IAppObj ctrlAppObj = ctrlIterator.next();
				IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
				IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
				
				String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
				
				if(ctrlID.equals(currID)){
										
					if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
						continue;
					
					//Replicando "Descrição do Controle"
					ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_OBJECTIVE).setRawValue(
							currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_OBJECTIVE).getRawValue()
					);
					
					//Replicando "Controle Chave"
					ctrlObj.getAttribute(IControlAttributeType.ATTR_KEY_CONTROL).setRawValue(
							currAppObj.getAttribute(IControlAttributeType.ATTR_KEY_CONTROL).getRawValue()
					);
					
					//Replicando "Classificação do Controle"
					IEnumAttribute currExecution = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_EXECUTION);
					IEnumAttribute ctrlExecution = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_EXECUTION);
					ctrlExecution.setRawValue(currExecution.getRawValue());
										
					//Replicando "Gestor do Controle"
					List<IAppObj> currMgrList = currAppObj.getAttribute(IControlAttributeType.LIST_MANAGER_GROUP).getElements(this.getUserContext());
					ctrlObj.getAttribute(IControlAttributeType.LIST_MANAGER_GROUP).removeAllElements(this.getUserContext());
					int idx = ctrlObj.getAttribute(IControlAttributeType.LIST_MANAGER_GROUP).getSize();
					idx += 1;
					for(IAppObj currMgrObj : currMgrList){
						ctrlObj.getAttribute(IControlAttributeType.LIST_MANAGER_GROUP).addElement(idx, currMgrObj, this.getUserContext());
					}
					
					//Replicando "Atividades de Controle"
					ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROLS).setRawValue(
							currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROLS).getRawValue()
					);
					
					//Replicando "Frequencia do Controle"
					IEnumAttribute currFreq = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_FREQUENCY);
					IEnumAttribute ctrlFreq = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_FREQUENCY);
					ctrlFreq.setRawValue(currFreq.getRawValue());
					
					//Replicando "Tipo de Controle"
					IEnumAttribute currType = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_TYPE);
					IEnumAttribute ctrlType = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_TYPE);
					ctrlType.setRawValue(currType.getRawValue());
					
					//Replicando "Definição de Testes"
					IListAttribute currTestDef = currAppObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS);
					ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).removeAllElements(this.getUserContext());
					if(currTestDef.getSize() > 0){
						ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).addElements(
								currTestDef.getElements(this.getUserContext()), this.getUserContext());
					}
					
					//Salvando a replicação
					ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
					ctrlFacade.releaseLock(ctrlOVID);
					
				}
			}
			ctrlQuery.release();
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
		
	}

}
