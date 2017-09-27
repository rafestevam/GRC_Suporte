package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomRASaveCommand extends BaseSaveActionCommand {
		
	protected void afterExecute(){
		
		IUIEnvironment currEnv = this.environment;
		IAppObj currAppObj = this.formModel.getAppObj();
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext()); //REO+ 27.09.2017 - EV113345
		
		try{
			//Inicio REO - 27.09.2017 - EV113345
			IUserContext jobCtx = jobEnv.getUserContext();
			IAppObjFacade rskAppFacade = FacadeFactory.getInstance().getAppObjFacade(jobCtx, ObjectType.RISK);
			//IAppObjFacade rskAppFacade = currEnv.getAppObjFacade(ObjectType.RISK);
			//Fim REO - 27.09.2017 - EV113345
			
			String ra_result = currAppObj.getAttribute(IRiskassessmentAttributeTypeCustom.ATTR_RESULT_ASSESSMENT).getRawValue();	
			List<IAppObj> currRskList = currAppObj.getAttribute(IRiskassessmentAttributeType.LIST_RISK).getElements(this.getFullGrantUserContext());
			Iterator<IAppObj> currRskIterator = currRskList.iterator();
			
			while(currRskIterator.hasNext()){
				IAppObj riskAppObj = currRskIterator.next();
				IOVID riskOVID = riskAppObj.getVersionData().getHeadOVID();
				
				rskAppFacade.allocateLock(riskOVID, LockType.FORCEWRITE);
				IAppObj rskUpdAppObj = this.updatePotencialRisk(riskOVID, rskAppFacade, ra_result);
												
				rskAppFacade.save(rskUpdAppObj, this.getDefaultTransaction(), true);
				rskAppFacade.releaseLock(riskOVID);
			}

		}catch(Exception e){
			this.log.error(e, (Throwable)e);
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
		
	}
	
	//Obter Risco Potencial a partir da Avaliação de Risco
	private IAppObj updatePotencialRisk(IOVID riskOVID, IAppObjFacade riskFacade, String resultAssessment) throws RightException{
		Calendar calendarDate = new GregorianCalendar(2050,Calendar.JANUARY,31);
		Date newEndDate = calendarDate.getTime();
		try{
			IAppObj rskUpdAppObj = riskFacade.load(riskOVID, this.getDefaultTransaction(), true);
			rskUpdAppObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).setRawValue(resultAssessment);
			rskUpdAppObj.getAttribute(IRiskAttributeType.ATTR_ENDDATE).setRawValue(newEndDate);	
			
			return rskUpdAppObj;
		}catch(RightException e){
			throw e;
		}
	}
}
