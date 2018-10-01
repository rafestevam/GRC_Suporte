package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ChainContext;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageUtility;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.AttributeDataType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.config.smtp.ASMTPConfiguration;
import com.idsscheer.webapps.arcm.services.InfrastructureServiceFactory;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.umc.IUMCFacade;

public class RiskassessmentDelayMailJob extends BaseJob {
	
	//private static final long serialVersionUID = 1L;
	//public static final String KEY_JOB_NAME = "enumeration.RiskassessmentDelayMailJob.DBI";
	public static final String KEY_JOB_NAME = "Teste";
	
	public RiskassessmentDelayMailJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}
	
	@Override
	protected void deallocateResources() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() throws JobAbortException, JobWarningException {
		// TODO Auto-generated method stub
		
		try{
		
			List<IAppObj> raDelayList = this.getRAforNote();
			
			ITransaction transaction = TransactionManager.getInstance().getReadOnlyTransaction();
			ChainContext cc = new ChainContext(null, getJobContext().getUserContext());
			cc.setTransaction(transaction);
			
			for(int i = 0; i < raDelayList.size(); i++){
				IAppObj raDelayObj = raDelayList.get(i);
				
				this.sendNotificationMail(
						raDelayObj.getAttribute(IRiskassessmentAttributeType.LIST_OWNER_GROUP).getElements(this.userContext), 
						raDelayObj,
						cc);
			}
			
			transaction.commit();
			
		}catch(Exception e){
			
			throw new JobAbortException(e.getMessage(), "error");
			
		}
		

	}

	@Override
	public String getJobNameKey() {
		// TODO Auto-generated method stub
		return KEY_JOB_NAME;
		//return null;
	}

	@Override
	public IEnumerationItem getJobType() {
		// TODO Auto-generated method stub
		return EnumerationsCustom.JOBS.JOBLISTCLEANINGJOB;
		//return null;
	}
	
	private List<IAppObj> getRAforNote() throws Exception{
		
		List<IAppObj> raListRet = new ArrayList<IAppObj>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2017, Calendar.MAY, 12);
		
		try{
			
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.RISKASSESSMENT);
			IAppObjQuery query = facade.createQuery();
			
			query.addRestriction(QueryRestriction.eq(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE, this.getDelayData(calendar.getTime())));
			query.addRestriction(QueryRestriction.eq(IRiskassessmentAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
			query.addRestriction(QueryRestriction.ne(IRiskassessmentAttributeType.ATTR_OWNER_STATUS, Enumerations.OWNER_STATUS.EFFECTIVE));
			
			IAppObjIterator itQuery = query.getResultIterator();
			while(itQuery.hasNext()){
				
				IAppObj raAppObj = itQuery.next();
				raListRet.add(raAppObj);
				
			}
			
		}catch(Exception e){
			throw e;
		}
		
		return (List<IAppObj>)raListRet;
		
	}
	
	private Date getDelayData(Date date){
		
		Date retDate = new Date();
		Calendar calendar = Calendar.getInstance();
		
		if(date != null){
			retDate = date;
		}else{
			calendar.add(Calendar.DATE, +3);
			retDate = calendar.getTime();
		}
		
		return retDate;
		
	}
	
	private void sendNotificationMail(List<IAppObj> toUsers, IAppObj triggeringAppObj, ChainContext cc){
		
		String templateID = "changereview_created";
		IEnumerationItem initiator = Enumerations.INITIATORS.ENUM.getItemById(templateID);
		if (null == initiator) {
			throw new IllegalArgumentException("message template with id '" + templateID + "' not found");
		}
		
		for(IAppObj toUser : toUsers){
			
			IMessage message = createMessage(triggeringAppObj, initiator, toUser);
			cc.send(message);		
			
		}
		
	}
	
	private IMessage createMessage(IAppObj p_triggeringAppObj, IEnumerationItem p_initiator,
			IAppObj toUser) {
		String fromUser = this.userContext.getExtendedID();
		IUserContext userContext = this.userContext;
		
		MessageUtility mu = MessageUtility.getInstance();
		if (null == fromUser) {
			return MessageFactory.createMessage(toUser, p_initiator, userContext.getUser(), p_triggeringAppObj);
		}
		String email = null;
		if (("whistleblow".equalsIgnoreCase(fromUser)) || ("anonymous".equalsIgnoreCase(fromUser))
				|| ("default".equalsIgnoreCase(fromUser))) {
			IUMCFacade facade = InfrastructureServiceFactory.getUMCFacade();
			email = new ASMTPConfiguration(facade).getSenderAddress();
		} else if (fromUser.contains("@")) {
			email = fromUser;
		} else if ((null != ObjectType.USER.getAttribute(fromUser))
				&& (AttributeDataType.STRING == ObjectType.USER.getAttribute(fromUser).getType())) {
			email = userContext.getUser().getRawValue((IStringAttributeType) ObjectType.USER.getAttribute(fromUser));
		} else {
			email = null;
		}
		if (null != email) {
			return MessageFactory.createMessage(toUser, p_initiator, email, p_triggeringAppObj);
		}

		List users = mu.getUsers(p_triggeringAppObj, fromUser);
		if (users.isEmpty()) {
			return MessageFactory.createMessage(toUser, p_initiator, userContext.getUser(), p_triggeringAppObj);
		}
		return MessageFactory.createMessage(toUser, p_initiator, (IAppObj) users.get(0), p_triggeringAppObj);
	}

}
