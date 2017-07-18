package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
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

public class CustomIssueSaveActionCommand extends IssueSaveActionCommand  {
	
	private static final com.idsscheer.batchserver.logging.Logger debuglog = new com.idsscheer.batchserver.logging.Logger();
	private String viewName = "customIssue_ap_2Object";
	private String filterColumn = "iro_id";
	private String column_creator_status = "creatorStatus";
	private String column_owner_status = "ownerStatus";
	private String column_reviewer_status = "reviewerStatus";
	private String column_obj_id = "is_id";
	private String column_plannedenddate = "plannedenddate";
	private String column_deactivated = "deactivated";
	//private int count_is_open = 0;
	//private int count_is_progress = 0;
	//private int count_is_fup = 0;
	private long deactivated;
		
	private static final boolean DEBUGGER_ON = true;
	protected void afterExecute(){
//				
		//IUIEnvironment currEnv = this.environment;
		IAppObj currIssueAppObj = this.formModel.getAppObj();
		IListAttribute iroList = currIssueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS);
		List<IAppObj> iroElements = iroList.getElements(this.getUserContext());
		//Iterator<IAppObj> iroIterator = iroElements.iterator();
		
		
		IAppObjFacade issueFacade = this.environment.getAppObjFacade(ObjectType.ISSUE);
		
		Map filterMap = new HashMap();
		
		IEnumAttribute issueTypeList = currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeList.getRawValue(), true);
		
		 if(issueType.getId().equals("actionplan")){					
			this.displayLog("Tipo : " + issueType.getId());		

		
			try{

				//while(iroIterator.hasNext()){
				for(int i = 0; i < iroElements.size(); i++){
					
					//IAppObj iroAppObj = iroIterator.next();
					IAppObj iroAppObj = iroElements.get(i);
					IOVID iroOVID = iroAppObj.getVersionData().getHeadOVID();
					IAppObj iroUpdObj = issueFacade.load(iroOVID, true);
							
					if(!iroAppObj.getObjectType().equals(ObjectType.ISSUE))
					
						continue;
					

					issueFacade.allocateWriteLock(iroOVID);
					
					Long version = currIssueAppObj.getAttribute(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue();
					
					IDateAttribute issueenddate = iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);
					Date issueendtateValue = issueenddate.getRawValue();
					
					IDateAttribute currDataFim = currIssueAppObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE);
					Date currDataFimValue = currDataFim.getRawValue();
	
							
					IDateAttribute dataIni = currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PLANDTINI);
					Date dataPlanIni = dataIni.getRawValue();
					
					Boolean breplaned = currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_REPLANNED).getRawValue();
					this.displayLog("Status Replanejado : " + breplaned);					
					 
					Long s_resch = currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_RESCHEDULING).getRawValue();
					
					this.displayLog("Reschedule  : " + s_resch );		
					
				 
					// Last Date List
					filterMap.put(this.filterColumn, iroUpdObj.getObjectId());
					Map listObjMap = this.getIssuesFromIRO(this.viewName, filterMap);
					List <CustomIssueObj> listObj = (List<CustomIssueObj>)listObjMap.get("list");
					int qtd = (Integer)listObjMap.get("qtd");
					//List<CustomIssueObj> listObj = this.getIssuesFromIRO(this.viewName, filterMap);					
					
						CustomIssueObj cstIssue = listObj.iterator().next();						
						Date lastDateList = cstIssue.getObjDate();								
															
					
					if(breplaned == true){											
						
						if(dataPlanIni==null){
							dataPlanIni = currDataFimValue;
							this.displayLog("Data Inicial do Planejado : " + currDataFimValue );
							currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PLANDTINI).setRawValue(currDataFimValue);
							
						}
				
					iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_REPLANNED).setRawValue("True");
					this.displayLog("Data DataFim corrente : " + currDataFimValue );
                    
					s_resch += 1;
					
                    this.displayLog("numero vezes + 1 : " + s_resch );
					
					this.displayLog("Quantidade de replanejamentos : " + s_resch);
					currIssueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_RESCHEDULING).setRawValue(s_resch);
					
					issueFacade.save(currIssueAppObj, this.getDefaultTransaction(), true);
					issueFacade.releaseLock(currIssueAppObj.getVersionData().getHeadOVID());

					if( qtd== 1 && version == 1){
						iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_REPLANNED).setRawValue("True");
						iroAppObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).setRawValue(lastDateList);
						issueFacade.save(iroAppObj, this.getDefaultTransaction(), true);
						issueFacade.releaseLock(iroAppObj);							
					}else{
						iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_REPLANNED).setRawValue("True");
						iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).setRawValue(lastDateList);
						issueFacade.save(iroUpdObj, this.getDefaultTransaction(), true);
						issueFacade.releaseLock(iroOVID);
					}				
					
					break;
					
					}else{
			
							CustomIssueObj cstIssues = listObj.iterator().next();						
							Date lastDateLists = cstIssues.getObjDate();	
							
						
						if( qtd == 1 && version == 1){
							iroAppObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).setRawValue(lastDateList);
							issueFacade.save(iroAppObj, this.getDefaultTransaction(), true);
							issueFacade.releaseLock(iroAppObj);							
						}else{
							iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).setRawValue(lastDateList);
							issueFacade.save(iroUpdObj, this.getDefaultTransaction(), true);
							issueFacade.releaseLock(iroOVID);
						}														
						break;							
					}
			
				}
			}catch(Exception e){
				this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage() , new String[] { getStringRepresentation(this.formModel.getAppObj()) });
			}
		}
		
	}
	
	//private List<CustomIssueObj> getIssuesFromIRO(String viewName, Map<String,Object> filterMap){
	private Map<String, Object> getIssuesFromIRO(String viewName, Map<String,Object> filterMap){
		
		List<CustomIssueObj> retList = new ArrayList<CustomIssueObj>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		 int number = 0;
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), viewName, filterMap, null,
				true, this.getDefaultTransaction());
		try{
			
			List<CustomIssueObj> listObj = new ArrayList<CustomIssueObj>();
			Iterator itQuery = query.getResultIterator();

			while(itQuery.hasNext()){
				
				CustomIssueObj cstIssueObj = new CustomIssueObj();
				IViewObj viewObj = (IViewObj)itQuery.next();
				Long objId = (Long) viewObj.getRawValue(this.column_obj_id);
				Date objDate = (Date) viewObj.getRawValue(this.column_plannedenddate);
				
				cstIssueObj.setObjDate(objDate);
				cstIssueObj.setObjId(objId);
				number += 1;				
				listObj.add(cstIssueObj);		
				
			}
			
			listObj.sort(new Comparator<CustomIssueObj>(){
				@Override
				public int compare(CustomIssueObj ant, CustomIssueObj post){
					long antTime = ant.getObjDate().getTime();
					long postTime = post.getObjDate().getTime();
					return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
				}
			});
			
			retList.add(listObj.get(listObj.size() - 1));
			
		}finally{
			query.release();
		}
		
		retMap.put("list", retList);
		retMap.put("qtd", number);
		
		return retMap;
		
		
	}
	
	private void displayLog(String message){
		if(DEBUGGER_ON){
			debuglog.info(this.getClass().getName(),message );
		}
	}
}
