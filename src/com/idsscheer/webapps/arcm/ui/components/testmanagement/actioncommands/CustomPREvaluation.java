package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomPREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
		
	}

	private IUserContext getJobUserContext() {
		// TODO Auto-generated method stub
		return new JobUIEnvironment(this.getFullGrantUserContext()).getUserContext();
	}

}
