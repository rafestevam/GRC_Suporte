package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IStringAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestCaseAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskException;
import com.idsscheer.webapps.arcm.custom.corprisk.CustomCorpRiskHierarchy;
import com.idsscheer.webapps.arcm.custom.procrisk.RiskAndControlCalculation;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;
import com.idsscheer.webapps.arcm.ui.framework.common.JobUIEnvironment;

public class CustomTestcaseSaveActionCommand extends TestcaseSaveActionCommand {
	
	/*private String origemTeste = "";
	private double riskVuln2line = 0;
	private double riskVuln3line = 0;*/
	/*private String viewName = "customcontrol2risk";
	private String view_column_obj_id = "risk_obj_id";
	private String view_column_version_number = "risk_version_number";
	private String view_testcase = "customtestdef2testaction";
	private String view_testcase_obj_id = "ta_id";
	private String view_testcase_version_number = "ta_version_number";*/
	private String riscoPotencial = "";
	private String fernanda = "";
	private String origemTeste = "";
	final Logger log = Logger.getLogger(CustomTestcaseSaveActionCommand.class.getName());
	private double countInef2line = 0;
	private double countInef3line = 0;
	private double countTotal2line = 0;
	private double countTotal3line = 0;
	private long tdObjectId = 0;
	private String riskClass1Line = "";
	private IUserContext jobCtx; //REO+ 27.09.2017 - EV113345

	protected void addForwardDialog() {
		this.executeCalculation();
		super.addForwardDialog();
	}
	
