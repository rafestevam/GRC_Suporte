package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IQueryExpression;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IQueryRestriction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ITextAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IQueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IQueryObjectDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.IFilterFactory;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;

public class CustomIssues3ViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter,
			List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery)
					throws BusViewException {
		// TODO Auto-generated method stub

		// Inicio REO - 29.01.2017 - EV131567
		Map<Integer, Integer> resultMap = new TreeMap<>();
		// if (currentObject == null) {
		IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.ISSUE);

		IAppObjQuery appQuery = facade.createQuery();

		IQueryRestriction restrAnd = null;
		for (IFilterCriteria filter : filters) {
			//Restricao por ID de Apontamento/Plano Ação
			IQueryExpression restr1 = null;
			if (filter.getAttributeAliasName().equals("IssueID")) {
				restr1 = QueryRestriction.eq(IIssueAttributeType.ATTR_OBJ_ID, filter.getValue());
			}
			
			//Restricao por Titulo de Apontamento/Plano de Ação
			IQueryExpression restr2 = null;
			if (filter.getAttributeAliasName().equals("name")) {
				restr2 = QueryRestriction.like(IIssueAttributeType.ATTR_NAME, (String) filter.getValue());
			}
			
			//Adicionando as Restrições
			if(restr1 != null){
				IQueryExpression restrFim = QueryRestriction.eq(IIssueAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
				restrAnd = QueryRestriction.and(restr1, restrFim);
				appQuery.addRestriction(restrAnd);
			}
			if(restr2 != null){
				IQueryExpression restrFim = QueryRestriction.eq(IIssueAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
				restrAnd = QueryRestriction.and(restr2, restrFim);
				appQuery.addRestriction(restrAnd);
			}
			if(restr1 != null && restr2 != null){
				IQueryExpression restrFim = QueryRestriction.eq(IIssueAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
				restrAnd = QueryRestriction.and(restr1, restr2, restrFim);
				appQuery.addRestriction(restrAnd);
			}
			// appQuery.addRestriction(restr1);
		}
		// }

		if (null == restrAnd) {
			IQueryExpression restr1 = QueryRestriction.eq(IIssueAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
			appQuery.addRestriction(restr1);
		}

		IAppObjIterator iterator = appQuery.getResultIterator();
		while (iterator.hasNext()) {
			IAppObj issueObj = iterator.next();
			Integer obj_id = (int) issueObj.getObjectId();
			Integer version_number = Integer.valueOf(
					issueObj.getAttribute(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue().intValue());
			resultMap.put(obj_id, version_number);
		}
		appQuery.release();

		printMap(resultMap);

		if (resultMap.size() > 0) {

			// Limpando query original
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			// filters.clear();

			// Criando nova query dinamicamente
			List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
				IFilterCriteria criteria = filterFactory
						.and(Arrays.asList(new IFilterCriteria[] {
								new SimpleFilterCriteria("IssueID", "IssueID", DataLayerComparator.EQUAL,
										entry.getKey()),
								new SimpleFilterCriteria("version_number", "version_number", DataLayerComparator.EQUAL,
										entry.getValue()) }));
				filtersAnd.add(criteria);
			}

			query.addFilterCriteria(filterFactory.or(filtersAnd));

		}
		// Fim REO - 29.01.2017 - EV131567

	}

	private static void printMap(Map<Integer, Integer> map) {
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
	}

}
