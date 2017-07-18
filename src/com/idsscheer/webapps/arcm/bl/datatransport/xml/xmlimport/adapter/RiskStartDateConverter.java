package com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.adapter;

import java.util.Calendar;

import com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.parsing.AAMMLImportException;

public class RiskStartDateConverter implements IConverterAdapter {

	@Override
	public String convert(String paramString) throws AAMMLImportException {
		// TODO Auto-generated method stub
		Calendar sysDate = Calendar.getInstance();
		if(paramString.equals("startdate")){
			return String.valueOf(sysDate.getTimeInMillis());
		}else{
			return null;
		}
	}

	@Override
	public String getConvertMessages() {
		// TODO Auto-generated method stub
		return "converting Risk Start Date";
	}

}
