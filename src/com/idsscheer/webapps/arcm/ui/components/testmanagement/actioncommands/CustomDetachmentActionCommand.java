package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseDetachmentActionCommand;

public class CustomDetachmentActionCommand extends BaseDetachmentActionCommand {
	
	protected void execute() {
		super.execute();
	}
	
	protected void afterExecute(){
		//this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, "attachment", new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		try{
			double inef1line = 0;
			double total1line = 0;
			double inef2line = 0;
			double total2line = 0;
			double inef3line = 0;
			double total3line = 0;
			Map<String,Double> mapTestLine = new HashMap<String,Double>();
			Map<String,String> mapClassCtrl = new HashMap<String,String>();
			
			IListAttribute ctrlList = this.appObj.getAttribute((IListAttributeType)lookupAttributeType(this.attributeType));
			for(IAppObj controlObj : ctrlList.getElements(getFullGrantUserContext())){
				
				//REO 17.08.2017 - EV108436
				if(controlObj.getVersionData().isDeleted())
					continue;
				
				//Contagem - Testes 1a Linha
				total1line += 1;
				inef1line += this.getInef1Line(controlObj);
				
				mapTestLine = this.getInef2and3Line(controlObj);
				
				//Contagem - Testes 2a Linha
				total2line += mapTestLine.get("total2line");
				inef2line += mapTestLine.get("inef2line");
				
				//Contagem - Testes 3a Linha
				total3line += mapTestLine.get("total3line");
				inef3line += mapTestLine.get("inef3line");
				
			}
			
			if(total1line == 0)
				inef1line = 0;
			
			if(total2line == 0)
				inef2line = 0;
			
			if(total3line == 0)
				inef3line = 0;
			
			double pond1line = inef1line / total1line;
			double pond2line = inef2line / total2line;
			double pond3line = inef3line / total3line;
			
			mapClassCtrl = this.getControlClassification(pond1line, pond2line, pond3line);	
			
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.ERROR, "attachment", new String[] { e.getLocalizedMessage() });
		}

	}
	
	private Map<String,String> getControlClassification(double pond1line, double pond2line, double pond3line){
		Map<String,String> mapReturn = new HashMap<String,String>();
		
		String riskClass1line = this.riskClassification(pond1line);
		String riskClass2line = this.riskClassification(pond2line);
		String riskClass3line = this.riskClassification(pond3line);
		
		String riskClassFinal = this.riskFinalClassification(riskClass1line, riskClass2line, riskClass3line);
		
		mapReturn.put("control1line", riskClass1line);
		mapReturn.put("control2line", riskClass2line);
		mapReturn.put("control3line", riskClass3line);
		mapReturn.put("controlfinal", riskClassFinal);
		
		return mapReturn;
	}
	
	private String riskResidualFinal(String riskPotencial, String riskControlFinal){
		
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Nao Avaliado"))
			return "N�o Avaliado";
			
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
	
	private Map<String,Double> getInef2and3Line(IAppObj controlObj) throws Exception{
		
		try{
			double cntTotal2Line = 0;
			double cntInef2Line = 0;
			double cntTotal3Line = 0;
			double cntInef3Line = 0;
		
			Map<String,Double> mapReturn = new HashMap<String,Double>();
			
			List<IAppObj> tstDefList = this.getTestDefFromControl(controlObj);
			for(IAppObj tstDefObj : tstDefList){
				List<IAppObj> tstCaseList = this.getTestCaseFromTestDef(tstDefObj.getObjectId());
				for(IAppObj tstCaseObj : tstCaseList){
					IEnumAttribute ownerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_OWNER_STATUS);
					IEnumerationItem ownerStatusItem = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
					IEnumAttribute reviewerStatusAttr = tstCaseObj.getAttribute(ITestcaseAttributeType.ATTR_REVIEWER_STATUS);
					IEnumerationItem reviewerStatusItem = ARCMCollections.extractSingleEntry(reviewerStatusAttr.getRawValue(), true);
					if(reviewerStatusItem.getId().equals("accepted")){
						if(this.testDefClass(tstDefObj).equals("1linhadefesa"))
							cntTotal2Line += 1;
							
						if(this.testDefClass(tstDefObj).equals("2linhadefesa"))
							cntTotal3Line += 1;
							
						if(ownerStatusItem.getId().equals("noneffective")){
							if(this.testDefClass(tstDefObj).equals("1linhadefesa")){
								cntInef2Line += 1;
							}
							if(this.testDefClass(tstDefObj).equals("2linhadefesa")){
								cntInef3Line += 1;
							}
						}
						
					}
				}
			}
			
			mapReturn.put("total2line", cntTotal2Line);
			mapReturn.put("inef2line", cntInef2Line);
			mapReturn.put("total3line", cntTotal3Line);
			mapReturn.put("inef3line", cntInef3Line);
			
			return mapReturn;
		}catch(Exception e){
			throw e;
		}
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
	
	private List<IAppObj> getTestCaseFromTestDef(long testDefObjID) throws Exception{
		
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("tdef_id", testDefObjID);
		
		IViewQuery query = QueryFactory.createQuery(this.getFullGrantUserContext(), "custom_TestDef2TestCase", filterMap, null,
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
	
	private String testDefClass(IAppObj tstDefObj){
		
		IEnumAttribute origTestAttr = tstDefObj.getAttribute(ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
		IEnumerationItem origTest = ARCMCollections.extractSingleEntry(origTestAttr.getRawValue(), true);
		
		return origTest.getId();
		
	}	
	
	private double getInef1Line(IAppObj controlObj) throws Exception{
		
		try{
			double count1line = 0;
			
			List<IAppObj> cetList = controlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).getElements(this.getUserContext());
			for(IAppObj cetObj : cetList){
				
				List<IAppObj> ceList = this.getCtrlExecFromCET(cetObj.getObjectId());
				for(IAppObj ceObj : ceList){
					IEnumAttribute ownerStatusAttr = ceObj.getAttribute(IControlexecutionAttributeType.ATTR_OWNER_STATUS);
					IEnumerationItem ownerStatus = ARCMCollections.extractSingleEntry(ownerStatusAttr.getRawValue(), true);
					IEnumAttribute statusAttr = ceObj.getAttribute(IControlexecutionAttributeTypeCustom.ATTR_CUSTOMCTRLEXECSTATUS);
					IEnumerationItem statusItem = ARCMCollections.extractSingleEntry(statusAttr.getRawValue(), true);
					if(ownerStatus.getId().equals("completed")){
						if(statusItem.getId().equals("ineffective")){
							count1line += 1;
						}
					}
				}
				
			}
			return count1line;
			
		}catch(Exception e){
			throw e;
		}
		
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
	
	/*
	private void detachTestDef2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		
		try{
		
			while(ctrlIterator.hasNext()){
				
				IAppObj ctrlAppObj = ctrlIterator.next();
				IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
				IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
				
				String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
				
				if(ctrlID.equals(currID)){
										
					if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
						continue;
					
					//Replicando dele��o da "Defini��o de Testes"
					ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).removeAllElements(this.getUserContext());
										
					//Salvando a replica��o
					ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
					ctrlFacade.releaseLock(ctrlOVID);
					
				}
			}
			ctrlQuery.release();		
		
		}catch(Exception e){
			throw e;
		}
	}
	
	private void detachCETask2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		
		try{
		
			while(ctrlIterator.hasNext()){
				
				IAppObj ctrlAppObj = ctrlIterator.next();
				IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
				IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
				
				String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
				
				if(ctrlID.equals(currID)){
										
					if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
						continue;
					
					//Replicando dele��o da "Defini��o de Testes"
					ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).removeAllElements(this.getUserContext());
										
					//Salvando a replica��o
					ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
					ctrlFacade.releaseLock(ctrlOVID);
					
				}
			}
			ctrlQuery.release();		
		
		}catch(Exception e){
			throw e;
		}
	}	*/

}
