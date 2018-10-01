package com.idsscheer.webapps.arcm.ui.components.controlexecution.control.object;

import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.common.constants.metadata.ButtonTypes;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations.USERROLE_TYPE;
import com.idsscheer.webapps.arcm.config.metadata.button.IButtonType;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomControlExecutionForm extends Form {

	public CustomControlExecutionForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
	}
	
	@Override
	public void init(IControlContext arg0) {
		super.init(arg0);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}
	
	// DMM - EV178278 - 2018-04-06 Remover botão Eliminar do proprietário de execução de controle
	@Override
	protected void renderButtons(List<IButtonType> activeButtonTypes) {

		if(this.form.getUserContext().getUserRights().hasRole(USERROLE_TYPE.CONTROLEXECUTIONOWNER)){
			activeButtonTypes.remove(ButtonTypes.DELETE);
		}
		
		super.renderButtons(activeButtonTypes);
	}
	
}
