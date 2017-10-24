package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

public class CustomIROCreateActionCommand extends IssueRelevantObjectCreateActionCommand {
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		super.execute();
		
		super.issueRelevantObjects.removeAll(issueRelevantObjects);
		
	}

}
