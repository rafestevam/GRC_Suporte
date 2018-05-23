package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseOpenActionCommand;

public class CustomIssueViewActionCommand extends BaseOpenActionCommand {
	
	@Override
	protected void execute() {
		super.execute();
		
		IStateMetadata metadata = WorkflowUtility.getStateMetadata(this.formModel.getAppObj());
		System.out.println(metadata.getStateId());
		
	}

}
