package com.idsscheer.webapps.arcm.ui.components.riskmanagement.control.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.form.IRoleSelectionModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
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
		
		try{
			
			String categRC = this.getCorpRiskCateg(this.form.getAppObj());
			
			if(page.getId().equals("cst_hierarchy")){
				for (IRowgroup rowGroup : page.getRowgroups()) {
					if(rowGroup.getId().equals("cst_hierarchy.1")){
						for(IRow row : rowGroup.getRows()){
							if(row.getId().equals("cst_rskcateg")){
								//super.renderRow(row);
								this.buffer.append("<tr id=\"tr_cst_rskcateg\" class=\"ATTRIBUTE_ROW contentfont \">")
										   .append("<th colspan=\"2\" class=\"ATTR_LABEL_WIDTH ATTR_LABEL_COLUMN CONTENT_ATTR_LABEL_READ contentfont\">Categoria de Risco</th>")
										   .append("<td class=\"ATTR_MANDATORY_WIDTH ATTR_MARK_NOT_MANDATORY_COLUMN\">")
										   .append("<img border=\"0\" src=\"/arcm/design/default/images/icons/blank.gif\" width=\"20\" height=\"1\">")
										   .append("</td>")
										   .append("<th></th>")
										   .append("<td colspan=\"2\" class=\"ATTR_VALUE_WIDTH NO_BORDER NO_MARGIN NO_PADDING CONTENT_ATTR_DATA_READ\">")
										   .append("<table class=\"ATTR_DATA_TABLE NO_BORDER NO_MARGIN NO_PADDING\">")
										   .append("<tbody><tr class=\"\"><td class=\"TEXTFIELD_READ\">")
										   .append("<input type=\"hidden\" value=\"TESTE\" id=\"cst_rskcateg\">")
										   .append(categRC)
										   .append("</td></tr></tbody></table>")
										   .append("</td>")
										   .append("</tr>");
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			this.environment.getDialogManager().getNotificationDialog().addError("Erro ao obter Categoria de Risco " + e.getMessage());
		}
		
	}
	
	private String getCorpRiskCateg(IAppObj rcObj) throws Exception{
		String retCateg = "";
		
		ITransaction defaultTransaction = this.environment.getBLTransactionManager().getReadOnlyTransaction();
		
		Map filterMap = new HashMap();
		filterMap.put("id", rcObj.getObjectId());
		
		IViewQuery query = QueryFactory.createQuery(this.environment.getUserContext(), "cst_view_corprisk", filterMap, null,
				true, defaultTransaction);
		
		try{
			
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				IViewObj viewObj = (IViewObj)itQuery.next();
				
				/*long categID = (Long)viewObj.getRawValue("ct_obj_id");
				long categVersionNumber = (Long)viewObj.getRawValue("ct_version_number");
				
				IOVID categOVID = OVIDFactory.getOVID(categID, categVersionNumber);
				IAppObj categAppObj = this.environment.getAppObjFacade(ObjectType.HIERARCHY).load(categOVID, defaultTransaction, true);*/
				
				retCateg = (String)viewObj.getRawValue("ct_name");
				break;
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return retCateg;
		
	}
	//Fim REO - 18.08.2017 - Riscos Corporativos
	
}
