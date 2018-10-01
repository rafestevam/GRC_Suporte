package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ARCMUtility;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
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
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.ILockService;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.ILockObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceException;

public class FillCustomOVIDJob extends BaseJob {

	private static final long serialVersionUID = 1L;
	private ArrayList<Long> validControls = new ArrayList<>();
	private TreeMap<Long, Long> hashedMapControls = new TreeMap<>();
	
	private IAppObjFacade facadeRisk;
	private Date criteriaDate;
	
	private IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
	private ITransaction defaultTransaction = TransactionManager.getInstance().getReadOnlyTransaction();

	final Logger log = Logger.getLogger(FillCustomOVIDJob.class.getName());
	
	public static final String JOB_NAME_KEY = "enumeration.jobs.AdjustControl1Line.DBI";
	
	public FillCustomOVIDJob(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
	}
	
	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		// TODO Auto-generated method stub
		// Preenche os facades que serão utilizados.
		getFacades();
		
		// Pega a data informada no atributo C_CHANGE_DATE da query "controldata3". Data é informada 
		// através de variáveis GET/HTTP
		// Exemplo: ...&qattr=C_CHANGE_DATE,LT~2018-03-28
		//Date criteriaDate = setDateFilter(filters);
		criteriaDate = setDateFilter();
		
		// Cria List com o filtro de data passada como parâmetro.
		List<IFilterCriteria> listCriteriaFilterRisk = getListRisksFilter(criteriaDate);
		
		// Cria VIEW com base em VIEW auxiliar custom_riskdataaux.
		IViewQuery queryRisks = setQueryRisksFilter(listCriteriaFilterRisk);
		
		try {
			
			// Loop nas versões de risco para determinar quais 
			// controles e versões eram válidos naquele risco
			// na data informada.
			fillValidControls2RiskList(queryRisks);
			
		} catch (Exception e) {
			queryRisks.release();
			log.info(e.getMessage());
		} finally {
			queryRisks.release();
		}
		
		// Filtro para VIEW auxiliar de controle custom_controldataaux.
		List<IFilterCriteria> listCriteriaFilterControl = getListControlFilter(criteriaDate);
		
		IViewQuery queryControls = setQueryControlFilter(listCriteriaFilterControl);
		
		try {
			
			// Com base na lista de controles válidos
			// preenchida anteriormente pelo 
			// método fillValidControls2RiskList()
			// é necessário agora determinar
			// qual a versão deste controle.
			fillValidControlsList(queryControls);
			
			if(hashedMapControls.size() > 0){
				for (Map.Entry<Long, Long> entry : hashedMapControls.entrySet()) {
					log.info("c_id = " + entry.getKey());
					log.info("c_version_number = " + entry.getValue());
					
				}
			}
			
		} catch (Exception e) {
			queryControls.release();
			log.info(e.getMessage());
		} finally {
			queryControls.release();
		}
	}


	private void setFinalQueryFilter(QueryDefinition query, List<IFilterCriteria> filters) {
		log.info("Verifica o tamanho da lista");
		if (hashedMapControls.size() > 0) {

			clearFilter(query, filters);

//			// Criando novo filtro de query.
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
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTime(criteriaDate);
//			query.addFilterCriteria(new SimpleFilterCriteria("c_change_date", "c_change_date", DataLayerComparator.LESSOREQUAL, calendar.getTimeInMillis()));

		}
	}

	private void clearFilter(QueryDefinition query, List<IFilterCriteria> filters) {
		log.info("Remove os filtros da query");
		// Limpando query original
		for (IQueryObjectDefinition def : query.getObjectDefinitions()) {
			query.markAsRemovable(def);
		}
		filters.clear();
	}

	private void fillValidControlsList(IViewQuery queryControls) {
		Iterator itQuery = queryControls.getResultIterator();
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
	}

	private IViewQuery setQueryControlFilter(List<IFilterCriteria> listCriteriaFilterControl) {
		log.info("Gera a query de controles.");
		IViewQuery queryTeste2 = QueryFactory.createQuery(userCtx/* this.getFullGrantUserContext() */,
				"custom_controldataaux", 
				listCriteriaFilterControl, 
				null, 
				defaultTransaction);
		return queryTeste2;
	}

	private List<IFilterCriteria> getListControlFilter(Date criteriaDate) {
		log.info("Criteria para query de controle.");
		List<IFilterCriteria> listCriteriaFilterControl = new ArrayList<>();
		log.info("Coloca o filtro de data no change date de controle.");
		listCriteriaFilterControl.add(new SimpleFilterCriteria("c_change_date", "c_change_date",
				DataLayerComparator.LESSOREQUAL, (Date)criteriaDate));
		return listCriteriaFilterControl;
	}

	private void fillValidControls2RiskList(IViewQuery queryTeste) throws RightException {
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
	}

	private IViewQuery setQueryRisksFilter(List<IFilterCriteria> listCriteriaFilterRisk) {
		log.info("Gera a query de riscos.");
		IViewQuery queryTeste = 
				QueryFactory.createQuery(userCtx/* this.getFullGrantUserContext() */,
										"custom_riskdataaux", 
										listCriteriaFilterRisk, 
										null, 
										defaultTransaction);
		return queryTeste;
	}

	private List<IFilterCriteria> getListRisksFilter(Date criteriaDate) {
		log.info("Criteria para query de riscos.");
		List<IFilterCriteria> listCriteriaFilterRisk = new ArrayList<>();
		log.info("Coloca o filtro de data no change date de riscos.");
		listCriteriaFilterRisk.add(new SimpleFilterCriteria("r_change_date", "r_change_date",
				DataLayerComparator.LESSOREQUAL, (Date)criteriaDate));
		return listCriteriaFilterRisk;
	}

	private Date setDateFilter() {
		log.info("Criação de objeto de data.");
		Date criteriaDateBuf = Calendar.getInstance().getTime();
		
		Date criteriaDate = DateUtils.normalizeLocalDate(criteriaDateBuf, DateUtils.Target.END_OF_DAY);
		
		/*for (IFilterCriteria filter : filters) {
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
		}*/
		return criteriaDate;
	}

	private void getFacades() {
		facadeRisk = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.RISK);
	}

	@Override
	protected void deallocateResources() {
		deallocateLocalSources();
	}


	@Override
	public String getJobNameKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEnumerationItem getJobType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void deallocateLocalSources() {
		ILockService lockService = ARCMServiceProvider.getInstance().getLockService();
		try {
			for (ILockObject lock : lockService.findLocks()) {
				lockService.releaseLock(lock.getObjectType(), lock.getLockUserId(), null);
			}
		} catch (LockServiceException e) {
			setJobFailed(KEY_ERR_JOB_ABORT, JOB_NAME_KEY);
		}
	}	

}
