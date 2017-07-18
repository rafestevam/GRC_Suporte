package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.Date;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;

public class CustomRiskSaveCommand extends BaseSaveActionCommand {
	
/*	protected void execute(){
		super.execute();
		
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "Tentando atribuir valor pra data", new String[] { getStringRepresentation(this.formModel.getAppObj()) });

		//LocalDateTime currentDate = LocalDateTime.now();
		//Date currentDate = new Date();
		//this.requestContext.setAttribute(IRiskAttributeType.STR_STARTDATE, (Object)currentDate);

		Date currentDate = new Date();
		IAppObj appObj = this.formModel.getAppObj();
		appObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).setRawValue(currentDate);
		
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "Salvando", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		super.saveSuccessful = false;
		save();
		
		//super.save();
		
		
	}*/
	
	protected void afterExecute(){
		
		super.afterExecute();
		
		Date currentDate = new Date();
		IAppObj appObj = this.formModel.getAppObj();
		
		if (appObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).getRawValue() == null){
			appObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).setRawValue(currentDate);
			//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "Salvando", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			//this.formModel.save(this.getDefaultTransaction(), IRiskAttributeType.STR_STARTDATE);
		}
	}

}