package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList);
		
		this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
		
		this.notificationDialog.addInfo("Quantidade de Controles: " + String.valueOf(controlList.size()));
		
		//Resultados - 1ª Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 1ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_1)));
		this.notificationDialog.addInfo("Controles Totais - 1ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_1)));
		this.notificationDialog.addInfo("Ponderação - 1ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_1)));
		this.notificationDialog.addInfo("Classificação - 1ª Linha: " + this.getMapValues(objCalc, "classification", DefLineEnum.LINE_1));
		
		//Resultados - 2ª Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 2ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_2)));
		this.notificationDialog.addInfo("Controles Totais - 2ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_2)));
		this.notificationDialog.addInfo("Ponderação - 2ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_2)));
		this.notificationDialog.addInfo("Classificação - 2ª Linha: " + this.getMapValues(objCalc, "classification", DefLineEnum.LINE_2));
		
		//Resultados - 3ª Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 3ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_3)));
		this.notificationDialog.addInfo("Controles Totais - 3ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_3)));
		this.notificationDialog.addInfo("Ponderação - 3ª Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_3)));
		this.notificationDialog.addInfo("Classificação - 3ª Linha: " + this.getMapValues(objCalc, "classification", DefLineEnum.LINE_3));
		
		//Resultados - Final
		this.notificationDialog.addInfo("Controles Inefetivos - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_F)));
		this.notificationDialog.addInfo("Controles Totais - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_F)));
		this.notificationDialog.addInfo("Ponderação - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_F)));
		this.notificationDialog.addInfo("Classificação - Final: " + this.getMapValues(objCalc, "classification", DefLineEnum.LINE_F));
		
	}

	private IUserContext getJobUserContext() {
		// TODO Auto-generated method stub
		return new JobUIEnvironment(this.getFullGrantUserContext()).getUserContext();
	}
	
	private Object getMapValues(RiskAndControlCalculation objCalc, String valueType, DefLineEnum defLine) {
		Object objReturn = null;
		Map<String, String> mapReturn = objCalc.calculateControlRate(defLine);
		
		Iterator<Entry<String, String>> iterator = mapReturn.entrySet().iterator();
		while(iterator.hasNext()){
			
			Entry<String, String> entry = iterator.next();
			
			if(entry.getKey().equals("classification") && valueType.equals("classification"))
				objReturn = (String)entry.getValue();
			
			if(entry.getKey().equals("rate") && valueType.equals("rate"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("total") && valueType.equals("total"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("ineffective") && valueType.equals("ineffective"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
		}
		return objReturn;
	}

}
