package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.LockInfoFlag;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.custom.procrisk.DefLineEnum;
import com.idsscheer.webapps.arcm.custom.procrisk.RiskAndControlCalculation;
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.ILockService;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.ILockObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

@CanBeScheduled
public class AdjustRisks extends BaseJob {

	private static final long serialVersionUID = 1L;
	private static final String JOB_NAME_KEY = "enumeration.jobs.AdjustRisks.DBI";
	
	public AdjustRisks(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void deallocateResources() {
		// TODO Auto-generated method stub
		deallocateLocalSources();
	}

	private void deallocateLocalSources() {
		ILockService lockService = ARCMServiceProvider.getInstance().getLockService();
		try {
			for (ILockObject lock : lockService.findLocks()) {
				lockService.releaseLock(lock.getLockObjectId(), lock.getLockUserId(), LockInfoFlag.CLEAN, lock.getRemoteClientID());
				//lockService.releaseLock(lock.getObjectType(), lock.getLockUserId(), null);
			}
		} catch (LockServiceException e) {
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
		}
	}

	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		// TODO Auto-generated method stub
		
		deallocateLocalSources();
		
		String riskResidualFinal = "";
		String riskResidual1Line = "";
		String riskResidual2Line = "";
		String riskResidual3Line = "";
		
		double countTotal1 = 0;
		double countTotal2 = 0;
		double countTotal3 = 0;
		double count1line = 0;
		double count2line = 0;
		double count3line = 0;
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.RISK);
		IAppObjQuery query = facade.createQuery();
		
		try{
		
		//IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.RISK);
		//IAppObjQuery query = facade.createQuery();
		query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
		query.setHeadRevisionsOnly(true);
		
		IAppObjIterator iterator = query.getResultIterator();
		setBaseObjectsToProcessCount(iterator.getSize());
		while(iterator.hasNext()){
			
			IAppObj riskObj = iterator.next();
			
			facade.allocateLock(riskObj.getVersionData().getOVID(), LockType.FORCEWRITE);
			
			List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userContext);
			String potencial = riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
			
			RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList, FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.CONTROL), getInternalTransaction());
			
			String riskClass1line = (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_1);
			String riskClass2line = (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_2);
			String riskClass3line = (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_3);
			String riskClassFinal = (String)this.getMapValues(objCalc, "classification", DefLineEnum.LINE_F);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).setRawValue(riskClass1line);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).setRawValue(riskClass2line);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).setRawValue(riskClass3line);
			
//			String riskClassFinalVal = riskClassFinal.equals("") ? riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue() : riskClassFinal;

			String riskClassFinalVal = riskClassFinal.equals("") ? "Não Avaliado" : riskClassFinal;
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinalVal);
			
			count1line = (Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_1);
			count2line = (Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_2);
			count3line = (Double)this.getMapValues(objCalc, "ineffective", DefLineEnum.LINE_3);
			countTotal1 = (Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_1);
			countTotal2 = (Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_2);
			countTotal3 = (Double)this.getMapValues(objCalc, "total", DefLineEnum.LINE_3);

			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF1LINE).setRawValue(count1line);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL1LINE).setRawValue(countTotal1);
			
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF2LINE).setRawValue(count2line);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL1LINE).setRawValue(countTotal2);
			
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF3LINE).setRawValue(count3line);
			riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL3LINE).setRawValue(countTotal3);
			
			IStringAttribute riscoPotencialAttr = riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT);
			String riscoPotencial = riscoPotencialAttr.isEmpty() ? "" : riscoPotencialAttr.getRawValue();
			if(!riscoPotencial.equals("Nao Avaliado")){
				riskResidual1Line = this.riskResidualFinal(riscoPotencial, riskClass1line);
				riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL1LINE).setRawValue(riskResidual1Line);
				
				riskResidual2Line = this.riskResidualFinal(riscoPotencial, riskClass2line);
				riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue(riskResidual2Line);
				
				riskResidual3Line = this.riskResidualFinal(riscoPotencial, riskClass3line);
				riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue(riskResidual3Line); 
				
				riskResidualFinal = this.riskResidualFinal(riscoPotencial, riskClassFinal);
				if(riskResidualFinal.equals("Não Avaliado"))
					riskResidualFinal = potencial;
				riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(riskResidualFinal);
			}
			
			facade.save(riskObj, getInternalTransaction(), true);
			increaseEditedObjectsCounter(1);
			
			facade.releaseLock(riskObj.getVersionData().getOVID());
			increaseProgress();
			
		}
		
		}catch(Exception e){
			deallocateLocalSources();
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
			throw new JobAbortException(e.getLocalizedMessage(), JOB_NAME_KEY);
		}finally{
			query.release();
			deallocateLocalSources();
		}
		
		
	}

	private String riskResidualFinal(String riskPotencial, String riskControlFinal) {
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Médio";
		
		if(riskPotencial.equals("Médio") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Médio"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		if(riskControlFinal.equals("") || riskControlFinal.equals("Não Avaliado"))
			riskResidualReturn = "Não Avaliado";
		
		return riskResidualReturn;
	}

	private Object getMapValues(RiskAndControlCalculation objCalc, String valueType, DefLineEnum defLine) throws Exception {
		Object objReturn = null;
		Map<String, String> mapReturn = objCalc.calculateControlRate(defLine);
		
		Iterator<Entry<String, String>> iterator = mapReturn.entrySet().iterator();
		while(iterator.hasNext()){
			
			Entry<String, String> entry = iterator.next();
			
			if(entry.getKey().equals("classification") && valueType.equals("classification"))
				objReturn = (String)entry.getValue();
			
			if(entry.getKey().equals("rate") && valueType.equals("rate"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("total") && valueType.equals("total"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
			if(entry.getKey().equals("ineffective") && valueType.equals("ineffective"))
				objReturn = (Double)Double.valueOf(entry.getValue());
			
		}
		return objReturn;
	}

	@Override
	public String getJobNameKey() {
		// TODO Auto-generated method stub
		return JOB_NAME_KEY;
	}

	@Override
	public IEnumerationItem getJobType() {
		// TODO Auto-generated method stub
		return EnumerationsCustom.CUSTOM_JOBS.CONTROL_RSK;
	}

}
