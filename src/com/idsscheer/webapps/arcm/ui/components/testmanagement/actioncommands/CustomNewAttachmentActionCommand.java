package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseNewAttachmentActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.IUIEnvironment;

public class CustomNewAttachmentActionCommand extends BaseNewAttachmentActionCommand {
	
	final Logger log = Logger.getLogger(CustomAttachmentActionCommand.class.getName());
	private List<IAppObj> cloneObjList = new ArrayList<IAppObj>();

	public CustomNewAttachmentActionCommand() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	protected boolean beforeExecute() {
		return super.beforeExecute();
	}
	
	protected void execute() {
		super.execute();
	}
	
	protected void lookupClientAttribute() {
		super.execute();
	}
	
	protected void setNavigatingRequestParameter() {
		super.setNavigatingRequestParameter();
	}
	
	protected void afterExecute() {
		
		this.createObjCopy();		
		super.afterExecute();
	}
	
	protected IAppObj create() {
		return super.create();
	}
	
	private void createObjCopy(){
		
		IAppObj currAppObj = this.formModel.getAppObj();
		IUIEnvironment currEnv = this.environment;
		String currID = currAppObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();
		
		try{
			if(currAppObj.getObjectType().equals(ObjectType.CONTROL)){
				this.setTestDef2Control(currAppObj, currEnv, currID);
				this.setCETask2Control(currAppObj, currEnv, currID);
			}
			/*if(currAppObj.getObjectType().equals(ObjectType.CONTROLEXECUTIONTASK)){
				this.setCETask2Control(currAppObj, currEnv, currID);
			}*/
		}catch(Exception e){
			this.formModel.addControlInfoMessage(NotificationTypeEnum.INFO, e.getMessage(), new String[] { getStringRepresentation(this.formModel.getAppObj()) });
		}		
		
	}
	
private void setTestDef2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		//IUserContext userCtx = this.getUserContext();
		
