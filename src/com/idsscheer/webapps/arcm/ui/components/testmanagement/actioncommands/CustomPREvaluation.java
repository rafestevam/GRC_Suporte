package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.custom.procrisk.DefLineEnum;
import com.idsscheer.webapps.arcm.custom.procrisk.RiskAndControlCalculation;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomPREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		try{
		
		IAppObj riskObj = this.formModel.getAppObj();
		List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(getFullGrantUserContext());
		RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList, FacadeFactory.getInstance().getAppObjFacade(getFullGrantUserContext(), ObjectType.CONTROL), this.getDefaultTransaction());
		
		this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
		
		this.notificationDialog.addInfo("Quantidade de Controles do Risco: " + String.valueOf(controlList.size()));
		
		//Resultados - 1� Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 1� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_1)));
		//this.notificationDialog.addInfo("Controles Efetivos - 1� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_1)));
		//this.notificationDialog.addInfo("Controles Totais - 1� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_1)));
		this.notificationDialog.addInfo("Pondera��o - 1� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_1)) + "%");
		String classification1 = this.getMapValues(objCalc, "classification", DefLineEnum.LINE_1).equals("") ? "N�o Avaliado" : (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_1);
		this.notificationDialog.addInfo("Classifica��o - 1� Linha: " + classification1);
		
		//Resultados - 2� Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 2� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_2)));
		//this.notificationDialog.addInfo("Controles Efetivos - 2� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_2)));
		//this.notificationDialog.addInfo("Controles Totais - 2� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_2)));
		this.notificationDialog.addInfo("Pondera��o - 2� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_2)) + "%");
		String classification2 = this.getMapValues(objCalc, "classification", DefLineEnum.LINE_2).equals("") ? "N�o Avaliado" : (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_2);
		this.notificationDialog.addInfo("Classifica��o - 2� Linha: " + classification2);
		
		//Resultados - 3� Linha
		this.notificationDialog.addInfo("Controles Inefetivos - 3� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_3)));
		//this.notificationDialog.addInfo("Controles Efetivos - 3� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_3)));
		//this.notificationDialog.addInfo("Controles Totais - 3� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_3)));
		this.notificationDialog.addInfo("Pondera��o - 3� Linha: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_3)) + "%");
		String classification3 = this.getMapValues(objCalc, "classification", DefLineEnum.LINE_3).equals("") ? "N�o Avaliado" : (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_3);
		this.notificationDialog.addInfo("Classifica��o - 3� Linha: " + classification3);
		
		//Resultados - Final
		this.notificationDialog.addInfo("Controles Inefetivos - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_F)));
		//this.notificationDialog.addInfo("Controles Efetivos - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_F)));
		//this.notificationDialog.addInfo("Controles Totais - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_F)));
		this.notificationDialog.addInfo("Pondera��o - Final: " + String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_F)) + "%");
		String classificationF = this.getMapValues(objCalc, "classification", DefLineEnum.LINE_F).equals("") ? "N�o Avaliado" : (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_F);
		this.notificationDialog.addInfo("Classifica��o - Final: " + classificationF);
		
		}catch(Exception e){
			this.notificationDialog.addError("Erro ao calcular risco residual");
		}
		
	}

	private IUserContext getJobUserContext() {
		// TODO Auto-generated method stub
		return new JobUIEnvironment(this.getFullGrantUserContext()).getUserContext();
	}
	
	private Object getMapValues(RiskAndControlCalculation objCalc, String valueType, DefLineEnum defLine) throws Exception {
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
			
			if(entry.getKey().equals("effective") && valueType.equals("effective"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
		}
		return objReturn;
	}

}
