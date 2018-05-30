package com.idsscheer.webapps.arcm.custom.procrisk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;

public class RiskAndControlCalculation {

	private List<IAppObj> controlList;
	private double countInef;
	private double countTotal;
	private double countEf;
	private double countNA;
	private IAppObjFacade facade;
	private ITransaction transaction;
	private Logger log = Logger.getLogger(RiskAndControlCalculation.class);
	private long objID = 0;
	private String lineAlreadyTested = "";
	
	public RiskAndControlCalculation(List<IAppObj> controlList) {
		this.controlList = controlList;
		this.countInef = 0;
		this.countTotal = 0;
		this.countEf = 0;

	}
	public RiskAndControlCalculation(List<IAppObj> controlList, IAppObjFacade facade, ITransaction transaction) {
		this.controlList = controlList;
		this.facade = facade;
		this.transaction = transaction;
		this.countInef = 0;
		this.countTotal = 0;
		this.countEf = 0;
		this.countNA = 0;

	}
	public RiskAndControlCalculation(List<IAppObj> controlList, IAppObjFacade facade, ITransaction transaction, long objID) {
		this.controlList = controlList;
		this.facade = facade;
		this.transaction = transaction;
		this.objID = objID;
		this.countInef = 0;
		this.countTotal = 0;
		this.countEf = 0;
		this.countNA = 0;
		
	}
	
	public RiskAndControlCalculation(List<IAppObj> controlList, double countInef, double countEf, double countTotal) {
		this.controlList = controlList;
		this.countInef = countInef;
		this.countEf = countEf;
		this.countTotal = countTotal;
	}
	
	public Map<String, String> calculateControlRate(DefLineEnum defLine) throws Exception {
		
		Map<String, String> returnMap = new HashMap<String, String>();
	    double riskVuln = 0;
	    double countInef = 0;
	    double countEf = 0;
	    double countTotal = 0;
	    
	    this.countNA = 0;
	    //double countNA = 0;
	    
	    //countInef += this.countInef;
	    //countTotal += this.countTotal;
	    //countEf += this.countEf;
		
		for(IAppObj controlObjIt : controlList){
			
			IAppObj controlObj = controlObjIt;
			
			log.info("*****************************************************************");
			log.info("Controle: " + controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue() + " : " + controlObj.getObjectId());
			log.info("Linha Testada: " + defLine.name());

			if((objID != 0) && (controlObj.getObjectId() == objID) && (lineAlreadyTested.equals(defLine.name()))){
				countTotal += 1;
				log.info("Contagem Inefetivos: " + this.countInef);
				log.info("Contagem Não Avaliados: " + this.countNA);
				log.info("Contagem Efetivos: " + this.countEf);
				log.info("Contagem Controles: " + countTotal);
				log.info("*****************************************************************");
				continue;
			}
			
			//controlObj = facade.load(controlObjIt.getVersionData().getHeadOVID(), this.transaction, true);
			
			//countTotal += 1;
			
			if(defLine.equals(DefLineEnum.LINE_1)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE);
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("inefetivo")){
					this.countInef += 1;
					this.countNA = 0;
				}
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("efetivo")){
					this.countEf += 1;
					this.countNA = 0;
				}
				if(status1LineAttr.isEmpty() && (this.countInef == 0 && this.countEf == 0))
					this.countNA += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_2)){
				IStringAttribute status2LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE);
				if((!status2LineAttr.isEmpty()) && status2LineAttr.getRawValue().equals("inefetivo")){
					this.countInef += 1;
					this.countNA = 0;
				}
				if((!status2LineAttr.isEmpty()) && status2LineAttr.getRawValue().equals("efetivo")){
					this.countEf += 1;
					this.countNA = 0;
				}
				if(status2LineAttr.isEmpty() && (this.countInef == 0 && this.countEf == 0))
					this.countNA += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_3)){
				IStringAttribute status3LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE);
				if((!status3LineAttr.isEmpty()) && status3LineAttr.getRawValue().equals("inefetivo")){
					this.countInef += 1;
					this.countNA = 0;
				}
				if((!status3LineAttr.isEmpty()) && status3LineAttr.getRawValue().equals("efetivo")){
					this.countEf += 1;
					this.countNA = 0;
				}
				if(status3LineAttr.isEmpty() && (this.countInef == 0 && this.countEf == 0))
					this.countNA += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_F)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE);
				IStringAttribute status2LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE);
				IStringAttribute status3LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE);
				IStringAttribute statusFLineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_FINAL);
				if((!statusFLineAttr.isEmpty()) && statusFLineAttr.getRawValue().equals("inefetivo"))
					this.countInef += 1;
				if((!statusFLineAttr.isEmpty()) && statusFLineAttr.getRawValue().equals("efetivo"))
					this.countEf += 1;
				if(statusFLineAttr.isEmpty() && (this.countInef == 0 && this.countEf == 0))
					this.countNA += 1;
				if(status1LineAttr.isEmpty() && status2LineAttr.isEmpty() && status3LineAttr.isEmpty())
					this.countNA += 1;
			}
			
			
			countTotal += 1;
			
			log.info("Contagem Inefetivos: " + this.countInef);
			log.info("Contagem Não Avaliados: " + this.countNA);
			log.info("Contagem Efetivos: " + this.countEf);
			log.info("Contagem Controles: " + countTotal);
			log.info("*****************************************************************");
			//facade.releaseLock(controlObj);
			
		}
		
