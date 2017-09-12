package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.dialog.IBLDialog;
import com.idsscheer.webapps.arcm.bl.models.dialog.IBLDialogModel;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;

public class CustomCREvaluation extends BaseSaveActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		/*String[] arr = new String[1];
		arr[1] = "TESTE";
		IBLDialogModel dialog = this.environment.getDialogManager().createConfirmationDialog(this.requestContext, "TESTE1", "TESTE2", arr);*/
		this.environment.getDialogManager().getNotificationDialog().addInfo("TESTETESTE");
	}

}
