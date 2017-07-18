package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Collections;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class CustomUpdateCreatorStatus implements ICommand {
	//final Logger log = Logger.getLogger(CustomUpdateCreatorStatus.class.getName());
	static final Logger log = new Logger();
/*	public CustomUpdateCreatorStatus() {
		// TODO Auto-generated constructor stub
	}*/

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		// TODO Auto-generated method stub
		IAppObj issueAppObj = cc.getChainContext().getTriggeringAppObj();
		//log.info("Objeto Corrente Obtido: " + issueAppObj.getAttribute(IIssueAttributeType.ATTR_NAME).getRawValue());
		log.info(this.getClass().getName(), "Objeto Corrente Obtido: " + issueAppObj.getAttribute(IIssueAttributeType.ATTR_NAME).getRawValue());
		//IUserContext userCtx = cc.getChainContext().getUserContext();
		String issueType = cc.getCommandXMLParameter("issueType");
		
		try{
		
			IEnumAttribute actionTypeAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
			//log.info("actionTypeAttr: " + actionTypeAttr.toString());
			log.info(this.getClass().getName(), "actionTypeAttr: " + actionTypeAttr.toString());
			if(!actionTypeAttr.equals(null)){
				IEnumerationItem actionTypeItem = ARCMCollections.extractSingleEntry(actionTypeAttr.getRawValue(), true);
				log.info(this.getClass().getName(), "actionTypeItem: " + actionTypeItem.getId());
				if(actionTypeItem.getId().equals("issue")){// || issueType.equals("issue")){
					log.info(this.getClass().getName(), "apontamento é issue!");
					IEnumAttribute creatorStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS);
					IEnumerationItem creatorStatus = ARCMCollections.extractSingleEntry(creatorStatusAttr.getRawValue(), true);
					if((!creatorStatus.equals(EnumerationsCustom.CENUM_IS_CREATOR_STATUS.NEW)) && 
						(!creatorStatus.equals(EnumerationsCustom.CENUM_IS_CREATOR_STATUS.IN_REVIEW))){
						if(creatorStatus.equals(EnumerationsCustom.CENUM_IS_CREATOR_STATUS.PLEASE_SELECT)){
							issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_CREATOR_STATUS.NEW)
							);
							log.info(this.getClass().getName(), "status issue é: " + EnumerationsCustom.CENUM_IS_CREATOR_STATUS.NEW.getId());
						}
					}
				}
				if(actionTypeItem.getId().equals("actionplan")){
					log.info(this.getClass().getName(), "apontamento é actionplan!");
					IEnumAttribute creatorStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS);
					IEnumerationItem creatorStatus = ARCMCollections.extractSingleEntry(creatorStatusAttr.getRawValue(), true);
					if(creatorStatus.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PLEASE_SELECT)){
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.NEW)
						);
						log.info(this.getClass().getName(), "status actionplan é: " + EnumerationsCustom.CENUM_IS_CREATOR_STATUS.NEW.getId());
					}
				}
			}
			
			return CommandResult.OK;
			
		}catch(Exception bl){
			log.info(this.getClass().getName(), "CustomUpdateCreatorStatus Exception: " + bl.getMessage());
			throw bl;
		}
		//return null;
	}

}
