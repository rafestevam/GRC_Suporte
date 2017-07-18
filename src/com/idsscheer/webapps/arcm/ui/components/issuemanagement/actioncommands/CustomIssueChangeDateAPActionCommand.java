package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class CustomIssueChangeDateAPActionCommand extends IssueCacheActionCommand {

	protected void afterExecute(){
		
		//IUIEnvironment currEnv = this.environment;
		IAppObj currIssueAppObj = this.formModel.getAppObj();
		IListAttribute iroList = currIssueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> iroElements = iroList.getElements(this.getUserContext());
		Iterator<IAppObj> iroIterator = iroElements.iterator();
		
		IAppObjFacade issueFacade = this.environment.getAppObjFacade(ObjectType.ISSUE);
		
		IEnumAttribute issueTypeList = currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(), true);

		
		
// Carrega a data final do apontamento para o Plano de Ação.
		
		
		if(issueType.getId().equals("actiontype2")){					
		
			try{

				while(iroIterator.hasNext()){
					
					IAppObj iroAppObj = iroIterator.next();
					
					IOVID iroOVID = iroAppObj.getVersionData().getHeadOVID();
					IAppObj iroLastObj = issueFacade.load(iroOVID,this.getDefaultTransaction(), true);
					
					
					currIssueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS).removeElement(iroAppObj, this.getUserContext());
		
											
					if(iroAppObj.getObjectType() != ObjectType.ISSUE)
						continue;
					
					IDateAttribute currDataFim = currIssueAppObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);
					Date currDataFimValue = currDataFim.getRawValue();
	
					IDateAttribute dataFim = iroLastObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);								
					Date dataFimValue = dataFim.getRawValue();
					
				   if(currDataFimValue == null){
						IDateAttribute dateAttr = currIssueAppObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);
						dateAttr.setRawValue(dataFimValue);
					
							
						this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, " Data Fim do Plano de Ação: " + dataFimValue , new String[] { getStringRepresentation(this.formModel.getAppObj()) });
					}
									
					break;
				}
			}catch(Exception e){
				this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage() , new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			}
		}
		
	}
	
}
