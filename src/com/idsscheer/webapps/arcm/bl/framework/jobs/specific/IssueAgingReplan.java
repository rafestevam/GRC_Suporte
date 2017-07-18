package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.idsscheer.utils.date.ADateHelper;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
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

@CanBeScheduled
public class IssueAgingReplan extends BaseJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUGGER_ON = true;
//	public static final String KEY_JOB_NAME = EnumerationsCustom.CUSTOM_JOBS.ISSUE_SLA.getPropertyKey();
	public static final String KEY_JOB_NAME = "EnumerationsCustom.updateIssueAgingReplan";
		
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public IssueAgingReplan(IOVID executingUser, Locale executingLocale) {
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

			try {

				Date actualDate = new Date();
						
				Calendar calAux = Calendar.getInstance();
				calAux.setTime(actualDate);
		
				this.displayLog("actualDate - " + actualDate.toString() );
				
				IAppObj iroUpdObj = facade.load(iroOVID, true);
				this.displayLog("iroUpdObj - " + iroUpdObj.toString());
				
				facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID());
				this.displayLog("facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID())");
				
				IEnumAttribute issueActionTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem issueActionType = ARCMCollections.extractSingleEntry(issueActionTypeList.getRawValue(), true);
				this.displayLog("issueActionType - " + issueActionType.toString() );
				
				IEnumAttribute issueReviewerStatusTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS);

				IEnumerationItem issueReviewerStatusType = ARCMCollections.extractSingleEntry(issueReviewerStatusTypeList.getRawValue(), true);
				this.displayLog("issueReviewrStatusType - " + issueReviewerStatusType.toString() );
	
		
				/*
				 * Apenas os "apontamentos" que são plano de ação.
				 */
				String dif01 = "até 30";
				String dif02 = "31 a 60";
				String dif03 = "61 a 90";
				String dif04 = "91 a 120";
				String dif05 = "121 a 150";
				String dif06 = "Acima 150";		
				
				if ( issueActionType == EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ACTIONPLAN ) {
					
					this.displayLog("Este objeto é um plano de ação." + issueActionType);
					
					if(issueReviewerStatusType == EnumerationsCustom.CENUM_AP_CREATOR_STATUS.IN_PROGRESS ||
					   issueReviewerStatusType == EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.FUP ){
						
						Date datePlanIni = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_PLANDTINI).getRawValue();						
						Date datePlanFin = iroUpdObj.getAttribute(IIssueAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
						
						int difference = ADateHelper.calculateDifference(datePlanFin, datePlanIni );
						
						if(difference <= 30){
							this.displayLog(dif01 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif01);
						}
						if(difference >= 31 && difference <= 60 ){
							this.displayLog(dif02 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif02);				
						}
						if(difference >= 60 && difference <= 90 ){
							this.displayLog(dif03 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif03);				
						}
						if(difference >= 91 && difference <= 120 ){
							this.displayLog(dif04 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif04);	
						}
						
						if(difference >= 121 && difference <= 150 ){
							this.displayLog(dif05 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif05);	
						}
						
						if(difference > 150 ){
							this.displayLog(dif06 + difference);
							iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_CST_AGING_PLAN).setRawValue(dif06);	
						}					
						
					}
					

				} /* fim plano de ação */

				// Libera o lock do objeto 
				facade.releaseLock(iroUpdObj.getVersionData().getHeadOVID());

			} catch (RightException e) {
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