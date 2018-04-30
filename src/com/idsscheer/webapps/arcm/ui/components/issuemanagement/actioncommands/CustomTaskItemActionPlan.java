package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.ObjectAccessException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectLockException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectNotUniqueException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUserAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.ValidationException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations.TASKITEM_STATUS;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITaskitemAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUserAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ILongAttributeType;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

public class CustomTaskItemActionPlan {

	private IAppObj actionPlanObj;
	private IAppObj taskItemObj;
	private IAppObjFacade taskItemFacade;
	private IAppObjFacade actionPlanFacade;
	private IUserAppObj userAppObj;

	private IUserContext context;
	private ITransaction transaction;

	public CustomTaskItemActionPlan(IAppObj actionPlanObj, IUserContext context, ITransaction transaction, IUserAppObj userAppObj) {
		this.actionPlanObj = actionPlanObj;
		this.context = context;
		this.transaction = transaction;
		this.taskItemObj = null;
		this.userAppObj = userAppObj;
		
		taskItemFacade = FacadeFactory.getInstance().getAppObjFacade(context, ObjectType.TASKITEM);
		actionPlanFacade = FacadeFactory.getInstance().getAppObjFacade(context, ObjectType.ISSUE);
	}

	// 13.03.2018 - FCT - EV131854
	public void createActionPlanTaskItem() {

		// Verifica se há TASKITEM para este action plan.
		IAppObjQuery taskItemQuery = taskItemFacade.createQuery();
		taskItemQuery.addRestriction(QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_ID,
				actionPlanObj.getRawValue(IIssueAttributeType.BASE_ATTR_OBJ_ID)));

		IAppObjIterator taskItemIterator = taskItemQuery.getResultIterator();

		// Se houver então modificar este TASKITEM.
		if (taskItemIterator.getSize() > 0) {
			while (taskItemIterator.hasNext()) {
				IAppObj taskItemObjAux = taskItemIterator.next();
				IOVID taskItemOVID = taskItemObjAux.getVersionData().getHeadOVID();
				
				try {
					taskItemObj = taskItemFacade.load(taskItemOVID, true);
					taskItemFacade.allocateLock(taskItemObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
					assignAttributesTaskItem(actionPlanObj.getObjectType().getId());
					
				} catch (RightException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				break;
			}
			// Caso contrário, então será necessário criar um novo objeto
			// TASKITEM.
		} else {

			// Método escreve os campos correspondentes.
			this.assignAttributesTaskItem(actionPlanObj.getObjectType().getId());

		}

		try {
			// Salvar o novo objeto de TASKITEM.
			taskItemFacade.save(taskItemObj, transaction, true);
			taskItemFacade.releaseLock(taskItemObj.getVersionData().getHeadOVID());
			
		} catch (ObjectLockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RightException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectNotUniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		taskItemQuery.release();

	}
	
	
	public void createActionPlanTaskItem(ILongAttributeType baseAttrObj) {

		// Verifica se há TASKITEM para este action plan.
		IAppObjQuery taskItemQuery = taskItemFacade.createQuery();
		taskItemQuery.addRestriction(QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_ID,
				actionPlanObj.getRawValue(baseAttrObj)));

		IAppObjIterator taskItemIterator = taskItemQuery.getResultIterator();

		// Se houver então modificar este TASKITEM.
		if (taskItemIterator.getSize() > 0) {
			while (taskItemIterator.hasNext()) {
				IAppObj taskItemObjAux = taskItemIterator.next();
				IOVID taskItemOVID = taskItemObjAux.getVersionData().getHeadOVID();
				
				try {
					taskItemObj = taskItemFacade.load(taskItemOVID, true);
					taskItemFacade.allocateLock(taskItemObj.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
					assignAttributesTaskItem(actionPlanObj.getObjectType().getId());
					
				} catch (RightException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				break;
			}
			// Caso contrário, então será necessário criar um novo objeto
			// TASKITEM.
		} else {

			// Método escreve os campos correspondentes.
			this.assignAttributesTaskItem(actionPlanObj.getObjectType().getId());

		}

		try {
			// Salvar o novo objeto de TASKITEM.
			taskItemFacade.save(taskItemObj, transaction, true);
			taskItemFacade.releaseLock(taskItemObj.getVersionData().getHeadOVID());
			
		} catch (ObjectLockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RightException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectNotUniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		taskItemQuery.release();

	}
	

	public void  assignAttributesTaskItem(String objectType) {

	
		try {

			if (taskItemObj == null) {
				taskItemObj = taskItemFacade.create();
			}

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_CLIENTSIGNS).setRawValue(",CIP,");

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_NAME)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.ATTR_NAME));

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_PLANNEDSTARTDATE)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.ATTR_PLANNEDSTARTDATE));
			
			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_PLANNEDENDDATE)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.ATTR_PLANNEDENDDATE));

