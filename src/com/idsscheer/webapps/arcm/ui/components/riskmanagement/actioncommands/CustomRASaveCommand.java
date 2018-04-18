package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.idsscheer.batchserver.administration.BatchServer;
import com.idsscheer.batchserver.administration.communication.IBatchServerCommunication;
import com.idsscheer.batchserver.config.BatchServerConfig;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.ForceLocksHandler;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskException;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomProcRiskResidualCalc;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceClient;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomRASaveCommand extends BaseSaveActionCommand {
	
	//Inicio REO 06.11.2017 - EV118286
	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		super.execute();
		
		IAppObj raObj = this.formModel.getAppObj();
		IEnumAttribute reviewerStatusAttr = raObj.getAttribute(IRiskassessmentAttributeType.ATTR_REVIEWER_STATUS);
		IEnumerationItem reviewerStatus = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
		
		try{
		
		if(reviewerStatus.getId().equals("agreed")){
			JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext());
			IUserContext jobCtx = jobEnv.getUserContext();
			IAppObjFacade rskAppFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.RISK);
			
			String riscoPotencial = raObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();
			List<IAppObj> currRskList = raObj.getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(jobEnv.getUserContext());
			for (IAppObj riskObj : currRskList) {
				
				IAppObj rskUpdAppObj = rskAppFacade.load(riskObj.getVersionData().getHeadOVID(), this.getDefaultTransaction(), true);
				rskAppFacade.allocateLock(riskObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				
				String riskName = rskUpdAppObj.getAttribute(IRiskAttributeType.ATTR_NAME).getRawValue();
				IStringAttribute classFinalAttr = rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL);
				IStringAttribute residualFinalAttr = rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL);
				String classFinal = classFinalAttr.isEmpty() ? "" : classFinalAttr.getRawValue();
				// FCT - 26/01/2018 
				// Por padrão o risco residual final deve ser igual ao risco potencial
				// quando não há ambiente de controle avaliado.
//				String residualFinal = residualFinalAttr.isEmpty() ? "" : residualFinalAttr.getRawValue();
				String residualFinal = residualFinalAttr.isEmpty() ? 
						riscoPotencial : 
								residualFinalAttr.getRawValue(); 
				
				//Inicio Alteracao - REO 18.12.2017 - EV126430
				/*if((!classFinal.equals("")) && (!residualFinal.equals(""))){
					CustomProcRiskResidualCalc residualCalc = new CustomProcRiskResidualCalc(riskName, riscoPotencial, classFinal);
					residualCalc.calculateResidualFinal();
					rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(residualCalc.getResidualFinal());
					rskAppFacade.save(rskUpdAppObj, getDefaultTransaction(), true);
				}else{*/
				IStringAttribute ctrl1Line = rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE);
				IStringAttribute ctrl2Line = rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE);
				IStringAttribute ctrl3Line = rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE);
				
				String class1Line = ctrl1Line.isEmpty() ? "" : ctrl1Line.getRawValue();
				String class2Line = ctrl2Line.isEmpty() ? "" : ctrl2Line.getRawValue();
				String class3Line = ctrl3Line.isEmpty() ? "" : ctrl3Line.getRawValue();
				
				//if((!class1Line.equals("")) || (!class2Line.equals("")) || (!class3Line.equals(""))){
				CustomProcRiskResidualCalc residualCalc = new CustomProcRiskResidualCalc(riskName, riscoPotencial, class1Line, class2Line, class3Line);
				residualCalc.calculateResidualFinal();
				rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).setRawValue(residualCalc.getResidual1Line());
				rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue(residualCalc.getResidual2Line());
				rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue(residualCalc.getResidual3Line());
				
				if (residualCalc.getResidualFinal() == "") {
					rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(riscoPotencial);
				} else {
					rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(residualCalc.getResidualFinal());	
				}
				
				rskAppFacade.save(rskUpdAppObj, getDefaultTransaction(), true);
				this.affectCorpRisk(rskUpdAppObj);
				//}	
				//}
				//Fim Alteracao - REO 18.12.2017 - EV126430
				
				rskAppFacade.releaseLock(riskObj.getVersionData().getHeadOVID());
			}
		}
		
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.ERROR, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
	
	}
	//Fim REO 06.11.2017 - EV118286
		
	protected void afterExecute(){
		
		IUIEnvironment currEnv = this.environment;
		IAppObj currAppObj = this.formModel.getAppObj();
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext()); //REO+ 27.09.2017 - EV113345
		
		// Início FCT - EV121384 - 28.11.2017
		// Apenas quando a avaliação do risco estiver com o 
		// status em avaliação que o risco potencial deve ser alterado
		// no artefato de risco.
		IEnumAttribute raReviewerStatus = currAppObj.getAttribute(IRiskassessmentAttributeType.ATTR_REVIEWER_STATUS);
		IEnumerationItem enumReviewerStatus = ARCMCollections.extractSingleEntry(raReviewerStatus.getRawValue(), true);
		
		// Salva no risco apenas se o status de revisor está aceito. 
		if (enumReviewerStatus.getId().equals("agreed")) {
			try{
				//Inicio REO - 27.09.2017 - EV113345
				IUserContext jobCtx = jobEnv.getUserContext();
				IAppObjFacade rskAppFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.RISK);
				//IAppObjFacade rskAppFacade = currEnv.getAppObjFacade(ObjectType.RISK);
				//Fim REO - 27.09.2017 - EV113345
				
				String ra_result = currAppObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();	
				List<IAppObj> currRskList = currAppObj.getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(this.getFullGrantUserContext());
				Iterator<IAppObj> currRskIterator = currRskList.iterator();
				
				while(currRskIterator.hasNext()){
					IAppObj riskAppObj = currRskIterator.next();
					IOVID riskOVID = riskAppObj.getVersionData().getHeadOVID();
					
					rskAppFacade.allocateLock(riskOVID, LockType.FORCEWRITE);
					IAppObj rskUpdAppObj = this.updatePotencialRisk(riskOVID, rskAppFacade, ra_result);
													
					rskAppFacade.save(rskUpdAppObj, this.getDefaultTransaction(), true);
					rskAppFacade.releaseLock(riskOVID);
				}

			}catch(Exception e){
				this.log.error(e, (Throwable)e);
				this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			}	
		}
		
		this.formModel.releaseLock();
				
	}
	
	//Obter Risco Potencial a partir da Avaliação de Risco
	private IAppObj updatePotencialRisk(IOVID riskOVID, IAppObjFacade riskFacade, String resultAssessment) throws RightException{
		Calendar calendarDate = new GregorianCalendar(2050,Calendar.JANUARY,31);
		Date newEndDate = calendarDate.getTime();
		try{
			IAppObj rskUpdAppObj = riskFacade.load(riskOVID, this.getDefaultTransaction(), true);
			rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(resultAssessment);
			rskUpdAppObj.getAttribute(IRiskAttributeType.ATTR_ENDDATE).setRawValue(newEndDate);	
			
			return rskUpdAppObj;
		}catch(RightException e){
			throw e;
		}
	}
	
	private void affectCorpRisk(IAppObj risk) throws Exception{
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext());
		IUserContext jobCtx = jobEnv.getUserContext();
		try{
			//Inicio REO - 27.09.2017 - EV113345
			//IAppObjFacade crFacade = this.environment.getAppObjFacade(ObjectType.HIERARCHY);
			IAppObjFacade crFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.HIERARCHY);
			//Fim REO - 27.09.2017 - EV113345
			
			List<IAppObj> corpRiskList = risk.getAttribute(IRiskAttributeType.LIST_RISK_CATEGORY).getElements(jobCtx);
			for(IAppObj corpRisk : corpRiskList){
				
				if(corpRisk.getVersionData().isDeleted())
					continue;
				
				if(corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_CORPRISK).getRawValue() != null){
					if(corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_CORPRISK).getRawValue()){
						crFacade.allocateLock(corpRisk.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
						CustomCorpRiskHierarchy crHierarchy = new CustomCorpRiskHierarchy(corpRisk, jobCtx, this.getDefaultTransaction());
						//Inicio REO - 17.01.2018 - EV124200
						//String ret = crHierarchy.calculateResidualCR();
						String ret = "";
						try{
							ret = crHierarchy.calculateResidualCR();
						}catch(CustomCorpRiskException ex){
							continue;
						}
						//Fim REO - 17.01.2018 - EV124200
						if(ret != null || (!ret.equals(""))){
							corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(ret);
							crFacade.save(corpRisk, this.getDefaultTransaction(), true);
							crFacade.releaseLock(corpRisk.getVersionData().getHeadOVID());
						}
					}
				}
			}
		}catch(Exception e){
			throw e;
		}
		
	}
	
}
