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
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
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
public class IssuePendingUpdateJob extends BaseJob {
//	public static final String KEY_JOB_NAME = EnumerationsCustom.CUSTOM_JOBS.ISSUE_PENDING.getPropertyKey();
	public static final String KEY_JOB_NAME = "Teste";
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public IssuePendingUpdateJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {

		logger.info(this.getClass().getName(), "execute()");
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE);
		logger.info(this.getClass().getName(), "created facade.");
		IAppObjQuery query = facade.createQuery();
		logger.info(this.getClass().getName(), "created query.");
		IAppObjIterator it = query.getResultIterator();
		logger.info(this.getClass().getName(), "created iterator.");

		while (it.hasNext()) {
			
			logger.info(this.getClass().getName(), "entered loop.");
			IAppObj appObj = it.next();
			logger.info(this.getClass().getName(), "created IAppObj - " + String.valueOf(appObj.getObjectId()));
			IOVID iroOVID = appObj.getVersionData().getHeadOVID();
			logger.info(this.getClass().getName(), "created IOVID - " + iroOVID.getAsString() + "version: " + iroOVID.getVersion());

			try {
				
				facade.allocateLock(iroOVID, LockType.FORCEWRITE);
				IAppObj iroUpdObj = facade.load(iroOVID, true);
				logger.info(this.getClass().getName(), "created UpdObj - " + String.valueOf(iroUpdObj.getObjectId()));
				Date actualDate = new Date();
				
				/*// Trecho para gerar datas de sistemas futuras para teste. Acrescenta 2 dias.
				Calendar calAux = Calendar.getInstance();
				calAux.setTime(actualDate);
				calAux.add(Calendar.DATE, 2);
				actualDate = calAux.getTime();*/
				
				logger.info(this.getClass().getName(), "created actual date - " + actualDate.toString());
				
				IEnumAttribute issueActionTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				logger.info(this.getClass().getName(), "created action type list.");
				IEnumerationItem issueActionType = ARCMCollections.extractSingleEntry(issueActionTypeList.getRawValue(),
						true);
				logger.info(this.getClass().getName(), "created action type - " + issueActionType.getId().toString());
			
				IEnumAttribute issueCreatorStatusTypeList = iroUpdObj
						.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS);
				logger.info(this.getClass().getName(), "created creator status list.");
				IEnumerationItem issueCreatorStatusType = ARCMCollections
						.extractSingleEntry(issueCreatorStatusTypeList.getRawValue(), true);
				logger.info(this.getClass().getName(), "created creator status type Value - " + issueCreatorStatusType.getId().toString());
				
				IEnumAttribute issueOwnerStatusTypeList = iroUpdObj
						.getAttribute(IIssueAttributeTypeCustom.ATTR_OWNER_STATUS);
				logger.info(this.getClass().getName(), "created owner status list.");
				IEnumerationItem issueOwnerStatusType = ARCMCollections
						.extractSingleEntry(issueOwnerStatusTypeList.getRawValue(), true);
				logger.info(this.getClass().getName(), "created owner status type - " + issueOwnerStatusType.getId().toString());

				Date issuePlannedDate = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_PLANNEDENDDATE)
						.getRawValue();
				
				if ( issuePlannedDate == null ) {
					logger.info(this.getClass().getName(), "created issue date is null");	
				} else {
					logger.info(this.getClass().getName(), "created issue date - " + issuePlannedDate.toString());	
				}
				
//				Date issueEndDateAction = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ENDDATEACTIONPLAN)
//						.getRawValue();
//				
//				if ( issueEndDateAction == null ) {
//					logger.info(this.getClass().getName(), "end action date is null.");	
//				} else {
//					logger.info(this.getClass().getName(), "end action date - " + issueEndDateAction.toString());
//				}

				
				if ( issueActionType == EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ACTIONPLAN && issuePlannedDate != null ) {
					logger.info(this.getClass().getName(), "entered condition for action plan issues.");
					/*
					 * O status do criador ainda está em preparação
					 * ("in_creation") e a data de preparação ultrapassou a data
					 * do sistema.
					 */
					if (actualDate.after(issuePlannedDate)
							&& issueCreatorStatusType == EnumerationsCustom.CENUM_AP_CREATOR_STATUS.NEW ) {

						logger.info(this.getClass().getName(), "entered condition for issue creator.");
						iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PENDING));
						logger.info(this.getClass().getName(), "changed information for creator status.");
						facade.save(iroUpdObj, this.getInternalTransaction(), true);
						logger.info(this.getClass().getName(), "saved status change for issue creator.");
						
						/*
						 * O status do proprietário ainda está em revisão
						 * ("in_progress") e a data de preparação ultrapassou a
						 * data do sistema.
						 */
//					} else if (issueEndDateAction != null) {
//						if ( actualDate.after(issueEndDateAction)
//							 && issueOwnerStatusType == EnumerationsCustom.CENUM_AP_OWNER_STATUS.NEW
//							 && issueCreatorStatusType == EnumerationsCustom.CENUM_AP_CREATOR_STATUS.RELEASED ) {
//							
//							logger.info(this.getClass().getName(), "entered condition for issue owner.");
//							iroUpdObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS)
//									.setRawValue(Collections.singletonList(EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING));
//							logger.info(this.getClass().getName(), "changed information for owner status.");
//							facade.save(iroUpdObj, this.getInternalTransaction(), true);
//							logger.info(this.getClass().getName(), "saved status change for owner creator.");
//							
//							
//						}
//					}
						
					} if ( actualDate.after(issuePlannedDate)
							 && issueOwnerStatusType == EnumerationsCustom.CENUM_AP_OWNER_STATUS.IN_PROGRESS
							 && issueCreatorStatusType == EnumerationsCustom.CENUM_AP_CREATOR_STATUS.IN_PROGRESS ) {
							
							logger.info(this.getClass().getName(), "entered condition for issue owner.");
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_OWNER_STATUS)
									.setRawValue(Collections.singletonList(EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING));
							logger.info(this.getClass().getName(), "changed information for owner status.");
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
							logger.info(this.getClass().getName(), "saved status change for owner creator.");

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