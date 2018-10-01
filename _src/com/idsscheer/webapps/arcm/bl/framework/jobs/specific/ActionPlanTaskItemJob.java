package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
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
import com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands.CustomTaskItemActionPlan;
import com.sun.javafx.scene.control.skin.EmbeddedTextContextMenuContent;

@CanBeScheduled
public class ActionPlanTaskItemJob extends BaseJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUGGER_ON = true;
	public static final String KEY_JOB_NAME = "enumeration.ActionPlanTaskItemJob.DBI";
	
	private CustomTaskItemActionPlan taskItemActionPlanEngine;
		
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public ActionPlanTaskItemJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {

		IAppObjFacade actionPlanFacade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE);
		IAppObjQuery queryActionPlan = actionPlanFacade.createQuery();
		
		queryActionPlan.addRestriction(
				QueryRestriction.eq(
					IIssueAttributeTypeCustom.ATTR_ACTIONTYPE, 
					EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ACTIONPLAN));
		
		Iterator<IAppObj> iteratorActionPlan = 
				queryActionPlan.getResultIterator();
		
		while (iteratorActionPlan.hasNext()) {

			IAppObj actionPlanObj = iteratorActionPlan.next();
			
			// 13.03.2018 - FCT - EV131854
			taskItemActionPlanEngine = 
					new CustomTaskItemActionPlan(
							actionPlanObj, this.userContext, this.getInternalTransaction(), this.userContext.getUser());
			taskItemActionPlanEngine.createActionPlanTaskItem();
				
		}
		
		queryActionPlan.release();
	
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