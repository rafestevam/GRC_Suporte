package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aris.arcm.transfer.parser.dynamic.WorkflowHelper;
import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssuerelevantobjectAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;

public class CustomUpdateIssueStatus implements ICommand {

	static Logger log = new Logger();
	private String viewName = "customIssue2Object";
	private String filterColumn = "iro_id";
	private String column_creator_status = "creatorStatus";
	private String column_owner_status = "ownerStatus";
	private String column_reviewer_status = "reviewerStatus";
	private String column_obj_id = "is_id";
	private int count_is_open = 0;
	private int count_is_progress = 0;
	private int count_is_fup = 0;
	
	public CustomUpdateIssueStatus() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		// TODO Auto-generated method stub
//		return null;
		
/*		//Variáveis de contagem para mudança de status do Apontamento
		int ctOpen = 0;
		int ctGoing = 0;
		int ctFup = 0;
		int ctAttended = 0;
		int ctRiskAssumed = 0;
		int ctSettled = 0;*/
		
		IEnumerationItem creatorStatus = null;
		IEnumerationItem ownerStatus = null;
		IEnumerationItem reviewerStatus = null;
		
		boolean lock = true;
		IAppObj iroUpdApp = null;
		IAppObjFacade iroFacade = null;
		
		Map filterMap = new HashMap();
		
		//Instancia do Objeto de Plano de Ação Corrente
		IAppObj issueAppObj = cc.getChainContext().getTriggeringAppObj();
		IUserContext userCtx = cc.getChainContext().getUserContext();
//		IUIEnvironment currEnv = 
		
		IEnumAttribute issueTypeAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueTypeItem = ARCMCollections.extractSingleEntry(issueTypeAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "issueTypeItem: " + issueTypeItem.getId());
		log.info(this.getClass().getName(), "condition: " + String.valueOf(!issueTypeItem.getId().equals("actionplan")));
		if(!issueTypeItem.getId().equals("actionplan")){
			log.info(this.getClass().getName(), "Issue não é Plano de Ação");
			return CommandResult.OK;
		}		
		
