package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssuerelevantobjectAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
public class CustomIssueGetRiskActionCommand extends IssueCacheActionCommand{

	private static final com.idsscheer.batchserver.logging.Logger debuglog = new com.idsscheer.batchserver.logging.Logger();	
	private static final boolean DEBUGGER_ON = true;
	private String viewName = "custom_risk2hierarchies";
	private String filterColumn = "r_id";
	private String filterColumn1 = "h_type";
	private String column_r_id = "r_id";
	private String column_r_client = "r_client";
	private String column_h_Name = "h_name";
	private String column_h_type = "h_type";
	private String column_h_type_name = "h_type_name";

	protected void afterExecute(){
		
		IAppObj issueAppObj = this.formModel.getAppObj();
		
		
		Map filterMap = new HashMap();
		
		//IListAttribute iroList = issueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS);
		IListAttribute iroList = issueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS);
		
		//List<IAppObj> iroElements = iroList.getElements(this.getUserContext());
		List<IAppObj> iroElements = iroList.getElements(this.getUserContext());		
		Iterator<IAppObj> iroIterator = iroElements.iterator();				
		//IAppObjFacade testFacade = this.environment.getAppObjFacade(ObjectType.TESTCASE);			
		//IAppObjFacade controlFacade = this.environment.getAppObjFacade(ObjectType.TESTCASE);
		IAppObjFacade facade = null;
		IEnumAttribute issueTypeList = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(),true);
		
				
		/*if(issueType.getId() != null ){*/
		if(issueType.getId().equals("issue")){
			
			try{
				
				while(iroIterator.hasNext()){
					
					IAppObj iroAppObj = iroIterator.next();
					IOVID iroOVID = iroAppObj.getVersionData().getHeadOVID();
					//IAppObj iroLstObj = testFacade.load(iroOVID, true);		
										
					if(!iroAppObj.getObjectType().equals(ObjectType.TESTCASE)){
						if(!iroAppObj.getObjectType().equals(ObjectType.CONTROLEXECUTION)){
							continue;
						}
					}
					
					facade = FacadeFactory.getInstance().getAppObjFacade(this.getFullGrantUserContext(), iroAppObj.getObjectType());
					IAppObj iroLstObj = facade.load(iroOVID, true);
					
					//testFacade.allocateWriteLock(iroLstObj.getVersionData().getHeadOVID());
					facade.allocateWriteLock(iroOVID);

					List<IAppObj> LstcontrolObj = iroLstObj.getAttribute( ITestcaseAttributeType.LIST_CONTROL).getElements(this.getUserContext());
					
					for(IAppObj controlObj: LstcontrolObj) {
						
						String ssubpro = controlObj.getAttribute(IHierarchyAttributeType.ATTR_NAME).getRawValue();
						
					}
									
					List<IAppObj> LstprocObj = iroLstObj.getAttribute(ITestcaseAttributeType.LIST_PROCESS).getElements(this.getUserContext());				
										
					for(IAppObj pcObj : LstprocObj ){
					
						String  smodelname = pcObj.getAttribute(IHierarchyAttributeType.ATTR_NAME ).getRawValue();
						String  sprocess = pcObj.getAttribute(IHierarchyAttributeType.ATTR_MODEL_NAME).getRawValue();
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_MODELNAME).setRawValue( smodelname);
						//issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PROCESS).setRawValue(sprocess);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PROCESS).setRawValue( smodelname);
					
					}					
										
					List<IAppObj> riskObj = iroLstObj.getAttribute(ITestcaseAttributeType.LIST_RISK).getElements(this.getUserContext());
										
					for(IAppObj rskObj : riskObj){
						
												
						String sresult = rskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
						String sresidual = rskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue();
						String srskname  = rskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_NAME).getRawValue();
						
						this.displayLog("Classificação Risco Potencial Associado: " + sresult);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(sresult);
						
						this.displayLog("Classificação Risco Residual Associado: " + sresidual);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(sresidual);
						
						this.displayLog("Classificação Risco Residual Associado: " + srskname );
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RSK_NAME).setRawValue(srskname);
						
						
						filterMap.put(this.filterColumn, rskObj.getObjectId());
						filterMap.put(this.filterColumn1, 11);
						List<CustomRiskObj> listObj = this.getRiskFromIRO(this.viewName, filterMap);					
														
							CustomRiskObj cstRisk = listObj.listIterator().next();					
							String h_Name = cstRisk.geth_Name();
							this.displayLog(h_Name);
							issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_APPSYSTEM).setRawValue(h_Name);
						break;

                  }
					
				}
			}catch(Exception e){
				
						this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(),new String[] {
								getStringRepresentation(this.formModel.getAppObj())
						}
				);
				
			}
			
			}
		else if (issueType.getId().equals("actionplan")) {
				try{
					this.displayLog("action type 2");
					while(iroIterator.hasNext()){
												
						
						IAppObj iroAppObj = iroIterator.next();
						IOVID iroOVID = iroAppObj.getVersionData().getHeadOVID();
						//IAppObj iroLstObj = testFacade.load(iroOVID, true);
						
						
						if(!iroAppObj.getObjectType().equals(ObjectType.ISSUE))
							continue;
						
						facade = FacadeFactory.getInstance().getAppObjFacade(this.getFullGrantUserContext(), iroAppObj.getObjectType());
						IAppObj iroLstObj = facade.load(iroOVID, true);
						
						String sresult = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
						this.displayLog("Resultado:" + sresult);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(sresult);
						
						String sresidual = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue();
						this.displayLog("Residual final:" + sresidual);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(sresidual);
						
						String smodelname = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_MODELNAME).getRawValue();
						this.displayLog("Marco Processo:" + smodelname );
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_MODELNAME).setRawValue(smodelname);
						
						String sprocesso = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PROCESS).getRawValue();
						this.displayLog("Processo:" + smodelname );
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PROCESS).setRawValue(sprocesso);
						
						
						String srskname = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RSK_NAME).getRawValue();
						this.displayLog("risco name:" + srskname);
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_RSK_NAME).setRawValue(srskname);
						
						String sappsystem = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_APPSYSTEM).getRawValue();
						issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_APPSYSTEM).setRawValue(sappsystem);
						
						break;
							
						}				
					
				}catch(Exception e){
					
							this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(),new String[] {
									getStringRepresentation(this.formModel.getAppObj())
							}
					);
					
				}
				
			} 
			
		}

private List<CustomRiskObj> getRiskFromIRO(String viewName, Map<String,Object> filterMap){
		
		List<CustomRiskObj> retList = new ArrayList<CustomRiskObj>();
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), viewName, filterMap, null,
				true, this.getDefaultTransaction());
		try{
			
			List<CustomRiskObj> listObj = new ArrayList<CustomRiskObj>();
			Iterator itQuery = query.getResultIterator();
			 
			while(itQuery.hasNext()){
				
				CustomRiskObj cstRiskObj = new CustomRiskObj();
				IViewObj viewObj = (IViewObj)itQuery.next();
				Long r_Id = (Long) viewObj.getRawValue(this.column_r_id);
				String h_Name = (String) viewObj.getRawValue(this.column_h_Name);
				
				
				cstRiskObj.seth_Name(h_Name);
				cstRiskObj.setr_Id(r_Id);
				
				listObj.add(cstRiskObj);			
				
			}
			
			retList.add(listObj.get(listObj.size() - 1));
						
		}finally{
			query.release();
		}
		
		return (List<CustomRiskObj>)retList;
		
	}
	
	private void displayLog(String message){
		if(DEBUGGER_ON){
			debuglog.info(this.getClass().getName(),message );
		}
	}
 }

