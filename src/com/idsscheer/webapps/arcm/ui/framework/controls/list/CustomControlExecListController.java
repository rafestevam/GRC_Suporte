package com.idsscheer.webapps.arcm.ui.framework.controls.list;

import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserRights;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations.USERROLE_TYPE;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;

public class CustomControlExecListController extends List {

	public CustomControlExecListController(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IControlContext p_context) {
		super.init(p_context);
	}
	
	@Override
	protected void renderControlBody() {
		IUserRights userRights = this.environment.getUserContext().getUserRights();
		StringBuffer jScript = new StringBuffer();
		
		if(userRights.hasRole(USERROLE_TYPE.CONTROLEXECUTIONOWNER)){
			jScript.append("$('#header_delete').hide()");
			//jScript.append("$(\"a[id*='delete']\").hide()");
			this.renderJavaScriptAfterLoad(jScript.toString());
		}
		
		super.renderControlBody();
	}
	
	@Override
	protected void renderControlFooter() {
		StringBuffer jScript = new StringBuffer();
		IUserRights userRights = this.environment.getUserContext().getUserRights();
		
		if(userRights.hasRole(USERROLE_TYPE.CONTROLEXECUTIONOWNER)){
			//jScript.append("$('#header_delete').hide()");
			jScript.append("$(\"a[id*='delete']\").hide()");
			this.renderJavaScriptAfterLoad(jScript.toString());
		}
		
		super.renderControlFooter();
	}

}
