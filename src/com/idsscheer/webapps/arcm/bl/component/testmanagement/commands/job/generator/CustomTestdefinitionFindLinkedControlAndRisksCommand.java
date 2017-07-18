package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.GeneratorConditionCheckCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IControlAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ITestdefinitionAppObj;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;

public class CustomTestdefinitionFindLinkedControlAndRisksCommand
		extends TestdefinitionFindLinkedControlAndRisksCommand {
	
	protected GeneratorConditionCheckCommand.CheckResult checkGeneratorCondition(IAppObj appObj, CommandContext cc)
			throws CommandException {
		return super.checkGeneratorCondition(appObj, cc);
	}
	
	protected IControlAppObj getControlToTestdefinition(ITestdefinitionAppObj testdefinition, CommandContext cc,
			INotificationList notificationList) {
		return super.getControlToTestdefinition(testdefinition, cc, notificationList);
	}
	
	protected List<IAppObj> getRisksToControl(IControlAppObj control, CommandContext cc,
			INotificationList notificationList) {
		return super.getRisksToControl(control, cc, notificationList);
	}
	
	protected List<IAppObj> getActiveRiskList(List<IAppObj> riskAppObjs, CommandContext cc,
			INotificationList notificationList) {
		
		Locale locale = cc.getChainContext().getLocale();
		IAppObj triggeringAppObj = cc.getChainContext().getTriggeringAppObj();
		List riskAppObjList = new ArrayList(riskAppObjs.size());
		/*     */ 
		for (IAppObj appObj : riskAppObjs) {
			if (!(appObj.getVersionData().isDeleted())) {
				if(appObj.getVersionData().isHeadRevision()){
					riskAppObjList.add(appObj);
				}
			}
		}
		/*     */ 
		if (riskAppObjs.isEmpty()) {
		    notificationList.add(NotificationTypeEnum.INFO, "job.generator.job.testcase.no.risks.active.INF", new String[] { AppObjUtility.getLocalisedAppObjName(triggeringAppObj, locale) });
		}
		/*     */ 
		return riskAppObjList;
	}
	
	protected boolean checkNecessaryRiskAttributes(List<IAppObj> risks, CommandContext cc,
			INotificationList notificationList) {
		return super.checkNecessaryRiskAttributes(risks, cc, notificationList);
	}
	
}
