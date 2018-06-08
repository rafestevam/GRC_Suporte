package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IUserAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryOrder;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITaskitemAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUserAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IQueryObjectDefinition;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.ISimpleFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria.FilterConnect;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.ndl.IFilterFactory;
import com.idsscheer.webapps.arcm.ndl.PersistenceAPI;

public class CustomTaskMyTasksViewHandler implements IViewHandler {

	protected static final Log log = LogFactory.getLog(CustomTaskMyTasksViewHandler.class);
	
	private IAppObjFacade facadeTaskItem;
	private IAppObjFacade facadeCE;
	private IAppObjFacade facadeUser;
	
	private IAppObjQuery queryTaskItem;
	private IAppObjQuery queryCE;
	
	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter,
			List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery)
					throws BusViewException {
		// TODO Auto-generated method stub
		
		// Controle dos controles que j� foram considerados na execu��o de controle.
		ArrayList<String> controlList = new ArrayList<>();
		
		TreeMap<Long, Long> hashedMapControls = new TreeMap<>();
		TreeMap<Long, Long> hashedMapIssues = new TreeMap<>();
		
		// Cria contexto administrativo - getFullReadAccessUserContext() utiliza o usu�rio internalsystem.
		IUserContext adminCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		
		// Pega o usu�rio que est� executando a VIEW de myTasks.
		Long userID = (Long)rightsFilter.get(0).getValue();
		
		// Cria facadas com contexto administrativo.
		createFacades(adminCtx);
		
		try {
			IUserAppObj user;
		
			user = (IUserAppObj) facadeUser.load(new OVIDFactory().getOVID(userID), true);
			
			createQueries();
			
			setQueryOrder();
			
			List<Long> listOVIDFilter = 
					getListUserGroups(user);
			
			// Apenas se encontrar grupos para esse usu�rio ser� necess�rio executar as
			// regras abaixo. Desnecess�rio se o usu�rio n�o estiver em nenhum grupo.
			if (listOVIDFilter.size() > 0) {
				setQueryFilters(listOVIDFilter, user);
				searchDuplicatedControls(controlList, hashedMapControls, hashedMapIssues);
				filterQuery(query, filters, hashedMapControls, hashedMapIssues);
			}			
			
		} catch (RightException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			queryCE.release();
			queryTaskItem.release();	
		}

	}

	private void filterQuery(QueryDefinition query, List<IFilterCriteria> filters, TreeMap<Long, Long> hashedMapControls, TreeMap<Long,Long> hashedMapIssues) {
		if (hashedMapControls.size() > 0) {

			// Limpando query original
//			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
//				query.markAsRemovable(def);
//			}
//			filters.clear();

			// Criando novos filtros dinamicamente.
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			for (Map.Entry<Long, Long> entry : hashedMapControls.entrySet()) {
				query.addFilterCriteria(new SimpleFilterCriteria( "object_id", DataLayerComparator.NOTEQUAL,
						entry.getValue()));
			}

		}
		
		if(hashedMapIssues.size() > 0){
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			List<IFilterCriteria> filterCriterias = new ArrayList<>();
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			//filters.clear();
			for (Map.Entry<Long, Long> entry : hashedMapIssues.entrySet()) {
//				query.addFilterCriteria(new SimpleFilterCriteria( "obj_id", DataLayerComparator.EQUAL,
//						entry.getKey()));
				filterCriterias.add(new SimpleFilterCriteria( "obj_id", DataLayerComparator.EQUAL,
						entry.getValue()));
			}
			ISimpleFilterCriteria filterCriteria = filterFactory.getSimpleFilterCriteria(filterCriterias, FilterConnect.OR);
			filters.add(filterCriteria);
		}
		
	}

	private void searchDuplicatedControls(ArrayList<String> controlList, TreeMap<Long, Long> hashedMapControls, TreeMap<Long, Long> hashedMapIssues) {
		IAppObjIterator iteratorTaskItem = 
				queryTaskItem.getResultIterator();
		
		while ( iteratorTaskItem.hasNext() ) {
			
			IAppObj currentTaskItem = (IAppObj) iteratorTaskItem.next();
			
			if(currentTaskItem.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE).getRawValue().equals("ISSUE")){
				long issueID = currentTaskItem.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_ID);
				long issueVersionNumber = currentTaskItem.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_VERSION_NUMBER);
				if(!hashedMapIssues.containsKey(issueID))
					hashedMapIssues.put(issueID, currentTaskItem.getObjectId());
				//hashedMapIssues.put(issueID, issueVersionNumber);
			}
			
			if(!currentTaskItem.getAttribute(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE).getRawValue().equals("CONTROLEXECUTION"))
				continue;

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
						hashedMapControls.put(ceOVID.getId(), ceOVID.getVersion());
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private List<Long> getListUserGroups(IUserAppObj user) {
		List<Long> listOVIDFilter = new ArrayList<Long>();
		
		// Para pegar os grupos que esse usu�rio tem acesso
		// � necess�rio utilizar o m�todo createReportUserContext().
		Iterator<IOVID> iteratorUserGroups = 
				ContextFactory.createReportUserContext(user.getRawValue(IUserAttributeType.ATTR_USERID), "", LocaleUtils.toLocale("US")).
				getUserRelations().getGroupsIDs().iterator();
		
		// Preenche a lista com os grupos que esse usu�rio tem acesso.
		while (iteratorUserGroups.hasNext()) {
			IOVID userGroup = iteratorUserGroups.next();
			listOVIDFilter.add(userGroup.getId());
		}
		
		listOVIDFilter.add(new Long(-1));
		
		return listOVIDFilter;
	}

	private void setQueryOrder() {
		//queryTaskItem.addOrder((QueryOrder.descending(ITaskitemAttributeType.BASE_ATTR_CREATE_DATE)));
		queryTaskItem.addOrder((QueryOrder.descending(ITaskitemAttributeType.ATTR_OBJ_ID)));
	}

	private void setQueryFilters(List<Long> listOVIDFilter, IUserAppObj user) {
		
		queryTaskItem.addRestriction(
				QueryRestriction.or(
						QueryRestriction.and(
								QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "ISSUE"),
								QueryRestriction.eq(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID, user.getObjectId())
						),
						QueryRestriction.and(
								QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "CONTROLEXECUTION"),
								QueryRestriction.in(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERGROUPID, listOVIDFilter)
						)
				)
		);
		
