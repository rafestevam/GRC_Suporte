package com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.parsing.validation;

import java.util.Date;

import org.xml.sax.Attributes;

import com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.adapter.IValidationAdapter;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerTransaction;

public class CustomConditionValidator extends ConditionValidator implements IValidationAdapter {
	
	public void setHandler(ValidationContentHandler validator) {
		super.setHandler(validator);
	}	

	public void setAttr(String obj_type, String name, String value) {
		Date sysDate = new Date();
		String newValue = "";
		
		if(obj_type.equals(ObjectType.RISK.getId())){
			if(name.equals("startdate")){
				newValue = String.valueOf(sysDate.getTime());
				super.setAttr(obj_type, name, newValue);
			}
			if(name.equals("assessment_activities")){
				newValue = "import";
				super.setAttr(obj_type, name, newValue);
			}
		}else{
			super.setAttr(obj_type, name, value);
		}
	}
	
	public void startElement(String obj_type, String tname, Attributes attrs) {
		super.startElement(obj_type, tname, attrs);
	}
	
	public void validate(IDataLayerTransaction transaction, String obj_type, int call, String tname) {
		super.validate(transaction, obj_type, call, tname);
	}

}
