package com.idsscheer.webapps.arcm.ui.components.issue.control.object;

import java.util.Map;

import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomIssueForm extends Form{

	public CustomIssueForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IControlContext p_context) {
		// TODO Auto-generated method stub
		super.init(p_context);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}

}
