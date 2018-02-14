package com.idsscheer.webapps.arcm.custom.procrisk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.common.IRequestContext;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class ResidualRiskCalculation {

	private List<IAppObj> controlList;
	private IUserContext userContext;
	private ITransaction transaction;
	private String guid;
	private IRequestContext reqCtx;

	public ResidualRiskCalculation(List<IAppObj> controlList, ITransaction transaction, IRequestContext reqCtx,
			String guid) {
		this.controlList = controlList;
		this.transaction = transaction;
		this.reqCtx = reqCtx;
		this.guid = guid;
		this.userContext = new JobUIEnvironment(ContextFactory.getFullReadAccessUserContext(new Locale("US")))
				.getUserContext();
	}

	public String calculateResidual1Line() throws Exception {

		int countTotal = 0;
		int countInef = 0;
		double risk1line = 0;
		String currStatus = this.getCurrStatus();

		for (IAppObj controlObj : controlList) {

			countTotal += 1;
			List<IAppObj> cetList = controlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS)
					.getElements(userContext);
			for (IAppObj cetObj : cetList) {
				List<IAppObj> ceList = this.getCtrlExecFromCET(cetObj.getObjectId());
				for (IAppObj ceObj : ceList) {
					if (ceObj.getGuid().equals(guid)) {
						if (reqCtx.getParameter(IControlexecutionAttributeType.STR_OWNER_STATUS).equals("3")) {
							if (currStatus.equals("ineffective")) {
								countInef += 1;
							}
						}
					} else {
						IEnumAttribute ownerStatusAttr = ceObj
								.getAttribute(IControlexecutionAttributeType.ATTR_OWNER_STATUS);
						IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(),
								true);
						IEnumAttribute statusAttr = ceObj
								.getAttribute(IControlexecutionAttributeTypeCustom.ATTR_CUSTOMCTRLEXECSTATUS);
						IEnumerationItem statusItem = ARCMCollections.extractSingleEntry(statusAttr.getRawValue(),
								true);
						if (ownerStatus.getId().equals("completed")) {
							if (statusItem.getId().equals("ineffective")) {
								countInef += 1;
							}
						}
					}
				}
			}
		}
		
		if(countTotal > 0)
			risk1line = ( countInef / countTotal );
		
		return this.riskClassification(risk1line);
		
	}
	
	public String calculateResidual2Line() throws Exception{
		
		int countTotal = 0;
		int countInef = 0;
		double risk2line = 0;
		
		for(IAppObj controlObj : controlList){
			
			countTotal += 1;
			
			List<IAppObj> tdList = this.getTestDefFromControl(controlObj);
			for (IAppObj tdObj : tdList) {
				
				List<IAppObj> tstCaseList = this.getTestCaseFromTestDef(tdObj.getObjectId());
				for (IAppObj tstCaseObj : tstCaseList) {
					IEnumAttribute ownerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
					IEnumerationItem ownerStatusItem = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
					IEnumAttribute reviewerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_REVIEWER_STATUS);
					IEnumerationItem reviewerStatusItem = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
					
					if(reviewerStatusItem.getId().equals("accepted")){
						if(ownerStatusItem.getId().equals("noneffective")){
							if(this.testDefClass(tdObj).equals("1linhadefesa")){
								countInef += 1;
							}
						}
					}
				}
				
			}
			
		}
		
		if(countTotal > 0)
			risk2line = ( countInef / countTotal );
		
		return this.riskClassification(risk2line);
		
	}
	
	private Object testDefClass(IAppObj tdObj) {
		IEnumAttribute origTestAttr = tdObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
		IEnumerationItem origTest = ARCMCollections.extractSingleEntry(origTestAttr.getRawValue(), true);
		
		return origTest.getId();
	}

	private List<IAppObj> getTestCaseFromTestDef(long testDefObjID) throws Exception {
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("tdef_id", testDefObjID);
		
		IViewQuery query = QueryFactory.createQuery(userContext, "custom_TestDef2TestCase", filterMap, null,
				true, transaction);
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long tcID = (Long)viewObj.getRawValue("tcase_id");
				long tcVersionNumber = (Long)viewObj.getRawValue("tcase_version_number");
				
				IAppObjFacade tcFacade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.TESTCASE);
				IOVID tcOVID = OVIDFactory.getOVID(tcID, tcVersionNumber);
				IAppObj tcAppObj = tcFacade.load(tcOVID, true);
				
				if(tcAppObj != null){
					if(tcAppObj.getVersionData().isHeadRevision())
						testCaseBuffer.add(tcAppObj);
				}
				
			}
			
			if(testCaseBuffer.size() > 0){
				testCaseBuffer.sort(new Comparator<IAppObj>(){
					@Override
					public int compare(IAppObj ant, IAppObj post){
						long antTime = ant.getVersionData().getCreateDate().getTime();
						long postTime = post.getVersionData().getCreateDate().getTime();
						return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
					}
				});
				testCaseReturn.add(testCaseBuffer.get(testCaseBuffer.size() - 1));
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}		
		
		return (List<IAppObj>) testCaseReturn;
	}

	private List<IAppObj> getTestDefFromControl(IAppObj controlObj) {
		List<IAppObj> testDefReturn = new ArrayList<IAppObj>();
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROL);
		IAppObjQuery query = facade.createQuery();
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.ATTR_CONTROL_ID, controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue()));
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
		
		IAppObjIterator itQuery = query.getResultIterator();
		
		try{
			
			while(itQuery.hasNext()){
								
				IAppObj ctrlObject = itQuery.next();
				
				if(!ctrlObject.getVersionData().isHeadRevision())
					continue;
				
				List<IAppObj> tdList = ctrlObject.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).getElements(userContext);
				for(IAppObj tdObj : tdList){
					if(!tdObj.getVersionData().isDeleted())
						testDefReturn.add(tdObj);
				}
				
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}		
		
		return (List<IAppObj>) testDefReturn;
	}

	public String calculateResidual3Line(){
		return null;
	}

	private String riskClassification(double riskVuln) {
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixo";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "Médio";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alto";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alto";
		}
		
		return riskClassif;
	}

	private String getCurrStatus() {
		String ceStatus = reqCtx.getParameter(IControlexecutionAttributeTypeCustom.STR_CUSTOMCTRLEXECSTATUS);
		String currStatus = "";

		if (ceStatus.equals("1"))
			currStatus = "effective";

		if (ceStatus.equals("2"))
			currStatus = "ineffective";

		return currStatus;
	}

	private List<IAppObj> getCtrlExecFromCET(long cetObjID) throws Exception {

		List<IAppObj> retList = new ArrayList<IAppObj>();
		List<IAppObj> bufList = new ArrayList<IAppObj>();

		Map filterMap = new HashMap();
		filterMap.put("cetask_id", cetObjID);

		IViewQuery query = QueryFactory.createQuery(userContext, "custom_CET2CE", filterMap, null, true, transaction);

		try {

			Iterator itQuery = query.getResultIterator();

			while (itQuery.hasNext()) {

				IViewObj viewObj = (IViewObj) itQuery.next();
				long ceID = (Long) viewObj.getRawValue("ce_id");
				long ceVersionNumber = (Long) viewObj.getRawValue("ce_version_number");

				IAppObjFacade ceFacade = FacadeFactory.getInstance().getAppObjFacade(userContext,
						ObjectType.CONTROLEXECUTION);
				IOVID ceOVID = OVIDFactory.getOVID(ceID, ceVersionNumber);
				IAppObj ceAppObj = ceFacade.load(ceOVID, true);

				if (ceAppObj != null)
					bufList.add(ceAppObj);

			}

			if (bufList.size() > 0) {
				bufList.sort(new Comparator<IAppObj>() {
					@Override
					public int compare(IAppObj ant, IAppObj post) {
						long antTime = ant.getVersionData().getCreateDate().getTime();
						long postTime = post.getVersionData().getCreateDate().getTime();
						return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
					}
				});
				retList.add(bufList.get(bufList.size() - 1));
			}

		} catch (Exception e) {
			query.release();
			throw e;
		} finally {
			query.release();
		}

		return (List<IAppObj>) retList;

	}

}
