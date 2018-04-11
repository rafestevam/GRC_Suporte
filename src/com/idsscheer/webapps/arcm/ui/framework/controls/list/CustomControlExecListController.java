package com.idsscheer.webapps.arcm.ui.framework.controls.list;

import java.util.Map;

public class CustomControlExecListController extends List {

	public CustomControlExecListController(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void renderControlBody() {
		StringBuffer jScript = new StringBuffer();
		
		
		jScript.append("$('#header_delete').hide()");
		this.renderJavaScriptAfterLoad(jScript.toString());
		
		super.renderControlBody();
	}

}
