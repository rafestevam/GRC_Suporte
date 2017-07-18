package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Collections;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class CustomUpdateReviewerStatus implements ICommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		// TODO Auto-generated method stub
		IAppObj issueAppObj = cc.getChainContext().getTriggeringAppObj();
		
		IEnumAttribute actionTypeAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		if(!(actionTypeAttr.equals(null))){
			IEnumerationItem actionTypeItem = ARCMCollections.extractSingleEntry(actionTypeAttr.getRawValue(), true);
			if(actionTypeItem.getId().equals("issue")){
				IEnumAttribute ownerStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS);
				IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
				if(ownerStatus.equals(EnumerationsCustom.CENUM_IS_OWNER_STATUS.ATTENDED)){
					issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
				}
				if(ownerStatus.equals(EnumerationsCustom.CENUM_IS_OWNER_STATUS.RISK_ASSUMED)){
					issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
				}
				if(ownerStatus.equals(EnumerationsCustom.CENUM_IS_OWNER_STATUS.SETTLED)){
					issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
				}
				if(ownerStatus.equals(EnumerationsCustom.CENUM_IS_OWNER_STATUS.TO_BE_REVIEWED)){
					issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
				}
			}
			if(actionTypeItem.getId().equals("actionplan")){
				
				IEnumAttribute creatorStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS);
				IEnumerationItem creatorStatus = ARCMCollections.extractSingleEntry(creatorStatusAttr.getRawValue(), true);
				if(!this.checkCreatorToReviewer(issueAppObj, creatorStatus)){
					IEnumAttribute ownerStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_OWNER_STATUS);
					IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
					if(ownerStatus.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.FUP)){
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS)
						);
					}
					if(ownerStatus.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.RISK_ASSUMED)){
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS)
						);
					}
				}
			}
		}
		
		return CommandResult.OK;
		
	}
	
	private boolean checkCreatorToReviewer(IAppObj issue, IEnumerationItem creatorStatus){
		
		boolean checked = false;
		
		if(creatorStatus.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.FUP)){
			issue.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS)
			);
			checked = true;
		}
		if(creatorStatus.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.RISK_ASSUMED)){
			issue.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS)
			);
			checked = true;
		}
		
		return checked;
		
	}

}