		//Atributo - Status do Criador do Plano de Ação
		IEnumAttribute creatorStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS);
		if(!(null == creatorStatusAttr))
			creatorStatus = ARCMCollections.extractSingleEntry(creatorStatusAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "Status Criador AP: " + creatorStatus.getId());
		
		//Atributo - Status do Proprietário do Plano de Ação
		IEnumAttribute ownerStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_OWNER_STATUS);
		if(!(null == ownerStatusAttr))
			ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "Status Proprietario AP: " + ownerStatus.getId());
		
		//Atributo - Status do Revisor do Plano de Ação
		IEnumAttribute reviewerStatusAttr = issueAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS);
		if(!(null == reviewerStatusAttr))
			reviewerStatus = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "Status Revisor AP: " + reviewerStatus.getId());
		
		if(creatorStatus == null){
			log.info(this.getClass().getName(), "Creator Status: " + creatorStatus.getId());
			return CommandResult.OK;
		}
		
		if(ownerStatus == null){
			log.info(this.getClass().getName(), "Owner Status: " + ownerStatus.getId());
			return CommandResult.OK;
		}
			
		if(reviewerStatus == null){
			log.info(this.getClass().getName(), "Reviewer Status: " + reviewerStatus.getId());
			return CommandResult.OK;
		}
		
		//this.getWorkflowStatus(issueAppObj);
		
		
		List<IAppObj> iroList = issueAppObj.getAttribute(IIssueAttributeType.LIST_ISSUERELEVANTOBJECTS).getElements(userCtx);
		log.info(this.getClass().getName(), "iroList length: " + String.valueOf(iroList.size()));
		if(iroList == null || iroList.isEmpty()){
			log.info(this.getClass().getName(), "Lista iroList vazia");
			return CommandResult.OK;
		}
		
		for(IAppObj iroAppObj : iroList){
			log.info(this.getClass().getName(), "iroAppObj ID: " + String.valueOf(iroAppObj.getObjectId()));
			log.info(this.getClass().getName(), "iroAppObj OBJ Type: " + iroAppObj.getObjectType().getId());
			log.info(this.getClass().getName(), "Objeto é ISSUE? " + String.valueOf(iroAppObj.getObjectType().getId().equals(ObjectType.ISSUE.getId())));
			
			if(!iroAppObj.getObjectType().getId().equals(ObjectType.ISSUE.getId()))
				continue;
			
			log.info(this.getClass().getName(), "Objeto é Head Version? " + String.valueOf(iroAppObj.getVersionData().isHeadRevision()));
			if(iroAppObj.getVersionData().isHeadRevision()){
				iroUpdApp = iroAppObj;
				//lock = cc.getChainContext().allocateWriteLock(iroUpdApp);
				log.info(this.getClass().getName(), "IRO Head Version");
				log.info(this.getClass().getName(), "iroUpdApp: " + iroUpdApp.toString());
				log.info(this.getClass().getName(), "lock: " + String.valueOf(lock));
			}else{
				IOVID iroOVID = iroAppObj.getVersionData().getHeadOVID();
				log.info(this.getClass().getName(), "OVID: " + iroOVID.toString());
				iroUpdApp = cc.getChainContext().load(iroOVID, ObjectType.ISSUE, true);
				lock = cc.getChainContext().allocateWriteLock(iroUpdApp);
				log.info(this.getClass().getName(), "iroUpdApp: " + iroUpdApp.toString());
				log.info(this.getClass().getName(), "lock: " + String.valueOf(lock));				
			}
				
			if(lock){
				iroFacade = FacadeFactory.getInstance().getAppObjFacade(cc.getChainContext().getUserContext(), iroUpdApp.getObjectType());
				iroFacade.allocateWriteLock(iroUpdApp.getVersionData().getOVID());
				IEnumAttribute iroTypeAttr = iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem iroTypeItem = ARCMCollections.extractSingleEntry(iroTypeAttr.getRawValue(), true);
				if(!iroTypeItem.getId().equals("issue"))
					continue;
				
				if(!this.getWorkflowStatus(iroUpdApp).equalsIgnoreCase("openForExecution"))
					continue;
				
				long iro_id = iroUpdApp.getAttribute(IIssuerelevantobjectAttributeType.ATTR_OBJ_ID).getRawValue();
				filterMap.put(this.filterColumn, iro_id);
				
				this.getIssuesFromIRO(cc);
				this.getIssuesFromIRO(cc, this.viewName, filterMap);
				
				if( this.count_is_open > 0 ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN)
					);
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
				}else{
					
					if( this.count_is_progress > 0 ){
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
						);
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
						);
						log.info(this.getClass().getName(), "Status do Apontamento Em Andamento");
					
					}else{
						
						if( this.count_is_fup > 0 ){
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.FUP)
							);
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.FUP)
							);
						}						
						
					}
					
				}
				
				iroFacade.save(iroUpdApp, cc.getChainContext().getTransaction(), true);
				log.info(this.getClass().getName(), "Apontamento Salvo");
				
				iroFacade.releaseLock(iroUpdApp.getVersionData().getOVID());
				cc.getChainContext().releaseWriteLock(iroUpdApp);
				log.info(this.getClass().getName(), "Lock Apontamento Liberado");
				
				
