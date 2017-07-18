package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.exception.ObjectAccessException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectLockException;
import com.idsscheer.webapps.arcm.bl.exception.ObjectNotUniqueException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.generic.CanBeScheduled;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.ValidationException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

@CanBeScheduled
public class IssueSLAUpdateJob extends BaseJob {
	private static final boolean DEBUGGER_ON = true;
//	public static final String KEY_JOB_NAME = EnumerationsCustom.CUSTOM_JOBS.ISSUE_SLA.getPropertyKey();
	public static final String KEY_JOB_NAME = "Teste";
	private static final int ISSUE_SLA = 15; // Número de dias de SLA.
	
	private static final com.idsscheer.batchserver.logging.Logger logger = new com.idsscheer.batchserver.logging.Logger();

	public IssueSLAUpdateJob(IOVID executingUser, Locale executingLocale) {
		super(executingUser, executingLocale);
	}

	protected void execute() throws JobAbortException, JobWarningException {

		this.displayLog("execute()");
		
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE);
		this.displayLog("IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.ISSUE)");
		IAppObjQuery query = facade.createQuery();
		this.displayLog("IAppObjQuery query = facade.createQuery()");
		IAppObjIterator it = query.getResultIterator();
		this.displayLog("AppObjIterator it = query.getResultIterator()");
		
		while (it.hasNext()) {

			this.displayLog("it.hasNext()");
			IAppObj appObj = it.next();
			this.displayLog("appObj - " + appObj.toString());			
			IOVID iroOVID = appObj.getVersionData().getHeadOVID();

			try {

				Date actualDate = new Date();
						
				/*Calendar calAux = Calendar.getInstance();
				calAux.setTime(actualDate);
				calAux.add(Calendar.DATE, 16);
				actualDate = calAux.getTime();
				this.displayLog("actualDate - " + actualDate.toString() );*/
				
				IAppObj iroUpdObj = facade.load(iroOVID, true);
				this.displayLog("iroUpdObj - " + iroUpdObj.toString());
				
				facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID());
				this.displayLog("facade.allocateWriteLock(iroUpdObj.getVersionData().getHeadOVID())");
				
				IEnumAttribute issueActionTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
				IEnumerationItem issueActionType = ARCMCollections.extractSingleEntry(issueActionTypeList.getRawValue(), true);
				this.displayLog("issueActionType - " + issueActionType.toString() );
				
				IEnumAttribute issueCreatorStatusTypeList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS);
				IEnumerationItem issueCreatorStatusType = ARCMCollections.extractSingleEntry(issueCreatorStatusTypeList.getRawValue(), true);
				this.displayLog("issueCreatorStatusType - " + issueCreatorStatusType.toString() );
				