//			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE).setRawValue("ISSUE");
			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE).setRawValue(objectType);

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_OVID)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.ATTR_OBJ_ID).toString() + ":"
							+ actionPlanObj.getRawValue(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER).toString());

			taskItemObj.getAttribute(ITaskitemAttributeType.BASE_ATTR_CREATOR_USER_ID)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.BASE_ATTR_CREATOR_USER_ID));

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_VERSION_NUMBER)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER));

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_ID)
					.setRawValue(actionPlanObj.getRawValue(IIssueAttributeType.ATTR_OBJ_ID));

			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_CLIENTSIGN).setRawValue("CIP");
			
			if(objectType.equals("ISSUE"))
				taskItemObj = this.modifyWorkflowTaskItem();
			
			if(objectType.equals("CONTROLEXECUTION"))
				taskItemObj = this.modifyCETaskItem();


		} catch (RightException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private IAppObj modifyCETaskItem() {
		
		IAppObjFacade userFacade = FacadeFactory.getInstance().getAppObjFacade(context, ObjectType.USER);
		
		IEnumerationItem ownerStatus = 
				ARCMCollections.extractSingleEntry(
						actionPlanObj.getAttribute(
								IControlexecutionAttributeType.ATTR_OWNER_STATUS).getRawValue(), true);
		
		Long userOwnerID = new Long(0);
		Long userReviewerID = new Long(0);
		Long userCreatorID = 
				this.actionPlanObj.
					getRawValue(IControlexecutionAttributeType.BASE_ATTR_CREATOR_USER_ID);
		
		Iterator userOwnerIterator = 
				this.actionPlanObj.getAttribute(IControlexecutionAttributeType.LIST_OWNER).
					getElementIds().iterator();
		
		// Pega o primeiro owner
		while (userOwnerIterator.hasNext()) {
			IOVID userOVID = (IOVID) userOwnerIterator.next();
			userOwnerID = userOVID.getId();
			break;
		}
		
		// Status de owner é Concluído, então finaliza
		if (StringUtils.contains("3", ownerStatus.getValue().toString())) {
			taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(-1);
			
			String workflowSt = "owner_status:" + TASKITEM_STATUS.COMPLETED.getId();
			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECTWORKFLOWSTATUS).setRawValue(workflowSt);
			taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_STATUS).setRawValue(Collections.singletonList(TASKITEM_STATUS.COMPLETED));
		}
		
		taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_LASTEDITOR)
				.setRawValue(userAppObj.getRawValue(IUserAttributeType.ATTR_NAME));

		return taskItemObj;
		
	}

	public IAppObj modifyWorkflowTaskItem() {
		
		IAppObjFacade userFacade = FacadeFactory.getInstance().getAppObjFacade(context, ObjectType.USER);

		IEnumerationItem creatorAPStatus = 
				ARCMCollections.extractSingleEntry(
						actionPlanObj.getAttribute(
								IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS).getRawValue(), true);
		
		IEnumerationItem ownerAPStatus = 
				ARCMCollections.extractSingleEntry(
						actionPlanObj.getAttribute(
								IIssueAttributeTypeCustom.ATTR_AP_OWNER_STATUS).getRawValue(), true);
		
		IEnumerationItem reviewerAPStatus = 
				ARCMCollections.extractSingleEntry(
						actionPlanObj.getAttribute(
								IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).getRawValue(), true);
		
		Long userOwnerID = new Long(0);
		Long userReviewerID = new Long(0);
		Long userCreatorID = 
				this.actionPlanObj.
					getRawValue(IIssueAttributeType.BASE_ATTR_CREATOR_USER_ID);
		
		Iterator userOwnerIterator = 
				this.actionPlanObj.getAttribute(IIssueAttributeType.LIST_OWNERS).
					getElementIds().iterator();
		
		Iterator userReviewerIterator = 
				this.actionPlanObj.getAttribute(IIssueAttributeType.LIST_REVIEWERS).
					getElementIds().iterator();
		
		// Pega o primeiro owner do action plan. 
		while (userOwnerIterator.hasNext()) {
			IOVID userOVID = (IOVID) userOwnerIterator.next();
			userOwnerID = userOVID.getId();
			break;
		}
		
		// Pega o primeiro reviewer do action plan.
		while (userReviewerIterator.hasNext()) {
			IOVID userOVID = (IOVID) userReviewerIterator.next();
			userReviewerID = userOVID.getId();
			break;
		}
		
		// Status de revisor Baixado, Atendido, Risco Assumido = Encerrado - Remove o usuário do TASKITEM.
		if (StringUtils.contains("456", reviewerAPStatus.getValue().toString())) {
			taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(-1);
		} 
		// Status de reprovado volta para o owner.
		else if (StringUtils.contains("3", reviewerAPStatus.getValue().toString())) {
			taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(userOwnerID);
		}
		// Status de owner concluído com risco assumido vai pro reviewer.
		else if (StringUtils.contains("34", ownerAPStatus.getValue().toString())) {
			taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(userReviewerID);
		}
		// Status de owner em andamento ou pendente mantem no owner.
		else if (StringUtils.contains("12", ownerAPStatus.getValue().toString())) {
			taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(userOwnerID);	
		}
		// Status de creator Please_Select, New, Pending mantem no creator.
		else if (StringUtils.contains("012", creatorAPStatus.getValue().toString())) {
			 taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(userCreatorID);
		}
		// Status de creator Concluído, Risco Assumido.
		else if (StringUtils.contains("45", creatorAPStatus.getValue().toString())) {
			 taskItemObj.getAttribute(
					 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID).setRawValue(userOwnerID);
		}
		
//		taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_TASKCONFIGURATIONID).setRawValue("");

//		taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_OBJECTWORKFLOWSTATUS).setRawValue("");
		
		// Leitura do nome do usuário.
		
		taskItemObj.getAttribute(ITaskitemAttributeType.ATTR_LASTEDITOR)
				.setRawValue(userAppObj.getRawValue(IUserAttributeType.ATTR_NAME));
		
//		IAppObjQuery userQuery = userFacade.createQuery();
//		userQuery.addRestriction(QueryRestriction.eq(IUserAttributeType.ATTR_OBJ_ID,
//				taskItemObj.getRawValue(
//						 ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID)));
//
//		IAppObjIterator userIterator = userQuery.getResultIterator();
//
//		while (userIterator.hasNext()) {
//			
//			break;
//		}
//
//		userQuery.release();

		return taskItemObj;
	}

}
