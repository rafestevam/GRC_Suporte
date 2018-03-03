package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class AdjustControl1Line extends BaseJob {

	private static final long serialVersionUID = 1L;
	public static final String JOB_NAME_KEY = "enumeration.jobs.AdjustControl1Line.DBI";
	//public static final String JOB_NAME_KEY = "TESTE_1";

	public AdjustControl1Line(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void deallocateResources() {
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {

		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROL);

		try {

			List<IAppObj> controlList = this.getControlList(facade);
			setBaseObjectsToProcessCount(controlList.size());
			for (IAppObj controlObj : controlList) {
				
				facade.allocateLock(controlObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				IAppObj controlUpdObj = facade.load(controlObj.getVersionData().getHeadOVID(), true);
				
				List<IAppObj> cetList = controlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS)
						.getElements(userContext);
				for (IAppObj cetObj : cetList) {

					List<IAppObj> ceList = this.getCtrlExecFromCET(cetObj.getObjectId());
					for (IAppObj ceObj : ceList) {
						
						if(ceObj.getVersionData().isDeleted())
							continue;
						
						IEnumAttribute ownerStatusAttr = ceObj.getAttribute(IControlexecutionAttributeType.ATTR_OWNER_STATUS);
						IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
						IEnumAttribute statusAttr = ceObj.getAttribute(IControlexecutionAttributeTypeCustom.ATTR_CUSTOMCTRLEXECSTATUS);
						IEnumerationItem statusItem = ARCMCollections.extractSingleEntry(statusAttr.getRawValue(), true);
						if(ownerStatus.getId().equals("completed")){
							log.info("Status EC: COMPLETED*");
							if(statusItem.getId().equals("ineffective")){
								controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE).setRawValue("inefetivo");
								this.setFinalControlStatus(controlUpdObj, "inefetivo");
							}else{
								controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_1LINE).setRawValue("efetivo");
								this.setFinalControlStatus(controlUpdObj, "efetivo");
							}
							
							facade.save(controlUpdObj, getInternalTransaction(), true);
							increaseEditedObjectsCounter(1);
						}
						
					}
					
				}
				
				facade.releaseLock(controlObj.getVersionData().getHeadOVID());
				increaseProgress();

			}
			
			//setJobEndState(JOBENDSTATE.SUCCESS);
			
		} catch (Exception e) {
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
			throw new JobAbortException(e.getLocalizedMessage(), JOB_NAME_KEY);
		}

	}

	private List<IAppObj> getCtrlExecFromCET(long cetObjID) throws Exception {
		List<IAppObj> retList = new ArrayList<IAppObj>();
		List<IAppObj> bufList = new ArrayList<IAppObj>();
		IAppObjFacade ceFacade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROLEXECUTION);

		Map filterMap = new HashMap();
		filterMap.put("cetask_id", cetObjID);

		IViewQuery query = QueryFactory.createQuery(userContext, "custom_CET2CE", filterMap, null, true,
				this.getInternalTransaction());

		try {

			Iterator itQuery = query.getResultIterator();

			while (itQuery.hasNext()) {

				IViewObj viewObj = (IViewObj) itQuery.next();
				long ceID = (Long) viewObj.getRawValue("ce_id");
				long ceVersionNumber = (Long) viewObj.getRawValue("ce_version_number");

				// IAppObjFacade ceFacade =
				// this.environment.getAppObjFacade(ObjectType.CONTROLEXECUTION);
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

	private List<IAppObj> getControlList(IAppObjFacade facade) {
		List<IAppObj> controlList = new ArrayList<IAppObj>();
		
		IAppObjQuery query = facade.createQuery();
		//query.addRestriction(QueryRestriction.ne(IControlAttributeType.BASE_ATTR_DEACTIVATED, false));
		query.addOrder(QueryOrder.ascending(IControlAttributeType.ATTR_NAME));
		
		IAppObjIterator iterator = query.getResultIterator();
		while (iterator.hasNext()) {

			IAppObj controlObj = iterator.next();
			if(!controlObj.getVersionData().isDeleted())
				controlList.add(controlObj);

		}

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
		// TODO Auto-generated method stub
		return JOB_NAME_KEY;
	}

	@Override
	public IEnumerationItem getJobType() {
		// TODO Auto-generated method stub
		return EnumerationsCustom.JOBS.JOBLISTCLEANINGJOB;
	}

}
