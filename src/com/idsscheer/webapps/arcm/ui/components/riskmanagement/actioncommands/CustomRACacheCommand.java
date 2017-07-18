package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import java.util.List;

import com.idsscheer.utils.commands.Command;
import com.idsscheer.webapps.arcm.bl.models.form.IFormModel;
import com.idsscheer.webapps.arcm.bl.models.form.IRiskassessmentFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuotationAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.actioncommand.ActionCommandId;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandIds;
import com.idsscheer.webapps.arcm.ui.framework.support.StringUtils;

public class CustomRACacheCommand extends RiskassessmentCacheActionCommand {
	
	protected void assumeData(String[] p_excludeParameters){
		//this.changeRAClass();
		super.assumeData(p_excludeParameters);
	}
	
	protected void afterExecute(){
		this.environment.getActionCommandFactory().getObjectActionCommand(ActionCommandIds.SAVE, ObjectType.RISKASSESSMENT.getId()).executeCommand(requestContext, getDefaultTransaction());
	}

}
