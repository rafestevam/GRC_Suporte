package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;

public class CustomUpdateClassification implements ICommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
/*		// TODO Auto-generated method stub
		IChainContext chainCtx = cc.getChainContext();
		IAppObj issue = chainCtx.getTriggeringAppObj();
		IUserContext userCtx = chainCtx.getUserContext();
		IListAttribute issueRelatedObjAttr = issue.getAttribute(IIssueAttributeTypeCustom.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> issueRelatedObjs = issueRelatedObjAttr.getElements(userCtx);
		
		for(IAppObj issueRelatedObj : issueRelatedObjs){
			
			IObjectType iroObjType = issueRelatedObj.getObjectType();
			
			if(iroObjType.equals(ObjectType.ISSUE)){
				issue.getAttribute(IIssueAttributeTypeCustom.ATTR_TESTE_ATTR).setRawValue("MUDEI");
				chainCtx.allocateWriteLock(issueRelatedObj, true);
				chainCtx.save(issueRelatedObj, true);
				chainCtx.releaseWriteLock(issueRelatedObj);
			}
			
		}
		
		return CommandResult.OK;*/
		return null;
	}

}
