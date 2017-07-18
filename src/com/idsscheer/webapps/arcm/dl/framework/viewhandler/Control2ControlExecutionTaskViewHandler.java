package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dbms.CDBMSException;
import com.idsscheer.webapps.arcm.dl.framework.dbms.DBMSLayer;
import com.idsscheer.webapps.arcm.dl.framework.dbms.mapping.MappingType;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;

public class Control2ControlExecutionTaskViewHandler implements IViewHandler {
	protected static final Log log = LogFactory.getLog(SurveysAndQuestionnairesPerAssignedRiskEvalViewHandler.class);
	
	@Override
	public void handleView(QueryDefinition paramQueryDefinition1, List<IRightsFilterCriteria> paramList,
			List<IFilterCriteria> paramList1, IDataLayerObject paramIDataLayerObject,
			QueryDefinition paramQueryDefinition2) throws BusViewException {
		// TODO Auto-generated method stub
		try{
			if (DBMSLayer.getInstance().getMapping().getDbmsType() == MappingType.DERBY) {
				
				
				
			}
		} catch (CDBMSException e) {
			log.error("Cannot get instance of dbms layer.", e);
			throw new BusViewException(BusViewException.ERROR_BUS_DBMS_CONNECTION_INVALID, e);
		}

	}

}
