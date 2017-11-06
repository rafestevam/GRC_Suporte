package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.job.DueCheckCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;

public class CustomDueCheckCommand extends DueCheckCommand {
	
	@Override
	protected boolean isDue(IAppObj appObj, CommandContext cc) {
		// TODO Auto-generated method stub
		if(super.isDue(appObj, cc)){
			//Resolver aqui a logica para definição se deve ser gerada nova avaliação de risco
			//-> TRUE - Continua logica normal para geração de Avaliação de Risco
			//-> FALSE - Não gera Avaliação de risco
		}
		return super.isDue(appObj, cc);
	}

}
