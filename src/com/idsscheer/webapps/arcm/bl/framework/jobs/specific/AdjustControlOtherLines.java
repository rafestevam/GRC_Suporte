package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;

@CanBeScheduled
public class AdjustControlOtherLines extends BaseJob{

	private static final long serialVersionUID = 1L;
	private static final String JOB_NAME_KEY = "enumeration.AdjustControlOtherLines.DBI";

	public AdjustControlOtherLines(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void deallocateResources() {
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getJobNameKey() {
		return JOB_NAME_KEY;
	}

	@Override
	public IEnumerationItem getJobType() {
		return EnumerationsCustom.JOBS.JOBLISTCLEANINGJOB;
	}

}
