package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.job.DueCheckCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;

public class CustomDueCheckCommand extends DueCheckCommand {
	
	@Override
	protected boolean isDue(IAppObj appObj, CommandContext cc) {
		// TODO Auto-generated method stub
		if(super.isDue(appObj, cc)){
			//Resolver aqui a logica para defini��o se deve ser gerada nova avalia��o de risco
			//-> TRUE - Continua logica normal para gera��o de Avalia��o de Risco
			//-> FALSE - N�o gera Avalia��o de risco
		}
		return super.isDue(appObj, cc);
	}

}
