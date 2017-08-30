package com.idsscheer.webapps.arcm.custom.corprisk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;

public class CustomCorpRiskHierarchy {
	
	private static List<IAppObj> riskList = new ArrayList<IAppObj>();
	private static IAppObjFacade riskFacade;

	public CustomCorpRiskHierarchy(IAppObj corpRiskObj, IUserContext userCtx, ITransaction defaultTransaction) throws CustomCorpRiskException{
		// TODO Auto-generated constructor stub
		try{
			riskFacade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.RISK);
			riskList = this.getCorpRisk2Risks(corpRiskObj.getObjectId(), userCtx, defaultTransaction);
		}catch(Exception e){
			throw new CustomCorpRiskException("erro ao gerar o calculo para risco corporativo", e.getCause());
		}
	}
	
	public String calculateResidualCR(){
		String residual = "";
		
		for(IAppObj riskObj : riskList){
			System.out.println(riskObj.toString());
		}
		
		return residual;
	}
	
	private List<IAppObj> getCorpRisk2Risks(long corpRiskID, IUserContext userCtx, ITransaction defaultTransaction) throws Exception{
		List<IAppObj> retCorprisk = new ArrayList<IAppObj>();
		
		Map filterMap = new HashMap();
		filterMap.put("idOR", corpRiskID);
		
		IViewQuery query = QueryFactory.createQuery(userCtx, "hierarchy2risk_refs_1", filterMap, null,
				true, defaultTransaction);
		
		try{
			
			Iterator itQuery = query.getResultIterator();
			
			while(itQuery.hasNext()){
				IViewObj viewObj = (IViewObj)itQuery.next();
				long riskID = (Long)viewObj.getRawValue("risk_obj_id");
				long riskVersionNumber = (Long)viewObj.getRawValue("risk_version_number");
				
				IOVID riskOVID = OVIDFactory.getOVID(riskID, riskVersionNumber);
				IAppObj riskAppObj = riskFacade.load(riskOVID, true);
				
				retCorprisk.add(riskAppObj);
			}
			
		}catch(Exception e){
			query.release();
			throw e;
		}finally{
			query.release();
		}
		
		return retCorprisk;
	} 
	
}
