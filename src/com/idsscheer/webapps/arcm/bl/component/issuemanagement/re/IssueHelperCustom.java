package com.idsscheer.webapps.arcm.bl.component.issuemanagement.re;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mvel2.util.ThisLiteral;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageHandler;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.re.REKey;
import com.idsscheer.webapps.arcm.bl.re.ext.CollectiveHelper;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.Message;
import com.idsscheer.webapps.arcm.common.util.Message.Type;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;
import com.idsscheer.webapps.arcm.ui.web.support.MessageContainer;

public class IssueHelperCustom extends CollectiveHelper {
//	DMM - BOF- Revisão da sprint - 14/05/2018
	public static boolean isUserSysAdmin() {
		REEnvironment env = REEnvironment.getInstance();
		return env.getUserContext().getUserRights().isSysadmin();
	}
//	DMM - EOF- Revisão da sprint - 14/05/2018
	
	public static void setIssueDelay(){
		//IAppObjFacade issueFacade = FacadeFactory.getInstance().getAppObjFacade(getFullReadAccessUserContext(), ObjectType.ISSUE);
		IAppObj issueObj = REEnvironment.getInstance().getRuleAppObj().getAppObj();
		if(!issueObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).isEmpty()){
			Date issueDate = issueObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
			Calendar calendar = Calendar.getInstance();
			
			if(issueDate.before(calendar.getTime()))
				CollectiveHelper.setValue(IIssueAttributeTypeCustom.STR_STATETIME, "overdue");
			
			if(!issueDate.before(calendar.getTime()))
				CollectiveHelper.setValue(IIssueAttributeTypeCustom.STR_STATETIME, "on_time");
		}
		//issueFacade.save(issueObj, , arg2);
		
	}
	
	public static void setRiskAndProcessClassification(){
		
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj ra = env.getRuleAppObj();
		REKey raResultKey = ra.createAtomicKey("ra_result");
		REKey raResFinalKey = ra.createAtomicKey("ra_residualfinal");
		REKey riskNameKey = ra.createAtomicKey("rsk_name");
		REKey mProcessKey = ra.createAtomicKey("cst_modelname");
		REKey processKey = ra.createAtomicKey("cst_process");
		REKey appSysKey = ra.createAtomicKey("cst_appsystem");
		IAppObj riskObj = null;
		
		List<IAppObj> iroList = ra.getAppObj().getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS).getElements(getFullReadAccessUserContext());
		for(IAppObj iroObj : iroList){
			
			if(iroObj.getObjectType().equals(ObjectType.CONTROLEXECUTION)){
				List<IAppObj> ceList = iroObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(getFullReadAccessUserContext());
				for(IAppObj ceObj : ceList){
					riskObj = getRiskFromControl(ceObj.getObjectId());
				}
				
			}
			
			if(iroObj.getObjectType().equals(ObjectType.TESTCASE)){
				List<IAppObj> tcList = iroObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(getFullReadAccessUserContext());
				for(IAppObj tcObj : tcList){
					riskObj = getRiskFromControl(tcObj.getObjectId());
				}
			}
			
			if(iroObj.getObjectType().equals(ObjectType.ISSUE)){
				IEnumAttribute issueTypeAttr = iroObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr.getRawValue(), true);
				if(issueType.getId().equals("issue")){
					
					List<IAppObj> issueList = iroObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS).getElements(getFullReadAccessUserContext());
					for(IAppObj issueObj : issueList){
						
						if(issueObj.getObjectType().equals(ObjectType.CONTROLEXECUTION)){
							List<IAppObj> ceList = issueObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(getFullReadAccessUserContext());
							for(IAppObj ceObj : ceList){
								riskObj = getRiskFromControl(ceObj.getObjectId());
							}
							
						}
						
						if(issueObj.getObjectType().equals(ObjectType.TESTCASE)){
							List<IAppObj> tcList = issueObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(getFullReadAccessUserContext());
							for(IAppObj tcObj : tcList){
								riskObj = getRiskFromControl(tcObj.getObjectId());
							}
						}
						
					}
					
				}
			}
			
		}
		
		if(!(riskObj == null)){
			String raResult = "Não Avaliado";
			String raResidualFinal = "Não Avaliado";
			
			if(!riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).isEmpty()){
				raResult = riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
			}
			if(!riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).isEmpty()){
				raResidualFinal = riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue();
			}
			
			ra.setRawValue(mProcessKey.getString(), getMProcess(riskObj));
			ra.setRawValue(processKey.getString(), getProcess(riskObj));
			ra.setRawValue(appSysKey.getString(), getAppSys(riskObj));
			ra.setRawValue(riskNameKey.getString(), riskObj.getAttribute(IRiskAttributeType.ATTR_NAME).getRawValue());
			ra.setRawValue(raResultKey.getString(), raResult);
			ra.setRawValue(raResFinalKey.getString(), raResidualFinal);
		}
	
	}
	
	private static String getMProcess(IAppObj riskObj){
		
		String retMProcess = "";
		
		List<IAppObj> hierList = riskObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(getFullReadAccessUserContext());
		for(IAppObj hierObj : hierList){
			retMProcess = hierObj.getAttribute(IHierarchyAttributeType.ATTR_MODEL_NAME ).getRawValue();
		}
		
		return retMProcess;
		
	}
	
	private static String getProcess(IAppObj riskObj){
		
		String retProcess = "";
		
		List<IAppObj> hierList = riskObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(getFullReadAccessUserContext());
		for(IAppObj hierObj : hierList){
			retProcess = hierObj.getAttribute(IHierarchyAttributeType.ATTR_NAME ).getRawValue();
		}
		
		return retProcess;
	}
	
	private static String getAppSys(IAppObj riskObj){
		
		String retAppSys = "";
		
		List<IAppObj> sysList = riskObj.getAttribute(IRiskAttributeType.LIST_APPSYSTEM).getElements(getFullReadAccessUserContext());
		for(IAppObj sysObj : sysList){
			retAppSys = sysObj.getAttribute(IHierarchyAttributeType.ATTR_NAME).getRawValue();
		}
		
		if(retAppSys.isEmpty())
			retAppSys = "N/A";
		
		return retAppSys;
	}
	
	private static IAppObj getRiskFromControl(long controlObjID){
		
		IAppObj riskAppObj = null;
		long riskID = 0;
		long riskVersionNumber = 0;
		
		Map filterMap = new HashMap();
		filterMap.put("control_obj_id", controlObjID);
		
		IViewQuery query = QueryFactory.createQuery(getFullReadAccessUserContext(), "customcontrol2risk", null, filterMap);
		
		/*IViewQuery query = QueryFactory.createQuery(getFullReadAccessUserContext(), "customcontrol2risk", filterMap, null,
				true, );*/
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				riskID = (Long)viewObj.getRawValue("risk_obj_id");
				riskVersionNumber = (Long)viewObj.getRawValue("risk_version_number");
				
			}
		
			//IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			IAppObjFacade riskFacade = FacadeFactory.getInstance().getAppObjFacade(getFullReadAccessUserContext(), ObjectType.RISK);
			IOVID riskOVID = OVIDFactory.getOVID(riskID, riskVersionNumber);
			riskAppObj = riskFacade.load(riskOVID, true);
		}catch(Exception e){
			query.release();
		}finally{
			query.release();
		}
		
		return riskAppObj;
		
	}
	
	public static boolean isLateThanFirstDate(){
		
		boolean isLate = false;
		REEnvironment reenv = REEnvironment.getInstance();
		RuleAppObj issue = reenv.getRuleAppObj();
		REKey plannedenddateKey = issue.createAtomicKey("plannedenddate");
		
		Date plannedenddate = (Date) issue.getRawValue(plannedenddateKey.getString());
		
		if(issue.isDirty(plannedenddateKey.getString())){
			if(plannedenddate != null){
				IAppObjFacade issueFacade = FacadeFactory.getInstance().getAppObjFacade(getFullReadAccessUserContext(), ObjectType.ISSUE);
				IOVID issueOVID = OVIDFactory.getOVID(issue.getObjectId(), issue.getAppObj().getVersionHistory().size());
				
				try {
					IAppObj issueFirstVersion = issueFacade.load(issueOVID, true);
					if(issueFirstVersion != null){
						Date date = issueFirstVersion.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
						Date issueDate = DateUtils.normalizeLocalDate(date, DateUtils.Target.END_OF_DAY);
						Date newDate = DateUtils.normalizeLocalDate(plannedenddate, DateUtils.Target.END_OF_DAY);
						
						if(newDate.before(issueDate))
							isLate = true;
					}
					
				} catch (RightException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}finally{
					issueFacade.releaseLock(issueOVID);
				}
				
			}
		}
		
		return isLate;
		
	}

}
