package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.openejb.terracotta.quartz.wrappers.JobFacade;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authorization.rights.config.RoleConfigFacade;
import com.idsscheer.webapps.arcm.bl.authorization.rights.runtime.accesscontrol.AccessRightController;
import com.idsscheer.webapps.arcm.bl.authorization.rights.runtime.accesscontrol.standard.UsergroupAccessControl;
import com.idsscheer.webapps.arcm.bl.authorization.rights.runtime.userrole.UserRoleFacade;
import com.idsscheer.webapps.arcm.bl.authorization.rights.support.RoleUtility;
import com.idsscheer.webapps.arcm.bl.authorization.rights.support.UserRoleUtility;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.JobHelper;
import com.idsscheer.webapps.arcm.bl.models.form.IRoleSelectionModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IBreadcrumb;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IFormBreadcrumb;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.StringUtility;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.IRole;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.Role;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;
import com.idsscheer.webapps.arcm.ui.framework.common.UIEnvironmentManager;

public class CustomSaveCEActionCommand extends BaseSaveActionCommand {
	
/*	private String viewName = "customcontrol2risk";
	private String view_column_obj_id = "risk_obj_id";
	private String view_column_version_number = "risk_version_number";
	private String view_ce = "customCET2CtrlExec";
	private String view_ce_obj_id = "obj_id";
	private String view_ce_version_number = "version_number";*/
	private String riscoPotencial = "";
	private String currStatus = "";
	private String control2line = "";
	private String control3line = "";
	final Logger log = Logger.getLogger(CustomSaveCEActionCommand.class.getName());
	private String ceControlExec = "";
	private double countInef = 0;
	private long cetObjectId = 0;

	protected void afterExecute(){
		
		try{
			
			/*IBreadcrumb peek = UIEnvironmentManager.get().getBreadcrumbStack().peek();
			IRoleSelectionModel roleSelModel = ((IFormBreadcrumb)peek).getModel().getRoleSelectionModel();
			
			roleSelModel.getDefaultRole();*/
			
			IAppObj currAppObj = this.formModel.getAppObj();
			//IAppObj currParentCtrlObj = this.parentControl(currAppObj);
			long parentControlObjId = this.parentControl(currAppObj.getObjectId());
			String ceStatus = this.requestContext.getParameter(IControlexecutionAttributeTypeCustom.STR_CUSTOMCTRLEXECSTATUS);
			this.ceControlExec = this.requestContext.getParameter(IControlexecutionAttributeType.STR_OWNER_STATUS);
			
			if(ceStatus.equals("1"))
				this.currStatus = "effective";
			
			if(ceStatus.equals("2")){
				this.currStatus = "ineffective";
				this.countInef += 1;
			}
			
			List<IAppObj> cetList = currAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROLEXECUTIONTASK).getElements(getUserContext());
			for(int i = 0; i < cetList.size(); i++){
				IAppObj cetObj = cetList.get(i);
				this.cetObjectId = cetObj.getObjectId();
			}
			
		
			//
			//IAppObj riskParentObj = this.getRiskFromControl(currParentCtrlObj);
			IAppObj riskParentObj = this.getRiskFromControl(parentControlObjId);
			//List<IAppObj>riskParentList = this.getRisksFromControl();
			
			//for(int i = 0; i < riskParentList.size(); i++){
				
				//IAppObj riskParentObj = riskParentList.get(i);
			
				log.info("risk parent obj: " + riskParentObj.toString());
				if(riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).isEmpty()){
					this.riscoPotencial = "Nao Avaliado";
				}else{
					this.riscoPotencial = riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
				}
				
