package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.command.job.DueCheckCommand;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.support.ConfigParameterPolicy;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

@ConfigParameterPolicy(acceptAllParameters=true)
public class CustomDueCheckCommand extends DueCheckCommand {
	
	@Override
	protected boolean isDue(IAppObj appObj, CommandContext cc) {
		
		boolean hasAssessment = false;
		
		// TODO Auto-generated method stub
		if(cc.getCommandXMLParameter("object_type").equals("riskassessment")){
			if(super.isDue(appObj, cc)){
				IAppObj riskObj = this.getLastRiskVersion(appObj, cc);
				hasAssessment = this.getAssessment4Risk(riskObj, cc);
			}
		}else{
			hasAssessment = super.isDue(appObj, cc);
		}
		
		return hasAssessment;
		
	}

	private IAppObj getLastRiskVersion(IAppObj appObj, CommandContext cc) {
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(cc.getChainContext().getUserContext(), ObjectType.RISK);
		try {
			IOVID riskOVID = appObj.getVersionHistory().get(appObj.getVersionHistory().size() - 1).getOVID();
			IAppObj riskObj = facade.load(riskOVID, true);
			return riskObj;
		} catch (RightException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean getAssessment4Risk(IAppObj riskObj, CommandContext cc){
		
		boolean bReturn = false;
		
		IUserContext jobCtx = getJobContext(cc);
		Calendar riskStartDate = new GregorianCalendar();
		riskStartDate.setTime(riskObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).getRawValue());
		Calendar riskEndDate = new GregorianCalendar();
		riskEndDate.setTime(riskObj.getAttribute(IRiskAttributeType.ATTR_STARTDATE).getRawValue());
		riskEndDate.add(Calendar.YEAR, 1);
		
		try {
			long raID = getRiskAssessment(riskObj, cc, jobCtx);
			
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.RISKASSESSMENT);
			IOVID raOVID = OVIDFactory.getOVID(raID);
			IAppObj raObj = facade.load(raOVID, true);
			if(raObj == null)
				return true;
			//
			IEnumAttribute statusAttr = raObj.getAttribute(IRiskassessmentAttributeType.ATTR_OWNER_STATUS);
			IEnumerationItem status = ARCMCollections.extractSingleEntry(statusAttr.getRawValue(), true);
			
			Calendar raEndDate = new GregorianCalendar();
			raEndDate.setTime(raObj.getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue());
				
			if((raEndDate.after(riskStartDate)) && (raEndDate.before(riskEndDate)))  {
				bReturn = false;
				/*if(!status.getId().equals("new")){
					bReturn = true;
				}else{
					bReturn = false;
				}*/
			}else{
				bReturn = true;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bReturn;
		
	}

	private long getRiskAssessment(IAppObj riskObj, CommandContext cc, IUserContext jobCtx) throws Exception {
		Map filterMap = new HashMap();
		filterMap.put("r_id", riskObj.getObjectId());
		long raID = 0;
		
		IViewQuery query = QueryFactory.createQuery(jobCtx, "ra2risk", filterMap, null,
				true, cc.getChainContext().getTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				raID = (Long)viewObj.getRawValue("ra_id");
				
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return raID;
	}

	private IUserContext getJobContext(CommandContext cc) {
		JobUIEnvironment jobEnv = new JobUIEnvironment(cc.getChainContext().getUserContext());
		IUserContext jobCtx = jobEnv.getUserContext();
		return jobCtx;
	}

}
