package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.ILockService;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.ILockObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class AdjustControlOtherLines extends BaseJob{

	private static final long serialVersionUID = 1L;
	private static final String JOB_NAME_KEY = "enumeration.jobs.AdjustControlOtherLines.DBI";

	public AdjustControlOtherLines(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void deallocateResources() {
		deallocateLocalSources();
	}

	private void deallocateLocalSources() {
		ILockService lockService = ARCMServiceProvider.getInstance().getLockService();
		try {
			for (ILockObject lock : lockService.findLocks()) {
				lockService.releaseLock(lock.getObjectType(), lock.getLockUserId(), null);
			}
		} catch (LockServiceException e) {
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
		}
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROL);
		
		try {

			List<IAppObj> controlList = this.getControlList(facade);
			//List<IAppObj> controlUpdList = getDuplicatedControls(controlList, facade);
			
			setBaseObjectsToProcessCount(controlList.size());
			for (IAppObj controlObj : controlList) {
				
				//facade.allocateLock(controlObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				IAppObj controlUpdObj = facade.load(controlObj.getVersionData().getHeadOVID(), true);
				
				List<IAppObj> tstDefList = controlUpdObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).getElements(userContext);
				for (IAppObj tstDefObj : tstDefList) {

					List<IAppObj> tstCaseList = this.getTestCaseFromTestDef(tstDefObj.getObjectId());
					for (IAppObj tstCaseObj : tstCaseList) {
						
						IEnumAttribute ownerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
						IEnumerationItem ownerStatusItem = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
						IEnumAttribute reviewerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_REVIEWER_STATUS);
						IEnumerationItem reviewerStatusItem = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
					
						if(reviewerStatusItem.getId().equals("accepted")){
							
							List<IAppObj> ctBaseList = tstCaseObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(userContext);
							List<IAppObj> dupControlList = this.getDuplicatedControls(ctBaseList, facade);
							
							for (IAppObj dupControlObj : dupControlList) {
								facade.allocateLock(dupControlObj.getVersionData().getOVID(), LockType.FORCEWRITE);
								if(ownerStatusItem.getId().equals("noneffective")){
									if(this.testDefClass(tstDefObj).equals("1linhadefesa")){
										dupControlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE).setRawValue("inefetivo");
										this.setFinalControlStatus(dupControlObj, "inefetivo");
									}
									if(this.testDefClass(tstDefObj).equals("2linhadefesa")){
										dupControlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE).setRawValue("inefetivo");
										this.setFinalControlStatus(dupControlObj, "inefetivo");
									}
								}else{
									if(this.testDefClass(tstDefObj).equals("1linhadefesa")){
										dupControlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE).setRawValue("efetivo");
										this.setFinalControlStatus(dupControlObj, "efetivo");
									}
									if(this.testDefClass(tstDefObj).equals("2linhadefesa")){
										dupControlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE).setRawValue("efetivo");
										this.setFinalControlStatus(dupControlObj, "efetivo");
									}
								}
								facade.save(dupControlObj, getInternalTransaction(), true);
								increaseEditedObjectsCounter(1);
								facade.releaseLock(controlObj.getVersionData().getOVID());
							}
							
						}
						
					}
					
				}
				
				//facade.releaseLock(controlObj.getVersionData().getHeadOVID());
				increaseProgress();

			}
		}catch(Exception e){
			deallocateLocalSources();
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
			throw new JobAbortException(e.getLocalizedMessage(), JOB_NAME_KEY);
		}
	}

	private List<IAppObj> getDuplicatedControls(List<IAppObj> controlList, IAppObjFacade controlFacade) throws Exception {
		List<IAppObj> controlReturn = new ArrayList<IAppObj>();
		
		for (IAppObj controlObj : controlList) {
		
			Map filterMap = new HashMap();
			filterMap.put("control_id", controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue());
			
			IViewQuery query = QueryFactory.createQuery(userContext, "control", filterMap, null,
					true, getInternalTransaction());
			
			try{
			
				Iterator itQuery = query.getResultIterator();
				
				while(itQuery.hasNext()){
					
					IViewObj viewObj = (IViewObj)itQuery.next();
					long controlID = (Long)viewObj.getRawValue("obj_id");
					long ctVersionNumber = (Long)viewObj.getRawValue("control_version_number");
					
					//IAppObjFacade ctFacade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.TESTCASE);
					IOVID ctOVID = OVIDFactory.getOVID(controlID, ctVersionNumber);
					IAppObj ctAppObj = controlFacade.load(ctOVID, true);
					
					if(ctAppObj != null && !ctAppObj.getVersionData().isDeleted()){
						if(ctAppObj.getVersionData().isHeadRevision()){
							controlReturn.add(ctAppObj);
							controlFacade.releaseLock(ctOVID);
						}
					}
					
				}
			
			}catch(Exception e){
				query.release();
				throw e;
			}finally{
				query.release();
			}
		
		}
		
		return (List<IAppObj>) controlReturn;
		
		
	}

	private Object testDefClass(IAppObj tstDefObj) {
		IEnumAttribute origTestAttr = tstDefObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
		IEnumerationItem origTest = ARCMCollections.extractSingleEntry(origTestAttr.getRawValue(), true);
		
		return origTest.getId();
	}

	private List<IAppObj> getTestCaseFromTestDef(long testDefObjID) throws Exception{
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("tdef_id", testDefObjID);
		
		IViewQuery query = QueryFactory.createQuery(userContext, "custom_TestDef2TestCase", filterMap, null,
				true, getInternalTransaction());
		
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

	private List<IAppObj> getControlList(IAppObjFacade facade) {
		List<IAppObj> controlList = new ArrayList<IAppObj>();
		
		IAppObjQuery query = facade.createQuery();
		//query.addRestriction(QueryRestriction.ne(IControlAttributeType.BASE_ATTR_DEACTIVATED, false));

		IAppObjIterator iterator = query.getResultIterator();
		while (iterator.hasNext()) {

			IAppObj controlObj = iterator.next();
			if(!controlObj.getVersionData().isDeleted())
				controlList.add(controlObj);

		}
		query.release();

		return controlList;
	}
	
	private void setFinalControlStatus(IAppObj controlUpdObj, String classification) {
		IStringAttribute stFinalAttr = controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_FINAL);
		IStringAttribute st1LineAttr = controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE);
		IStringAttribute st2LineAttr = controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE);
		IStringAttribute st3LineAttr = controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE);
		if(stFinalAttr.isEmpty()){
			stFinalAttr.setRawValue(classification);
		}else{
			if(stFinalAttr.getRawValue().equals("efetivo")){
				stFinalAttr.setRawValue(classification);
			}else{
				if( (st1LineAttr.isEmpty() || st1LineAttr.getRawValue().equals("efetivo")) && 
					(st2LineAttr.isEmpty() || st2LineAttr.getRawValue().equals("efetivo")) &&
					(st3LineAttr.isEmpty() || st3LineAttr.getRawValue().equals("efetivo"))){
					stFinalAttr.setRawValue(classification);
				}
			}
		}
	}

	@Override
	public String getJobNameKey() {
		return JOB_NAME_KEY;
	}

	@Override
	public IEnumerationItem getJobType() {
		return EnumerationsCustom.CUSTOM_JOBS.CONTROL_AOL;
	}

}
