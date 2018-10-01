package com.idsscheer.webapps.arcm.ui.components.issue.control.object;

import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomIssueForm extends Form{

	public CustomIssueForm(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IControlContext p_context) {
		// TODO Auto-generated method stub
		super.init(p_context);
		this.form.setVersion(this.form.getAppObj().getVersionData().getVersionCount());
	}
	
	@Override
//	DMM - EV190711 - Início - 2018-04-13
	protected void renderControlFooter() {
		super.renderControlFooter();
		StringBuffer jScript = new StringBuffer();
		
		IAppObj issueObj = this.form.getAppObj();
		
		IEnumAttribute issueTypeList = issueObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(), true);
		
		jScript.append("$('#tr_owner_status th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Status do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Status do Apontamento");
		jScript.append("');");

		
		jScript.append("$('#tr_obj_id th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("ID do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("ID do Apontamento");
		jScript.append("');");

		
		jScript.append("$('#tr_name th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Título do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Título do Apontamento");
		jScript.append("');");

		
		jScript.append("$('#tr_issue_date th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Data de Criação do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Data de Criação do Apontamento");
		jScript.append("');");

		
		jScript.append("$('#tr_issueRelevantObjects th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Objetos relevantes do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Objetos relevantes do Apontamento");
		jScript.append("');");

		
		jScript.append("$('#tr_creator th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Criador do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Criador do Apontamento");
		jScript.append("');");


		jScript.append("$('#tr_reviewers th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Revisor do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Revisor do Apontamento");
		jScript.append("');");
		
		
		jScript.append("$('#tr_owners th:nth-child(1)').text('");
		if(issueType.getId().equals("actionplan"))
			jScript.append("Dono do Plano de Ação");
		
		if(issueType.getId().equals("issue"))
			jScript.append("Dono do Apontamento");
		jScript.append("');");
		
		if(issueType.getId().equals("actionplan")){
			jScript.append("$('#tr_description th:nth-child(1)').text('");
			jScript.append("Descrição do Plano de Ação");
			jScript.append("');");
		}
		
		renderJavaScriptAfterLoad(jScript.toString());
//		DMM - EV190711 - Fim - 2018-04-13		
	}

}