//		if(defLine.equals(DefLineEnum.LINE_1)){
//			log.info("1a Linha: Contagem de controles efetivos: " + this.countEf);
//			log.info("1a Linha: Contagem de controles inefetivos: " + this.countInef);
//			log.info("1a Linha: Contagem de controles Não Avaliados: " + this.countNA);
//		}
//		if(defLine.equals(DefLineEnum.LINE_2)){
//			log.info("2a Linha: Contagem de controles efetivos: " + this.countEf);
//			log.info("2a Linha: Contagem de controles inefetivos: " + this.countInef);
//			log.info("2a Linha: Contagem de controles Não Avaliados: " + this.countNA);
//		}
//		if(defLine.equals(DefLineEnum.LINE_3)){
//			log.info("3a Linha: Contagem de controles efetivos: " + this.countEf);
//			log.info("3a Linha: Contagem de controles inefetivos: " + this.countInef);
//			log.info("3a Linha: Contagem de controles Não Avaliados: " + this.countNA);
//		}
//		if(defLine.equals(DefLineEnum.LINE_F)){
//			log.info("Final: Contagem de controles efetivos: " + this.countEf);
//			log.info("Final: Contagem de controles inefetivos: " + this.countInef);
//			log.info("Final: Contagem de controles Não Avaliados: " + this.countNA);
//		}
//		
//		log.info("******************************************************************");
//		log.info("Total de Controles: " + countTotal);
//		log.info("******************************************************************");
		
		try{
			riskVuln = (this.countInef / countTotal);
		}catch(Exception e){
			throw e;
		}
		
		if(this.countNA == 0){
			returnMap.put("classification", this.riskClassification(riskVuln));
		}else{
			returnMap.put("classification", "");
		}
		
		returnMap.put("rate", String.valueOf((riskVuln * 100)));
		returnMap.put("total", String.valueOf(countTotal));
		returnMap.put("ineffective", String.valueOf(this.countInef));
		returnMap.put("effective", String.valueOf(this.countEf));
		
		this.countInef = 0;
		this.countEf = 0;
		
		return returnMap;
		
	}
	
	public double getCountInef() {
		return countInef;
	}
	public void setCountInef(double countInef) {
		this.countInef = countInef;
	}
	public double getCountEf() {
		return countEf;
	}
	public void setCountEf(double countEf) {
		this.countEf = countEf;
	}
	
	private String riskClassification(double riskVuln) {
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixo";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "Médio";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alto";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alto";
		}
		
		return riskClassif;
	}
	
	public String getLineAlreadyTested() {
		return lineAlreadyTested;
	}
	
	public void setLineAlreadyTested(String lineAlreadyTested) {
		this.lineAlreadyTested = lineAlreadyTested;
	}

}