		IAppObjFacade cloneFacade = currEnv.getAppObjFacade(ObjectType.TESTDEFINITION);
		IListAttribute currTestDef = currAppObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS);
		
		try{
			
			if(currTestDef.getSize() > 0){
				
				List<IAppObj> currTestList = currTestDef.getElements(this.getUserContext());
				this.cloneObjs(currTestList, cloneFacade);
				List<IAppObj> cloneTestList = this.getCloneObjs();
		
				while(ctrlIterator.hasNext()){
					
					IAppObj ctrlAppObj = ctrlIterator.next();
					IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
					IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
					
					String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
					
					if(ctrlID.equals(currID)){
											
						if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
							continue;
						
						//Replicando "Defini��o de Testes"
						//IListAttribute currTestDef = currAppObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS);
						//ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).removeAllElements(this.getUserContext());
						//if(currTestDef.getSize() > 0){
													
						/*for(IAppObj currTestObj : currTestList){
								IAppObj cloneTestObj = cloneFacade.duplicate(currTestObj, this.getDefaultTransaction());
								IAppObj cloneTestObj = cloneFacade.create();
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_RELATED_CLIENT);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_NAME);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_TESTINGSTEPS);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_TEST_NATURE);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_TESTTYPE);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.LIST_MANAGER_GROUP);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_TESTEXTEND);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.LIST_OWNER_GROUP);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.LIST_REVIEWER_GROUP);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_FREQUENCY);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_EVENT_DRIVEN_ALLOWED);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_DURATION);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_STARTDATE);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_ENDDATE);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.LIST_ORGUNIT);
								AppObjUtility.copyAttributeValue(currTestObj, cloneTestObj, ITestdefinitionAttributeType.ATTR_ISFOLLOWUP);
								
								//cloneTestList.add(cloneTestObj);
								cloneFacade.save(cloneTestObj, this.getDefaultTransaction(), true);
								cloneFacade.releaseLock(cloneTestObj);
								
								ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).addLastElement(cloneTestObj, this.getUserContext());
								
							}
							
							//ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).addElements(cloneTestList, this.getUserContext());
						}*/
						
						//Replicando Defini��es de Teste Anexas
						ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).removeAllElements(this.getUserContext());
						ctrlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).addElements(cloneTestList, this.getUserContext());
						
						//Salvando a replica��o
						ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
						ctrlFacade.releaseLock(ctrlOVID);
						
					}
				}
				ctrlQuery.release();
			
			}
		
		}catch(Exception e){
			throw e;
		}
	}
	
	private void setCETask2Control(IAppObj currAppObj, IUIEnvironment currEnv, String currID) throws Exception{
		IAppObjFacade ctrlFacade = currEnv.getAppObjFacade(ObjectType.CONTROL);
		IAppObjQuery ctrlQuery = ctrlFacade.createQuery();
		IAppObjIterator ctrlIterator = ctrlQuery.getResultIterator();
		
		IAppObjFacade cloneFacade = currEnv.getAppObjFacade(ObjectType.CONTROLEXECUTIONTASK);
		IListAttribute currCETask = currAppObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS);
		
		try{
			
			if(currAppObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).getSize() > 0){
				
				List<IAppObj> currCETList = currCETask.getElements(this.getUserContext());
				this.cloneObjs(currCETList, cloneFacade);
				List<IAppObj> cloneCETList = this.getCloneObjs();
		
				while(ctrlIterator.hasNext()){
					
					IAppObj ctrlAppObj = ctrlIterator.next();
					IOVID ctrlOVID = ctrlAppObj.getVersionData().getHeadOVID();
					IAppObj ctrlObj = ctrlFacade.load(ctrlOVID, this.getDefaultTransaction(), true);
					
					String ctrlID = ctrlObj.getAttribute(IControlAttributeType.ATTR_CONTROL_ID).getRawValue();				
					
					if(ctrlID.equals(currID)){
											
						if(ctrlObj.getGuid().equals(currAppObj.getGuid()))
							continue;
						
						//Replicando "Control Execution Task"
						//IListAttribute currCETask = currAppObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS);
						/*ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).removeAllElements(this.getUserContext());
						if(currCETask.getSize() > 0){
							List<IAppObj> currCETList = currCETask.getElements(this.getUserContext());
							List<IAppObj> cloneCETList = new ArrayList<IAppObj>();
							
							for(IAppObj currCETObj : currCETList){
								IAppObj cloneCETObj = cloneFacade.duplicate(currCETObj, this.getDefaultTransaction());
								
								cloneCETList.add(cloneCETObj);
								cloneFacade.save(cloneCETObj, this.getDefaultTransaction(), true);
								cloneFacade.releaseLock(cloneCETObj);
							}
							
							ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).addElements(cloneCETList, this.getFullGrantUserContext());

						}*/
						ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).removeAllElements(this.getUserContext());
						ctrlObj.getAttribute(IControlAttributeType.LIST_CONTROLEXECUTIONTASKS).addElements(cloneCETList, this.getFullGrantUserContext());
						
						//Salvando a replica��o
						ctrlFacade.save(ctrlObj, this.getDefaultTransaction(), true);
						ctrlFacade.releaseLock(ctrlOVID);
						
					}
				}
				ctrlQuery.release();
				
			}
		
		}catch(Exception e){
			throw e;
		}		
	}
	
	private void cloneObjs(List<IAppObj> sourceObjList, IAppObjFacade targetFacade) throws Exception{
		
		try{
			for(IAppObj sourceObj : sourceObjList){
				//log.info(sourceObj.getAttribute(ITestdefinitionAttributeType.ATTR_NAME).getRawValue());
				IAppObj cloneObj = targetFacade.duplicate(sourceObj, this.getDefaultTransaction());	
				
/*				IAppObj cloneObj = targetFacade.create();
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_RELATED_CLIENT);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_NAME);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeTypeCustom.ATTR_ORIGEMTESTE);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_TESTINGSTEPS);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_TEST_NATURE);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_TESTTYPE);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.LIST_MANAGER_GROUP);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_TESTEXTEND);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.LIST_OWNER_GROUP);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.LIST_REVIEWER_GROUP);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_FREQUENCY);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_EVENT_DRIVEN_ALLOWED);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_DURATION);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_STARTDATE);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_ENDDATE);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_CONTROL_PERIOD_OFFSET);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.LIST_ORGUNIT);
				AppObjUtility.copyAttributeValue(sourceObj, cloneObj, ITestdefinitionAttributeType.ATTR_ISFOLLOWUP);*/				
				
				targetFacade.save(cloneObj, this.getDefaultTransaction(), true);
				targetFacade.releaseLock(cloneObj);
				
				this.cloneObjList.add(cloneObj);
			}
			
		}catch(Exception e){
			throw e;
		}
	}
	
	private ArrayList<IAppObj> getCloneObjs(){
		return (ArrayList<IAppObj>) this.cloneObjList;
	}	

}
