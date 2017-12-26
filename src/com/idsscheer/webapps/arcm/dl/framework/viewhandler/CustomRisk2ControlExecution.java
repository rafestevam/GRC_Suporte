package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.dl.framework.BusException;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.TransactionFactory;

public class CustomRisk2ControlExecution implements IViewHandler{
	
	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException {
		
		List<Long> ctrlIDList = new ArrayList();
		
		try {
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			if(currentObject.getObjMetaData().getObjectType().getId().equalsIgnoreCase("RISK")){
				
				IAppObj rskAppObj = facade.load(currentObject.getHeadObjId(), true);
				
				List<IAppObj> listCtrlID = rskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx);
				for (IAppObj controlObj : listCtrlID) {
					if(controlObj.getVersionData().isHeadRevision())
						ctrlIDList.add(controlObj.getObjectId());
					
					/*String ctrlID = controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue();
					List<IAppObj> testDefList = this.getTestDefFromControl(userCtx, ctrlID);
					for (IAppObj tdAppObj : testDefList) {
						
						for(IAppObj tcAppObj : this.getTestCaseFromTestDef(tdAppObj.getObjectId(), userCtx)){
							if(tcAppObj.getVersionData().isHeadRevision())
								tcIDList.add(tcAppObj.getObjectId());
						}
						
					}*/
					
				}
				
				if(ctrlIDList.size() > 0)
					filters.add(new SimpleFilterCriteria("control_objId", DataLayerComparator.IN, ctrlIDList));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private List<IAppObj> getTestCaseFromTestDef(long objectId, IUserContext userCtx) throws Exception{
		// TODO Auto-generated method stub
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("tdef_id", objectId);
		
		ITransaction defaultTransaction = TransactionManager.getInstance().getReadOnlyTransaction();
		
		IViewQuery query = QueryFactory.createQuery(userCtx, "custom_TestDef2TestCase", filterMap, null,
				true, defaultTransaction);
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long tcID = (Long)viewObj.getRawValue("tcase_id");
				long tcVersionNumber = (Long)viewObj.getRawValue("tcase_version_number");
				
				IAppObjFacade tcFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.TESTCASE);
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

	private List<IAppObj> getTestDefFromControl(IUserContext userCtx, String ctrlID) throws Exception{
		// TODO Auto-generated method stub
		List<IAppObj> returnList = new ArrayList();
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROL);
		IAppObjQuery query = facade.createQuery();
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.ATTR_CONTROL_ID, ctrlID));
		query.addRestriction(
				QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, true));
		
		IAppObjIterator itQuery = query.getResultIterator();
		
		try{
			
			while(itQuery.hasNext()){
								
				IAppObj controlObj = itQuery.next();
				
				if(!controlObj.getVersionData().isHeadRevision())
					continue;
				
				List<IAppObj> tdList = controlObj.getAttribute(IControlAttributeType.LIST_TESTDEFINITIONS).getElements(userCtx);
				for(IAppObj tdObj : tdList){
					if(tdObj.getVersionData().isHeadRevision())
						returnList.add(tdObj);
				}
			}
			
		}catch(Exception e){
			throw e;
		}
		
		return returnList;
				
		
	}

/*	private List<IAppObj> getTestCaseFromControl(IUserContext userCtx, String objectId) throws Exception {
		// TODO Auto-generated method stub
		List<IAppObj> testCaseReturn = new ArrayList<IAppObj>();
		List<IAppObj> testCaseBuffer = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("control_ctrl_id", objectId);
		
		ITransaction defaultTransaction = TransactionManager.getInstance().getReadOnlyTransaction();
		
		IViewQuery query = QueryFactory.createQuery(userCtx, "control2testaction", filterMap, null,
				true, defaultTransaction);
		
		try{
		
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				
				IViewObj viewObj = (IViewObj)itQuery.next();
				long tcID = (Long)viewObj.getRawValue("ta_id");
				
				IAppObjFacade tcFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.TESTCASE);
				IOVID tcOVID = OVIDFactory.getOVID(tcID);
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
	}*/
	
	

}
