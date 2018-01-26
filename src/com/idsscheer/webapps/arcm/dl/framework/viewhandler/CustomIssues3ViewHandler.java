package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IQueryExpression;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;

public class CustomIssues3ViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter, List<IFilterCriteria> filters,
			IDataLayerObject currentObject, QueryDefinition parentQuery) throws BusViewException {
		// TODO Auto-generated method stub
		
		Map<Integer, Integer> resultMap = new HashMap<>();
		if(currentObject == null){
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.ISSUE);
			
			IAppObjQuery appQuery = facade.createQuery();
			
			IQueryExpression restr1 = QueryRestriction.eq(IIssueAttributeType.BASE_ATTR_VERSION_ACTIVE, true);
			appQuery.addRestriction(restr1);
			
			IAppObjIterator iterator = appQuery.getResultIterator();
			while(iterator.hasNext()){
				IAppObj issueObj = iterator.next();
				Integer obj_id = (int)issueObj.getObjectId();
				Integer version_number = Integer.valueOf(
						issueObj.getAttribute(IIssueAttributeType.BASE_ATTR_VERSION_NUMBER).getRawValue().intValue()
						);
				resultMap.put(obj_id, version_number);
			}
			
			//sortMap(resultMap);
			sortByComparator(resultMap, true);
			printMap(resultMap);
				
		}

	}
	
	private static void sortMap(Map<Integer, Integer> unsortMap){
		Object[] array = unsortMap.entrySet().toArray();
		Arrays.sort(array, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		
	}
	
	private static Map<Integer, Integer> sortByComparator(Map<Integer, Integer> unsortMap, final boolean order)
    {

        List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, Integer>>()
        {
            public int compare(Entry<Integer, Integer> o1,
                    Entry<Integer, Integer> o2)
            {
                if (order)
                {
                	return o1.getKey().compareTo(o2.getKey());
                    //return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                	return o2.getKey().compareTo(o1.getKey());
                    //return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
        for (Entry<Integer, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	private static void printMap(Map<Integer, Integer> map)
    {
        for (Entry<Integer, Integer> entry : map.entrySet())
        {
            System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
        }
    }

}
