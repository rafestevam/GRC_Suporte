package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.aspose.imaging.internal.bc.ce;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITaskitemAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.IFilterFactory;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;

public class CustomTaskMyTasksViewHandler implements IViewHandler {

	private IAppObjFacade facadeTaskItem;
	private IAppObjFacade facadeCE;
	
	private IAppObjQuery queryTaskItem;
	private IAppObjQuery queryCE;
	
	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter,
			List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery)
					throws BusViewException {
		// TODO Auto-generated method stub

		Map<Integer, Integer> resultMap = new TreeMap<>();
		
		ArrayList<String> controlList = new ArrayList<>();
		
		TreeMap<Long, Long> hashedMapControls = new TreeMap<>();
		
		IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		
		createFacades(userCtx);
		
		createQueries();
		
		setQueryFilter(userCtx);
		
		IAppObjIterator iteratorTaskItem = 
				queryTaskItem.getResultIterator();
		
		while ( iteratorTaskItem.hasNext() ) {
			IAppObj currentTaskItem = (IAppObj) iteratorTaskItem.next();

			long ceID = currentTaskItem.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_ID);
			long ceVersionNumber = currentTaskItem.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_VERSION_NUMBER);
			
			IOVID ceOVID = OVIDFactory.getOVID(ceID, ceVersionNumber);
			IAppObj ceAppObj;
			
			try {
				ceAppObj = facadeCE.load(ceOVID, true);
				
				if (ceAppObj != null) {
					String controlID = ceAppObj.getRawValue(IControlAttributeType.ATTR_CONTROL_ID);
					
					if (!controlList.contains(controlID)) {
						controlList.add(controlID);
					} else {
						hashedMapControls.put(ceID, ceVersionNumber);
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		if (hashedMapControls.size() > 0) {

			// Limpando query original
//			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
//				query.markAsRemovable(def);
//			}
//			filters.clear();

			// Criando nova query dinamicamente
			List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			for (Map.Entry<Long, Long> entry : hashedMapControls.entrySet()) {
				IFilterCriteria criteria = filterFactory
						.and(Arrays.asList(new IFilterCriteria[] {
								new SimpleFilterCriteria( "object_id", DataLayerComparator.NOTIN,
										entry.getKey())})
						);
				filtersAnd.add(criteria);
			}
			
			query.addFilterCriteria(filterFactory.or(filtersAnd));

		}
		
	}

	private void setQueryFilter(IUserContext userCtx) {
		
//		List<IOVID> userGroupList = userCtx.getUserRelations().getGroupsIDs();
//		Iterator iterator = userGroupList.iterator();
		
//		while (iterator.hasNext()) {
//			IOVID userGroup = (IOVID) iterator.next();
//			queryTaskItem.addRestriction
//						QueryRestriction.eq(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERGROUPID, 
//								userGroup.getId()));
//		}
		
		queryTaskItem.addRestriction(QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "CONTROLEXECUTION"));
	}

	private void createQueries() {
		queryTaskItem = facadeTaskItem.createQuery();
		queryCE = facadeCE.createQuery();
		
		queryTaskItem.setHeadRevisionsOnly(true);
		queryTaskItem.setIncludeDeletedObjects(false);
		
		queryCE.setHeadRevisionsOnly(true);
		queryCE.setIncludeDeletedObjects(false);
	}

	private void createFacades(IUserContext userCtx) {
		facadeTaskItem = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.TASKITEM);
		facadeCE = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROLEXECUTION);
	}

}
