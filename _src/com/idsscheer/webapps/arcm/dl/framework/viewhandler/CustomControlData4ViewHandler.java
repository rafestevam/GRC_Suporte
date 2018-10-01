package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IQueryObjectDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.IFilterFactory;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;

public class CustomControlData4ViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter, List<IFilterCriteria> filters,
			IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
		
		IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.RISK);
		IAppObjQuery appQuery = facade.createQuery();
		
		Date criteriaDate = Calendar.getInstance().getTime();
		
		for (IFilterCriteria filter : filters) {
			
			for (IFilterCriteria filter2 : filter.getFilters()) {
//				appQuery.addRestriction(QueryRestriction.le(IControlAttributeType.BASE_ATTR_CHANGE_DATE, filter2.getValue()));
				//System.out.println(filter2.getValue());
				criteriaDate = (Date)filter2.getValue();
				criteriaDate.setHours(23);
				criteriaDate.setMinutes(59);
				criteriaDate.setSeconds(59);
			}
		}
		appQuery.setHeadRevisionsOnly(false);
		appQuery.setIncludeDeletedObjects(true);
		appQuery.addRestriction(QueryRestriction.eq(IRiskAttributeTypeCustom.ATTR_RISK_ID, "RP_10"));
		
		appQuery.addOrder(QueryOrder.descending(IRiskAttributeType.BASE_ATTR_VERSION_NUMBER));
		
		IAppObjIterator itResult = appQuery.getResultIterator();
		Map<IAppObj, List<IAppObj>> hashedMap = new HashMap<>();
		
		while(itResult.hasNext()){
			
			int frequency = 0;
			IAppObj riskObj = itResult.next();
			
			long riskDate = riskObj.getAttribute(IRiskAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue().getTime();
			if(riskDate > criteriaDate.getTime())
				continue;
			
			for(Map.Entry<IAppObj, List<IAppObj>> entry : hashedMap.entrySet()){
				if(entry.getKey().getObjectId() == riskObj.getObjectId())
					frequency += 1;
			}
			
			if(frequency > 0)
				continue;
			
			List<IAppObj> controlList = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx);
			List<IAppObj> cfList = new ArrayList<>();
			for (IAppObj controlObj : controlList) {
//				long controlDate = controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue().getTime();
//				if(controlDate > criteriaDate.getTime())
//					continue;
					
				cfList.add(controlObj);
				
			}
			hashedMap.put(riskObj, cfList);
			
		}
		
		appQuery.release();
		
		if (hashedMap.size() > 0) {

			// Limpando query original
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			 filters.clear();

			// Criando nova query dinamicamente
				List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
				IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
				for (Map.Entry<IAppObj, List<IAppObj>> entry : hashedMap.entrySet()) {
					//IFilterCriteria criteria = null;
					for(IAppObj cfObj : entry.getValue()){
						IFilterCriteria criteria = filterFactory
								.and(Arrays.asList(new IFilterCriteria[] {
										new SimpleFilterCriteria("risk_obj_id", "risk_obj_id", DataLayerComparator.EQUAL,
												entry.getKey().getObjectId()),
										new SimpleFilterCriteria("risk_version_number", "risk_version_number", DataLayerComparator.EQUAL,
												entry.getKey().getAttribute(IRiskAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue()),
										new SimpleFilterCriteria("control_obj_id", "control_obj_id", DataLayerComparator.EQUAL,
												cfObj.getObjectId()),
										new SimpleFilterCriteria("control_version_number", "control_version_number", DataLayerComparator.EQUAL,
												cfObj.getAttribute(IControlAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue())
						}));
						filtersAnd.add(criteria);
					}

				}			 
				
			query.addFilterCriteria(filterFactory.or(filtersAnd));

		}

	}

}
