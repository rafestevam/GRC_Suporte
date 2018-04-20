package com.idsscheer.webapps.arcm.ui.components.issue.control.object;

import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.form.IPage;
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
	protected void renderControlHeader() {
		// TODO Auto-generated method stub
		super.renderControlHeader();
//		StringBuffer jScript = new StringBuffer();
//		IAppObj issueObj = this.form.getAppObj();
//		
//		IEnumAttribute issueTypeList = issueObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
//		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(), true);
//		
//		if(issueType.getId().equals("issue"))
//			jScript.append("$('#issue a:nth-child(1) span:nth-child(1)').text('Apontamento / Plano de Ação');");
			
//		if(issueType.getId().equals("actionplan"))
//			jScript.append("$('#issue a:nth-child(1) span:nth-child(1)').text('Plano de Ação');");
		
//		renderJavaScriptAfterLoad(jScript.toString());
	}
	
	@Override
	protected void renderPage(IPage arg0) {
		// TODO Auto-generated method stub
		super.renderPage(arg0);
//		StringBuffer jScript = new StringBuffer();
//			jScript.append("$('#issue a:nth-child(1) span:nth-child(1)').text('Apontamento / Plano de Ação');");
//		renderJavaScriptAfterLoad(jScript.toString());		
	}
	
	@Override
//	DMM - EV190711 - Início - 2018-04-13
	protected void renderControlFooter() {
		super.renderControlFooter();
		StringBuffer jScript = new StringBuffer();
		
		IAppObj issueObj = this.form.getAppObj();
		
		IEnumAttribute issueTypeList = issueObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(), true);
		if(issueType.getId().equals("please_select")) {
			jScript.append("$('#tr_owner_status th:nth-child(1)').text('Status');");
			jScript.append("$('#tr_obj_id th:nth-child(1)').text('ID');");
			jScript.append("$('#tr_name th:nth-child(1)').text('Título');");
			jScript.append("$('#tr_issue_date th:nth-child(1)').text('Data de Criação');");
			jScript.append("$('#tr_issueRelevantObjects th:nth-child(1)').text('Objetos relevantes');");
			jScript.append("$('#tr_creator th:nth-child(1)').text('Criador');");
			jScript.append("$('#tr_reviewers th:nth-child(1)').text('Revisor');");
			jScript.append("$('#tr_owners th:nth-child(1)').text('Descrição');");
//			jScript.append("$('#tr_origemteste').hide()");
		}
		else {
//			jScript.append("$('#tr_origemteste').show()");
		
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
			
		}
		renderJavaScriptAfterLoad(jScript.toString());
//		DMM - EV190711 - Fim - 2018-04-13		
	}

}
