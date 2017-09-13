package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.dialog.DialogRequest;
import com.idsscheer.webapps.arcm.bl.models.dialog.IBLDialogModel;
import com.idsscheer.webapps.arcm.bl.models.dialog.IDialogRequest;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IPage;
import com.idsscheer.webapps.arcm.config.metadata.actioncommand.ActionCommandId;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandIds;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.dialog.InputDialog;

public class CustomCREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		//this.environment.getDialogManager().getNotificationDialog().addInfo("TESTETESTE");
		//this.environment.getDialogManager().getNotificationDialog().addJavascript("alert('TESTE')");
		//this.environment.getDialogManager().getNotificationDialog().getJavascript();
		
		
		/*String js = this.notificationDialog.getJavascript();
		this.environment.getDialogManager().getNotificationDialog().addWarning(js);*/
		
		//Confirmation Dialog
		IPage page = this.environment.getBreadcrumbStack().peek().getPage();
		String[] arr = new String[1];
		IBLDialogModel dialogModel = this.environment.getDialogManager().createConfirmationDialog(this.requestContext, "okcancel", "TESTE2", arr);
		//this.environment.getBreadcrumbStack().peek().addDialog(dialogModel).getDialog().setStatusFromNewToRunning();
		
		//Testes para criação de Janela Pop Up
		DialogRequest dialogReq = new DialogRequest("no_object_lock");
		//IDialogRequest dialogReq = this.environment.getBreadcrumbStack().peek().getFlow().getDialogRequest();
		IBLDialogModel dialogModel1 = this.environment.getDialogManager().createDynamicDialog(this.requestContext, dialogReq);
		this.environment.getBreadcrumbStack().peek().addDialog(dialogModel1);//.getDialog().setStatusFromNewToRunning();
		
	}

}
