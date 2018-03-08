package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
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
		IAppObjFacade facadeRisk = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.RISK);

		IAppObjQuery appQueryControl = facade.createQuery();
		IAppObjQuery appQueryRisk = facadeRisk.createQuery();
		
		Date criteriaDate = Calendar.getInstance().getTime();
		
		for (IFilterCriteria filter : filters) {
			for (IFilterCriteria filter2 : filter.getFilters()) {
				criteriaDate = (Date)filter2.getValue();
				criteriaDate.setHours(23);
				criteriaDate.setMinutes(59);
				criteriaDate.setSeconds(59);
				
				appQueryControl.addRestriction(QueryRestriction.le(IControlAttributeType.BASE_ATTR_CHANGE_DATE, criteriaDate));
				appQueryRisk.addRestriction(QueryRestriction.le(IRiskAttributeType.BASE_ATTR_CHANGE_DATE, criteriaDate));
				//System.out.println(filter2.getValue());

			}
		}
		
		appQueryControl.setHeadRevisionsOnly(false);
		appQueryControl.setIncludeDeletedObjects(true);
		
		appQueryRisk.setHeadRevisionsOnly(false);
		appQueryRisk.setIncludeDeletedObjects(true);
		appQueryRisk.addRestriction(QueryRestriction.eq(IRiskAttributeType.ATTR_OBJ_ID, 14850));
		
		
		appQueryRisk.addOrder(QueryOrder.descending(IRiskAttributeType.BASE_ATTR_VERSION_NUMBER));
		appQueryControl.addOrder(QueryOrder.descending(IControlAttributeType.BASE_ATTR_VERSION_NUMBER));

		IAppObjIterator iteratorQueryResult = appQueryControl.getResultIterator();
		IAppObjIterator iteratorQueryRiskResult = appQueryRisk.getResultIterator();
		
		ArrayList<Long> listFlagRisks = new ArrayList<>();
		ArrayList<Long> validControls = new ArrayList<>();
		TreeMap<Long, Long> hashedMapControls = new TreeMap<>();
		
		try{
			while (iteratorQueryRiskResult.hasNext()) {
				// Objeto de risco.
				IAppObj riskObj = iteratorQueryRiskResult.next();
				
				// Pega o ID do risco corrente.
				Long currentRiskID = riskObj.getRawValue(IRiskAttributeType.BASE_ATTR_OBJ_ID);
				// Pega lista de controles da versão do risco corrente.
				List<IOVID> controls = riskObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getRawValue();
				
				
				// Verifica se os controles desta versão de risco já foram
				// contabilizados.
				if (!listFlagRisks.contains(currentRiskID)) {
					
					listFlagRisks.add(currentRiskID);
					
					// Cria Iterator da lista de controles deste risco.
					Iterator<IOVID> currRiskControlsIterator = controls.iterator();
					while (currRiskControlsIterator.hasNext()) {
						
						Long currentControlID = currRiskControlsIterator.next().getId();
						// Adiciona esse controle na lista de controles válidos.
						validControls.add(currentControlID);
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		try{
			while (iteratorQueryResult.hasNext()) {
				
				IAppObj controlObj = iteratorQueryResult.next();
				
//				long controlDate = controlObj.getAttribute(IControlAttributeType.BASE_ATTR_CHANGE_DATE).getRawValue().getTime();
				
	//			System.out.println("if(controlDate > criteriaDate.getTime())");
//				if(controlDate > criteriaDate.getTime())
//					continue;
				
				long controlId = controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID);
				
				if (!hashedMapControls.containsKey(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID))) {
					if (validControls.contains(controlId)) {
						hashedMapControls.put(controlObj.getRawValue(IControlAttributeType.BASE_ATTR_OBJ_ID), 
								 controlObj.getRawValue(IControlAttributeType.BASE_ATTR_VERSION_NUMBER));
					}
				}
	//			}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		
		
		IDataLayerTransaction transaction;
		
		try {
			transaction = PersistenceAPI.getTransactionFactory().createTransaction();
		} catch (PersistenceException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}

		appQueryControl.release();

		if (hashedMapControls.size() > 0) {

			// Limpando query original
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			filters.clear();

			// Criando nova query dinamicamente
			List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			for (Map.Entry<Long, Long> entry : hashedMapControls.entrySet()) {
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