//		queryTaskItem.addRestriction(
//				QueryRestriction.and(
//						QueryRestriction.or(
//							QueryRestriction.eq(ITaskitemAttributeType.ATTR_STATUS, Enumerations.TASKITEM_STATUS.OPEN), 
//							QueryRestriction.eq(ITaskitemAttributeType.ATTR_STATUS, Enumerations.TASKITEM_STATUS.NOT_COMPLETED)),
//						QueryRestriction.or(
//								QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "CONTROLEXECUTION"),
//								QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "ISSUE")),
//						QueryRestriction.or(
//								QueryRestriction.eq(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERID, user.getObjectId()),
//								QueryRestriction.in(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERGROUPID, listOVIDFilter))));
//
	}

	private void createQueries() {
		queryTaskItem = facadeTaskItem.createQuery();
		queryCE = facadeCE.createQuery();
		
		queryTaskItem.setHeadRevisionsOnly(true);
		queryTaskItem.setIncludeDeletedObjects(false);
		
		queryCE.setHeadRevisionsOnly(true);
		queryCE.setIncludeDeletedObjects(false);
	}

	private void createFacades(IUserContext adminCtx) {
		facadeTaskItem = FacadeFactory.getInstance().getAppObjFacade(adminCtx, ObjectType.TASKITEM);
		facadeCE = FacadeFactory.getInstance().getAppObjFacade(adminCtx, ObjectType.CONTROLEXECUTION);
		facadeUser = FacadeFactory.getInstance().getAppObjFacade(adminCtx, ObjectType.USER); 
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		queryCE.release();
		queryTaskItem.release();
	}

}
