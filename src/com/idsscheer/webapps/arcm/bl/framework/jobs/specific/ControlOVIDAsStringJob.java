package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.ILockService;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.ILockObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class ControlOVIDAsStringJob extends BaseJob {

	private static final long serialVersionUID = 1L;
	public static final String JOB_NAME_KEY = "enumeration.jobs.ControlOVIDAsStringJob.DBI";
	private static final Logger log = Logger.getLogger(ControlOVIDAsStringJob.class);

	public ControlOVIDAsStringJob(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void deallocateResources() {
		deallocateLocalResources();		
	}

	private void deallocateLocalResources() {
		ILockService lockService = ARCMServiceProvider.getInstance().getLockService();
		try {
			for (ILockObject lock : lockService.findLocks()) {
				log.info(lock.getLockObjectId());
				lockService.releaseLock(lock.getObjectType(), lock.getLockUserId(), null);
			}
		} catch (LockServiceException e) {
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
		}
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		
		try{
			Properties connProps = new Properties();
			connProps.put("user", "arcm");
			connProps.put("password", "pwd!!!123456789");
			Connection conn = DriverManager.getConnection("jdbc:sqlserver://CIP109DNV:1292;DataBaseName=arcm", connProps);
			String updateControlSQL = "UPDATE A_CONTROL_TBL SET CUSTOM_OVID = ? WHERE OBJ_ID = ? AND VERSION_NUMBER = ?";
			
			IAppObjFacade controlFacade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROL);
			IAppObjQuery query = controlFacade.createQuery();
			query.setHeadRevisionsOnly(false);
			query.setIncludeDeletedObjects(false);
			IAppObjIterator iterator = query.getResultIterator();
			setBaseObjectsToProcessCount(iterator.getSize());
			while(iterator.hasNext()){
				
				IAppObj control = iterator.next();
				//controlFacade.allocateLock(control.getVersionData().getOVID(), LockType.FORCEWRITE);
				
				log.info("Definindo OVID para Controle: " + control.getVersionData().getOVID().getAsString()); 
				PreparedStatement updStatement = conn.prepareStatement(updateControlSQL);
				updStatement.setString(1, control.getVersionData().getOVID().getAsString());
				updStatement.setLong(2, control.getObjectId());
				updStatement.setLong(3, control.getVersionData().getOVID().getVersion());
				//control.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_OVID).setRawValue(control.getVersionData().getOVID().getAsString());
				log.info("OVID Determinado: " + control.getVersionData().getOVID().getAsString());
				
				//controlFacade.save(control, getInternalTransaction(), true);
				updStatement.executeUpdate();
				log.info("Salvou Controle");
				
				log.info("-------------------------------------------------");
				
				increaseEditedObjectsCounter(1);
				increaseProgress();
			}
		}catch(Exception e){
			deallocateLocalResources();
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
			throw new JobAbortException(e.getLocalizedMessage(), JOB_NAME_KEY);
		}
		
		
	}

	@Override
	public String getJobNameKey() {
		return JOB_NAME_KEY;
	}

	@Override
	public IEnumerationItem getJobType() {
		return EnumerationsCustom.CUSTOM_JOBS.CONTROL_OVID;
	}

}