				Date issueCreationDate = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.BASE_ATTR_CREATE_DATE).getRawValue();
				this.displayLog("issueCreationDate - " + issueCreationDate.toString() );
				
				// Realiza offset de SLA na data de criação do apontamento.
				Calendar cal = Calendar.getInstance();
				this.displayLog("Calendar cal = Calendar.getInstance()");
				cal.setTime(issueCreationDate);
				this.displayLog("cal.setTime(issueCreationDate)");
				cal.add(Calendar.DATE, ISSUE_SLA);
				this.displayLog("cal.add(Calendar.DATE, ISSUE_SLA)");
				Date issuefinalDateSLA = cal.getTime();
				this.displayLog("issuefinalDateSLA - " + issuefinalDateSLA.toString() );
				
				/*
				 * Apenas os "apontamentos" que são plano de ação.
				 */
				if ( issueActionType == EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ACTIONPLAN ) {
					
					this.displayLog("Este objeto é um plano de ação.");
					
					/*
					 * O status do criador ainda está em preparação
					 * ("in_creation") e a data de criação do plano de ação
					 * ultrapassou a data do sistema.
					 */

					 if ( actualDate.after(issuefinalDateSLA) && 
						  issueCreatorStatusType == EnumerationsCustom.CENUM_AP_CREATOR_STATUS.NEW ) {

						this.displayLog("este plano de ação deverá ter seu status de criador alternado para pendente.");
						 
						iroUpdObj.getAttribute(IIssueAttributeTypeCustom.ATTR_AP_CREATOR_STATUS).setRawValue(
								Collections.singletonList(EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PENDING));
						this.displayLog("alterou o status do plano de ação para " + EnumerationsCustom.CENUM_AP_CREATOR_STATUS.PENDING.toString());

						facade.save(iroUpdObj, this.getInternalTransaction(), true);						
						this.displayLog("facade.save(iroUpdObj, this.getInternalTransaction(), true)");

						// Cria atributo de lista dos apontamentos relevantes a este plano de ação.
						IListAttribute iroList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.LIST_ISSUERELEVANTOBJECTS);
						this.displayLog("IListAttribute iroList = iroUpdObj.getAttribute(IIssueAttributeTypeCustom.LIST_ISSUERELEVANTOBJECTS)");
						
						// Cria lista de elements dos apontamentos relevantes.
						List<IAppObj> iroElements = iroList.getElements(this.userContext);
						this.displayLog("List<IAppObj> iroElements = iroList.getElements(this.userContext)");
						
						// Cria objeto de iterator para fazer loop em lista.
						Iterator<IAppObj> iroIterator = iroElements.iterator();
						this.displayLog("Iterator<IAppObj> iroIterator = iroElements.iterator()");
						
						// Cria iterator para os apontamentos deste plano de ação.
						while (iroIterator.hasNext()) {

							this.displayLog("iroIterator.hasNext()");
							
							// Cria um objeto para o apontamento do loop.
							IAppObj objRelIssue = iroIterator.next();
							this.displayLog("objRelIssue - " + objRelIssue.toString());
							
							// Passa para o próximo membro do loop caso não for objeto de ISSUE.
							if (objRelIssue.getObjectType() != ObjectType.ISSUE)
								continue;

							// Cria objeto OVID para este apontamento.
							IOVID ovidRelIssue = objRelIssue.getVersionData().getHeadOVID();
							this.displayLog("IOVID ovidRelIssue = objRelIssue.getVersionData().getHeadOVID()");
							
							// Cria objeto que será utilizado para realizar o
							// UPDATE na tabela (alteração no status do apontamento).
							IAppObj objUpdRelIssue = facade.load(ovidRelIssue, true);
							this.displayLog("IAppObj objUpdRelIssue = facade.load(ovidRelIssue, true)");
							
							// Permite escrita.
							facade.allocateWriteLock(objUpdRelIssue.getVersionData().getHeadOVID());
							this.displayLog("facade.allocateWriteLock(objUpdRelIssue.getVersionData().getHeadOVID())");
							
							IEnumAttribute eaRelIssueAction = objUpdRelIssue.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
							IEnumerationItem eiRelIssueAction = ARCMCollections.extractSingleEntry(eaRelIssueAction.getRawValue(), true);
							this.displayLog("IEnumerationItem eiRelIssueAction = ARCMCollections.extractSingleEntry(eaRelIssueAction.getRawValue(), true)");
							
							/* 
							 * Apenas o que é apontamento ("actiontype1"). Não entram os planos de ação 
							 * que possam estar relacionados a esteplano de ação.
							 */  
							if ( eiRelIssueAction == EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ISSUE ) {
								
								this.displayLog("eiRelIssueAction == EnumerationsCustom.CENUM_IS_ACTION_TYPE.actiontype1");
								
								// Cria um enumeration attribute para o creator status do apontamento.
								IEnumAttribute eaRelIssueCreatorStatus = objUpdRelIssue.getAttribute( IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS );
								this.displayLog("IEnumAttribute eaRelIssueCreatorStatus = objUpdRelIssue.getAttribute( IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS )");
								
								// Cria um enumeration item para o owner status dos apontamentos relevantes.
								IEnumerationItem eiRelIssueCreatorStatus = ARCMCollections.extractSingleEntry(eaRelIssueCreatorStatus.getRawValue(), true);
								this.displayLog("IEnumerationItem eiRelIssueCreatorStatus = ARCMCollections.extractSingleEntry(eaRelIssueCreatorStatus.getRawValue(), true)");
								
								// Cria um enumeration attribute para o owner status do apontamento.
								IEnumAttribute eaRelIssueOwnerStatus = objUpdRelIssue.getAttribute( IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS );
								this.displayLog("IEnumAttribute eaRelIssueOwnerStatus = objUpdRelIssue.getAttribute( IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS )");
								
								// Cria um enumeration item para o owner status dos apontamentos relevantes.
								IEnumerationItem eiRelIssueOwnerStatus = ARCMCollections.extractSingleEntry(eaRelIssueOwnerStatus.getRawValue(), true);
								this.displayLog("IEnumerationItem eiRelIssueOwnerStatus = ARCMCollections.extractSingleEntry(eaRelIssueOwnerStatus.getRawValue(), true)");
								
								if ( eiRelIssueCreatorStatus == EnumerationsCustom.CENUM_IS_CREATOR_STATUS.IN_REVIEW ) { 
//								     eiRelIssueOwnerStatus == EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN	 ) {

									this.displayLog("o apontamento relacionado a este plano de ação deverá alternar o seu status de proprietário para EM ANDAMENTO.");
									
									objUpdRelIssue.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_OWNER_STATUS).setRawValue(
											Collections.singletonList(EnumerationsCustom.CENUM_IS_OWNER_STATUS.OPEN ));

									facade.save(objUpdRelIssue, this.getInternalTransaction(), true);

								}

							}

							// Libera o lock dos apontamentos associados a este
							// plano de ação.
							facade.releaseLock(objUpdRelIssue.getVersionData().getHeadOVID());
						}

					}

				}

				// Libera o lock do objeto de plano de ação.
				facade.releaseLock(iroUpdObj.getVersionData().getHeadOVID());

			} catch (RightException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectLockException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectNotUniqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ObjectAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
	
	private void displayLog(String message) {
		if (DEBUGGER_ON) {
			logger.info(this.getClass().getName(), message);
		}
	}
}