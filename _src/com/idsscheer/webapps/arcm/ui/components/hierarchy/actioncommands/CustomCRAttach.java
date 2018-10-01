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
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseReverseAttachmentActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomCRAttach extends BaseReverseAttachmentActionCommand {
	
	
	@Override
	protected void execute() {
		super.execute();
	}
	
	@Override
	protected void afterExecute() {
		IUserContext jobCtx = this.getJobContext();
		
		try{
			IAppObjFacade objFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.HIERARCHY);
			IAppObj corpRisk = this.formModel.getAppObj();
			CustomCorpRiskHierarchy crCalc = new CustomCorpRiskHierarchy(corpRisk, getFullGrantUserContext(), getDefaultTransaction());
			
			String residual = crCalc.calculateResidualCR();
			
			objFacade.allocateLock(corpRisk.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
			corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(residual);
			objFacade.save(corpRisk, getDefaultTransaction(), true);
			objFacade.releaseLock(corpRisk.getVersionData().getHeadOVID());
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), "");
		}
	}

	private IUserContext getJobContext() {
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext());
        return jobEnv.getUserContext();
	}

}
