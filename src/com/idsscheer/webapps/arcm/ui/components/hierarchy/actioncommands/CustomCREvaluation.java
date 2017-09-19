package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import java.util.Map;

import javax.swing.text.html.HTML;

import org.w3c.dom.html.HTMLAnchorElement;

import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.support.breadcrumb.IHTMLPage;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLAnchor;

public class CustomCREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		try{
			StringBuilder strBuilder = new StringBuilder();
			CustomCorpRiskHierarchy crEvaluation = new CustomCorpRiskHierarchy(this.formModel.getAppObj(), this.getFullGrantUserContext(), this.getDefaultTransaction());
			String riskClass = crEvaluation.calculateResidualCR();
			if(null == riskClass || riskClass.equals("")){
				riskClass = "Não Classificado";
			}
			
			this.notificationDialog.setHeaderMessageKey("message.corp_risk_evaluation.DBI");
	
			this.notificationDialog.addInfo("Quantidade de Riscos Processo: " + crEvaluation.getTotalRisks().toString());// <qtd_riscos>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Baixo: " + crEvaluation.getResGrade("baixo").toString()); // <pontos_baixo>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Médio: " + crEvaluation.getResGrade("medio").toString()); //<pontos_medio>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Alto: " + crEvaluation.getResGrade("alto").toString()); //<pontos_alto>");
			this.notificationDialog.addInfo("Qtd. Riscos Processo - Rating Muito Alto: " + crEvaluation.getResGrade("muito_alto").toString()); //<pontos_muito_alto>");
			this.notificationDialog.addInfo("Pontos - Rating Baixo: " + crEvaluation.getPtsGrade("baixo").toString()); //<pontos_baixo>");
			this.notificationDialog.addInfo("Pontos - Rating Médio: " + crEvaluation.getPtsGrade("medio").toString()); //<pontos_medio>");
			this.notificationDialog.addInfo("Pontos - Rating Alto: " + crEvaluation.getPtsGrade("alto").toString()); //<pontos_alto>");
			this.notificationDialog.addInfo("Pontos - Rating Muito Alto: " + crEvaluation.getPtsGrade("muito_alto").toString()); //<pontos_muito_alto>");
			this.notificationDialog.addInfo("Nota Final: " + crEvaluation.getFinalGrade().toString()); //<nota_final>");
			this.notificationDialog.addInfo("Classificação do Risco: " + riskClass); //<classificacao_do_risco>");
		
		}catch(Exception e){
			this.notificationDialog.addError(e.getMessage());
			//this.notificationDialog.addMessage(e.getMessage());
		}
		
	}

}
