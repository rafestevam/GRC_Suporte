package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;

public class CustomIRODeleteActionCommand extends IssueRelevantObjectDeleteActionCommand {
	
	@Override
	protected void loadApplicationObjects() {
		// TODO Auto-generated method stub
		super.loadApplicationObjects();
		
		List<IOVID> iros = this.environment.getUserContext().getUserRelations().getMarkedAsIssueRelevantObjectIds();
		for (IOVID id : iros) {
			for(IOVID iovid : this.requestContext.getSelectedOVIDList()){
				if(iovid.getId() == id.getId()){
					
					try {
						IAppObj loadedAppObj = this.appObjFacade.load(id, true);
						if (null != loadedAppObj) {
							this.model.addObject(loadedAppObj);
							this.environment.getAppObjFacade(loadedAppObj.getObjectType())
									.allocateWriteLock(loadedAppObj.getVersionData().getOVID());
						}
					} catch (RightException e) {
						this.log.error("loadApplicationObjects failed.", e);
					}
					
				}
			}
		}
		
	}

}
