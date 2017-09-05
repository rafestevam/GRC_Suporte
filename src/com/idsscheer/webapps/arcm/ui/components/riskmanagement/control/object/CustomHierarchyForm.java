package com.idsscheer.webapps.arcm.ui.components.riskmanagement.control.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.models.form.IRoleSelectionModel;
import com.idsscheer.webapps.arcm.config.metadata.form.IPage;
import com.idsscheer.webapps.arcm.config.metadata.form.IRowgroup;
import com.idsscheer.webapps.arcm.config.metadata.uicommon.IRow;
import com.idsscheer.webapps.arcm.ui.framework.controls.object.Form;

public class CustomHierarchyForm extends Form {
	
	public CustomHierarchyForm(String p_instanceName, Map<String, String> p_parameters){
		super(p_instanceName,p_parameters);
	}
	
	//Inicio REO - 18.08.2017 - Riscos Corporativos
	@Override
	protected void renderRoleSelection(IRoleSelectionModel model) {
		// TODO Auto-generated method stub
		super.renderRoleSelection(model);
		
		List<String> jsScripts = new ArrayList<String>();
		jsScripts.add("custom_corprisk");
		this.renderJavaScriptRef(jsScripts);
	}
	
	@Override
	protected void renderPage(IPage page) {
		// TODO Auto-generated method stub
		super.renderPage(page);
		
		if(page.getId().equals("cst_hierarchy")){
			for (IRowgroup rowGroup : page.getRowgroups()) {
				if(rowGroup.getId().equals("cst_hierarchy.1")){
					for(IRow row : rowGroup.getRows()){
						if(row.getId().equals("cst_rskcateg")){
							//super.renderRow(row);
							this.buffer.append("<tr id=\"tr_cst_model_name\" class=\"ATTRIBUTE_ROW contentfont \">")
									   .append("<th colspan=\"2\" class=\"ATTR_LABEL_WIDTH ATTR_LABEL_COLUMN CONTENT_ATTR_LABEL_READ contentfont\">Categoria de Risco</th>")
									   .append("</tr>");
						}
					}
				}
			}
		}
		
	}
	//Fim REO - 18.08.2017 - Riscos Corporativos
	
}
