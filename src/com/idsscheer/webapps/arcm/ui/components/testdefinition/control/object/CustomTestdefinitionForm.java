package com.idsscheer.webapps.arcm.ui.components.testdefinition.control.object;

import java.util.Map;

import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomTestdefinitionForm extends Form{

	public CustomTestdefinitionForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
	}
	
	@Override
	public void init(IControlContext arg0) {
		super.init(arg0);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}

}
