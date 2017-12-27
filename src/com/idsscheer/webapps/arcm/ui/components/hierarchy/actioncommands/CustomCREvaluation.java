package com.idsscheer.webapps.arcm.ui.components.hierarchy.actioncommands;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomCREvaluation extends BaseCacheActionCommand{
	
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
		try{
			StringBuilder strBuilder = new StringBuilder();
			IAppObj riskCorpObj = this.formModel.getAppObj();
			CustomCorpRiskHierarchy crEvaluation = new CustomCorpRiskHierarchy(riskCorpObj, this.getFullGrantUserContext(), this.getDefaultTransaction());
			String riskClass = crEvaluation.calculateResidualCR();
			if(null == riskClass || riskClass.equals("")){
				riskClass = "Não Classificado";
			}
			
			IStringAttribute residualAttr = riskCorpObj.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL);
			String currRiskClass = residualAttr.isEmpty() ? "" : residualAttr.getRawValue();
			
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
			
			if(riskCorpObj.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).isEmpty() || (!riskClass.equals(currRiskClass))){
				IUserContext jobCtx = this.getJobUserContext();
				IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.HIERARCHY);
				facade.allocateLock(riskCorpObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				IAppObj riskCorpUpd = facade.load(riskCorpObj.getVersionData().getHeadOVID(), true);
				riskCorpUpd.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(riskClass);
				facade.save(riskCorpUpd, this.getDefaultTransaction(), true);
				facade.releaseLock(riskCorpObj.getVersionData().getHeadOVID());
			}
		
		}catch(Exception e){
			this.notificationDialog.addError(e.getMessage());
			//this.notificationDialog.addMessage(e.getMessage());
		}
		
	}

	private IUserContext getJobUserContext() {
		// TODO Auto-generated method stub
		return new JobUIEnvironment(this.getFullGrantUserContext()).getUserContext();
	}

}
