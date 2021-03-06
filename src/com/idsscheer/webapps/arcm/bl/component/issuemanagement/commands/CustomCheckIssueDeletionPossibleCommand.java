package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;



import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserRights;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IClientAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.RoleLevel;

public class CustomCheckIssueDeletionPossibleCommand extends CheckIssueDeletionPossibleCommand implements ICommand {
	private static final IUserContext fullGrant = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		// TODO Auto-generated method stub
		
		IAppObj issueObj = cc.getChainContext().getTriggeringAppObj();
		IEnumAttribute actionTypeAttr = issueObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem actionTypeItem = ARCMCollections.extractSingleEntry(actionTypeAttr.getRawValue(), true);
		if(actionTypeItem.getId().equals("issue")){
			return super.execute(cc);
		}else{
			return this.localExecute(cc);
		}
		
	}
	
	private CommandResult localExecute(CommandContext cc) throws BLException{
		INotificationList m = new NotificationList();
		IChainContext chainCtx = cc.getChainContext();
		IAppObj issue = chainCtx.getTriggeringAppObj();
		IUserContext ctx = chainCtx.getUserContext();
		boolean deletionAllowed = false;

		if ((!(deletionAllowed)) && (this.isManagerForThisIssue(issue, ctx))
				&& (((super.isInWorkflowState(issue, "apOpenForExecution"))
						|| (super.isInWorkflowState(issue, "apOpenForCreation"))))) {
			deletionAllowed = true;
		}

		if (!(deletionAllowed)) {
			List thisUserID = Collections.singletonList(ctx.getUserID());
			if (((OVIDUtility.hasIntersection(
					issue.getAttribute(IIssueAttributeType.LIST_CREATOR).getPersistentRawValue(), thisUserID, true))
					&& (OVIDUtility.hasIntersection(
							issue.getAttribute(IIssueAttributeType.LIST_OWNERS).getPersistentRawValue(), thisUserID,
							true))
					&& (OVIDUtility.hasIntersection(
							issue.getAttribute(IIssueAttributeType.LIST_REVIEWERS).getPersistentRawValue(), thisUserID,
							true))
					&& (super.isInWorkflowState(issue, "apOpenForExecution"))) || (super.isInWorkflowState(issue, "apOnHold"))) {
				deletionAllowed = true;
			}
		}

		if (!(deletionAllowed)) {
			List thisUserID = Collections.singletonList(ctx.getUserID());
			if ((OVIDUtility.hasIntersection(
					issue.getAttribute(IIssueAttributeType.LIST_CREATOR).getPersistentRawValue(), thisUserID, true))
					&& (super.isInWorkflowState(issue, "apOpenForCreation"))) {
				deletionAllowed = true;
			}
		}

		if (!(deletionAllowed))
			m.add(NotificationTypeEnum.INFO, "error.issue.delete.not.allowed.ERR", new String[0]);
		CommandResult result;
		if (m.isEmpty())
			result = new CommandResult(CommandResult.STATUS.OK);
		else {
			result = new CommandResult(CommandResult.STATUS.FAILED, m, null);
		}

		return result;		
	}
	
	@Override
	protected boolean isManagerForThisIssue(IAppObj issue, IUserContext ctx) {
		// TODO Auto-generated method stub
		//return super.isManagerForThisIssue(issue, ctx);
		IUserRights userRights = ctx.getUserRights();
	    boolean result = false;
	    
	    if (userRights.hasRole(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_LEVEL.CROSSCLIENT)){
		      return true;
	    }else{
	    	if (userRights.hasRole(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_LEVEL.CLIENT)){
				return true;
	    	}
	    }
	    
	    return result;
	}
	
	/*protected boolean isManagerForThisIssue(IAppObj issue, IUserContext ctx){
	    IUserRights userRights = ctx.getUserRights();
	    boolean result = false;
	    
	    if (userRights.hasRole(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_LEVEL.CROSSCLIENT)){
	      return true;
	    }else{
			List<IClientAppObj>  clients;
			Iterator i;
			IAppObj iro;
			if (userRights.hasRole(Enumerations.USERROLE_TYPE.ISSUEMANAGER, Enumerations.USERROLE_LEVEL.CLIENT)){
				result = false;
				List<IAppObj> iros = issue.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS).getElements(fullGrant);
				if (!(iros.isEmpty())){
					clients = ctx.getUserRelations().getClients(Enumerations.USERROLE_TYPE.ISSUEMANAGER, new RoleLevel[] { RoleLevel.C });
					for (i = iros.iterator(); i.hasNext(); ){
						iro = (IAppObj)i.next();
						for (IClientAppObj clientAppObj : clients){
							if (AppObjUtility.hasRelationToClient(iro, clientAppObj)) {
								return true;
							}
						}
					}
				}else {
					return false; 
				}
			}
	    }
	    return result;
	}*/
}
