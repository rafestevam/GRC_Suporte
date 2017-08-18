package com.idsscheer.webapps.arcm.ui.components.riskmanagement.control.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.form.IRoleSelectionModel;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.IBaseRole;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.IRole;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomHierarchyForm extends Form {
	
	public CustomHierarchyForm(String p_instanceName, Map<String, String> p_parameters){
		super(p_instanceName,p_parameters);
	}
	
	//Inicio REO - 18.08.2017 - Riscos Corporativos
	@Override
	protected void renderRoleSelection(IRoleSelectionModel model) {
		// TODO Auto-generated method stub
		super.renderRoleSelection(model);
		
		List<String> jsScripts = new ArrayList<String>();
		jsScripts.add("custom_corprisk");
		this.renderJavaScriptRef(jsScripts);
	}
	//Fim REO - 18.08.2017 - Riscos Corporativos

}