	//protected void afterExecute(){
	private void executeCalculation(){
			
		try{
			this.addUserRights(); //REO+ 27.09.2017 - EV113345
		
			IAppObj currAppObj = this.formModel.getAppObj();
			IAppObj currParentCtrlObj = this.parentControl(currAppObj);
			String parentControlId = currParentCtrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
			//long parentControlObjId = this.parentControl(currAppObj.getObjectId());
			//String ownerStatus = this.requestContext.getParameter(ITestcaseAttributeType.STR_OWNER_STATUS);
			//this.origemTeste = this.requestContext.getParameter(ITestCaseAttributeTypeCustom.STR_ORIGEMTESTE);
			IEnumAttribute ownerStatusAttr = currAppObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
			IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
			
			IEnumAttribute origemTesteAttr = currAppObj.getAttribute(ITestCaseAttributeTypeCustom.ATTR_ORIGEMTESTE);
			IEnumerationItem origemTeste = ARCMCollections.extractSingleEntry(origemTesteAttr.getRawValue(), true);
			this.origemTeste = origemTeste.getId();
			log.info("Origem do Teste: " + this.origemTeste);
			
			if(this.origemTeste.equals("1linhadefesa"))
				this.countTotal2line += 1;
			if(this.origemTeste.equals("2linhadefesa"))
				this.countTotal3line += 1;
			
			if(ownerStatus.getId().equals("effective"))
				this.fernanda = "effective";
			
			if(ownerStatus.getId().equals("noneffective")){
				this.fernanda = "noneffective";
				if(this.origemTeste.equals("1linhadefesa"))
					this.countInef2line += 1;
				if(this.origemTeste.equals("2linhadefesa"))
					this.countInef3line += 1;
			}
			
			List<IAppObj> tdList = currAppObj.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION).getElements(getUserContext());
			for(int i = 0; i < tdList.size(); i++){
				IAppObj tdObj = tdList.get(i);
				this.tdObjectId = tdObj.getObjectId();
			}
				
			log.info("Status de Proprietário: " + ownerStatus);
			
			log.info("Testes Inef 2a Linha: " + String.valueOf(this.countInef2line));
			log.info("Total Testes 2a Linha: " + String.valueOf(this.countTotal2line));
			
			log.info("Testes Inef 3a Linha: " + String.valueOf(this.countInef3line));
			log.info("Total Testes 3a Linha: " + String.valueOf(this.countTotal3line));
		
			//IAppObj riskParentObj = this.getRiskFromControl(currParentCtrlObj);
			//IAppObj riskParentObj = this.getRiskFromControl(parentControlObjId);
			
			List<IAppObj> riskAppList = this.getRisksFromControl(parentControlId);
			
			for(int i = 0; i < riskAppList.size(); i++){
				
				//Inicio REO 07.11.2017 - Ajuste de dados para Mashzone (EV118286)
				IAppObj riskParentObj = this.environment.getAppObjFacade(ObjectType.RISK).load(riskAppList.get(i).getVersionData().getHeadOVID(), true);
				//IAppObj riskParentObj = riskAppList.get(i);
				//Fim REO 07.11.2017 - Ajuste de dados para Mashzone (EV118286)
				
				if(riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).isEmpty()){
					this.riscoPotencial = "Nao Avaliado";
				}else{
					this.riscoPotencial = riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
				}
				
				if(!riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).isEmpty())
					this.riskClass1Line = riskParentObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).getRawValue();
				
				log.info("");
				log.info("****************************************************************************************");
				log.info("Risco Pai: " + riskParentObj.getAttribute(IRiskAttributeType.ATTR_RISK_ID).getRawValue());
				log.info("Controle Corrente: " + currAppObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(getFullGrantUserContext()).get(0).getRawValue(IControlAttributeType.ATTR_NAME));
				log.info("****************************************************************************************");
				//if(ownerStatus.equals("3") || ownerStatus.equals("4"))
				if(this.requestContext.getParameter(ITestcaseAttributeType.STR_REVIEWER_STATUS).equals("1")){
					List<IAppObj> controlList = currAppObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(getUserContext());
					this.controlClassification(controlList);
					this.affectResidualRisk(riskParentObj);
					this.affectCorpRisk(riskParentObj);
				}
			
			}
		
		}catch(CustomCorpRiskException e1){
			//this.environment.getDialogManager().getNotificationDialog().addInfo(e1.getMessage());
		}catch(Exception e){
			//this.environment.getDialogManager().createSilentForwardDialog("ERRO", e.getMessage());
			this.environment.getDialogManager().getNotificationDialog().addInfo(e.getMessage());
		}
		
	}
	
	private IAppObj parentControl(IAppObj childAppObj){
		
		IAppObj parentControlObj = null;
		
		List<IAppObj> controlList = childAppObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(this.getFullGrantUserContext());
		for(IAppObj controlObj : controlList){
			parentControlObj = controlObj;
			//parentControlName = controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue();
		}
		
		return parentControlObj;
		
	}
	
	private long parentControl(long ceObjId) throws Exception{
		
		Map filterMap = new HashMap();
		filterMap.put("tc_id", ceObjId);
		long controlID = 0;
		
		IViewQuery query = QueryFactory.createQuery(this.jobCtx/*this.getFullGrantUserContext()*/, "custom_TestCase2Control", filterMap, null,
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
		//log.info("infra para obtenção de risco pronta");
		
		while(riskIterator.hasNext()){
			
			IAppObj riskObj = riskIterator.next();
			//log.info("risco lido" + riskObj.getAttribute(IRiskAttributeType.ATTR_NAME).getRawValue());
			
			if(!(riskAppObj == null))
				break;
			
			List<IAppObj> ctrlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getFullGrantUserContext());
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
		log.info("filtermap declarado");
		log.info("Código do controle " + controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue());
		filterMap.put("control_obj_id", controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue());
		
		IAppObj riskReturn = null;
		IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
		log.info("risk facade declarado");
		log.info(riskFacade.toString());
		
		IViewQuery query = QueryFactory.createQuery(this.getUserContext(), this.viewName, filterMap, null,
				true, this.getDefaultTransaction());
		try{
			
			log.info("query executada");
			
			Iterator it = query.getResultIterator();
			while(it.hasNext()){
				
				log.info("iterator");
				
				IViewObj viewObj = (IViewObj) it.next();
				Long obj_id = (Long) viewObj.getRawValue(view_column_obj_id);
				log.info("obj_id: " + String.valueOf(obj_id));
				Long version_number = (Long) viewObj.getRawValue(view_column_version_number);
				log.info("version_number: " + String.valueOf(version_number));
				
				IOVID riskOVID = OVIDFactory.getOVID(obj_id.longValue(), version_number.longValue());
				log.info("riskOVID: " + riskOVID.toString());
				riskReturn = riskFacade.load(riskOVID, true);
				log.info("riskOBJ carregado: " + riskReturn.toString());
				
			}
			query.release();
			return riskReturn;
			
		}catch(RightException re){
			log.info("Exceção Direitos: " + re.getMessage());
			throw re;
		}catch(Exception e){
			log.info("Exceção Query: " + e.getMessage());
			throw e;
		}*/
		
	}
	
	private IAppObj getRiskFromControl(long controlObjID) throws Exception{
		
		IAppObj riskAppObj = null;
		long riskID = 0;
		long riskVersionNumber = 0;
		
		Map filterMap = new HashMap();
		filterMap.put("control_obj_id", controlObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.jobCtx/*this.getFullGrantUserContext()*/, "customcontrol2risk", filterMap, null,
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
	
	private List<IAppObj> getRisksFromControl(String controlID) throws Exception{
		
		//IAppObj riskAppObj = null;
		List<IAppObj> riskAppList = new ArrayList<IAppObj>();
		long riskID = 0;
		long riskVersionNumber = 0;
		
		Map filterMap = new HashMap();
		filterMap.put("control_id", controlID);
		
		IViewQuery query = QueryFactory.createQuery(this.jobCtx/*this.getFullGrantUserContext()*/, "customcontrol2risk", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			IAppObjFacade riskFacade = FacadeFactory.getInstance().getAppObjFacade(this.jobCtx, ObjectType.RISK); //REO+ 27.09.2017 - EV113345
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				riskID = (Long)viewObj.getRawValue("risk_obj_id");
				riskVersionNumber = (Long)viewObj.getRawValue("risk_version_number");
				
				
				//IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK); //REO- 27.09.2017 - EV113345
				IOVID riskOVID = OVIDFactory.getOVID(riskID, riskVersionNumber);
				IAppObj riskAppObj = riskFacade.load(riskOVID, true);
				
				if(riskAppObj.getVersionData().isHeadRevision())
					riskAppList.add(riskAppObj);
				
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return (List<IAppObj>)riskAppList;
		//return riskAppObj;
		
	}
	
	private void affectResidualRisk(IAppObj riskObj) throws Exception{
		
		double cntTotal2Line = 0;
		double cntTotal3Line = 0;
		double cntInef2Line = 0;
		double cntInef3Line = 0;
		
		try{
			
			//Inicio REO - 27.09.2017 - EV113345
			//IAppObjFacade riskFacade = this.environment.getAppObjFacade(ObjectType.RISK);
			IAppObjFacade riskFacade = FacadeFactory.getInstance().getAppObjFacade(this.jobCtx, ObjectType.RISK);
			IOVID riskOVID = riskObj.getVersionData().getHeadOVID();
			//Fim REO - 27.09.2017 - EV113345
			
			IAppObj riskUpdObj = riskFacade.load(riskOVID, true);
			riskFacade.allocateLock(riskOVID, LockType.FORCEWRITE);
			
			//IAppObjFacade controlFacade = this.environment.getAppObjFacade(ObjectType.CONTROL);
		
			List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getFullGrantUserContext());
			
			//Inicio Inclusão - REO - 14.02.2018 - EV1333332
			//RiskAndControlCalculation objCalc = new RiskAndControlCalculation(controlList);
			
			//Fim Inclusão - REO - 14.02.2018 - EV1333332
			
			//Inicio Exclusao - REO - 14.02.2018 - EV1333332
			//Alteração da logica do calculo de Risco Residual
			for(IAppObj controlObj : controlList){
				
				//REO 17.08.2017 - EV108436
				if(controlObj.getVersionData().isDeleted())
					continue;
				
				log.info("Controle Afetado: " + controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue());
				
				//IOVID controlOVID = controlObj.getVersionData().getHeadOVID();
				//IAppObj controlUpdObj = controlFacade.load(controlOVID, true);
				//controlFacade.allocateWriteLock(controlOVID);
				
				//List<IAppObj> tstDefList = controlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).getElements(this.getFullGrantUserContext());
				//String controlID = controlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
				//List<IAppObj> tstDefList = this.getTestDefFromControl(controlID);
				List<IAppObj> tstDefList = this.getTestDefFromControl(controlObj);
				for(IAppObj tstDefObj : tstDefList){
					
					if(tstDefObj.getObjectId() == this.tdObjectId){
						//if(this.sameRisk(this.formModel.getAppObj(), riskObj))
							continue;
					}
					
					//if(tstDefObj.getObjectId() == this.tdObjectId)
					//	continue;
					//
					//if(this.testDefClass(tstDefObj).equals("1linhadefesa"))
					//	cntTotal2Line += 1;
					//
					//if(this.testDefClass(tstDefObj).equals("2linhadefesa"))
					//	cntTotal3Line += 1;
					
					log.info(tstDefObj.getAttribute(ITestdefinitionAttributeType.ATTR_NAME).getRawValue());
					
					//log.info("Total Testes 2a Linha: " + String.valueOf(cntTotal2Line));
					//log.info("Total Testes 3a Linha: " + String.valueOf(cntTotal3Line));
					
					List<IAppObj> tstCaseList = this.getTestCaseFromTestDef(tstDefObj.getObjectId());
					for(IAppObj tstCaseObj : tstCaseList){
						
						IEnumAttribute ownerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
						IEnumerationItem ownerStatusItem = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
						IEnumAttribute reviewerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_REVIEWER_STATUS);
						IEnumerationItem reviewerStatusItem = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
						//log.info("Caso de Teste: " + tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_NAME).getRawValue());
						log.info("ID Caso de Teste: " + String.valueOf(tstCaseObj.getObjectId()));
						log.info("Efetividade do Teste: " + ownerStatusItem.getId());
						if(reviewerStatusItem.getId().equals("accepted")){
							if(this.testDefClass(tstDefObj).equals("1linhadefesa"))
								cntTotal2Line += 1;
								
							if(this.testDefClass(tstDefObj).equals("2linhadefesa"))
								cntTotal3Line += 1;
								
							if(ownerStatusItem.getId().equals("noneffective")){
								if(this.testDefClass(tstDefObj).equals("1linhadefesa")){
									cntInef2Line += 1;
									log.info("TC ineficaz 2 linha: " + String.valueOf(cntInef2Line));
								}
								if(this.testDefClass(tstDefObj).equals("2linhadefesa")){
									cntInef3Line += 1;
									log.info("TC ineficaz 3 linha: " + String.valueOf(cntInef3Line));
								}
								//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
							}else{
								//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
							}
							
							log.info("Total Testes 2a Linha: " + String.valueOf(cntTotal2Line));
							log.info("Total Testes 3a Linha: " + String.valueOf(cntTotal3Line));
						}
						//if(ownerStatusItem.getId().equals("new")){
						//	if(this.fernanda.equals("noneffective")){
						//		if(this.testDefClass(tstDefObj).equals("1linhadefesa")){
						//			cntInef2Line += 1;
						//			log.info("TC ineficaz 2 linha: " + String.valueOf(cntInef2Line));
						//		}
						//		if(this.testDefClass(tstDefObj).equals("2linhadefesa")){
						//			cntInef3Line += 1;
						//			log.info("TC ineficaz 3 linha: " + String.valueOf(cntInef3Line));
						//		}
						//		controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
						//	}else{
						//		controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
						//	}
						//}
					}
					
				}
				
			}
			
			String riskClassFinal = "";
			
			cntInef2Line = cntInef2Line + this.countInef2line;
			cntInef3Line = cntInef3Line + this.countInef3line;
			
			cntTotal2Line = cntTotal2Line + this.countTotal2line;
			cntTotal3Line = cntTotal3Line + this.countTotal3line;
			
			if(cntTotal2Line == 0)
				cntInef2Line = 0;
			
			if(cntTotal3Line == 0)
				cntInef3Line = 0;
			
			log.info("TC ineficaz 2 linha: " + String.valueOf(cntInef2Line));
			log.info("Total Testes 2a Linha: " + String.valueOf(cntTotal2Line));
			
			log.info("TC ineficaz 3 linha: " + String.valueOf(cntInef3Line));
			log.info("Total Testes 3a Linha: " + String.valueOf(cntTotal3Line));
			
			String riskClass2line = "";
			String riskClass3line = "";
			
			if(this.origemTeste.equals("1linhadefesa")){
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF2LINE).setRawValue(cntInef2Line);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL2LINE).setRawValue(cntTotal2Line);
				double risk2line = ( cntInef2Line / cntTotal2Line );
				log.info("Ponderacao 2 linha: " + String.valueOf(risk2line));
				riskClass2line = this.riskClassification(risk2line);
				if(!riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).isEmpty()){
					riskClass3line = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue();
				}
				log.info("Classificacao 2 linha: " + riskClass2line);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).setRawValue(riskClass2line);
				log.info("Classificacao 2 linha - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue());
				//riskClassFinal = riskClass2line;
				//riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
				log.info("Classificacao Final - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue());
			}
			
			if(this.origemTeste.equals("2linhadefesa")){
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_INEF3LINE).setRawValue(cntInef3Line);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_FINAL3LINE).setRawValue(cntTotal3Line);
				double risk3line = ( cntInef3Line / cntTotal3Line );
				log.info("Ponderacao 3 linha: " + String.valueOf(risk3line));
				riskClass3line = this.riskClassification(risk3line);
				if(!riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).isEmpty()){
					riskClass2line = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue();
				}
				log.info("Classificacao 3 linha: " + riskClass3line);
				riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).setRawValue(riskClass3line);
				log.info("Classificacao 3 linha - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue());
				//riskClassFinal = riskClass3line;
				//riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
				log.info("Classificacao Final - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue());
			}
			
			if(this.riskClass1Line.equals("")){
				if((!riskClass2line.equals("")) && (riskClass3line.equals(""))){
					riskClassFinal = this.riskFinalClassification("", riskClass2line, "");
				}else{
					if((riskClass2line.equals("")) && (!riskClass3line.equals(""))){
						riskClassFinal = this.riskFinalClassification("", "", riskClass3line);
					}else{
						riskClassFinal = this.riskFinalClassification("", riskClass2line, riskClass3line);
					}
				}
			}else{
				if((!riskClass2line.equals("")) && (riskClass3line.equals(""))){
					riskClassFinal = this.riskFinalClassification(this.riskClass1Line, riskClass2line, "");
				}else{
					if((riskClass2line.equals("")) && (!riskClass3line.equals(""))){
						riskClassFinal = this.riskFinalClassification(this.riskClass1Line, "", riskClass3line);
					}else{
						riskClassFinal = this.riskFinalClassification(this.riskClass1Line, riskClass2line, riskClass3line);
					}
				}
			}
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
			
			//String riskClass1line = riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).getRawValue();
			//if(riskClass1line == null)
			//	riskClass1line = "";
			//
			//double risk2line = ( cntInef2Line / cntTotal2Line );
			//log.info("Ponderacao 2 linha: " + String.valueOf(risk2line));
			//String riskClass2line = this.riskClassification(risk2line);
			//log.info("Classificacao 2 linha: " + riskClass2line);
			//riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).setRawValue(riskClass2line);
			//log.info("Classificacao 2 linha - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue());
			//
			//double risk3line = ( cntInef3Line / cntTotal3Line );
			//log.info("Ponderacao 3 linha: " + String.valueOf(risk3line));
			//String riskClass3line = this.riskClassification(risk3line);
			//log.info("Classificacao 3 linha: " + riskClass3line);
			//riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).setRawValue(riskClass3line);
			//log.info("Classificacao 3 linha - ATTR: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue());
			//
			//log.info("Calculando Amb Controle Final");
			//String riskClassFinal = this.riskFinalClassification(riskClass1line, riskClass2line, riskClass3line);
			//riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).setRawValue(riskClassFinal);
			//log.info("Classificacao Ctrl Final: " + riskClassFinal);
			//log.info("Amb Controle Final Calculado...");
			
			String riskResidual2Line = this.riskResidualFinal(this.riscoPotencial, riskClass2line);
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL2LINE).setRawValue(riskResidual2Line);
			
			String riskResidual3Line = this.riskResidualFinal(this.riscoPotencial, riskClass3line);
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUAL3LINE).setRawValue(riskResidual3Line);
			
			log.info("Calculando Residual Final");
			String riskResidualFinal = this.riskResidualFinal(this.riscoPotencial, riskClassFinal);
			riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).setRawValue(riskResidualFinal);
			log.info("Classificacao Res Final: " + riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue());
			log.info("Residual Final Calculado...");
			//Fim Exclusao - REO - 14.02.2018 - EV1333332
			
			log.info("Salvando Risco");
			riskFacade.save(riskUpdObj, this.getDefaultTransaction(), true);
			log.info("Risco Salvo: " + riskUpdObj.getAttribute(IRiskAttributeType.ATTR_RISK_ID).getRawValue());
			riskFacade.releaseLock(riskOVID);
			log.info("Risco Liberado: " + riskUpdObj.getAttribute(IRiskAttributeType.ATTR_RISK_ID).getRawValue());
			
		}
		catch(Exception e){
			log.info("Risco Exception: " + e.getMessage());
			throw e;
		}
		
	}
	
	private void controlClassification(List<IAppObj> controlList) throws Exception{
		
		//Inicio REO - 27.09.2017 - EV113345
		//IAppObjFacade controlFacade = this.environment.getAppObjFacade(ObjectType.CONTROL);
		IAppObjFacade controlFacade = FacadeFactory.getInstance().getAppObjFacade(this.jobCtx, ObjectType.CONTROL);
		//Fim REO - 27.09.2017 - EV113345
		
		IOVID ovid = null;
		try{
		
			Iterator itControl = controlList.iterator();
			while(itControl.hasNext()){
				
				IAppObj controlObj = (IAppObj)itControl.next();
				IOVID controlOVID = controlObj.getVersionData().getHeadOVID();
				ovid = controlOVID;
				IAppObj controlUpdObj = controlFacade.load(controlOVID, true);
				controlFacade.allocateLock(controlOVID, LockType.FORCEWRITE);
				
				//REO 08.08.2017 - EV108028
				//if(this.fernanda.equals("ineffective")){ 
				if(this.fernanda.equals("noneffective")){
					//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("ineficaz");
					if(this.origemTeste.equals("1linhadefesa")){
						controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE).setRawValue("inefetivo");
						this.setFinalControlStatus(controlUpdObj, "inefetivo");
					}
					if(this.origemTeste.equals("2linhadefesa")){
						controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE).setRawValue("inefetivo");
						this.setFinalControlStatus(controlUpdObj, "inefetivo");
					}
				}else{
					//controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).setRawValue("eficaz");
					if(this.origemTeste.equals("1linhadefesa")){
						controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_2LINE).setRawValue("efetivo");
						this.setFinalControlStatus(controlUpdObj, "efetivo");
					}
					if(this.origemTeste.equals("2linhadefesa")){
						controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_3LINE).setRawValue("efetivo");
						this.setFinalControlStatus(controlUpdObj, "efetivo");
					}
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
	
	private String testDefClass(IAppObj tstDefObj){
		
		IEnumAttribute origTestAttr = tstDefObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
		IEnumerationItem origTest = ARCMCollections.extractSingleEntry(origTestAttr.getRawValue(), true);
		
		return origTest.getId();
		
	}
	
	private void setFinalControlStatus(IAppObj controlUpdObj, String classification) {
		IStringAttribute stFinalAttr = controlUpdObj.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS_FINAL);
		if(stFinalAttr.isEmpty()){
			stFinalAttr.setRawValue(classification);
		}else{
			if(stFinalAttr.getRawValue().equals("efetivo")){
				stFinalAttr.setRawValue(classification);
			}
		}
	}
	
	private List<IAppObj> getTestCaseFromTestDef(IAppObj testDefObj) throws Exception{
		
		IAppObjFacade tcFacade = this.environment.getAppObjFacade(ObjectType.TESTCASE);
		IAppObjQuery tcQuery = tcFacade.createQuery();
		IAppObjIterator tcIterator = tcQuery.getResultIterator();
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		//log.info("Infra para obtenção de Test Case montada");
		
		while(tcIterator.hasNext()){
			
			IAppObj tcObj = tcIterator.next();
			//log.info("TC Lido: " + tcObj.getAttribute(ITestcaseAttributeType.ATTR_NAME).getRawValue());
			
			List<IAppObj> tdList = tcObj.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION).getElements(this.getFullGrantUserContext());
			for(IAppObj tdObj : tdList){
				if(tdObj.getGuid().equals(testDefObj.getGuid())){
					//log.info("TestCase Adicionado: " + tcObj.getAttribute(ITestcaseAttributeType.ATTR_NAME).getRawValue());
					testCaseBuffer.add(tcObj);
				}
			}
		}
		
		//Ordenação por data de criação dos Test Case
		testCaseBuffer.sort(new Comparator<IAppObj>(){
			@Override
			public int compare(IAppObj ant, IAppObj post){
				long antTime = ant.getVersionData().getCreateDate().getTime();
				long postTime = post.getVersionData().getCreateDate().getTime();
				return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
			}
		});
		
		testCaseReturn.add(testCaseBuffer.get(testCaseBuffer.size() - 1));
		tcQuery.release();
		
		return (List<IAppObj>) testCaseReturn;
		
		/*Map filterMap = new HashMap();
		
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		IAppObjFacade testCaseFacade = this.environment.getAppObjFacade(ObjectType.TESTCASE);
		
		IViewQuery query = QueryFactory.createQuery(this.getUserContext(), this.view_testcase, filterMap, null,
				true, this.getDefaultTransaction());
		try{
			
			Iterator it = query.getResultIterator();
			while(it.hasNext()){
				
				IViewObj viewObj = (IViewObj) it.next();
				Long obj_id = (Long) viewObj.getRawValue(this.view_testcase_obj_id);
				Long version_number = (Long) viewObj.getRawValue(this.view_testcase_version_number);
				
				IOVID testCaseOVID = OVIDFactory.getOVID(obj_id.longValue(), version_number.longValue());
				testCaseReturn.add(testCaseFacade.load(testCaseOVID, true));
				
			}
			query.release();
			return (List<IAppObj>)testCaseReturn;
			
		}catch(Exception e){
			throw e;
		}*/
		
		
	}
	
	private List<IAppObj> getTestCaseFromTestDef(long testDefObjID) throws Exception{
		
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("tdef_id", testDefObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.jobCtx/* this.getFullGrantUserContext() */, "custom_TestDef2TestCase", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long tcID = (Long)viewObj.getRawValue("tcase_id");
				long tcVersionNumber = (Long)viewObj.getRawValue("tcase_version_number");
				
				IAppObjFacade tcFacade = this.environment.getAppObjFacade(ObjectType.TESTCASE);
				IOVID tcOVID = OVIDFactory.getOVID(tcID, tcVersionNumber);
				IAppObj tcAppObj = tcFacade.load(tcOVID, true);
				
				if(tcAppObj != null){
					if(tcAppObj.getVersionData().isHeadRevision())
						testCaseBuffer.add(tcAppObj);
				}
				
			}
			
			if(testCaseBuffer.size() > 0){
				testCaseBuffer.sort(new Comparator<IAppObj>(){
					@Override
					public int compare(IAppObj ant, IAppObj post){
						long antTime = ant.getVersionData().getCreateDate().getTime();
						long postTime = post.getVersionData().getCreateDate().getTime();
						return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
					}
				});
				testCaseReturn.add(testCaseBuffer.get(testCaseBuffer.size() - 1));
			}
		
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}		
		
		return (List<IAppObj>) testCaseReturn;
		
	}
	
	private List<IAppObj> getTestDefFromControl(String controlID) throws Exception{
		
		List<IAppObj> testDefReturn = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("control_id", controlID);
		
		IViewQuery query = QueryFactory.createQuery(this.jobCtx/*this.getFullGrantUserContext()*/, "custom_control2testdef", filterMap, null,
				true, this.getDefaultTransaction());
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long tdID = (Long)viewObj.getRawValue("testdefinition_obj_id");
				long tdVersionNumber = (Long)viewObj.getRawValue("testdefinition_version_number");
				
				IAppObjFacade tdFacade = this.environment.getAppObjFacade(ObjectType.TESTDEFINITION);
				IOVID tdOVID = OVIDFactory.getOVID(tdID, tdVersionNumber);
				IAppObj tdAppObj = tdFacade.load(tdOVID, true);
				
				if(tdAppObj != null)
					testDefReturn.add(tdAppObj);
				
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}		
		
		return (List<IAppObj>) testDefReturn;
		
	}
	
	private List<IAppObj> getTestDefFromControl(IAppObj control) throws Exception{
		
		List<IAppObj> testDefReturn = new ArrayList<IAppObj>();
		//List<IAppObj> testDefBuffer = new ArrayList<IAppObj>();
		
		IAppObjFacade facade = this.environment.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery query = facade.createQuery();
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.ATTR_CONTROL_ID, control.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue()));
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
		
		IAppObjIterator itQuery = query.getResultIterator();
		
		try{
			
			while(itQuery.hasNext()){
								
				//IAppObjFacade tdFacade = this.environment.getAppObjFacade(ObjectType.TESTDEFINITION);
				IAppObj controlObj = itQuery.next();
				
				if(!controlObj.getVersionData().isHeadRevision())
					continue;
				
				List<IAppObj> tdList = controlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).getElements(getFullGrantUserContext());
				for(IAppObj tdObj : tdList){
					if(!tdObj.getVersionData().isDeleted())
						testDefReturn.add(tdObj);
					/*IOVID tdOVID = tdObj.getVersionData().getHeadOVID();
					IAppObj tdAppObj = tdFacade.load(tdOVID, true);
					if(tdAppObj != null){
						if(!tdAppObj.getVersionData().isDeleted())
							testDefReturn.add(tdAppObj);
					}*/
				}
				
			}
			
			/*if(testDefBuffer.size() > 0){
				testDefBuffer.sort(new Comparator<IAppObj>(){
					@Override
					public int compare(IAppObj ant, IAppObj post){
						long antTime = ant.getVersionData().getCreateDate().getTime();
						long postTime = post.getVersionData().getCreateDate().getTime();
						return antTime < postTime ? -1 : antTime == postTime ? 0 : 1;
					}
				});
				testDefReturn.add(testDefBuffer.get(testDefBuffer.size() - 1));
			}*/
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}		
		
		return (List<IAppObj>) testDefReturn;
		
	}		
	
	private String riskClassification(double riskVuln){
		
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixo";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "Médio";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alto";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alto";
		}
		
		return riskClassif;
		
	}
	
	private String riskFinalClassification(String risk1line, String risk2line, String risk3line) throws Exception{
		
		int height_1line = 0;
		int height_2line = 0;
		int height_3line = 0;
		String riskClassFinal = "";
		
		try{
			
			if(!risk1line.equals("")){
				//Classificação - Amb. Controles 1a Linha
				if(risk1line.equalsIgnoreCase("Muito Alto"))
					height_1line = 4;
				if(risk1line.equalsIgnoreCase("Alto"))
					height_1line = 3;
				if(risk1line.equalsIgnoreCase("Médio"))
					height_1line = 2;
				if(risk1line.equalsIgnoreCase("Baixo"))
					height_1line = 1;
				log.info("Height 1 Line: " + String.valueOf(height_1line));
			}
			
			
			
			if(!risk2line.equals("")){
				//Classificação - Amb. Controles 2a Linha
				if(risk2line.equalsIgnoreCase("Muito Alto"))
					height_2line = 4;
				if(risk2line.equalsIgnoreCase("Alto"))
					height_2line = 3;
				if(risk2line.equalsIgnoreCase("Médio"))
					height_2line = 2;
				if(risk2line.equalsIgnoreCase("Baixo"))
					height_2line = 1;
				log.info("Height 2 Line: " + String.valueOf(height_2line));
			}
			
			if(!risk3line.equals("")){
				//Classificação - Amb. Controles 3a Linha
				if(risk3line.equalsIgnoreCase("Muito Alto"))
					height_3line = 4;
				if(risk3line.equalsIgnoreCase("Alto"))
					height_3line = 3;
				if(risk3line.equalsIgnoreCase("Médio"))
					height_3line = 2;
				if(risk3line.equalsIgnoreCase("Baixo"))
					height_3line = 1;		
				log.info("Height 3 Line: " + String.valueOf(height_3line));
			}
			
			int maxHeightCtrl = Math.max(height_1line, Math.max(height_2line, height_3line));
			log.info("Height Max: " + String.valueOf(maxHeightCtrl));
			
			switch(maxHeightCtrl){
			case 4:
				riskClassFinal = "Muito Alto";
				break;
			case 3:
				riskClassFinal = "Alto";
				break;
			case 2:
				riskClassFinal = "Médio";
				break;
			case 1:
				riskClassFinal = "Baixo";
				break;
			default:
				riskClassFinal = "Não Avaliado";
				break;
			}
			
			log.info("Class Final Amb Contr: " + riskClassFinal);
			return riskClassFinal;
		
		}catch(Exception e){
			log.info("Risk Final Class Exception: " + e.getMessage());
			throw e;
		}
		
	}
	
	private String riskResidualFinal(String riskPotencial, String riskControlFinal){
		
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Nao Avaliado"))
			return "Não Avaliado";
			
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
		
		return riskResidualReturn;
		
	}
	
	private boolean sameRisk(IAppObj testCase, IAppObj risk){
		
		boolean isSame = false;
		
		List<IAppObj> listRisk = testCase.getAttribute(ITestcaseAttributeType.LIST_RISK).getElements(getFullGrantUserContext());
		for(IAppObj riskObj : listRisk){
			if(riskObj.getObjectId() == risk.getObjectId())
				isSame = true;
		}
		
		return isSame;
		
	}
	
	private void affectCorpRisk(IAppObj risk) throws Exception{
		
		try{
			//Inicio REO - 27.09.2017 - EV113345
			//IAppObjFacade crFacade = this.environment.getAppObjFacade(ObjectType.HIERARCHY);
			IAppObjFacade crFacade = FacadeFactory.getInstance().getAppObjFacade(this.jobCtx, ObjectType.HIERARCHY);
			//Fim REO - 27.09.2017 - EV113345
			
			List<IAppObj> corpRiskList = risk.getAttribute(IRiskAttributeType.LIST_RISK_CATEGORY).getElements(this.jobCtx);
			for(IAppObj corpRisk : corpRiskList){
				
				if(corpRisk.getVersionData().isDeleted())
					continue;
				
				if(corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_CORPRISK).getRawValue() != null){
					if(corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_CORPRISK).getRawValue()){
						crFacade.allocateLock(corpRisk.getVersionData().getHeadOVID(), LockType.FORCEWRITE);
						CustomCorpRiskHierarchy crHierarchy = new CustomCorpRiskHierarchy(corpRisk, this.jobCtx, this.getDefaultTransaction());
						//Inicio REO - 17.01.2018 - EV124200
						//String ret = crHierarchy.calculateResidualCR();
						String ret = "";
						try{
							ret = crHierarchy.calculateResidualCR();
						}catch(CustomCorpRiskException ex){
							continue;
						}
						//Fim REO - 17.01.2018 - EV124200
						if(ret != null || (!ret.equals(""))){
							corpRisk.getAttribute(IHierarchyAttributeTypeCustom.ATTR_RESIDUAL).setRawValue(ret);
							crFacade.save(corpRisk, this.getDefaultTransaction(), true);
							crFacade.releaseLock(corpRisk.getVersionData().getHeadOVID());
						}
					}
				}
			}
		}catch(Exception e){
			throw e;
		}
		
	}
	
	//Inicio REO - 27.09.2017 - EV113345
	private void addUserRights(){
		JobUIEnvironment jobEnv = new JobUIEnvironment(getFullGrantUserContext());
		this.jobCtx = jobEnv.getUserContext();
	}
	//Fim REO - 27.09.2017 - EV113345
	
	
