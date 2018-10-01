package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class CorpRiskFixingJob extends BaseJob {
	
	private static final long serialVersionUID = 1L;
	//private static final long serialVersionUID = 1L;
	
	public static final String KEY_JOB_NAME = "enumeration.CorpRiskFixingJob.DBI";
	
	public CorpRiskFixingJob(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
	}

	@Override
	protected void deallocateResources() {
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.HIERARCHY);
		List<IAppObj> corpRiskList = getCorpRisk(facade);
		long processedObjs = 0;
		
		for (IAppObj corpRisk : corpRiskList) {
			try {
				CustomCorpRiskHierarchy crCalc = new CustomCorpRiskHierarchy(corpRisk, userContext, getInternalTransaction());
				
				String residual = crCalc.calculateResidualCR();
				
				facade.allocateLock(corpRisk.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(residual);
				facade.save(corpRisk, getInternalTransaction(), true);
				facade.releaseLock(corpRisk.getVersionData().getHeadOVID());
				
				processedObjs += 1;
				
			} catch (Exception e) {
				throw new JobAbortException(e.getMessage(), "CorpRiskFixingJob");
			}
		}
		
		this.setProcessedBaseObjectsCount(processedObjs);

	}

	private List<IAppObj> getCorpRisk(IAppObjFacade facade) {
		List<IAppObj> corpRiskList = new ArrayList<>();
		IAppObjQuery query = facade.createQuery();
		query.addRestriction(QueryRestriction.eq(IHierarchyAttributeTypeCustom.ATTR_CORPRISK, true));
		
		IAppObjIterator iterator = query.getResultIterator();
		while(iterator.hasNext()){
			
			IAppObj corpRisk = iterator.next();
			corpRiskList.add(corpRisk);
			
		}
		return corpRiskList;
	}

	@Override
	public String getJobNameKey() {
		// TODO Auto-generated method stub
		return KEY_JOB_NAME;
	}

	@Override
	public IEnumerationItem getJobType() {
		return EnumerationsCustom.JOBS.JOBLISTCLEANINGJOB;
	}

}
