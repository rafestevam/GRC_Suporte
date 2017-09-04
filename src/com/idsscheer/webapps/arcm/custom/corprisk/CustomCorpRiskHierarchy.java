package com.idsscheer.webapps.arcm.custom.corprisk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
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
		int totalRisks = 0;
		int finalGrade = 0;
		Map<String,Integer> resGrade = new HashMap<String, Integer>();
		Map<String,Integer> ptsGrade = new HashMap<String, Integer>();
		Map<String, Integer[]> distGrade = new HashMap<String, Integer[]>();
		Map<String,Integer> heightGrade = this.getHeightScale();
		
		resGrade.put("baixo", new Integer(0));
		resGrade.put("medio", new Integer(0));
		resGrade.put("alto", new Integer(0));
		resGrade.put("muito_alto", new Integer(0));
		
		for(IAppObj riskObj : riskList){
			//System.out.println(riskObj.toString());
			totalRisks += 1;
			String resClass = riskObj.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESIDUALFINAL).getRawValue();
			if(resClass.equalsIgnoreCase("Baixo")){
				resGrade.replace("baixo", Math.incrementExact(1));
			}
			if(resClass.equalsIgnoreCase("Médio")){
				resGrade.replace("medio", Math.incrementExact(1));
			}
			if(resClass.equalsIgnoreCase("Alto")){
				resGrade.replace("alto", Math.incrementExact(1));
			}
			if(resClass.equalsIgnoreCase("Muito Alto")){
				resGrade.replace("muito_alto", Math.incrementExact(1));
			}
		}
		
		ptsGrade.put("baixo", (resGrade.get("baixo") * heightGrade.get("pBaixo")));
		ptsGrade.put("medio", (resGrade.get("medio") * heightGrade.get("pMedio")));
		ptsGrade.put("alto", (resGrade.get("alto") * heightGrade.get("pAlto")));
		ptsGrade.put("muito_alto", (resGrade.get("muito_alto") * heightGrade.get("pMAlto")));
		
		Iterator it = ptsGrade.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,Integer> mapEntry = (Map.Entry<String, Integer>)it.next();
			finalGrade += mapEntry.getValue();
		}
		
		distGrade = this.computeDistGrade(totalRisks, heightGrade);
		residual = this.getResidualCR(finalGrade, distGrade);		
		
		return residual;
	}
	
	private String getResidualCR(int finalGrade, Map<String,Integer[]> distGrade){
		String residual = "";
		
		if(this.getClassification(distGrade.get("baixo"), finalGrade, "baixo"))
			residual = "Baixo";
		
		if(this.getClassification(distGrade.get("medio"), finalGrade, "medio"))
			residual = "Médio";
		
		if(this.getClassification(distGrade.get("alto"), finalGrade, "alto"))
			residual = "Alto";
		
		if(this.getClassification(distGrade.get("muito_alto"), finalGrade, "muito_alto"))
			residual = "Muito Alto";
		
		return residual;
	}
	
	private Boolean getClassification(Integer[] arr, int target, String classification){
		boolean retClass = false;
		int a = 0;
		
		if(classification.equals("baixo")){
			a = Arrays.binarySearch(arr, target);
			if(a > 0){
				return true;
			}
		}
		
		if(classification.equals("medio")){
			a = Arrays.binarySearch(arr, target);
			if(a > 0){
				return true;
			}
		}
		
		if(classification.equals("alto")){
			a = Arrays.binarySearch(arr, target);
			if(a > 0){
				return true;
			}
		}
		
		if(classification.equals("muito_alto")){
			a = Arrays.binarySearch(arr, target);
			if(a > 0){
				return true;
			}
		}
		
		return retClass;
	}
	
	private Map<String,Integer[]> computeDistGrade(int totalRisks, Map<String,Integer> heightGrade){
		Map<String, Integer[]> mapRet = new HashMap<String, Integer[]>();
		int limitBaixo = totalRisks * heightGrade.get("pBaixo");
		int limitMedio = totalRisks * heightGrade.get("pMedio");
		int limitAlto = totalRisks * heightGrade.get("pAlto");
		int limitMAlto = totalRisks * heightGrade.get("pMAlto");
		
		List<Integer> listBaixo = new ArrayList<Integer>();
		List<Integer> listMedio = new ArrayList<Integer>();
		List<Integer> listAlto = new ArrayList<Integer>();
		List<Integer> listMAlto = new ArrayList<Integer>();
		
		int count = 0;
		for(int i = 1; i <= limitBaixo; i++){
			count += 1;
			listBaixo.add(count);
		}
		Integer[] arrBaixo = Arrays.copyOf(listBaixo.toArray(), listBaixo.size(), Integer[].class);
		mapRet.put("baixo", arrBaixo);
		
		count = limitBaixo;
		for(int i = (limitBaixo + 1); i <= limitMedio; i++){
			count += 1;
			listMedio.add(count);
		}
		Integer[] arrMedio = Arrays.copyOf(listMedio.toArray(), listMedio.size(), Integer[].class);
		mapRet.put("medio", arrMedio);
		
		count = limitMedio;
		for(int i = (limitMedio + 1); i <= limitAlto; i++){
			count += 1;
			listAlto.add(count);
		}
		Integer[] arrAlto = Arrays.copyOf(listAlto.toArray(), listAlto.size(), Integer[].class);
		mapRet.put("alto", arrAlto);
		
		count = limitAlto;
		for(int i = (limitAlto + 1); i <= limitMAlto; i++){
			count += 1;
			listMAlto.add(count);
		}
		Integer[] arrMAlto = Arrays.copyOf(listMAlto.toArray(), listMAlto.size(), Integer[].class);
		mapRet.put("muito_alto", arrMAlto);
		
		return mapRet;
	}
	
	private Map<String, Integer> getHeightScale(){
		Map<String, Integer> mapRet = new HashMap<String, Integer>();
		
		mapRet.put("pBaixo", 3);
		mapRet.put("pMedio", 6);
		mapRet.put("pAlto", 12);
		mapRet.put("pMAlto", 24);
		
		return mapRet;
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
				IAppObj riskAppObj = riskFacade.load(riskOVID, defaultTransaction, true);
				
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
