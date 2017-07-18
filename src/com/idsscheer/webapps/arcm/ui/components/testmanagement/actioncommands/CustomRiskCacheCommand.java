package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;

public class CustomRiskCacheCommand extends BaseCacheActionCommand {
	
	/*
	protected void assumeData(String[] p_excludeParameters){
		super.assumeData(p_excludeParameters);
		
		IRiskassessmentFormModel model = (IRiskassessmentFormModel) this.formModel;
		
		List excludeParameters = Arrays.asList(p_excludeParameters);
		String[] parameters = this.requestContext.getParameters("__editableFields");
		if (null == parameters) {
			return;
		}
		for(String parameter : parameters){
			
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, parameter, new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			
		}
		
	}*/
	
	/*
	protected void execute(){
		super.execute();
		
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "Comando Funcionando", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		
		
		//LocalDateTime currentDate = LocalDateTime.now();
		Date currentDate = new Date();
		//this.requestContext.setAttribute(IRiskAttributeType.STR_STARTDATE, (Object)currentDate);
		IAppObj appObj = this.formModel.getAppObj();
		appObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).setRawValue(currentDate);
		
	}*/

}
