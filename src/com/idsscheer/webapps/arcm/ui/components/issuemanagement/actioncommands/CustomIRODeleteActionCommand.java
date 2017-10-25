package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.workflow.IWorkflowTransition;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IIssuerelevantobjectAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssuerelevantobjectAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;

public class CustomIRODeleteActionCommand extends IssueRelevantObjectDeleteActionCommand {
	
	@Override
	protected void loadApplicationObjects() {
		// TODO Auto-generated method stub
		super.loadApplicationObjects();
		
		for(IOVID id : this.requestContext.getSelectedOVIDList()){
			try{
				IAppObj loadedAppObj = this.appObjFacade.load(id, true);
				if (null != loadedAppObj){
					long objid = loadedAppObj.getAttribute(IIssuerelevantobjectAttributeType.ATTR_OBJECT_ID).getRawValue();
					
					IAppObjQuery objQuery = FacadeFactory.getInstance().getAppObjFacade(getFullGrantUserContext(), ObjectType.ISSUERELEVANTOBJECT).createQuery();
					objQuery.addRestriction(QueryRestriction.eq(IIssuerelevantobjectAttributeType.ATTR_OBJECT_ID, objid));
					objQuery.addRestriction(QueryRestriction.eq(IIssuerelevantobjectAppObj.BASE_ATTR_CREATOR_USER_ID, Long.valueOf(this.environment.getUserContext().getUserID().getId())));
					objQuery.addRestriction(QueryRestriction.isNull(IIssuerelevantobjectAppObj.ATTR_IRO_ISSUEID));
					
					IAppObjIterator it = objQuery.getResultIterator();
					while(it.hasNext()){
						IAppObj deleteAppObj = it.next();
						if(!this.model.getCurrentObject().equals(deleteAppObj)){
							this.model.addObject(deleteAppObj);
							this.environment.getAppObjFacade(deleteAppObj.getObjectType()).allocateWriteLock(deleteAppObj.getVersionData().getOVID());	
						}
					}
					
				}
				
			}catch (RightException e) {
				this.log.error("loadApplicationObjects failed.", e);
			}
		}
		
	}

}
