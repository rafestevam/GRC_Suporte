package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar;
import java.util.Date;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.common.command.SendMailCommand;
import com.idsscheer.webapps.arcm.bl.framework.command.ChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.JobMessageContainer;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.LinkedListInformation;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.MessageInformation;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageHandler;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageUtility;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IObjectAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskassessmentAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITransactionalAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.AttributeDataType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.config.smtp.ASMTPConfiguration;
import com.idsscheer.webapps.arcm.services.InfrastructureServiceFactory;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.umc.IUMCFacade;

@CanBeScheduled
public class RADelayMailJob extends BaseJob {
//	public static final String KEY_JOB_NAME = EnumerationsCustom.CUSTOM_JOBS.ISSUE_PENDING.getPropertyKey();
	public static final String KEY_JOB_NAME = "enumeration.RADelayMailJob.DBI";
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public RADelayMailJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {
		//long processedObj = 0;
		
		try{

			List<IAppObj> raDelayList = this.getRAforNote();
			ITransaction transaction = TransactionManager.getInstance().createTransaction();
			
			for(int i = 0; i < raDelayList.size(); i++){
				IAppObj raDelayObj = raDelayList.get(i);
				
				Map<String, Object> param = new HashMap<String, Object>();
				
				param.put("template", "riskowner_notification");
				param.put("to", "owner_group");
								
				ChainContext cc = new ChainContext(raDelayObj, this.jobContext.getUserContext());
				cc.setTransaction(transaction);
				CommandContext commandContext = new CommandContext(cc, (HashMap)param);
				SendMailCommand command = new SendMailCommand();
				command.execute(commandContext);
				
				transaction.commit();
				super.increaseEditedObjectsCounter();
			}
			
			//transaction.commit();
			
		}catch(Exception e){
			
			throw new JobAbortException(e.getMessage(), "error");
			
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
	
	private List<IAppObj> getRAforNote() throws Exception{
		
		List<IAppObj> raListRet = new ArrayList<IAppObj>();
		IAppObjFacade facade = null;
		IAppObjQuery query = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(2017, Calendar.MAY, 12);*/
		
		Date delayDate = this.getDelayData(null);
		
		try{
			
			facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.RISKASSESSMENT);
			query = facade.createQuery();
			
			//query.addRestriction(QueryRestriction.eq(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE, this.getDelayData(calendar.getTime())));
			//query.addRestriction(QueryRestriction.eq(IRiskassessmentAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
			//query.addRestriction(QueryRestriction.ne(IRiskassessmentAttributeType.ATTR_OWNER_STATUS, Enumerations.OWNER_STATUS.EFFECTIVE));
			
			IAppObjIterator itQuery = query.getResultIterator();
			while(itQuery.hasNext()){
				
				IAppObj raAppObj = itQuery.next();
				
				Date expireDate = raAppObj.getAttribute(IRiskassessmentAttributeType.ATTR_PLANNEDENDDATE).getRawValue();
				//if(!expireDate.equals(calendar.getTime()))
				if(!dateFormat.format(expireDate).equals(dateFormat.format(delayDate)))
					continue;
				
				IEnumAttribute ownerStatusAttr = raAppObj.getAttribute(IRiskassessmentAttributeType.ATTR_OWNER_STATUS);
				IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
				if(ownerStatus.getValue().equals("3"))
					continue;
				
				raListRet.add(raAppObj);
				
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return (List<IAppObj>)raListRet;
		
	}
	
	private Date getDelayData(Date date){
		
		Date retDate = new Date();
		Calendar calendar = Calendar.getInstance();
		
		if(date != null){
			retDate = date;
		}else{
			calendar.add(Calendar.DATE, +2);
			retDate = calendar.getTime();
		}
		
		return retDate;
		
	}
	
	private void sendNotificationMail(List<IAppObj> userGroupL, IAppObj triggeringAppObj, ChainContext cc){
		
		String templateID = "riskowner_notification";
		IEnumerationItem initiator = Enumerations.INITIATORS.ENUM.getItemById(templateID);
		
		//MessageHandler messHdl = MessageHandler.getInstance();
		
		if (null == initiator) {
			throw new IllegalArgumentException("message template with id '" + templateID + "' not found");
		}
		
		for(IAppObj userGroup : userGroupL){
		
			List<IAppObj> toUsers = userGroup.getAttribute(IUsergroupAttributeType.LIST_GROUPMEMBERS).getElements(this.userContext);
			for(IAppObj toUser : toUsers){
				
				ITransaction transaction = TransactionManager.getInstance().getReadOnlyTransaction();
				//IMessage message = createMessage(triggeringAppObj, initiator, toUser);
				IMessage message = MessageFactory.createMessage(
						toUser, 
						EnumerationsCustom.CUSTOM_INITIATORS.RISKOWNER_NOTIFICATION, 
						this.getJobContext().getUserContext().getUser(), 
						triggeringAppObj);
				MessageHandler.getInstance().addMessage(transaction, message);
				//cc.send(message);
				
			}
		
		}
		
	}
	
	private void sendNotificationMail(IChainContext cc, IUserContext userCtx, IAppObj raObj){
		
		//IEnumerationItem initiator = EnumerationsCustom.CUSTOM_INITIATORS.RISKOWNER_NOTIFICATION;
		String templateID = "riskowner_notification";
		IEnumerationItem initiator = Enumerations.INITIATORS.ENUM.getItemById(templateID);
		
		Set receiverOVIDsSet = this.readReceivers(raObj, userContext);
		
		String clientSign = raObj.getAttribute(IObjectAttributeType.BASE_ATTR_CLIENT_SIGN).getRawValue()
				.getAttribute(IClientAttributeType.ATTR_SIGN).getRawValue();

		//LinkedListInformation linkedListInformation = getLinkedListInformation(cc);
		LinkedListInformation llInfo = new LinkedListInformation("", "", "", "");

		MessageInformation messageInformation = new MessageInformation(initiator, receiverOVIDsSet, clientSign,
				raObj.getObjectType(), llInfo);
		
		JobMessageContainer messageContainer = (JobMessageContainer) cc.get("MESSAGE_CONTAINER");

		if (messageContainer != null) {
			messageContainer.addMessageInformation(messageInformation);
		}
		
	}
	
	private IMessage createMessage(IAppObj p_triggeringAppObj, IEnumerationItem p_initiator,
			IAppObj toUser) {
		//String fromUser = this.userContext.getExtendedID();
		String fromUser = "SuporteCIP@cip-bancos.org.br";
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
	
	private Set<IOVID> readReceivers(IAppObj obj, IUserContext userContext) {
		Set receiverOVIDsSet = new HashSet();

		for (IAppObj ownerGroup : obj.getAttribute(ITransactionalAttributeType.LIST_OWNER_GROUP)
				.getElements(userContext)) {
			for (IAppObj user : ownerGroup.getAttribute(IUsergroupAttributeType.LIST_GROUPMEMBERS)
					.getElements(userContext)) {
				if (!(user.getVersionData().isDeleted()))
					;
				IOVID userOVID = user.getVersionData().getOVID();
				receiverOVIDsSet.add(userOVID);
			}
		}
		if (!(receiverOVIDsSet.isEmpty())) {
			return receiverOVIDsSet;
		}

		for (IAppObj user : obj.getAttribute(ITransactionalAttributeType.LIST_OWNER).getElements(userContext)) {
			if (!(user.getVersionData().isDeleted()))
				;
			IOVID userOVID = user.getVersionData().getOVID();
			receiverOVIDsSet.add(userOVID);
		}

		return receiverOVIDsSet;
	}
	
}