/*				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM ELABORAÇÃO" ou "PENDENTE"
				 * Então Apontamento relacionado é "EM ABERTO"
				 
				if( creatorStatus.getId().equals("new") || creatorStatus.getId().equals("pending") ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN)
					);
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
					);
					log.info(this.getClass().getName(), "Status do Apontamento Em Aberto");
					//IWorkflowTransition wfProcessor = WorkflowProcessor.getInstance().update(iroUpdApp, cc.getChainContext().getUserContext(), "openForExecution", cc.getChainContext().getTransaction());
					//wfProcessor.
					//wfProcessor.perform();
					//log.info(this.getClass().getName(), "Status WorkFlow: " + wfProcessor.getStatus().name());
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM ELABORAÇÃO" ou "PENDENTE" e
				 * Se Status do Proprietário do Plano de Ação é:
				 * "PENDENTE"
				 * Então Apontamento relacionado é "EM ABERTO"
				 
				if( creatorStatus.getId().equals("new") || creatorStatus.getId().equals("pending") ){
					if(ownerStatus.getId().equals("pending")){
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN)
						);
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
						);
						log.info(this.getClass().getName(), "Status do Apontamento Em Aberto");
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM ANDAMENTO" ou "EM FUP" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "EM ANDAMENTO"
				 
				if( creatorStatus.getId().equals("in_progress") || creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
					);
					log.info(this.getClass().getName(), "Status do Apontamento Em Andamento");
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM ANDAMENTO" ou "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "EM ANDAMENTO" ou "EM FUP" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "EM ANDAMENTO"
				 
				if( creatorStatus.getId().equals("in_progress") || creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("in_progress") || ownerStatus.getId().equals("fup") || ownerStatus.getId().equals("risk_assumed") ){
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
						);
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
						);
						log.info(this.getClass().getName(), "Status do Apontamento Em Andamento");
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM ANDAMENTO" ou "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "EM ANDAMENTO" ou "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Revisor do Plano de Ação é:
				 * "EM FUP" ou "BAIXADO" ou "ATENDIDO" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "EM ANDAMENTO"
				 			
				if( creatorStatus.getId().equals("in_progress") || creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("in_progress") || ownerStatus.getId().equals("fup") || ownerStatus.getId().equals("risk_assumed") ){
						if( reviewerStatus.getId().equals("fup") || reviewerStatus.getId().equals("settled") || reviewerStatus.getId().equals("attended") || reviewerStatus.getId().equals("risk_assumed") ){
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
							);
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.IN_PROGRESS)
							);
							log.info(this.getClass().getName(), "Status do Apontamento Em Andamento");
						}
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM FUP" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "EM FUP"
				 
				if( creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.FUP)
					);
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.FUP)
					);
					log.info(this.getClass().getName(), "Status do Apontamento FUP");
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "EM FUP" ou "RISCO ASSUMIDO"
				 *  Então Apontamento relacionado é "EM FUP"
				 
				if( creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("fup") || ownerStatus.getId().equals("risk_assumed") ){
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.FUP)
						);
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.FUP)
						);
						log.info(this.getClass().getName(), "Status do Apontamento FUP");
					}
				}
				 
				
				 * Se Status do Criador do Plano de Ação é:
				 * "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "EM FUP" ou "RISCO ASSUMIDO"
				 * e Status do Revisor do Plano de Ação é:
				 * "EM FUP" ou "BAIXADO" ou "ATENDIDO" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "EM FUP"
				 			
				if( creatorStatus.getId().equals("fup") || creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("fup") || ownerStatus.getId().equals("risk_assumed") ){
						if( reviewerStatus.getId().equals("fup") || reviewerStatus.getId().equals("settled") || reviewerStatus.getId().equals("attended") || reviewerStatus.getId().equals("risk_assumed") ){
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.FUP)
							);
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.FUP)
							);
							log.info(this.getClass().getName(), "Status do Apontamento FUP");
						}
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "ATENDIDO"
				 
				if( creatorStatus.getId().equals("risk_assumed") ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.ATTENDED)
					);
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.ATTENDED)
					);
					log.info(this.getClass().getName(), "Status do Apontamento Atendido");
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "ATENDIDO"
				 
				if( creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("risk_assumed") ){
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.ATTENDED)
						);
						iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.ATTENDED)
						);
						log.info(this.getClass().getName(), "Status do Apontamento Atendido");
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * e Status do Revisor do Plano de Ação é:
				 * "BAIXADO" ou "ATENDIDO" ou "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "ATENDIDO"
				 			
				if( creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("risk_assumed") ){
						if( reviewerStatus.getId().equals("settled") || reviewerStatus.getId().equals("attended") || reviewerStatus.getId().equals("risk_assumed") ){
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.ATTENDED)
							);
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.ATTENDED)
							);
							log.info(this.getClass().getName(), "Status do Apontamento Atendido");
						}
					}
				}
				
				
				 * Se Status do Criador do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * e Status do Proprietário do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * e Status do Revisor do Plano de Ação é:
				 * "RISCO ASSUMIDO"
				 * Então Apontamento relacionado é "RISCO ASSUMIDO"
				 			
				if( creatorStatus.getId().equals("risk_assumed") ){
					if( ownerStatus.getId().equals("risk_assumed") ){
						if( reviewerStatus.getId().equals("risk_assumed") ){
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.RISK_ASSUMED)
							);
							iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
									Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.RISK_ASSUMED)
							);
							log.info(this.getClass().getName(), "Status do Apontamento Risco Assumido");
						}
					}
				}
				
				
				 * Se Status do Revisor do Plano de Ação é:
				 * "BAIXADO"
				 * Então Apontamento relacionado é "BAIXADO"
				 			
				if( reviewerStatus.getId().equals("settled") ){
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.SETTLED)
					);
					iroUpdApp.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_REVIEWER_STATUS).setRawValue(
							Collections.singletonList(EnumerationsCustom.CENUM_IS_REVIEWER_STATUS.SETTLED)
					);
					log.info(this.getClass().getName(), "Status do Apontamento Baixado");
				}
				
				iroFacade.save(iroUpdApp, cc.getChainContext().getTransaction(), true);
				log.info(this.getClass().getName(), "Apontamento Salvo");
				
				iroFacade.releaseLock(iroUpdApp.getVersionData().getOVID());
				log.info(this.getClass().getName(), "Lock Apontamento Liberado");*/
				//cc.getChainContext().save(iroUpdApp, true);
				//log.info(this.getClass().getName(), "Apontamento Salvo");
				
				//cc.getChainContext().releaseWriteLock(iroUpdApp);
				//log.info(this.getClass().getName(), "Lock Apontamento Liberado");

			}
			
			/*//Se Apontamento é "Pendente", então Plano de Ação é "Em Aberto"
			if(creatorStatus.getId().equals("new"))
				iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN)
			);
			
			//Se Apontamento é "Em Andamento", então Plano de Ação é "Em Andamento"
			if(creatorStatus.getId().equals("new"))
				iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
			);
			
			//Se Apontamento é "Em Andamento", então Plano de Ação é "Em Andamento"
			if(creatorStatus.getId().equals("new"))
				iroAppObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.IN_PROGRESS)
			);				
				
			
			
			
			if(ownerStatus.getId().equals("pending")){
				ctOpen += 1;
			}
			
			if((creatorStatus.getId().equals("new")) || (creatorStatus.getId().equals("fup")) || (creatorStatus.getId().equals("risk_assumed"))){
				ctGoing += 1;
			}
			
			if((ownerStatus.getId().equals("in_progress")) || (ownerStatus.getId().equals("fup")) || (ownerStatus.getId().equals("risk_assumed"))){
				ctGoing += 1;
			} 
			
			if((reviewerStatus.getId().equals("settled")) || (reviewerStatus.getId().equals("fup")) || (reviewerStatus.getId().equals("risk_assumed")) || (reviewerStatus.getId().equals("attended"))){
				ctGoing += 1;
			}
			
			if((creatorStatus.getId().equals("fup")) || (creatorStatus.getId().equals("fup"))){
				ctFup += 1;
			}
			
			if((ownerStatus.getId().equals("fup")) || (ownerStatus.getId().equals("fup"))){
				ctFup += 1;
			}
			
			if( ((iroCreatorStatus.getId().equals("fup")) || (iroCreatorStatus.getId().equals("risk_assumed"))) &&
				((iroOwnerStatus.getId().equals("fup")) || (iroOwnerStatus.getId().equals("risk_assumed"))) &&
				((iroReviewerStatus.getId().equals("settled")) || (iroReviewerStatus.getId().equals("fup")) || (iroReviewerStatus.getId().equals("risk_assumed")) || (iroReviewerStatus.getId().equals("attended"))) ){
				ctFup += 1;
			}
			
			if( ((iroCreatorStatus.getId().equals("risk_assumed"))) &&
				((iroOwnerStatus.getId().equals("risk_assumed"))) &&
				((iroReviewerStatus.getId().equals("settled")) || (iroReviewerStatus.getId().equals("risk_assumed")) || (iroReviewerStatus.getId().equals("attended"))) ){
				ctAttended += 1;
			}
			
			if( ((iroCreatorStatus.getId().equals("risk_assumed"))) &&
				((iroOwnerStatus.getId().equals("risk_assumed"))) &&
				((iroReviewerStatus.getId().equals("risk_assumed"))) ){
				ctRiskAssumed += 1;
			}
			
			if( ((iroCreatorStatus.getId().equals("settled"))) &&
				((iroOwnerStatus.getId().equals("settled"))) &&
				((iroReviewerStatus.getId().equals("settled"))) ){
				ctSettled += 1;
			}*/
			
		}
		
		/*if(ctOpen > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.OPEN)
			);
			return CommandResult.OK;
		}
		
		if(ctGoing > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.ON_GOING)
			);
			return CommandResult.OK;
		}
		
		if(ctFup > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.FUP)
			);
			return CommandResult.OK;
		}
		
		if(ctAttended > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.ATTENDED)
			);
			return CommandResult.OK;
		}
		
		if(ctRiskAssumed > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.RISK_ASSUMED)
			);
			return CommandResult.OK;
		}
		
		if(ctSettled > 0){
			issueAppObj.getAttribute(IIssueAttributeType.ATTR_OWNER_STATUS).setRawValue(
					Collections.singletonList(EnumerationsCustom.ISSUE_OWNER_STATUS.SETTLED)
			);
			return CommandResult.OK;
		}*/
				
		return CommandResult.OK;
		//return null;
		 
	}
	
	private void getIssuesFromIRO(CommandContext cc){
		
		IAppObj currObj = cc.getChainContext().getTriggeringAppObj();
		
		IEnumerationItem creatorStatusSet = ARCMCollections.extractSingleEntry(currObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS).getRawValue(), true);
		IEnumerationItem ownerStatusSet = ARCMCollections.extractSingleEntry(currObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_OWNER_STATUS).getRawValue(), true);
		IEnumerationItem reviewerStatusSet = ARCMCollections.extractSingleEntry(currObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_REVIEWER_STATUS).getRawValue(), true);
		
		if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.NEW))
			this.count_is_open += 1;
		
		if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PENDING))
			this.count_is_open += 1;
		
		if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING))
			this.count_is_open += 1;
		
		/*if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.IN_PROGRESS))
			this.count_is_progress += 1;*/
		
		if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.IN_PROGRESS))
			this.count_is_progress += 1;
		
		if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.RISK_ASSUMED))
			this.count_is_fup += 1;
		
		if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.FUP))
			this.count_is_fup += 1;
		
		if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.RISK_ASSUMED))
			this.count_is_fup += 1;
		
		if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.FUP))
			this.count_is_fup += 1;
		
		if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS))
			this.count_is_fup += 1;
		
		if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.SETTLED))
			this.count_is_fup += 1;
		
		if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.ATTENDED))
			this.count_is_fup += 1;
		
		if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.RISK_ASSUMED))
			this.count_is_fup += 1;
		
	}
	
	private void getIssuesFromIRO(CommandContext cc, String viewName, Map<String,Object> filterMap){
		
		List<IAppObj> retList = new ArrayList<IAppObj>();
		
		IViewQuery query = QueryFactory.createQuery(cc.getChainContext().getUserContext(), viewName, filterMap, null,
				true, cc.getChainContext().getTransaction());
		try{
			
			Iterator itQuery = query.getResultIterator();
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				
				IEnumerationItem creatorStatusSet = viewObj.getEnumItem(this.column_creator_status);
				IEnumerationItem ownerStatusSet = viewObj.getEnumItem(this.column_owner_status);
				IEnumerationItem reviewerStatusSet = viewObj.getEnumItem(this.column_reviewer_status);
				Long objId = (Long) viewObj.getRawValue(this.column_obj_id);
				
				if(objId.equals(cc.getChainContext().getTriggeringAppObj().getAttribute(IIssueAttributeType.ATTR_OBJ_ID).getRawValue()))
					continue;
				
				if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.NEW))
					this.count_is_open += 1;
				
				if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PENDING))
					this.count_is_open += 1;
				
				if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.PENDING))
					this.count_is_open += 1;
				
				/*if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.IN_PROGRESS))
					this.count_is_progress += 1;*/
				
				if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.IN_PROGRESS))
					this.count_is_progress += 1;
				
				if(creatorStatusSet.equals(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.RISK_ASSUMED))
					this.count_is_fup += 1;
				
				if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.FUP))
					this.count_is_fup += 1;
				
				if(ownerStatusSet.equals(EnumerationsCustom.CENUM_AP_OWNER_STATUS.RISK_ASSUMED))
					this.count_is_fup += 1;
				
				if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.FUP))
					this.count_is_fup += 1;
				
				if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.IN_PROGRESS))
					this.count_is_fup += 1;
				
				if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.SETTLED))
					this.count_is_fup += 1;
				
				if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.ATTENDED))
					this.count_is_fup += 1;
				
				if(reviewerStatusSet.equals(EnumerationsCustom.CENUM_AP_REVIEWER_STATUS.RISK_ASSUMED))
					this.count_is_fup += 1;
				
			}
			
		}finally{
			query.release();
		}
		
		//return (List<IAppObj>)retList;
		
	}
	
	private String getWorkflowStatus(IAppObj currObj){
		
		String wfState = "";
		IEnumAttribute ownerStatusAttr = currObj.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS);
		IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
		
		try{
		
			//LinkedList<IStateMetadata> wfStates = WorkflowHelper.getWorkflowStates(currObj);
			//IStateMetadata wfStateMD = WorkflowUtility.getStateMetadata(currObj);
			//wfState = wfStateMD.getStateId();
			
			if(!ownerStatus.equals(EnumerationsCustom.CENUM_IS_OWNER_STATUS.TO_BE_REVIEWED))
				wfState = "openForExecution";
			
		}catch(Exception e){
			
		}
		return wfState;
		
	}

}
