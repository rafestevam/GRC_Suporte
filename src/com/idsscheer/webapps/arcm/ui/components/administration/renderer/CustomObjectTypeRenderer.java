package com.idsscheer.webapps.arcm.ui.components.administration.renderer;

import java.util.Map;
import java.util.TreeMap;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.ui.framework.renderer.AbstractBaseRenderer;

public class CustomObjectTypeRenderer extends AbstractBaseRenderer {
	
	private Map<String, String> objectDescList = new TreeMap<>();

	@Override
	protected void renderWritable() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void renderReadonly() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		String objectType = (String) this.context.getAttributeRawValue("object_objtype");
		if (objectType.equals("ISSUE")) {
			
			Long objectId = (Long) this.context.getAttributeRawValue("object_id"); 
			
			// Cria contexto administrativo - getFullReadAccessUserContext() utiliza o usuário internalsystem.
			IUserContext adminCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			
			IAppObjFacade facadeIssue = FacadeFactory.getInstance().getAppObjFacade(adminCtx, ObjectType.ISSUE);;
			
			try {
				IAppObj issue = facadeIssue.load(OVIDFactory.getOVID(objectId), true);
				
				IEnumAttribute actionTypeEnum = issue.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem extractSingleEntry = ARCMCollections.extractSingleEntry(actionTypeEnum.getRawValue(), true);
				
				if (extractSingleEntry.getValue().equals("1")) {
					objectType = "ACTION";
				}
			} catch (RightException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		setTypeDescription();
		this.result.append(getTypeDescription(objectType));
		
	}
	
	private void setTypeDescription() {
		objectDescList.put("USER","Usuário");
		objectDescList.put("DOCUMENTLINKTYPE","Tipo de vínculo");
		objectDescList.put("CONTROLEXECUTIONTASK","Tarefa de execução da controle");
		objectDescList.put("TASKITEM","Tarefa");
		objectDescList.put("RISK","Risco");
		objectDescList.put("REPORT","Relatórios");
		objectDescList.put("ISSUERELEVANTOBJECT","Objeto relevante para o apontamento");
		objectDescList.put("INTERNALMESSAGE","Notificação interna");
		objectDescList.put("NEWSMESSAGE","Notícias");
		objectDescList.put("JOBINFORMATION","Informação sobre a tarefa");
		objectDescList.put("HIERARCHY","Hierarquia");
		objectDescList.put("USERGROUP","Grupo de usuários");
		objectDescList.put("CONTROLEXECUTION","Execução da controle");
		objectDescList.put("DOCUMENT","Documento");
		objectDescList.put("CONTROL","Controle");
		objectDescList.put("ISSUE","Apontamento");
		objectDescList.put("ACTION","Plano de Ação");
		objectDescList.put("TESTCASE","Caso de Teste");
	}
	
	private String getTypeDescription(String objectType) {
		return objectDescList.get(objectType);
	}

}
