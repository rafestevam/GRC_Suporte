package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.List;

import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;

public class CustomAuditControlSelectionViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition paramQueryDefinition1, List<IRightsFilterCriteria> paramList,
			List<IFilterCriteria> paramList1, IDataLayerObject paramIDataLayerObject,
			QueryDefinition paramQueryDefinition2) throws BusViewException {
		// TODO Auto-generated method stub
		
		Long userID = null;

	}

}
