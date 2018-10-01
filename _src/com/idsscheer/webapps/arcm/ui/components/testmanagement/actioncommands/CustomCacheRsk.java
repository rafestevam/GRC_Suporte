package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;


import org.apache.log4j.Logger;

import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;

public class CustomCacheRsk extends BaseCacheActionCommand {
	
	final Logger log = Logger.getLogger(CustomCacheRsk.class.getName());
	//final Logger log = new Logger();

	protected void afterExecute(){
//	protected void assumeData(String[] p_excludeParameters){
		//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "TAB LOADED", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
//		super.assumeData(p_excludeParameters);
		
		String riskPotencial;
		String riskControl1line;
		String riskControl2line;
		String riskControl3line;
		String riskControlfinal;
		
		riskPotencial = this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
		if(riskPotencial == null)
			riskPotencial = "";
		log.info("Risco Potencial: " + riskPotencial);
		
		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).isEmpty()){
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue("Não Avaliado");
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).setRawValue("Não Avaliado");
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue("Não Avaliado");
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue("Não Avaliado");
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue("Não Avaliado");
		}
		
//		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).isEmpty()){
		riskControl1line = this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).getRawValue();
		if(riskControl1line == null || riskControl1line.equals("Não Avaliado"))
			riskControl1line = "";
		if(!riskControl1line.equals("")){
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).setRawValue(
					this.riskResidualFinal(riskPotencial, riskControl1line)
			);
			log.info("Risco Residual 1a Linha: " + this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).getRawValue());
		}
//		}
		
//		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).isEmpty()){
		riskControl2line = this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue();
		if(riskControl2line == null || riskControl2line.equals("Não Avaliado"))
			riskControl2line = "";
		if(!riskControl2line.equals("")){
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue(
					this.riskResidualFinal(riskPotencial, riskControl2line)
			);
			log.info("Risco Residual 2a Linha: " + this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).getRawValue());
		}
//		}
		
//		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).isEmpty()){
		riskControl3line = this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue();
		if(riskControl3line == null || riskControl3line.equals("Não Avaliado"))
			riskControl3line = "";
		if(!riskControl3line.equals("")){
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue(
					this.riskResidualFinal(riskPotencial, riskControl3line)
			);
			log.info("Risco Residual 3a Linha: " + this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).getRawValue());
		}
//		}
		riskControlfinal = this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue();
		if(riskControlfinal == null || riskControlfinal.equals("Não Avaliado"))
			riskControlfinal = "";
		if(!riskControlfinal.equals("")){
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(
					this.riskResidualFinal(riskPotencial, riskControlfinal)
			);
			log.info("Risco Residual Final: " + this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue());
		}
		
		/*if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).isEmpty())
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).setRawValue("Não Avaliado");
		
		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).isEmpty())
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue("Não Avaliado");
		
		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).isEmpty())
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue("Não Avaliado");
		
		if(this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).isEmpty())
			this.formModel.getAppObj().getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue("Não Avaliado");*/
		
		/*
		IUIEnvironment formEnv = this.environment;
		IAppObj currAppObj = this.formModel.getAppObj();
		String currRiskGuid = currAppObj.getGuid();
		boolean hasEffective = false;
		boolean hasRiskAssessment = false;
		Date sysDate = new Date();
		long lastDiffDays = 0;
		
		try{
			IAppObjFacade objFacade = formEnv.getAppObjFacade(ObjectType.RISKASSESSMENT);
			IAppObjQuery appQuery = objFacade.createQuery();
			IAppObjIterator appIterator = appQuery.getResultIterator();
			
			while(appIterator.hasNext()){
				IAppObj appObj = appIterator.next();

				Date endDate = appObj.getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
				
				IListAttribute riskList = appObj.getAttribute(IRiskassessmentAttributeType.LIST_RISK);
				if(!this.isParentRisk(riskList, currRiskGuid))
					continue;
				
				hasRiskAssessment = true;
				
				IEnumAttribute ownerEnumStatus = appObj.getAttribute(IRiskassessmentAttributeType.ATTR_OWNER_STATUS);
				IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerEnumStatus.getRawValue(), true);
				
				this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, ownerStatus.getId(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
				
				if(ownerStatus.equals(Enumerations.OWNER_STATUS.EFFECTIVE)){
					hasEffective = true;
					long diffDays = this.getDateDiff(sysDate, endDate, TimeUnit.DAYS);
					if( (lastDiffDays != 0) && (diffDays < lastDiffDays) ){
						String ra_result = appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();
						//currAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(ra_result);
					}else{
						if(lastDiffDays == 0){
							String ra_result = appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();
							//currAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(ra_result);							
						}
					}
					
				}else{
					if(hasEffective)
						continue;
					
					if(this.isLate(sysDate, endDate))
						continue;	
					
					long diffDays = this.getDateDiff(sysDate, endDate, TimeUnit.DAYS);
					if( (lastDiffDays != 0) && (diffDays < lastDiffDays) ){
						String ra_result = appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();
						//currAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(ra_result);
					}else{
						if(lastDiffDays == 0){
							String ra_result = appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();
							//currAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(ra_result);							
						}
					}					
				}	
			}
			
			if(!hasRiskAssessment);
				//currAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue("Não Avaliado");
			
		}catch(Exception e){
			String exp = e.getMessage();
			//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, exp, new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			this.notificationDialog.addError(exp);
		}*/
				
	}
	
	private String riskResidualFinal(String riskPotencial, String riskControlFinal){
		
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		log.info("Avaliação - Risco Potencial: " + riskPotencial);
		log.info("Avaliação - Controle: " + riskControlFinal);
		
		return riskResidualReturn;
		
	}	
	
	/*private boolean isParentRisk(IListAttribute riskList, String currRiskGuid){
		
		boolean retParent = false;
		
		List<IAppObj> riskAppObjList = riskList.getElements(this.getUserContext());
		for(IAppObj riskAppObj : riskAppObjList){
			if(currRiskGuid.equals(riskAppObj.getGuid())){
				retParent = true;
				break;
			}
		}
		
		return retParent;
		
	}
	
	private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	private boolean isLate(Date sysDate, Date endDate){
		if(sysDate.after(endDate)){
			return true;
		}else{
			return false;
		}
	}*/

}
