package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
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
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IQueryObjectDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;

public class CustomControl2ControlExecution implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException  {
		// TODO Auto-generated method stub
		
		List<Long> ceIDList = new ArrayList<>();
		if(currentObject != null)
			ceIDList = getAllControlsFromOriginal(currentObject);
		
		if(currentObject == null)
			ceIDList = getAllControlsFromOriginal(filters);
		
		
		
		if(ceIDList.size() > 0){
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			
			filters.clear();
			filters.add(new SimpleFilterCriteria("obj_id", DataLayerComparator.IN, ceIDList));
		}
		
	}

	private List<Long> getAllControlsFromOriginal(IDataLayerObject currentObject) {
		
		List<Long> ceIDList = new ArrayList<>();
		
		try{
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade controlFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			IAppObjFacade ceFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROLEXECUTION);
		
			IAppObj origControl = controlFacade.load(currentObject.getHeadObjId(), true);
			String origControlID = origControl.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue();
			
			IAppObjQuery ceQuery = ceFacade.createQuery()
					.addRestriction(
							QueryRestriction.eq(IControlexecutionAttributeTypeCustom.ATTR_CONTROL_ID, origControlID)
//							QueryRestriction.and(
//									QueryRestriction.eq(IControlexecutionAttributeTypeCustom.ATTR_CONTROL_ID, origControlID),
//									QueryRestriction.eq(IControlexecutionAttributeType.ATTR_OWNER_STATUS, CONTROLEXECUTION_OWNER_STATUS.COMPLETED)
//							)
					);
					
			ceQuery.setHeadRevisionsOnly(true);
			ceQuery.setIncludeDeletedObjects(false);
			
			IAppObjIterator ceIterator = ceQuery.getResultIterator();
			while(ceIterator.hasNext()){
				
				IAppObj ceObj = ceIterator.next();
				ceIDList.add(ceObj.getObjectId());
				
			}
			
			ceQuery.release();
			
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{

		}
		
		return ceIDList;
	}
	
	private List<Long> getAllControlsFromOriginal(List<IFilterCriteria> filters) {
		
		List<Long> ceIDList = new ArrayList<>();
		
		try{
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			ITransaction transaction = TransactionManager.getInstance().createTransaction();
//			IAppObjFacade controlFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROL);
			Map filterMap = new HashMap<>();
			
//			IAppObjQuery controlQuery = controlFacade.createQuery();
			for(IFilterCriteria filter : filters){
				filterMap.put(filter.getAttributeAliasName(), filter.getValue());
//				if(filter.getAttributeAliasName().equals("control_name")) {
//					IQueryExpression restr = QueryRestriction.like(IControlAttributeType.ATTR_NAME, (String)filter.getValue());
//					controlQuery.addRestriction(restr);
//				}
			}
			
			filterMap.put("version_active", true);
			
			IViewQuery query = QueryFactory.createQuery(userCtx, "controlexecutiondata", filterMap, null, true, transaction);
			Iterator<IViewObj> iterator = query.getResultIterator();
			while(iterator.hasNext()){
				IViewObj viewObj = iterator.next();
				ceIDList.add(viewObj.getObjectId());
			}
			
			
//			controlQuery.setHeadRevisionsOnly(true);
//			controlQuery.setIncludeDeletedObjects(false);
			
//			List<String> controlIDList = new ArrayList<>(); 
//			IAppObjIterator controlIterator = controlQuery.getResultIterator();
//			while(controlIterator.hasNext()){
//				IAppObj controlObj = controlIterator.next();
//				controlIDList.add(controlObj.getAttribute(IControlAttributeTypeCustom.ATTR_CONTROL_ID).getRawValue());
//			}
//			controlQuery.release();
//			
//			IAppObjFacade ceFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROLEXECUTION);
//			
//			IAppObjQuery ceQuery = ceFacade.createQuery()
//					.addRestriction(
//							QueryRestriction.in(IControlexecutionAttributeTypeCustom.ATTR_CONTROL_ID, controlIDList)
////							QueryRestriction.and(
////									QueryRestriction.eq(IControlexecutionAttributeTypeCustom.ATTR_CONTROL_ID, origControlID),
////									QueryRestriction.eq(IControlexecutionAttributeType.ATTR_OWNER_STATUS, CONTROLEXECUTION_OWNER_STATUS.COMPLETED)
////							)
//							);
//			
//			ceQuery.setHeadRevisionsOnly(true);
//			ceQuery.setIncludeDeletedObjects(false);
//			
//			IAppObjIterator ceIterator = ceQuery.getResultIterator();
//			while(ceIterator.hasNext()){
//				
//				IAppObj ceObj = ceIterator.next();
//				ceIDList.add(ceObj.getObjectId());
//				
//			}
//			
//			ceQuery.release();
			
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			
		}
		
		return ceIDList;
	}

}

