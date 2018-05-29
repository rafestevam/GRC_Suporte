package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Calendar;
import java.util.Calendar;
import java.util.Collections;
import java.util.Collections;
import java.util.Date;
import java.util.Date;
import java.util.Locale;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.exception.ObjectAccessException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectAccessException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectLockException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectLockException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectNotUniqueException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectNotUniqueException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.ValidationException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.ValidationException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

@CanBeScheduled
public class IssueOverdueJob extends BaseJob {
	// public static final String KEY_JOB_NAME =
	// EnumerationsCustom.CUSTOM_JOBS.ISSUE_PENDING.getPropertyKey();
	public static final String KEY_JOB_NAME = "Teste";
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public IssueOverdueJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {

		logger.info(this.getClass().getName(), "execute()");
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE);
		logger.info(this.getClass().getName(), "created facade.");
		IAppObjQuery query = facade.createQuery();
		logger.info(this.getClass().getName(), "created query.");
		
		//Somente para debug
		query.addRestriction(
			QueryRestriction.eq(IIssueAttributeType.ATTR_OBJ_ID, 392870)
		);
		
		IAppObjIterator it = query.getResultIterator();
		logger.info(this.getClass().getName(), "created iterator.");

		while (it.hasNext()) {

			logger.info(this.getClass().getName(), "entered loop.");
			IAppObj appObj = it.next();
			logger.info(this.getClass().getName(), "created IAppObj - " + String.valueOf(appObj.getObjectId()));
			IOVID iroOVID = appObj.getVersionData().getHeadOVID();
			logger.info(this.getClass().getName(),
					"created IOVID - " + iroOVID.getAsString() + "version: " + iroOVID.getVersion());

			try {

				facade.allocateLock(iroOVID, LockType.FORCEWRITE);
				IAppObj iroUpdObj = facade.load(iroOVID, true);
				
				logger.info(this.getClass().getName(), "created UpdObj - " + String.valueOf(iroUpdObj.getObjectId()));
				Date actualDateVal = new Date();

				IEnumAttribute issueActionTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				logger.info(this.getClass().getName(), "created action type list.");
				IEnumerationItem issueActionType = ARCMCollections.extractSingleEntry(issueActionTypeList.getRawValue(),
						true);
				logger.info(this.getClass().getName(), "created action type - " + issueActionType.getId().toString());

				Date issuePlannedDateVal = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_PLANNEDENDDATE)
						.getRawValue();
				
				IEnumAttribute stateTimeAttr = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME);
				IEnumerationItem stateTime = ARCMCollections.extractSingleEntry(stateTimeAttr.getRawValue(), true);
				
				Date actualDate = DateUtils.normalizeLocalDate(actualDateVal, DateUtils.Target.END_OF_DAY);
				Date issuePlannedDate = DateUtils.normalizeLocalDate(issuePlannedDateVal, DateUtils.Target.END_OF_DAY);
				
				/*if(iroUpdObj.getObjectId() == 169560)
					System.out.println("teste");*/

				if (issuePlannedDate == null) {
					logger.info(this.getClass().getName(), "created issue date is null");
				} else {
					logger.info(this.getClass().getName(), "created issue date - " + issuePlannedDate.toString());
					if (actualDate.after(issuePlannedDate)) {

						logger.info(this.getClass().getName(), "Is overdue.");

						logger.info(this.getClass().getName(), "Current State: " + 
								iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME));
						
						/*if (!iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME)
								.equals(Collections.singletonList(Enumerations.ISSUESTATETIME.OVERDUE)))*/
						if(!stateTime.getId().equals("overdue")) {
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME)
									.setRawValue(Collections.singletonList(Enumerations.ISSUESTATETIME.OVERDUE));
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}

					} else {
						logger.info(this.getClass().getName(), "Is not overdue.");
						/*if (iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME)
								.equals(Collections.singletonList(Enumerations.ISSUESTATETIME.OVERDUE)))*/ 
						if(stateTime.getId().equals("overdue")){
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_STATETIME)
									.setRawValue(Collections.singletonList(Enumerations.ISSUESTATETIME.ON_TIME));
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
					}
				}

				facade.releaseLock(iroUpdObj.getVersionData().getHeadOVID());
				logger.info(this.getClass().getName(), "release for issue creator.");

			} catch (RightException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectLockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectNotUniqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public String getJobNameKey() {
		return KEY_JOB_NAME;
	}

	public IEnumerationItem getJobType() {
		return EnumerationsCustom.JOBS.JOBLISTCLEANINGJOB;
	}

	protected void deallocateResources() {
	}
}