package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.form.IRiskassessmentFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;

public class CustomRiskLoadCommand extends RiskassessmentCacheActionCommand{
	
	
	protected void assumeData(String[] p_excludeParameters){
		super.assumeData(p_excludeParameters);
		IRiskassessmentFormModel model = (IRiskassessmentFormModel) this.formModel;
		
//		model.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_PROGRESS).setRawValue(this.defineTermStatus(model));
//		model.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RISK_ID).setRawValue(this.getRiskID(model));
		
	}
	
	private String defineTermStatus (IRiskassessmentFormModel model){
		Date sysDate = new Date();
		Date planEndDate = this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
		
		if(sysDate.before(planEndDate)){
			return "Dentro do Prazo";
			//this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_PROGRESS).setRawValue("Dentro do Prazo");
			//this.formModel.save(this.getDefaultTransaction(), IRiskassessmentAttributeTypeCustom.STR_PROGRESS);
		}else{
			return "Em Atraso";
			//this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_PROGRESS).setRawValue("Em Atraso");
			//this.formModel.save(this.getDefaultTransaction(), IRiskassessmentAttributeTypeCustom.STR_PROGRESS);
		}
	}
	
	private String getRiskID (IRiskassessmentFormModel model){
		
		String riskID = "";
		List<IAppObj> riskList = model.getAppObj().getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(this.getUserContext());
		for(IAppObj riskObj : riskList){
			riskID = riskObj.getAttribute(IRiskAttributeType.ATTR_RISK_ID).getRawValue();
		}
		return riskID;
	}
	
	protected void afterExecute(){
/*		String commandId = this.requestContext.getCommandId();
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, commandId, new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		
		String planInitDateStr = this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDSTARTDATE).getRawValue().toString();
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, planInitDateStr, new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		
		String planEndDateStr = this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue().toString();
		this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, planEndDateStr, new String[] { getStringRepresentation(this.formModel.getAppObj()) });*/
		
		/*Date sysDate = new Date();
		//Date planInitDate = this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDSTARTDATE).getRawValue();
		Date planEndDate = this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
		
		if(sysDate.before(planEndDate)){
			this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_PROGRESS).setRawValue("Dentro do Prazo");
			//this.formModel.save(this.getDefaultTransaction(), IRiskassessmentAttributeTypeCustom.STR_PROGRESS);
		}else{
			this.formModel.getAppObj().getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_PROGRESS).setRawValue("Em Atraso");
			//this.formModel.save(this.getDefaultTransaction(), IRiskassessmentAttributeTypeCustom.STR_PROGRESS);
		}*/
		
/*		IRiskassessmentFormModel model = (IRiskassessmentFormModel) this.formModel;
		IAppObj appObj = this.formModel.getAppObj();
		String[] parameters = this.requestContext.getParameters("__editableFields");
		String result_assessment = "";
		
		boolean isFilled = false;
		IListAttribute listImpTypeAttr = appObj.getAttribute(IRiskassessmentAttributeType.LIST_IMPACTTYPES);
		List<IAppObj> listImpType = listImpTypeAttr.getElements(this.getUserContext());
		for(IAppObj itemImpType : listImpType){
			boolean status = itemImpType.getAttribute(IRa_impacttypeAttributeType.ATTR_ISFILLED).getRawValue();
			if(status){
				isFilled = status;
				break;
			}
		}
		
		double height_set = ((appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).getRawValue() == null) ||
				             (appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).getRawValue() == 0)) ?
				            		 0 : appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).getRawValue();
		double height = 0;
		
		for(String param : parameters){
			if(param.contains("lossQual")){
				
				String[] value = this.requestContext.getParameters(param);
				for (int i = 0; i < value.length; ++i) {
					value[i] = StringUtils.escapeLineBreaks(value[i]);
				}
				String[] identifiers = param.split("#", 3);
				IOVID ovid = OVIDFactory.getOVID(identifiers[1]);
				IFormModel internalModel = model.getModel(ovid);
				IAppObj intAppObj = internalModel.getAppObj();
				
				if(value.length == 0){
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("");
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(0);
				}else{
					IListAttribute raList = intAppObj.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
					List<IAppObj> raAppList = raList.getElements(internalModel.getUserContext());

					for(IAppObj raAppItem : raAppList){
						result_assessment = raAppItem.getRawValue(IQuotationAttributeType.ATTR_NAME);
						height = raAppItem.getRawValue(IQuotationAttributeType.ATTR_VALUE);
					}
					if(!isFilled){
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue(result_assessment);
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(height);
					}else{
						if(height == 0){
							appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue(result_assessment);
							appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(height);
						}else{
							if(height > height_set){
								appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue(result_assessment);
								appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(height);
							}
						}
					}
				}		
			}			
		}*/
	}
}
