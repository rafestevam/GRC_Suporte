package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomProcRiskResidualCalc;
import com.idsscheer.webapps.arcm.custom.procrisk.DefLineEnum;
import com.idsscheer.webapps.arcm.custom.procrisk.RiskAndControlCalculation;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomPREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		IAppObj riskObj = this.formModel.getAppObj();
		List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(getFullGrantUserContext());
		RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList, new Double(0), new Double(0));
		
		this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
		
		//Resultados - 1ª Linha
		Map<String, String> map1Line = objCalc.calculateControlRate(DefLineEnum.LINE_1);
		
		this.notificationDialog.addInfo("TESTE");
		
	}

	private IUserContext getJobUserContext() {
		// TODO Auto-generated method stub
		return new JobUIEnvironment(this.getFullGrantUserContext()).getUserContext();
	}

}
