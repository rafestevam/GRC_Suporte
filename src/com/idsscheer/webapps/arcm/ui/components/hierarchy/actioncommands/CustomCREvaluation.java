package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import java.util.Map;

import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.support.breadcrumb.IHTMLPage;

public class CustomCREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		try{
		
			CustomCorpRiskHierarchy crEvaluation = new CustomCorpRiskHierarchy(this.formModel.getAppObj(), this.getFullGrantUserContext(), this.getDefaultTransaction());
			String riskClass = crEvaluation.calculateResidualCR();
			if(null == riskClass || riskClass.equals("")){
				riskClass = "N�o Classificado";
			}
			
			this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
	
			this.notificationDialog.addInfo("Quantidade de Riscos Processo: " + crEvaluation.getTotalRisks().toString());// <qtd_riscos>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Baixo: " + crEvaluation.getResGrade("baixo").toString()); // <pontos_baixo>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating M�dio: " + crEvaluation.getResGrade("medio").toString()); //<pontos_medio>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Alto: " + crEvaluation.getResGrade("alto").toString()); //<pontos_alto>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Muito Alto: " + crEvaluation.getResGrade("muito_alto").toString()); //<pontos_muito_alto>");
			this.notificationDialog.addInfo("Nota Final: " + crEvaluation.getFinalGrade().toString()); //<nota_final>");
			this.notificationDialog.addInfo("Pontos - Rating Baixo: <pontos_baixo>");
			this.notificationDialog.addInfo("Pontos - Rating M�dio: <pontos_medio>");
			this.notificationDialog.addInfo("Pontos - Rating Alto: <pontos_alto>");
			this.notificationDialog.addInfo("Pontos - Rating Muito Alto: <pontos_muito_alto>");
			this.notificationDialog.addInfo("Classifica��o do Risco: " + riskClass); //<classificacao_do_risco>");
		
		}catch(Exception e){
			this.notificationDialog.addError(e.getMessage());
			//this.notificationDialog.addMessage(e.getMessage());
		}
		
	}

}