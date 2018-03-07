package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.aris.arcm.ui.framework.support.converter.DateConverter;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.ILongAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IQueryRestriction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerTransaction;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IQueryObjectDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.IFilterFactory;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;
import com.idsscheer.webapps.arcm.ndl.PersistenceException;

public class CustomControlData3ViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter,
			List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery)
					throws BusViewException {
		// TODO Auto-generated method stub

		
		Map<Integer, Integer> resultMap = new TreeMap<>();

		IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROL);

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
//		appQuery.addRestriction(QueryRestriction.eq(IControlAttributeTypeCustom.ATTR_CONTROL_ID, "CO_125"));
		
		
		appQuery.addOrder(QueryOrder.descending(IControlAttributeType.BASE_ATTR_VERSION_NUMBER));
//		appQuery.addOrder(QueryOrder.ascending(IControlAttributeType.BASE_ATTR_OBJ_ID));

			
//		appQuery.addRestriction(
//				QueryRestriction.or(
//						QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, true), 
//						QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, false)));
		
		IAppObjIterator iteratorQueryResult = appQuery.getResultIterator();
		TreeMap<Long, Long> hashedMap = new TreeMap<>();
		try{
		while (iteratorQueryResult.hasNext()) {
//			System.out.println("IAppObj controlObj = iteratorQueryResult.next()");
			IAppObj controlObj = iteratorQueryResult.next();
			
//			System.out.println("System.out.println(controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue())");
			System.out.println(controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue());
			
//			System.out.println("long controlDate = controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue().getTime()");
			long controlDate = controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue().getTime();
			
//			System.out.println("if(controlDate > criteriaDate.getTime())");
			if(controlDate > criteriaDate.getTime())
				continue;
			
//			System.out.println("if(!controlObj.getVersionData().isDeleted())");
//			if(!controlObj.getVersionData().isDeleted()) {
//				Long maxVersionNumber = PersistenceAPI.getPersistenceManager().getMaxVersionNumber(issueObj.getVersionData().getOVID(), transaction);
//				Integer obj_id = (int) issueObj.getObjectId();
//				if (controlObj.getRawValue(IControlAttributeTypeCustom.ATTR_CONTROL_ID).equals("CO_125")) {
//					int teste = 0;
//					teste = teste + 1;
//				}
//				System.out.println("if (!hashedMap.containsKey(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID)))");
				if (!hashedMap.containsKey(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID))) {
//					System.out.println("hashedMap.put(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID)");
					hashedMap.put(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID), 
							 controlObj.getRawValue(IControlAttributeType.BASE_ATTR_VERSION_NUMBER));
				}
//			}
		}
		}catch(Exception e){
			throw new RuntimeException(e);
		}

//		if (null == restrAnd) {
//			IQueryExpression restr1 = QueryRestriction.eq(IControlAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
//			appQuery.addRestriction(restr1);
//		}
		
		IDataLayerTransaction transaction;
		try {
			transaction = PersistenceAPI.getTransactionFactory().createTransaction();
		} catch (PersistenceException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}

//		IAppObjIterator iterator = appQuery.getResultIterator();
//		while (iterator.hasNext()) {
//			IAppObj controlObj = iterator.next();
//			try {
//				Long maxVersionNumber = PersistenceAPI.getPersistenceManager().getMaxVersionNumber(controlObj.getVersionData().getOVID(), transaction);
//				Integer obj_id = (int) controlObj.getObjectId();
///*				Integer version_number = Integer.valueOf(
//						issueObj.getAttribute(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue().intValue());*/
//				resultMap.put(obj_id, maxVersionNumber.intValue());
//			} catch (PersistenceException e) {
//				// TODO Auto-generated catch block
//				throw new RuntimeException(e);
//			}
//		}
		appQuery.release();

//		printMap(resultMap);

		if (hashedMap.size() > 0) {

			// Limpando query original
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			 filters.clear();

			// Criando nova query dinamicamente
			List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			for (Map.Entry<Long, Long> entry : hashedMap.entrySet()) {
				IFilterCriteria criteria = filterFactory
						.and(Arrays.asList(new IFilterCriteria[] {
								new SimpleFilterCriteria("c_id", "c_id", DataLayerComparator.EQUAL,
										entry.getKey()),
								new SimpleFilterCriteria("c_version_number", "c_version_number", DataLayerComparator.EQUAL,
										entry.getValue()) }));
				filtersAnd.add(criteria);
			}
			
//			IFilterCriteria criteria2 = filterFactory
//					.or(Arrays.asList(new IFilterCriteria[] {
//							new SimpleFilterCriteria("r_version_active", "r_version_active", DataLayerComparator.EQUAL,
//									true),
//							new SimpleFilterCriteria("r_version_active", "r_version_active", DataLayerComparator.EQUAL,
//									false) }));
			
//			query.addFilterCriteria(new SimpleFilterCriteria("deactivated", 0));	
			query.addFilterCriteria(filterFactory.or(filtersAnd));
//			query.addFilterCriteria(criteria2);

		}
		// Fim REO - 29.01.2017 - EV131567

	}

//	private static void printMap(Map<Integer, Integer> map) {
//		for (Entry<Long, Long> entry : map.entrySet()) {
//			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
//		}
//	}

}
