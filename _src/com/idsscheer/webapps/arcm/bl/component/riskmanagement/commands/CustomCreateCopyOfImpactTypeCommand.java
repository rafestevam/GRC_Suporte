package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;

public class CustomCreateCopyOfImpactTypeCommand implements ICommand {

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
	/*	// TODO Auto-generated method stub
		IUserContext fullGrantUserContext = ContextFactory
				.createFullGrantUserContext(cc.getChainContext().getUserContext());

		String originalImpactTypeId = (String) cc.getChainContext().getContextAttribute("original");
		if (("0".equals(originalImpactTypeId)) || (StringUtility.isEmpty(originalImpactTypeId)))
			return CommandResult.OK;

		IImpacttypeAppObj originalImpactType = (IImpacttypeAppObj) cc.getChainContext()
				.load(OVIDFactory.getOVID(originalImpactTypeId), ObjectType.IMPACTTYPE, false);
		IImpacttypeAppObj newImpactType = (IImpacttypeAppObj) cc.getChainContext().getTriggeringAppObj();

		CustomImpacttypeCopyHelper helper = new CustomImpacttypeCopyHelper(cc.getChainContext(), fullGrantUserContext);

		helper.copy(originalImpactType, newImpactType, cc.getChainContext());

		helper.saveAtCC(newImpactType, cc.getChainContext());

		return CommandResult.OK;		
	*/
		return null;
	}

}
