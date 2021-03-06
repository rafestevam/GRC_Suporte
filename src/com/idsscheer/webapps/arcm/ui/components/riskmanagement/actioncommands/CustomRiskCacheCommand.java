package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.form.IFormModel;
import com.idsscheer.webapps.arcm.bl.models.form.IRiskassessmentFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuotationAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.StringUtility;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandIds;
import com.idsscheer.webapps.arcm.ui.framework.support.StringUtils;

public class CustomRiskCacheCommand extends RiskassessmentCacheActionCommand {
	
	protected void assumeData(String[] p_excludeParameters){
		//this.changeRAClass();
		super.assumeData(p_excludeParameters);
		
		//Inicio REO - 30.10.2017 - EV117469
		//this.changeRAClass();
		
		IRiskassessmentFormModel model = (IRiskassessmentFormModel)this.formModel;
		IAppObj appObj = this.formModel.getAppObj();

		try{
		
			String selectedImpactTypeID = this.requestContext.getParameter("__selectedImpactTypeID");
			if (!(StringUtility.isEmpty(selectedImpactTypeID))){
				model.setCurrentImpactType(OVIDFactory.getOVID(selectedImpactTypeID));
				
				List<Double> raHeights = this.getRAHeights(appObj);
				
				raHeights.sort(new Comparator<Double>(){
					@Override
					public int compare(Double ant, Double post) {
						// TODO Auto-generated method stub
						return ant < post ? -1 : ant == post ? 0 : 1;
					}
				});
				
				Double heavy = raHeights.get(raHeights.size() - 1);
				if(heavy == 1)
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("Baixo");
				if(heavy == 2)
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("M�dio");
				if(heavy == 3)
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("Alto");
				if(heavy == 4)
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("Muito Alto");
				
				appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(heavy);
				
				if(appObj.getAttribute(IRiskassessmentAttributeType.ATTR_REMARK).getRawValue().equals(""))
					appObj.getAttribute(IRiskassessmentAttributeType.ATTR_REMARK).setRawValue("Em avalia��o");
			}
		
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
		//Fim REO - 30.10.2017 - EV117469
		
	}

	private List<Double> getRAHeights(IAppObj appObj) {
		List<Double> raHeights = new ArrayList<>();
		IListAttribute listImpTypeAttr = appObj.getAttribute(IRiskassessmentAttributeType.LIST_IMPACTTYPES);
		List<IAppObj> listImpType = listImpTypeAttr.getElements(this.getUserContext());
		for(IAppObj itemImpType : listImpType){
			IListAttribute raList = itemImpType.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
			List<IAppObj> raAppList = raList.getElements(this.getUserContext());
			for (IAppObj raAppItem : raAppList) {
				//result_assessment = raAppItem.getRawValue(IQuotationAttributeType.ATTR_NAME);
				raHeights.add(raAppItem.getRawValue(IQuotationAttributeType.ATTR_VALUE));
			}
		}
		
		return raHeights;
	}
	
	protected void afterExecute(){
		//this.changeRAClass();		
	}
	
	/*private void changeRAClass(){
		
		try{
			
			IRiskassessmentFormModel model = (IRiskassessmentFormModel) this.formModel;
			IAppObj appObj = this.formModel.getAppObj();
			String[] parameters = this.requestContext.getParameters("__editableFields");
			String result_assessment = "";
			
			if(appObj.getAttribute(IRiskassessmentAttributeType.ATTR_REMARK).getRawValue().equals("")){
				appObj.getAttribute(IRiskassessmentAttributeType.ATTR_REMARK).setRawValue("Em avalia��o");
			}
			
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
					boolean isChanged = intAppObj.isDirty();
					
					//Inicio REO 31.07.2017 - Ajuste para Mashzone
					if(isChanged){
						if(!intAppObj.getAttribute(IRa_impacttypeAttributeType.ATTR_ISFILLED).getRawValue()){
							height = height_set;
						}
					}
					//Fim REO 31.07.2017 - Ajuste para Mashzone
					
					if(value.length == 0){
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue("");
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(0);
					}else{
						IListAttribute raList = intAppObj.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
						List<IAppObj> raAppList = raList.getElements(internalModel.getUserContext());
	
						for(IAppObj raAppItem : raAppList){
							if(isChanged){
								result_assessment = raAppItem.getRawValue(IQuotationAttributeType.ATTR_NAME);
								height = raAppItem.getRawValue(IQuotationAttributeType.ATTR_VALUE);
								break;
							}
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
			}
			
			this.checkAllFilled(parameters, listImpType, model, appObj);
			
			this.environment.getActionCommandFactory().getObjectActionCommand(ActionCommandIds.SAVE, ObjectType.RISKASSESSMENT.getId()).executeCommand(requestContext, getDefaultTransaction());
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}

	}
	
	private void checkAllFilled(String[] parameters, List<IAppObj> listImpType, IRiskassessmentFormModel model, IAppObj appObj){
		
		String result_assessment = "";
		double height = 0;
		double height_set = 0;
		
		if(this.isAllFilled(listImpType)){
			for(IAppObj impTypeObj: listImpType){
				IListAttribute raList = impTypeObj.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
				List<IAppObj> raAppList = raList.getElements(this.getFullGrantUserContext());

				for(IAppObj raAppItem : raAppList){
					result_assessment = raAppItem.getRawValue(IQuotationAttributeType.ATTR_NAME);
					height = raAppItem.getRawValue(IQuotationAttributeType.ATTR_VALUE);
				}
				
				if(height == 0){
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue(result_assessment);
					appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(height);
				}else{
					if(height > height_set){
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).setRawValue(result_assessment);
						appObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_HEIGHT).setRawValue(height);
						height_set = height;
					}
				}
			}
			
		}
		
	}
	
	private boolean isAllFilled(List<IAppObj> listImpType){
		
		boolean isFilled = true;
		for(IAppObj itemImpType : listImpType){
			boolean status = itemImpType.getAttribute(IRa_impacttypeAttributeType.ATTR_ISFILLED).getRawValue();
			if(!status){
				isFilled = status;
				break;
			}
		}
		return isFilled;
		
	}
*/
}
