package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.dialog.IDialogRequest;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;

public class CustomIssueCreateActionCommand extends IssueCreateActionCommand {

	@Override
	protected void setCustomDialogInitParameter(IDialogRequest p_dialogRequest) {
		// TODO Auto-generated method stub
		super.setCustomDialogInitParameter(p_dialogRequest);
	}
	
	@Override
	protected boolean isRequestPerfromedOnIROList() {
		// TODO Auto-generated method stub
		return super.isRequestPerfromedOnIROList();
	}
	
	@Override
	protected List<IAppObj> loadIssueRelevantObjects() {
		// TODO Auto-generated method stub
		List<IAppObj> iroBufferList = super.loadIssueRelevantObjects();
		List<IAppObj> iroList = new ArrayList<>();
		///Set<IAppObj> iroSet = new HashSet<>();
		
		iroBufferList.sort(new Comparator<IAppObj>(){
			@Override
			public int compare(IAppObj ant, IAppObj post) {
				// TODO Auto-generated method stub
				String objTypeAnt  = ant.getObjectType().getId();
				String objTypePost = post.getObjectType().getId();
				return objTypeAnt.compareTo(objTypePost);
			}
		});
				
		for(IAppObj iroBuffer : iroBufferList){
			boolean isFound = false;
			for (IAppObj iroObj : iroList) {
				
				if(iroObj.hasAttributeType(IRiskAttributeTypeCustom.STR_RISK_ID) && iroBuffer.hasAttributeType(IRiskassessmentAttributeTypeCustom.STR_RISK_ID)){
					String riskIDBuffer = iroBuffer.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RISK_ID).getRawValue();
					String riskID = iroObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RISK_ID).getRawValue();
					if(riskIDBuffer.equals(riskID)){
						isFound = true;
						break;
					}
				}
				
				if(iroObj.hasAttributeType(IControlAttributeTypeCustom.STR_CONTROL_ID) && iroBuffer.hasAttributeType(IControlAttributeTypeCustom.STR_CONTROL_ID)){
					String controlIDBuffer = iroBuffer.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue();
					String controlID = iroObj.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue();
					if(controlIDBuffer.equals(controlID)){
						isFound = true;
						break;
					}
				}
				
				if(iroObj.getObjectId() == iroBuffer.getObjectId()){
					isFound = true;
					break;
				}
			}
			if(!isFound) iroList.add(iroBuffer);
		}
		
		return iroList;
		
	}

}

