package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.Calendar;
import java.util.Date;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;

public class CustomIssueDateCommand extends IssueCacheActionCommand {
		

	//@SuppressWarnings("deprecation")
	
	private static final com.idsscheer.batchserver.logging.Logger debuglog = new com.idsscheer.batchserver.logging.Logger();	
	private static final boolean DEBUGGER_ON = true;
	
	protected void afterExecute(){
		
		super.afterExecute();
		
		// Preencher a data corrente no apontamento	
		Date data = new Date();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String years = ""+year;		
		
		IAppObj appObj = this.formModel.getAppObj();
		
		IDateAttribute issuedate = appObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ISSUE_DATE);
		this.displayLog("Issue Date : " + issuedate);
		Date issuedateValue = issuedate.getRawValue();
			
		if (issuedateValue == null){			
			appObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ISSUE_DATE).setRawValue(data);
			appObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ISSUE_YEAR).setRawValue(years);
		}
		
	}
	private void displayLog(String message){
		if(DEBUGGER_ON){
			debuglog.info(this.getClass().getName(),message );
		}
	}
	
}