/*		
		
		IAppObj currAppObj = this.formModel.getAppObj();
		IUIEnvironment currEnv = this.environment;
		String currCtrlParentName = this.parentControl(currAppObj);
		
		IEnumAttribute ra_controlresattr = currAppObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
		IEnumerationItem ra_controlresitem = ARCMCollections.extractSingleEntry(ra_controlresattr.getRawValue(), true);
		String ra_controlres = ra_controlresitem.getId();		
		log.info("ra_controlres: " + ra_controlres);
		
		this.defineOrigemTeste(currAppObj);
		this.setRiskVuln(currAppObj);
		log.info("origem teste: " + this.getOrigemTeste());
		
		
		
		try{
			
			IAppObjFacade tcFacade = currEnv.getAppObjFacade(ObjectType.TESTCASE);
			IAppObjQuery tcQuery = tcFacade.createQuery();
			IAppObjIterator tcIterator = tcQuery.getResultIterator();
			
			while(tcIterator.hasNext()){
				
				IAppObj tcAppObj = tcIterator.next();
				IOVID tcOVID = tcAppObj.getVersionData().getHeadOVID();
				IAppObj tcUpdObj = tcFacade.load(tcOVID, true);
				
				if(tcUpdObj.getGuid().equals(currAppObj.getGuid()))
					continue;
				
				String ceCtrlParentName = this.parentControl(tcUpdObj);
				
				if(!(currCtrlParentName.equals(ceCtrlParentName)))
					continue;				
				
				this.copyEditableAttr(currAppObj, tcUpdObj);
				tcFacade.save(tcAppObj, this.getDefaultTransaction(), true);
				tcFacade.releaseLock(tcOVID);
				
				List<IAppObj> tcCtrlList = tcUpdObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
				for(IAppObj tcCtrlObj : tcCtrlList){
					log.info("gravando riscos...");
					this.affectResidualRisk(currEnv, tcCtrlObj, ra_controlres);
				}
				
			}
			tcQuery.release();
			
			List<IAppObj> currCtrlList = currAppObj.getAttribute(IControlexecutionAttributeType.LIST_CONTROL).getElements(this.getUserContext());
			for(IAppObj currCtrlObj : currCtrlList){
				this.affectResidualRisk(currEnv, currCtrlObj, ra_controlres);
			}
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}
		
	}
	
	private void setOrigemTeste(String origemTeste){
		this.origemTeste = origemTeste;
	}
	
	private String getOrigemTeste(){
		return this.origemTeste;
	}
	
	private void defineOrigemTeste(IAppObj currObj){
		
		List<IAppObj> testDefList = currObj.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION).getElements(this.getUserContext());
		for(IAppObj testDefObj : testDefList){
			IEnumAttribute origAttr = testDefObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
			IEnumerationItem origItem = ARCMCollections.extractSingleEntry(origAttr.getRawValue(), true);
			String origemTeste = origItem.getId();
			this.setOrigemTeste(origemTeste);
		}
		
	}
	
	private String parentControl(IAppObj childAppObj){
		
		String parentControlName = "";
		
		List<IAppObj> controlList = childAppObj.getAttribute(ITestcaseAttributeType.LIST_CONTROL).getElements(this.getUserContext());
		for(IAppObj controlObj : controlList){
			parentControlName = controlObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue();
		}
		
		return parentControlName;
		
	}
	
	private void copyEditableAttr(IAppObj sourceObj, IAppObj targetObj){
		
		List<IAttribute> attrList = sourceObj.getEditableAttributes(this.getUserContext());
		for(IAttribute attr : attrList){
			log.info(attr.getAttributeType().getId());
			AppObjUtility.copyAttributeValue(sourceObj, targetObj, attr.getAttributeType());
		}
		
	}
	
	private void affectResidualRisk(IUIEnvironment currEnv, IAppObj controlAppObj, String testResult) throws Exception{
		
		IAppObjFacade riskFacade = currEnv.getAppObjFacade(ObjectType.RISK);
		IAppObjQuery riskQuery = riskFacade.createQuery();
		IAppObjIterator riskIterator = riskQuery.getResultIterator();
		
		log.info("Resultado Controle: " + testResult);
		
		//String controlID = controlAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
		//log.info("Parametro ControlID: " + controlID);
		
		try{
		
			while(riskIterator.hasNext()){
				
				IAppObj riskObj = riskIterator.next();
				IOVID riskOVID = riskObj.getVersionData().getHeadOVID();
				IAppObj riskUpdObj = riskFacade.load(riskOVID, true);
				riskFacade.allocateWriteLock(riskOVID);
				
				String riskClass2line = this.riskClassification(this.getRiskVuln2Line());
				String riskClass3line = this.riskClassification(this.getRiskVuln3Line());
				List<IAppObj> ctrlList = riskUpdObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(this.getFullGrantUserContext());
				for(IAppObj ctrlObj : ctrlList){
					String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
					//log.info("Parametro CtrlID: " + controlID);
					if(ctrlID.equals(controlID)){
						if(this.getOrigemTeste().equals("1linhadefesa")){
							riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).setRawValue(testResult);
						}
						if(this.getOrigemTeste().equals("2linhadefesa")){
							riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).setRawValue(testResult);
						}
					}
				}
				//log.info(riskUpdObj);
				
				if(this.getOrigemTeste().equals("1linhadefesa")){
					riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).setRawValue(riskClass2line);
				}
				if(this.getOrigemTeste().equals("2linhadefesa")){
					riskUpdObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).setRawValue(riskClass3line);
				}
				
				riskFacade.save(riskUpdObj, this.getDefaultTransaction(), true);
				riskFacade.releaseLock(riskOVID);
			}
			riskQuery.release();
		
		}catch(Exception e){
			throw e;
		}
		
	}
	
	private void setRiskVuln(IAppObj currObj){
		
		List<IAppObj> testDefList = currObj.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION).getElements(this.getUserContext());
		double count2line = 0;
		double count3line = 0;
		double countTotal = 0;
		for(IAppObj testDefObj : testDefList){
			IEnumAttribute origAttr = testDefObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
			IEnumerationItem origItem = ARCMCollections.extractSingleEntry(origAttr.getRawValue(), true);
			String origemTeste = origItem.getId();
			countTotal += 1;
			//this.setOrigemTeste(origemTeste);
			if(origemTeste.equals("1linhadefesa"))
				count2line += 1;
			if(origemTeste.equals("2linhadefesa"))
				count3line += 1;
		
		}
		
		this.riskVuln2line = (count2line / countTotal);
		this.riskVuln3line = (count3line / countTotal);
		
	}
	
	private double getRiskVuln2Line(){
		return this.riskVuln2line;
	}
	
	private double getRiskVuln3Line(){
		return this.riskVuln3line;
	}
	
	private String riskClassification(double riskVuln){
		
		String riskClassif = "";
		
		if( (riskVuln >= 0.00) && (riskVuln <= 0.19) ){
			riskClassif = "Baixa";
		}
		
		if( (riskVuln >= 0.20) && (riskVuln <= 0.49) ){
			riskClassif = "Média";
		}
		
		if( (riskVuln >= 0.50) && (riskVuln <= 0.69) ){
			riskClassif = "Alta";
		}
		
		if( (riskVuln >= 0.70) && (riskVuln <= 1.00) ){
			riskClassif = "Muito Alta";
		}
		
		return riskClassif;
		
	}*/

}
