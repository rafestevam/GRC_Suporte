package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.ui.framework.dialog.ForwardDialog;

public class CustomTestDefSaveActionCommand extends TestdefinitionSaveActionCommand {

	protected void addForwardDialog() {
		super.addForwardDialog();
	}
	
	protected void addTestdefintionOption(IAppObj p_appObj, ForwardDialog p_forwardDialog) {
		super.addTestdefintionOption(p_appObj, p_forwardDialog);
	}
	
	protected void addControlOption(IAppObj p_appObj, ForwardDialog p_forwardDialog) {
		super.addControlOption(p_appObj, p_forwardDialog);
	}
	
	protected void afterExecute(){
		//
	}

}
