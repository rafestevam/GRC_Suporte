package com.idsscheer.webapps.arcm.ui.components.riskassessment.control.object;

import java.util.Map;

import com.idsscheer.webapps.arcm.ui.components.riskmanagement.control.object.RiskassessmentForm;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;

public class CustomRiskassessmentForm extends RiskassessmentForm {

	public CustomRiskassessmentForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
	}
	
	@Override
	public void init(IControlContext arg0) {
		super.init(arg0);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}

}
