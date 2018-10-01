package com.idsscheer.webapps.arcm.ui.components.risk.control.object;

import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.common.constants.metadata.ButtonTypesCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations.USERROLE_TYPE;
import com.idsscheer.webapps.arcm.config.metadata.button.IButtonType;
import com.idsscheer.webapps.arcm.config.metadata.form.IPage;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomRiskForm extends Form {

	public CustomRiskForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IControlContext arg0) {
		super.init(arg0);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}
	
	@Override
	protected void renderButtons(List<IButtonType> activeButtonTypes) {
		if (this.form.getUserContext().getUserRights().hasRole(USERROLE_TYPE.RISKMANAGER)){
			activeButtonTypes.add(ButtonTypesCustom.CUSTOM_PR_VALIDATION);
		}else{
			activeButtonTypes.remove(ButtonTypesCustom.CUSTOM_PR_VALIDATION);
		}
		super.renderButtons(activeButtonTypes);
	}
	
	@Override
	protected void renderPage(IPage page) {
		
		super.renderPage(page);
		
		/*IAppObj riskObj = this.form.getAppObj();
		List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.environment.getUserContext());
		RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList);
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_1)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_1)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_1)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_1)) + "|");
		sb.append(this.getMapValues(objCalc, "classification", DefLineEnum.LINE_1) + "|");
		
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_2)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_2)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_2)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_2)) + "|");
		sb.append(this.getMapValues(objCalc, "classification", DefLineEnum.LINE_2) + "|");
		
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_3)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_3)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_3)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_3)) + "|");
		sb.append(this.getMapValues(objCalc, "classification", DefLineEnum.LINE_3) + "|");
		
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_F)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "effective", DefLineEnum.LINE_F)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_F)) + "|");
		sb.append(String.valueOf((Double)this.getMapValues(objCalc, "rate", DefLineEnum.LINE_F)) + "|");
		sb.append(this.getMapValues(objCalc, "classification", DefLineEnum.LINE_F) + "|");
				
		if(page.getId().equals("riskassessment")){
			
			for(IRowgroup rowGroup : page.getRowgroups()){
				if(rowGroup.getId().equals("riskassessment.1")){
					
					for(IRow row : rowGroup.getRows()){
						if(row.getId().equals("cst_residual_eval")){
							this.buffer
								.append("<tr id=\"tr_cst_residual_eval\" class=\"ATTRIBUTE_ROW contentfont \">")
								.append("<th colspan=\"2\" class=\"ATTR_LABEL_WIDTH ATTR_LABEL_COLUMN CONTENT_ATTR_LABEL_READ contentfont\">Avaliação de Risco Residual</th>")
								.append("<td class=\"ATTR_MANDATORY_WIDTH ATTR_MARK_NOT_MANDATORY_COLUMN\">")
								.append("<img border=\"0\" src=\"/arcm/design/default/images/icons/blank.gif\" width=\"20\" height=\"1\">")
								.append("</td>").append("<th></th>")
								.append("<td colspan=\"2\" class=\"ATTR_VALUE_WIDTH NO_BORDER NO_MARGIN NO_PADDING CONTENT_ATTR_DATA_READ\">")
								.append("<table class=\"ATTR_DATA_TABLE NO_BORDER NO_MARGIN NO_PADDING\">")
								.append("<tbody><tr class=\"\"><td class=\"TEXTFIELD_READ\">")
								.append("<a href=\"#\" id=\"cst_residual_eval\">Avaliação de Risco Residual</a>")
								.append("</td></tr></tbody></table>").append("</td>")
								.append("</tr>");
						}
						
					}
					
				}
			}
			
			this.buffer.append("<input type=\"hidden\" value=\"" + sb.toString() + "\" id=\"cst_residual_eval\">");		
		}*/
		
	}
	
	/*private Object getMapValues(RiskAndControlCalculation objCalc, String valueType, DefLineEnum defLine) {
		Object objReturn = null;
		Map<String, String> mapReturn = objCalc.calculateControlRate(defLine);
		
		Iterator<Entry<String, String>> iterator = mapReturn.entrySet().iterator();
		while(iterator.hasNext()){
			
			Entry<String, String> entry = iterator.next();
			
			if(entry.getKey().equals("classification") && valueType.equals("classification"))
				objReturn = (String)entry.getValue();
			
			if(entry.getKey().equals("rate") && valueType.equals("rate"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("total") && valueType.equals("total"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("ineffective") && valueType.equals("ineffective"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("effective") && valueType.equals("effective"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
		}
		return objReturn;
	}*/


}
