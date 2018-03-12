package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.derby.tools.sysinfo;
import org.apache.log4j.Logger;
import org.apache.myfaces.shared.util.LocaleUtils;
import org.apache.poi.util.SystemOutLogger;

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
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
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
import com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands.CustomTestcaseSaveActionCommand;

public class CustomControlData3ViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilter,
			List<IFilterCriteria> filters, IDataLayerObject currentObject, QueryDefinition parentQuery)
					throws BusViewException {
		// TODO Auto-generated method stub

		final Logger log = Logger.getLogger(CustomControlData3ViewHandler.class.getName());
		IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
		ITransaction defaultTransaction = TransactionManager.getInstance().getReadOnlyTransaction();
		IAppObjFacade facadeRisk = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.RISK);
		IAppObjFacade facadeControl = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.CONTROL);

		ArrayList<Long> validControls = new ArrayList<>();
		ArrayList<Long> invalidControls = new ArrayList<>();
		TreeMap<Long, Long> hashedMapControls = new TreeMap<>();

		log.info("Criação de objeto de data.");
		Date criteriaDate = Calendar.getInstance().getTime();

		for (IFilterCriteria filter : filters) {
			for (IFilterCriteria filter2 : filter.getFilters()) {
				log.info("Pega data do filtro da view.");
				criteriaDate = (Date) filter2.getValue();
				criteriaDate.setHours(23);
				criteriaDate.setMinutes(59);
				criteriaDate.setSeconds(59);

				// appQueryControl.addRestriction(QueryRestriction.le(IControlAttributeType.BASE_ATTR_CHANGE_DATE,
				// criteriaDate));
				// appQueryRisk.addRestriction(QueryRestriction.le(IRiskAttributeType.BASE_ATTR_CHANGE_DATE,
				// criteriaDate));
				// log.info(filter2.getValue());

			}
		}

		log.info("Criteria para query de riscos.");
		List<IFilterCriteria> listCriteriaFilterRisk = new ArrayList<>();
		log.info("Coloca o filtro de data no change date de riscos.");
		listCriteriaFilterRisk.add(new SimpleFilterCriteria("r_change_date", "r_change_date",
				DataLayerComparator.LESSOREQUAL, (Date)criteriaDate));

		log.info("Gera a query de riscos.");
		IViewQuery queryTeste = 
				QueryFactory.createQuery(userCtx/* this.getFullGrantUserContext() */,
										"custom_riskdataaux", 
										listCriteriaFilterRisk, 
										null, 
										defaultTransaction);

		try {

			Iterator itQuery = queryTeste.getResultIterator();
			log.info("Início do iterator da query de riscos.");
			while (itQuery.hasNext()) {

				IViewObj viewObj = (IViewObj) itQuery.next();
				long r_id = (Long) viewObj.getRawValue("r_id");
				long r_version_number = (Long) viewObj.getRawValue("r_version_number");
				
				log.info("r_id = " + r_id);
				log.info("r_version_number = " + r_version_number);

				IOVID tcOVID = OVIDFactory.getOVID(r_id, r_version_number);
				IAppObj rAppObj = facadeRisk.load(tcOVID, false);
				log.info("Objeto de riscos = " + rAppObj.toString());

				// Pega lista de controles da versão do risco corrente.
				List<IOVID> controls = rAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getRawValue();
				log.info("Pega os controles deste riscos para essa versão");

				// Cria Iterator da lista de controles deste risco.
				Iterator<IOVID> currRiskControlsIterator = controls.iterator();
				log.info("Executa o iterator para os controles deste risco.");
				while (currRiskControlsIterator.hasNext()) {

					Long currentControlID = currRiskControlsIterator.next().getId();
					log.info("Controle = " + currentControlID);
					// Adiciona esse controle na lista de controles válidos.
					validControls.add(currentControlID);
				}
			}

		} catch (Exception e) {
			queryTeste.release();
//			 throw e;
		} finally {
			queryTeste.release();
		}

		log.info("Criteria para query de controle.");
		List<IFilterCriteria> listCriteriaFilterControl = new ArrayList<>();
		log.info("Coloca o filtro de data no change date de controle.");
		listCriteriaFilterControl.add(new SimpleFilterCriteria("c_change_date", "c_change_date",
				DataLayerComparator.LESSOREQUAL, (Date)criteriaDate));

//		filterMap2.put("c_change_date", criteriaDate);

		log.info("Gera a query de controles.");
		IViewQuery queryTeste2 = QueryFactory.createQuery(userCtx/* this.getFullGrantUserContext() */,
				"custom_controldataaux", 
				listCriteriaFilterControl, 
				null, 
				defaultTransaction);

		try {

			Iterator itQuery = queryTeste2.getResultIterator();
			log.info("Início do iterator da query de controles.");
			while (itQuery.hasNext()) {

				IViewObj viewObj = (IViewObj) itQuery.next();
				long c_id = (Long) viewObj.getRawValue("c_id");
				long c_version_number = (Long) viewObj.getRawValue("c_version_number");
				
				log.info("c_id = " + c_id);
				log.info("c_version_number = " + c_version_number);
				
				 if (validControls.contains(c_id)) {
					 log.info("O controle " + c_id + " está válido");
					 hashedMapControls.put(c_id,
							 c_version_number);
				 }

			}
			
		} catch (Exception e) {
			queryTeste2.release();
			// throw e;
		} finally {
			queryTeste2.release();
		}

		log.info("Verifica o tamanho da lista");
		if (hashedMapControls.size() > 0) {

			log.info("Remove os filtros da query");
			// Limpando query original
			for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
				query.markAsRemovable(def);
			}
			filters.clear();

			// Criando nova query dinamicamente
			List<IFilterCriteria> filtersAnd = new ArrayList<IFilterCriteria>();
			IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
			log.info("Loop nos membros da lista hashedMapControls");
			for (Map.Entry<Long, Long> entry : hashedMapControls.entrySet()) {
				log.info("c_id = " + entry.getKey());
				log.info("c_version_number = " + entry.getValue());
				IFilterCriteria criteria = filterFactory.and(Arrays.asList(new IFilterCriteria[] {
						new SimpleFilterCriteria("c_id", "c_id", DataLayerComparator.EQUAL, entry.getKey()),
						new SimpleFilterCriteria("c_version_number", "c_version_number", DataLayerComparator.EQUAL,
								entry.getValue()) }));
				filtersAnd.add(criteria);
			}
			
			log.info("Adiciona o filtro para query.");
			query.addFilterCriteria(filterFactory.or(filtersAnd));
			

		}

	}

}
