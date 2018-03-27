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
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
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
		
		// Controle dos controles que já foram considerados na execução de controle.
		ArrayList<String> controlList = new ArrayList<>();
		
		TreeMap<Long, Long> hashedMapControls = new TreeMap<>();
		
		// Cria contexto administrativo - getFullReadAccessUserContext() utiliza o usuário internalsystem.
		IUserContext adminCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		
		// Pega o usuário que está executando a VIEW de myTasks.
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
			
			// Apenas se encontrar grupos para esse usuário será necessário executar as
			// regras abaixo. Desnecessário se o usuário não estiver em nenhum grupo.
			if (listOVIDFilter.size() > 0) {
				setQueryFilters(listOVIDFilter);
				searchDuplicatedControls(controlList, hashedMapControls);
				filterQuery(query, hashedMapControls);
			}			
			
		} catch (RightException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			queryCE.release();
			queryTaskItem.release();	
		}

	}

	private void filterQuery(QueryDefinition query, TreeMap<Long, Long> hashedMapControls) {
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
						entry.getKey()));
			}

		}
	}

	private void searchDuplicatedControls(ArrayList<String> controlList, TreeMap<Long, Long> hashedMapControls) {
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
		
		// Para pegar os grupos que esse usuário tem acesso
		// é necessário utilizar o método createReportUserContext().
		Iterator<IOVID> iteratorUserGroups = 
				ContextFactory.createReportUserContext(user.getRawValue(IUserAttributeType.ATTR_USERID), "", LocaleUtils.toLocale("US")).
				getUserRelations().getGroupsIDs().iterator();
		
		// Preenche a lista com os grupos que esse usuário tem acesso.
		while (iteratorUserGroups.hasNext()) {
			IOVID userGroup = iteratorUserGroups.next();
			listOVIDFilter.add(userGroup.getId());
		}
		
		return listOVIDFilter;
	}

	private void setQueryOrder() {
		queryTaskItem.addOrder((QueryOrder.descending(ITaskitemAttributeType.BASE_ATTR_CREATE_DATE)));
	}

	private void setQueryFilters(List<Long> listOVIDFilter) {
				
		queryTaskItem.addRestriction(
				QueryRestriction.and(
						QueryRestriction.or(
							QueryRestriction.eq(ITaskitemAttributeType.ATTR_STATUS, Enumerations.TASKITEM_STATUS.OPEN), 
							QueryRestriction.eq(ITaskitemAttributeType.ATTR_STATUS, Enumerations.TASKITEM_STATUS.NOT_COMPLETED)),
						QueryRestriction.eq(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE, "CONTROLEXECUTION"),
						QueryRestriction.in(ITaskitemAttributeType.ATTR_RESPONSIBLEUSERGROUPID, listOVIDFilter)));

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
