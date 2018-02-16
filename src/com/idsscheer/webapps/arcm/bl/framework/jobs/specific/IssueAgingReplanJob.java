package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.idsscheer.utils.date.ADateHelper;
import com.idsscheer.webapps.arcm.bl.exception.ObjectAccessException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectLockException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectNotUniqueException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.ValidationException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class IssueAgingReplanJob extends BaseJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUGGER_ON = true;
//	public static final String KEY_JOB_NAME = EnumerationsCustom.CUSTOM_JOBS.ISSUE_SLA.getPropertyKey();
	public static final String KEY_JOB_NAME = "enumeration.IssueAgingReplanJob.DBI";
		
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public IssueAgingReplanJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {

		this.displayLog("execute()");
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE);
		this.displayLog("IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE)");
		IAppObjQuery query = facade.createQuery();
		this.displayLog("IAppObjQuery query = facade.createQuery()");
		IAppObjIterator it = query.getResultIterator();
		this.displayLog("AppObjIterator it = query.getResultIterator()");
		
		while (it.hasNext()) {

			this.displayLog("it.hasNext()");
			IAppObj appObj = it.next();
			this.displayLog("appObj - " + appObj.toString());			
			IOVID iroOVID = appObj.getVersionData().getHeadOVID();

			this.displayLog("Job Aging... Inicio -> ");
			
			try {	

				Date actualDate = new Date();
						
				Calendar calAux = Calendar.getInstance();
				calAux.setTime(actualDate);
		
				this.displayLog("actualDate - " + actualDate.toString() );
				
				IAppObj iroUpdObj = facade.load(iroOVID, true);
				this.displayLog("iroUpdObj - " + iroUpdObj.toString());
				
				//facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID());
				facade.allocateLock(iroUpdObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
				this.displayLog("facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID())");
				
				IEnumAttribute issueActionTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem issueActionType = ARCMCollections.extractSingleEntry(issueActionTypeList.getRawValue(), true);
				this.displayLog("issueActionType - " + issueActionType.toString() );
				
				IEnumAttribute issueReviewerStatusList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS);
				IEnumerationItem issueReviewerStatus = ARCMCollections.extractSingleEntry(issueReviewerStatusList.getRawValue(), true);
				this.displayLog("Tipo - :" + issueReviewerStatusList.getRawValue());
		
				IEnumAttribute issueOwnerStatusList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_OWNER_STATUS);
				IEnumerationItem issueOwnerStatus = ARCMCollections.extractSingleEntry(issueOwnerStatusList.getRawValue(), true);
				this.displayLog("Tipo - :" + issueReviewerStatusList.getRawValue());
				
				/*
				 * Apenas os "apontamentos" que são plano de ação.
				 */
				String dif01 = "até 30";
				String dif02 = "31 a 60";
				String dif03 = "61 a 90";
				String dif04 = "91 a 120";
				String dif05 = "121 a 150";
				String dif06 = "Acima 150";		
				
				this.displayLog("valor action type = " + issueActionType);
				
				if ( issueActionType == EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ACTIONPLAN ) {
					
					this.displayLog("Este objeto é um plano de ação." + issueActionType);
					
					//if(issueReviewerStatus == EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.ATTENDED ||
					//   issueReviewerStatus == EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.FUP ){
						if(issueReviewerStatus == EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.FUP ){
						this.displayLog("Inicio -----------------------");
						Date datePlanIni = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PLANDTINI).getRawValue();						
						Date datePlanFin = iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
						
						this.displayLog("Inicio = " + datePlanIni);
						this.displayLog("Fim = " + datePlanFin);
						 
						int difference =  ADateHelper.calculateDifference(datePlanFin, datePlanIni );
						
						if(difference <= 30){
							this.displayLog(dif01 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif01);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						if(difference >= 31 && difference <= 60 ){
							this.displayLog(dif02 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif02);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						if(difference >= 60 && difference <= 90 ){
							this.displayLog(dif03 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif03);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						if(difference >= 91 && difference <= 120 ){
							this.displayLog(dif04 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif04);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						
						if(difference >= 121 && difference <= 150 ){
							this.displayLog(dif05 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif05);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						
						if(difference > 150 ){
							this.displayLog(dif06 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif06);
							facade.save(iroUpdObj, this.getInternalTransaction(), true);
						}
						
						 
					}

				//	else {
					
						//if(issueReviewerStatus == EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING){
						if(issueOwnerStatus == EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING){
									this.displayLog("Inicio Pendente -----------------------");
									Date datePlanIni = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PLANDTINI).getRawValue();						
									Date datePlanFin = iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
									
									this.displayLog("Inicio = " + datePlanIni);
									this.displayLog("Fim = " + datePlanFin);
									this.displayLog("Data atual = " + actualDate );
									 
																		
									if(datePlanFin.before(actualDate)){										
									
									int difference =  ADateHelper.calculateDifference(actualDate, datePlanIni );
									
																
									if(difference <= 30){
										this.displayLog(dif01 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif01);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
									if(difference >= 31 && difference <= 60 ){
										this.displayLog(dif02 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif02);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
									if(difference >= 60 && difference <= 90 ){
										this.displayLog(dif03 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif03);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
									if(difference >= 91 && difference <= 120 ){
										this.displayLog(dif04 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif04);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
									
									if(difference >= 121 && difference <= 150 ){
										this.displayLog(dif05 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif05);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
									
									if(difference > 150 ){
										this.displayLog(dif06 + difference);
										iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PEND).setRawValue(dif06);
										facade.save(iroUpdObj, this.getInternalTransaction(), true);
									}
								}
						}
						
						
					
					
					

				} /* fim plano de ação */

				// Libera o lock do objeto 
		
				facade.releaseLock(iroUpdObj.getVersionData().getHeadOVID());
				
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
	
	private void displayLog(String message) {
		if (DEBUGGER_ON) {
			logger.info(this.getClass().getName(), message);
		}
	}
}