package com.idsscheer.webapps.arcm.custom.procrisk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;

public class RiskAndControlCalculation {

	private List<IAppObj> controlList;
	private double countInef;
	private double countTotal;
	private double countEf;
	private double countNA;
	private IAppObjFacade facade;
	private ITransaction transaction;
	
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
			
			IAppObj controlObj = null;
			
			controlObj = facade.load(controlObjIt.getVersionData().getHeadOVID(), this.transaction, true);
			
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
				if(statusFLineAttr.isEmpty())
					this.countNA += 1;
				if(status1LineAttr.isEmpty() && status2LineAttr.isEmpty() && status3LineAttr.isEmpty())
					this.countNA += 1;
			}
			
			countTotal += 1;
			
			facade.releaseLock(controlObj);
			
		}
		
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
	
	private String riskClassification(double riskVuln) {
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixo";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "M�dio";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alto";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alto";
		}
		
		return riskClassif;
	}

}