				if(!riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).isEmpty())
					this.control2line = riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue();
				
				if(!riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).isEmpty())
					this.control3line = riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue();
				
				if(this.ceControlExec.equals("3")){
					this.controlClassification(currAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(getUserContext()));
					this.affectResidualRisk(riskParentObj);
				}
			
			//}
			
			
		}catch(Exception e){
			//this.environment.getDialogManager().createSilentForwardDialog("ERRO", e.getMessage());
			this.environment.getDialogManager().getNotificationDialog().addInfo(e.getMessage());
		}		
		
	}
	
	private IAppObj parentControl(IAppObj childAppObj){
		
		IAppObj parentControlObj = null;
		
		List<IAppObj> controlList = childAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
		for(IAppObj controlObj : controlList){
			parentControlObj = controlObj;
			//parentControlName = controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue();
		}
		
		return parentControlObj;
		
	}
	
	private long parentControl(long ceObjId) throws Exception{
		
		Map filterMap = new HashMap();
		filterMap.put("ce_id", ceObjId);
		long controlID = 0;
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), "custom_CE2Control", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				controlID = (Long)viewObj.getRawValue("ct_id");
				
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return controlID;
		
	}
	
	private IAppObj getRiskFromControl(IAppObj controlObj) throws Exception{
			
			IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			IAppObjQuery riskQuery = riskFacade.createQuery();
			IAppObjIterator riskIterator = riskQuery.getResultIterator();
			IAppObj riskAppObj = null;
			//log.info("infra para obten��o de risco pronta");
			
			while(riskIterator.hasNext()){
				
				IAppObj riskObj = riskIterator.next();
				//log.info("risco lido" + riskObj.getAttribute(IRiskAttributeType.ATTR_NAME).getRawValue());
				
				if(!(riskAppObj == null))
					break;
				
				List<IAppObj> ctrlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getUserContext());
				for(IAppObj ctrlObj : ctrlList){
					if(ctrlObj.getGuid().equals(controlObj.getGuid())){
						//log.info("risco do controle" + riskObj.getAttribute(IRiskAttributeType.ATTR_NAME).getRawValue());
						riskAppObj = riskObj;
						break;
					}
				}
				
			}
			riskQuery.release();
			
			return riskAppObj;
			
			
			/*Map filterMap = new HashMap();
			//filterMap.put("control_obj_id", controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue());
			log.info("RFC filtermap declarado");
			log.info("RFC Control ID: " + controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue());
					
			IAppObj riskReturn = null;
			IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			log.info("RFC risk facade declarado");
			log.info("RFC " + riskFacade.toString());
			
			log.info(this.viewName);
			IViewQuery query = QueryFactory.createQuery(this.getUserContext(), this.viewName, filterMap, null,
					true, this.getDefaultTransaction());
			
			log.info(query.getSize());
			
			try{
				
				log.info("RFC query executada");
				
				Iterator it = query.getResultIterator();
				while(it.hasNext()){
					
					log.info("RFC iterator");
					
					IViewObj viewObj = (IViewObj) it.next();
					Long obj_id = (Long) viewObj.getRawValue(view_column_obj_id);
					log.info("RFC obj_id: " + String.valueOf(obj_id));
					Long version_number = (Long) viewObj.getRawValue(view_column_version_number);
					log.info("RFC version_number: " + String.valueOf(version_number));
					
					IOVID riskOVID = OVIDFactory.getOVID(obj_id.longValue(), version_number.longValue());
					log.info("RFC riskOVID: " + riskOVID.toString());
					riskReturn = riskFacade.load(riskOVID, true);
					log.info("RFC riskOBJ carregado: " + riskReturn.toString());
					
				}
				query.release();
				return riskReturn;
				
			}catch(RightException re){
				log.info("RFC Exce��o Direitos: " + re.getMessage());
				throw (Exception)re;
			}catch(ObjectLockException ole){
				log.info("RFC Exce��o Objeto Lockado: " + ole.getMessage());
	//			throw (Exception)ole;
			}catch(ValidationException ve){
				log.info("RFC Exce��o Validation: " + ve.getMessage());
	//			throw ve;
			}catch(ObjectNotUniqueException onue){
				log.info("RFC Exce��o Obj Nao Unico: " + onue.getMessage());
	//			throw onue;
			}catch(ObjectAccessException oae){
				log.info("RFC Exce��o Obj Sem Acesso: " + oae.getMessage());
	//			throw oae;
			}catch(Exception e){
				log.info("RFC Exce��o Query: " + e.getMessage());
				throw e;
			}*/
			
		}

	private IAppObj getRiskFromControl(long controlObjID) throws Exception{
		
		IAppObj riskAppObj = null;
		long riskID = 0;
		long riskVersionNumber = 0;
		
		Map filterMap = new HashMap();
		filterMap.put("control_obj_id", controlObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), "customcontrol2risk", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				riskID = (Long)viewObj.getRawValue("risk_obj_id");
				riskVersionNumber = (Long)viewObj.getRawValue("risk_version_number");
				
			}
		
			IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			IOVID riskOVID = OVIDFactory.getOVID(riskID, riskVersionNumber);
			riskAppObj = riskFacade.load(riskOVID, true);
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return riskAppObj;
		
	}
	
	private List<IAppObj> getRisksFromControl() throws Exception{
		
		//IAppObj riskAppObj = null;
		List<IAppObj> riskAppList = new ArrayList<IAppObj>();
		long riskID = 0;
		long riskVersionNumber = 0;
		
		Map filterMap = new HashMap();
		//filterMap.put("control_obj_id", controlObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), "customcontrol2risk", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				riskID = (Long)viewObj.getRawValue("risk_obj_id");
				riskVersionNumber = (Long)viewObj.getRawValue("risk_version_number");
				
				IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
				IOVID riskOVID = OVIDFactory.getOVID(riskID, riskVersionNumber);
				IAppObj riskAppObj = riskFacade.load(riskOVID, true);
				
				riskAppList.add(riskAppObj);
				
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		//return riskAppObj;
		return (List<IAppObj>)riskAppList;
		
	}
	
	private void affectResidualRisk(IAppObj riskObj) throws Exception{
		
		double countTotal = 0;
		double count1line = 0;
		//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		log.info("Eficacia do Controle Corrente: " + this.currStatus);
		/*if(this.currStatus.equals("ineffective"))
			count1line += 1;*/
		
		String riskResidualFinal = "";
		
		try{
			
			IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			IOVID riskOVID = riskObj.getVersionData().getHeadOVID();
			IAppObj riskUpdObj = riskFacade.load(riskOVID, true);
			riskFacade.allocateWriteLock(riskOVID);
			
			//IAppObjFacade controlFacade = this.environment.getAppObjFacade(ObjectType.CONTROL);
		
			List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getUserContext());
			for(IAppObj controlObj : controlList){
				
				/*IOVID controlOVID = controlObj.getVersionData().getHeadOVID();
				IAppObj controlUpdObj = controlFacade.load(controlOVID, true);
				controlFacade.allocateWriteLock(controlOVID);*/
				
				//Date ceDate = null;
				log.info("==================================================");
				log.info("Controle Lido: " + controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue());
				log.info("==================================================");
				countTotal +=1;
				
				List<IAppObj> cetList = controlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).getElements(this.getUserContext());
				for(IAppObj cetObj : cetList){
					if(cetObj.getObjectId() == this.cetObjectId)
						continue;
					
					//List<IAppObj> ceList = this.getCtrlExecFromCET(cetObj);
					List<IAppObj> ceList = this.getCtrlExecFromCET(cetObj.getObjectId());
					for(IAppObj ceObj : ceList){
						log.info("Data EC: " + String.valueOf(ceObj.getVersionData().getCreateDate().getTime()));
						
						if(ceObj.getGuid().equals(this.formModel.getAppObj().getGuid())){
							/*if(this.requestContext.getParameter(IControlexecutionAttributeType.STR_OWNER_STATUS).equals("3")){
								//countTotal += 1;
								log.info("Status EC: COMPLETED");
								if(this.currStatus.equals("ineffective")){
									count1line += 1;
									controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
								}else{
									controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
								}
							}*/
						}else{
							IEnumAttribute ownerStatusAttr = ceObj.getAttribute(IControlexecutionAttributeType.ATTR_OWNER_STATUS);
							IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
							IEnumAttribute statusAttr = ceObj.getAttribute(IControlexecutionAttributeTypeCustom.ATTR_CUSTOMCTRLEXECSTATUS);
							IEnumerationItem statusItem = ARCMCollections.extractSingleEntry(statusAttr.getRawValue(), true);
							if(ownerStatus.getId().equals("completed")){
								//countTotal += 1;
								log.info("Status EC: COMPLETED*");
								if(statusItem.getId().equals("ineffective")){
									count1line += 1;
									//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
								}else{
									//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
								}
							}
						}
						
						//countTotal += 1;
						log.info("CE Totais: " + String.valueOf(countTotal));
						log.info("CE Inefetivos: " + String.valueOf(count1line));
					}
					
				}
				/*controlFacade.save(controlUpdObj, this.getDefaultTransaction(), true);
				controlFacade.releaseLock(controlOVID);*/
				
			}
			count1line = count1line + this.countInef;
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF1LINE).setRawValue(count1line);
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL1LINE).setRawValue(countTotal);
			double risk1line = 0;
			if(countTotal > 0)
				risk1line = ( count1line / countTotal );
			
			String riskClass1line = this.riskClassification(risk1line);
			log.info("Controles Inefetivos: " + String.valueOf(count1line));
			log.info("Total de Controles: " + String.valueOf(countTotal));
			log.info("Pondera��o: " + String.valueOf(risk1line));
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).setRawValue(riskClass1line);
			
			/*String riskClass2line = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue();
			if(riskClass2line == null)
				riskClass2line = "";
				
			String riskClass3line = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue();
			if(riskClass3line == null)
				riskClass3line = "";*/
			
			//String riskClassFinal = this.riskFinalClassification(riskClass1line, riskClass2line, riskClass3line);
			//String riskClassFinal = riskClass1line;
			String riskClassFinal = "";
			if((this.control2line.equals("")) && (this.control3line.equals(""))){
				riskClassFinal = riskClass1line;
			}else{
				if((this.control2line.equals("")) && (!this.control2line.equals(""))){
					riskClassFinal = this.riskFinalClassification(riskClass1line, "", this.control3line);
				}else{
					if((!this.control2line.equals("")) && (this.control3line.equals(""))){
						riskClassFinal = this.riskFinalClassification(riskClass1line, this.control2line, "");
					}else{
						riskClassFinal = this.riskFinalClassification(riskClass1line, this.control2line, this.control3line);
					}
				}
			}			
			
			if(riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).isEmpty()){
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
				log.info("CONTROLFINAL � Vazio");
				log.info("CONTROLFINAL: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue());
			}else{
				String riskFinal = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue();
				log.info("riskFinal: " + riskFinal);
				log.info("riskClass1line: " + riskClass1line);
				
				log.info("riskClassFinal: " + riskClassFinal);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
				log.info("CONTROLFINAL n�o � Vazio");
				log.info("CONTROLFINAL: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue());
			}
			log.info("Amb. Controle Final: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue());
			log.info("Risco Potencial: " + this.riscoPotencial);
			
			if(!this.riscoPotencial.equals("Nao Avaliado")){
				riskResidualFinal = this.riskResidualFinal(this.riscoPotencial, riskClassFinal);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(riskResidualFinal);
			}
			
			riskFacade.save(riskUpdObj, this.getDefaultTransaction(), true);
			riskFacade.releaseLock(riskOVID);
		
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	private List<IAppObj> getCtrlExecFromCET(IAppObj cetObj) throws Exception{
		
		IAppObjFacade ceFacade = this.environment.getAppObjFacade(ObjectType.CONTROLEXECUTION);
		IAppObjQuery ceQuery = ceFacade.createQuery();
		IAppObjIterator ceIterator = ceQuery.getResultIterator();
		List<IAppObj> ceListReturn = new ArrayList<IAppObj>();
		List<IAppObj> ceListBuffer = new ArrayList<IAppObj>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		//log.info("Infra para obten��o de Control Execution");
		
		while(ceIterator.hasNext()){
			
			IAppObj ceObj = ceIterator.next();
			//log.info("CExec Lido: " + ceObj.getAttribute(IControlexecutionAttributeType.ATTR_NAME).getRawValue());
			
			List<IAppObj> ctList = ceObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROLEXECUTIONTASK).getElements(this.getUserContext());
			for(IAppObj ctObj : ctList){
				if(ctObj.getGuid().equals(cetObj.getGuid())){
					//log.info("CExec Adicionado: " + ceObj.getAttribute(IControlexecutionAttributeType.ATTR_NAME).getRawValue());
					ceListBuffer.add(ceObj);
				}
			}
			
		}
		if(ceListBuffer.size() > 0){
			ceListBuffer.sort(new Comparator<IAppObj>(){
				@Override
				public int compare(IAppObj ant, IAppObj post){
					long antTime = ant.getVersionData().getCreateDate().getTime();
					long postTime = post.getVersionData().getCreateDate().getTime();
					return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
				}
			});
			/*log.info("Lista Ordenada");
			log.info("=================================");
			log.info("Resultado da Ordena��o");
			for(int i = 0; i < ceListBuffer.size(); i++){
				//IAppObj ce = (IAppObj)ceListItBuffer.next();
				IAppObj ce = ceListBuffer.get(i);
				
				log.info("Data Cria��o CE: " + dateFormat.format(ce.getVersionData().getCreateDate()) + 
						" - " + String.valueOf(ce.getVersionData().getCreateDate().getTime()));
			}*/
			
			/*for(int i = 0; i < ceListReturn.size(); i++){
				if(i == 0)
					continue;
				ceListReturn.remove(i);
				i -= 1;
			}*/
			ceListReturn.add(ceListBuffer.get(ceListBuffer.size() - 1));
			log.info("Remo��o de Todos os CE n�o relevantes");
			log.info("Tamanho da Lista de CE: " + String.valueOf(ceListReturn.size()));
		}
		
		ceQuery.release();
		
		return (List<IAppObj>)ceListReturn;
		
		/*Map filterMap = new HashMap();
		List<IAppObj> ceReturn = new ArrayList<IAppObj>();
		IAppObjFacade ceFacade = this.environment.getAppObjFacade(ObjectType.CONTROLEXECUTION);
		log.info("CEFCET - Facade Criado: " + ceFacade.toString());
		
		IViewQuery query = QueryFactory.createQuery(this.getUserContext(), this.view_ce, filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
			
			log.info("CEFCET - Query Executada");
			
			Iterator it = query.getResultIterator();
			while(it.hasNext()){
				
				IViewObj viewObj = (IViewObj) it.next();
				Long obj_id = (Long) viewObj.getRawValue(this.view_ce_obj_id);
				log.info("CEFCET - CE Obj ID: " + String.valueOf(obj_id));
				Long version_number = (Long) viewObj.getRawValue(this.view_ce_version_number);
				log.info("CEFCET - CE Vers Number: " + String.valueOf(version_number));
				
				IOVID ceOVID = OVIDFactory.getOVID(obj_id.longValue(), version_number.longValue());
				log.info("CEFCET ceOVID: " + ceOVID.toString());
				ceReturn.add(ceFacade.load(ceOVID, true));
				log.info("CEFCET ceOBJ Carregado: " + ceOVID.toString());
				
			}
			query.release();
			return (List<IAppObj>)ceReturn;
			
		}catch(Exception e){
			log.info("CEFCET Exception: " + e.getMessage());
			throw e;
		}*/

	}
	
	private List<IAppObj> getCtrlExecFromCET(long cetObjID) throws Exception{
		
		List<IAppObj> retList = new ArrayList<IAppObj>();
		List<IAppObj> bufList = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("cetask_id", cetObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), "custom_CET2CE", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long ceID = (Long)viewObj.getRawValue("ce_id");
				long ceVersionNumber = (Long)viewObj.getRawValue("ce_version_number");
				
				IAppObjFacade ceFacade = this.environment.getAppObjFacade(ObjectType.CONTROLEXECUTION);
				IOVID ceOVID = OVIDFactory.getOVID(ceID, ceVersionNumber);
				IAppObj ceAppObj = ceFacade.load(ceOVID, true);
				
				if(ceAppObj != null)
					bufList.add(ceAppObj);
				
			}
			
			if(bufList.size() > 0){
				bufList.sort(new Comparator<IAppObj>(){
					@Override
					public int compare(IAppObj ant, IAppObj post){
						long antTime = ant.getVersionData().getCreateDate().getTime();
						long postTime = post.getVersionData().getCreateDate().getTime();
						return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
					}
				});
				retList.add(bufList.get(bufList.size() - 1));
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return (List<IAppObj>)retList;
		
	}
	
	private void controlClassification(List<IAppObj> controlList) throws Exception{
		
		IAppObjFacade controlFacade = this.environment.getAppObjFacade(ObjectType.CONTROL);
		IOVID ovid = null;
		try{
			
			Iterator itControl = controlList.iterator();
			while(itControl.hasNext()){
				
				IAppObj controlObj = (IAppObj)itControl.next();
				IOVID controlOVID = controlObj.getVersionData().getHeadOVID();
				ovid = controlOVID;
				IAppObj controlUpdObj = controlFacade.load(controlOVID, true);
				controlFacade.allocateLock(controlOVID, LockType.TYPEDFORCEWRITE);
				
				if(this.currStatus.equals("ineffective")){
					controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
				}else{
					controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
				}
				
				controlFacade.save(controlUpdObj, this.getDefaultTransaction(), true);
				controlFacade.releaseLock(controlOVID);
				
				break;
				
			}
		
		}catch(Exception e){
			controlFacade.releaseLock(ovid);
			throw e;
		}
		
	}
	
	private String riskClassification(double riskVuln){
		
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixo";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "M�dio";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alto";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alto";
		}
		
		return riskClassif;
		
	}		
	
	private String riskFinalClassification(String risk1line, String risk2line, String risk3line){
		
		int height_1line = 0;
		int height_2line = 0;
		int height_3line = 0;
		String riskClassFinal = "";
		
		if(!risk1line.equals("")){
			//Classifica��o - Amb. Controles 1a Linha
			if(risk1line.equalsIgnoreCase("Muito Alto"))
				height_1line = 4;
			if(risk1line.equalsIgnoreCase("Alto"))
				height_1line = 3;
			if(risk1line.equalsIgnoreCase("M�dio"))
				height_1line = 2;
			if(risk1line.equalsIgnoreCase("Baixo"))
				height_1line = 1;
		}
		
		if(!risk2line.equals("")){
			//Classifica��o - Amb. Controles 2a Linha
			if(risk2line.equalsIgnoreCase("Muito Alto"))
				height_2line = 4;
			if(risk2line.equalsIgnoreCase("Alto"))
				height_2line = 3;
			if(risk2line.equalsIgnoreCase("M�dio"))
				height_2line = 2;
			if(risk2line.equalsIgnoreCase("Baixo"))
				height_2line = 1;
		}
		
		if(!risk3line.equals("")){
			//Classifica��o - Amb. Controles 3a Linha
			if(risk3line.equalsIgnoreCase("Muito Alto"))
				height_3line = 4;
			if(risk3line.equalsIgnoreCase("Alto"))
				height_3line = 3;
			if(risk3line.equalsIgnoreCase("M�dio"))
				height_3line = 2;
			if(risk3line.equalsIgnoreCase("Baixo"))
				height_3line = 1;
		}
		
		int maxHeightCtrl = Math.max(height_1line, Math.max(height_2line, height_3line));
		switch(maxHeightCtrl){
		case 4:
			riskClassFinal = "Muito Alto";
			break;
		case 3:
			riskClassFinal = "Alto";
			break;
		case 2:
			riskClassFinal = "M�dio";
			break;
		case 1:
			riskClassFinal = "Baixo";
			break;
		}
		
		return riskClassFinal;
		
	}
	
	private String riskFinalClass(String risk1line, String riskFinal){
		
		int height_1line = 0;
		int height_final = 0;
		String riskClassFinal = "";
		

		if(risk1line.equalsIgnoreCase("Muito Alto"))
			height_1line = 4;
		if(risk1line.equalsIgnoreCase("Alto"))
			height_1line = 3;
		if(risk1line.equalsIgnoreCase("M�dio"))
			height_1line = 2;
		if(risk1line.equalsIgnoreCase("Baixo"))
			height_1line = 1;
		log.info("height_1line: " + height_1line);

		if(riskFinal.equalsIgnoreCase("Muito Alto"))
			height_final = 4;
		if(riskFinal.equalsIgnoreCase("Alto"))
			height_final = 3;
		if(riskFinal.equalsIgnoreCase("M�dio"))
			height_final = 2;
		if(riskFinal.equalsIgnoreCase("Baixo"))
			height_final = 1;
		log.info("height_final: " + height_final);
		
		if(height_1line >= height_final){
			switch(height_1line){
			case 4:
				riskClassFinal = "Muito Alto";
				break;
			case 3:
				riskClassFinal = "Alto";
				break;
			case 2:
				riskClassFinal = "M�dio";
				break;
			case 1:
				riskClassFinal = "Baixo";
				break;
			}			
		}else{
			riskClassFinal = riskFinal;
		}
		return riskClassFinal;
	}
	
	private String riskResidualFinal(String riskPotencial, String riskControlFinal){
		
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		return riskResidualReturn;
		
	}	
	
/*	
		//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "saveCET", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		IAppObj currAppObj = this.formModel.getAppObj();
		IUIEnvironment currEnv = this.environment;
		String currCtrlParentName = this.parentControl(currAppObj);
		
		IEnumAttribute ra_control1lineattr = currAppObj.getAttribute(IControlexecutionAttributeTypeCustom.ATTR_CUSTOMCTRLEXECSTATUS);
		IEnumerationItem ra_control1lineitem = ARCMCollections.extractSingleEntry(ra_control1lineattr.getRawValue(), true);
		// = ra_control1lineitem.getParameter(IControlexecutionAttributeTypeCustom.STR_CUSTOMCTRLEXECSTATUS);
		String ra_control1line = ra_control1lineitem.getId();
		//log.info("ra_control1line: " + ra_control1line);
		
		try{
		
			IAppObjFacade ceFacade = currEnv.getAppObjFacade(ObjectType.CONTROLEXECUTION);
			IAppObjQuery ceQuery = ceFacade.createQuery();
			IAppObjIterator ceIterator = ceQuery.getResultIterator();
			
			while(ceIterator.hasNext()){
				
				IAppObj ceAppObj = ceIterator.next();
				IAppObj ceUpdAppObj = ceFacade.load(ceAppObj.getVersionData().getHeadOVID(), true);
				
				if(ceUpdAppObj.getGuid().equals(currAppObj.getGuid()))
					continue;
				
				String ceCtrlParentName = this.parentControl(ceUpdAppObj);
				
				if(!(currCtrlParentName.equals(ceCtrlParentName)))
					continue;
				
				this.copyEditableAttr(currAppObj, ceUpdAppObj);
				ceFacade.save(ceUpdAppObj, this.getDefaultTransaction(), true);
				ceFacade.releaseLock(ceUpdAppObj.getVersionData().getOVID());
				
				List<IAppObj> ceCtrlList = ceUpdAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
				for(IAppObj ceCtrlObj : ceCtrlList){
					this.affectResidualRisk(currEnv, ceCtrlObj, ra_control1line);
				}
				
			}
			ceQuery.release();
			
			List<IAppObj> currCtrlList = currAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
			for(IAppObj currCtrlObj : currCtrlList){
				this.affectResidualRisk(currEnv, currCtrlObj, ra_control1line);
			}
		
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
		
	}
	
	private String parentControl(IAppObj childAppObj){
		
		String parentControlName = "";
		
		List<IAppObj> controlList = childAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
		for(IAppObj controlObj : controlList){
			parentControlName = controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue();
		}
		
		return parentControlName;
		
	}
	
	private void copyEditableAttr(IAppObj sourceObj, IAppObj targetObj){
		
		List<IAttribute> attrList = sourceObj.getEditableAttributes(this.getUserContext());
		for(IAttribute attr : attrList){
			AppObjUtility.copyAttributeValue(sourceObj, targetObj, attr.getAttributeType());
		}
		
	}
	
	private void affectResidualRisk(IUIEnvironment currEnv, IAppObj controlAppObj, String testResult) throws Exception{
		
		IAppObjFacade riskFacade = currEnv.getAppObjFacade(ObjectType.RISK);
		IAppObjQuery riskQuery = riskFacade.createQuery();
		IAppObjIterator riskIterator = riskQuery.getResultIterator();
		
		//log.info("Resultado 1a Linha Controle: " + testResult);
		
		String controlID = controlAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
		//log.info("Parametro ControlID: " + controlID);
		
		try{
		
			while(riskIterator.hasNext()){
				
				IAppObj riskObj = riskIterator.next();
				IOVID riskOVID = riskObj.getVersionData().getHeadOVID();
				IAppObj riskUpdObj = riskFacade.load(riskOVID, true);
				riskFacade.allocateWriteLock(riskOVID);
				
				List<IAppObj> ctrlList = riskUpdObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getFullGrantUserContext());
				for(IAppObj ctrlObj : ctrlList){
					String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
					//log.info("Parametro CtrlID: " + controlID);
					if(ctrlID.equals(controlID)){
						riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).setRawValue(testResult);
					}
				}
				//log.info(riskUpdObj);
				riskFacade.save(riskUpdObj, this.getDefaultTransaction(), true);
				riskFacade.releaseLock(riskOVID);
			}
			riskQuery.release();
		
		}catch(Exception e){
			throw e;
		}
		
	}*/

}
