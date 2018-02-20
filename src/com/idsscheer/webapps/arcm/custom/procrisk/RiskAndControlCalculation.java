package com.idsscheer.webapps.arcm.custom.procrisk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;

public class RiskAndControlCalculation {

	private List<IAppObj> controlList;

	public RiskAndControlCalculation(List<IAppObj> controlList) {
		this.controlList = controlList;
	}
	
	public Map<String, String> calculateControlRate(DefLineEnum defLine) {
		
		Map<String, String> returnMap = new HashMap<String, String>();
		double countInef = 0;
	    double countTotal = 0;
	    double riskVuln = 0;
		
		for(IAppObj controlObj : controlList){
			
			countTotal += 1;
			
			if(defLine.equals(DefLineEnum.LINE_1)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE);
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("ineffective"))
					countInef += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_2)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE);
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("ineffective"))
					countInef += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_3)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE);
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("ineffective"))
					countInef += 1;
			}
			if(defLine.equals(DefLineEnum.LINE_F)){
				IStringAttribute status1LineAttr = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_FINAL);
				if((!status1LineAttr.isEmpty()) && status1LineAttr.getRawValue().equals("ineffective"))
					countInef += 1;
			}
			
		}
		
		riskVuln = (countInef / countTotal);
		
		returnMap.put("classification", this.riskClassification(riskVuln));
		returnMap.put("rate", String.valueOf(riskVuln));
		returnMap.put("total", String.valueOf(countTotal));
		returnMap.put("ineffective", String.valueOf(countInef));
		
		return returnMap;
		
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

}
