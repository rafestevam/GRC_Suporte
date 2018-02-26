package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseReverseDetachmentActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomCRDetach extends BaseReverseDetachmentActionCommand {

	@Override
	protected void execute() {
		super.execute();
	}
	
	@Override
	protected void afterExecute() {
		//preSaveCorpRisk(objFacade, corpRisk);
		
		try{
			
			IUserContext jobCtx = this.getJobContext();
			IAppObjFacade objFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.HIERARCHY);
			IAppObj corpRisk = this.formModel.getAppObj();
			
			CustomCorpRiskHierarchy crCalc = new CustomCorpRiskHierarchy(corpRisk, getFullGrantUserContext(), getDefaultTransaction());
			
			String residual = crCalc.calculateResidualCR();
			
			objFacade.allocateLock(corpRisk.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
			corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(residual);
			objFacade.save(corpRisk, getDefaultTransaction(), true);
			objFacade.releaseLock(corpRisk.getVersionData().getHeadOVID());
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.ERROR, e.getMessage(), "");
		}
	}

	/*private void preSaveCorpRisk(IAppObjFacade objFacade, IAppObj corpRisk) {
		try {
			objFacade.save(corpRisk, getDefaultTransaction(), CONTINUE_EXECUTION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.formModel.addControlInfoMessage(NotificationTypeEnum.ERROR, e.getMessage(), "");
		}
	}*/
	
	private IUserContext getJobContext() {
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext());
        return jobEnv.getUserContext();
	}
	
}
