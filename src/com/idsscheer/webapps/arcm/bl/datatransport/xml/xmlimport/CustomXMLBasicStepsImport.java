package com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.datatransport.xml.AAMMLLogger;
import com.idsscheer.webapps.arcm.bl.datatransport.xml.resources.AAMMLResourceBundle;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.migframe.IMerge;
import com.idsscheer.webapps.arcm.dl.migframe.IMigrationRecord;
import com.idsscheer.webapps.arcm.dl.migframe.MigrationException;
import com.idsscheer.webapps.arcm.dl.migframe.logic.IColumnMap;
import com.idsscheer.webapps.arcm.dl.migframe.logic.IObjMap;

public class CustomXMLBasicStepsImport extends XMLImportMigrationBasisSteps {
	private final String CLASSNAME = super.getClass().getName();
	private final AAMMLResourceBundle rsBundle = AAMMLResourceBundle.getBundle((Locale) null);
	//private final String CLIENT_ADMIN_PREFIX = "system_";
	//private final String CLIENT_ADMINGROUP_PREFIX = "Administrators_";
	//private Map objectListsMap = new HashMap();
	//private IFilterFactory filterFactory = PersistenceAPI.getFilterFactory();
	//private IPersistenceManager persistenceManager = PersistenceAPI.getPersistenceManager();

	public boolean preCondition(IMigrationRecord sourceRec, IMigrationRecord targetRec, IObjMap objectMap)
			throws MigrationException {
		return super.preCondition(sourceRec, targetRec, objectMap);
	}

	protected boolean isObjectWithoutARISChangedate(String objectName) {
		return super.isObjectWithoutARISChangedate(objectName);
	}

	public void adddummyclient(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.adddummyclient(sourceRec, targetRec, columnMap);
	}

	public void changelistsizeone(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.changelistsizeone(sourceRec, targetRec, columnMap);
	}

	public void ignore(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.ignore(sourceRec, targetRec, columnMap);
	}

	public void setlistsizeone(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setlistsizeone(sourceRec, targetRec, columnMap);
	}

	public void mergelist(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.mergelist(sourceRec, targetRec, columnMap);
	}

	public void copylist(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.copylist(sourceRec, targetRec, columnMap);
	}

	public void preservevalue(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.preservevalue(sourceRec, targetRec, columnMap);
	}

	public void setpassword(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setpassword(sourceRec, targetRec, columnMap);
	}

	public void setchangeuserid(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setchangeuserid(sourceRec, targetRec, columnMap);
	}

	public void setchangetype(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setchangetype(sourceRec, targetRec, columnMap);
	}

	public void setcreatoruserid(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setcreatoruserid(sourceRec, targetRec, columnMap);
	}

	public void setvisibledefault(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setvisibledefault(sourceRec, targetRec, columnMap);
	}

	public void setchangedate(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setchangedate(sourceRec, targetRec, columnMap);
	}

	public void setisroot(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setisroot(sourceRec, targetRec, columnMap);
	}

	public void setselectable(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setselectable(sourceRec, targetRec, columnMap);
	}

	public void setdocumenttype(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setdocumenttype(sourceRec, targetRec, columnMap);
	}

	public IDataLayerObject findDocument(IMigrationRecord record, IMerge mergeInfos) throws MigrationException {
		return super.findDocument(record, mergeInfos);
	}

	protected IDataLayerObject queryObject(String objName, String queryField, String value, String client) {
		return super.queryObject(objName, queryField, value, client);
	}

	public void preservedata(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.preservedata(sourceRec, targetRec, columnMap);
	}

	public void setdeficiencyid(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setdeficiencyid(sourceRec, targetRec, columnMap);
	}

	public void adaptclientadmin(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.adaptclientadmin(sourceRec, targetRec, columnMap);
	}

	public void adaptclientadmingroup(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.adaptclientadmingroup(sourceRec, targetRec, columnMap);
	}

	public void id2objfromdb(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.id2objfromdb(sourceRec, targetRec, columnMap);
	}

	public IDataLayerObject reloadObjectFromDB(String objName, String aliasName, long objID, long versionNumber) {
		return super.reloadObjectFromDB(objName, aliasName, objID, versionNumber);
	}

	public void checkassessmentexists(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.checkassessmentexists(sourceRec, targetRec, columnMap);
	}

	public void setassessor(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setassessor(sourceRec, targetRec, columnMap);
	}

	public void setmanagementrelevant(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		super.setmanagementrelevant(sourceRec, targetRec, columnMap);
	}

	public void setstartdate(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try {
			Date e = new Date();
			targetRec.setDate(columnMap.getSource(), e);
		} catch (Exception arg4) {
			AAMMLLogger.error(this.CLASSNAME, this.rsBundle.getMessage("error.import.risk.setstartdate.ZZZ"));
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}
	}
	
	public void setenddate(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try {
			Calendar sysDate = new GregorianCalendar(2050, Calendar.JANUARY, 31);
			//Date e = new Date(2050, 12, 31);
			Date e = sysDate.getTime();
			targetRec.setDate(columnMap.getSource(), e);
		} catch (Exception arg4) {
			AAMMLLogger.error(this.CLASSNAME, this.rsBundle.getMessage("error.import.risk.setstartdate.ZZZ"));
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}
	}
	
	public void setraresult(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.RISK);
			IAppObjQuery query = facade.createQuery();
			String ra_result = "";
			
			query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj risk = iterator.next();
				IOVID riskOVID = risk.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(riskOVID, true);
				
				ra_result = lastRisk.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_RESULT).getRawValue();
				
			}
			query.release();
			
			//facade.load(paramIOVID, paramBoolean)
			
			//String ra_result = sourceRec.getString("ra_result");
			targetRec.setString(columnMap.getSource(), ra_result);
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}finally{
			//query
		}
	}
	
	public void setracontrol1line(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.RISK);
			IAppObjQuery query = facade.createQuery();
			String ra_result = "";
			
			query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj risk = iterator.next();
				IOVID riskOVID = risk.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(riskOVID, true);
				
				ra_result = lastRisk.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL1LINE).getRawValue();
				
			}
			query.release();
			
			//facade.load(paramIOVID, paramBoolean)
			
			//String ra_result = sourceRec.getString("ra_result");
			targetRec.setString(columnMap.getSource(), ra_result);
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}finally{
			//query
		}
	}
	
	public void setracontrol2line(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.RISK);
			IAppObjQuery query = facade.createQuery();
			String ra_result = "";
			
			query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj risk = iterator.next();
				IOVID riskOVID = risk.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(riskOVID, true);
				
				ra_result = lastRisk.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL2LINE).getRawValue();
				
			}
			query.release();
			
			//facade.load(paramIOVID, paramBoolean)
			
			//String ra_result = sourceRec.getString("ra_result");
			targetRec.setString(columnMap.getSource(), ra_result);
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}finally{
			//query
		}
	}
	
	public void setracontrol3line(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.RISK);
			IAppObjQuery query = facade.createQuery();
			String ra_result = "";
			
			query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj risk = iterator.next();
				IOVID riskOVID = risk.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(riskOVID, true);
				
				ra_result = lastRisk.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROL3LINE).getRawValue();
				
			}
			query.release();
			
			//facade.load(paramIOVID, paramBoolean)
			
			//String ra_result = sourceRec.getString("ra_result");
			targetRec.setString(columnMap.getSource(), ra_result);
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}finally{
			//query
		}
	}
	
	public void setracontrolfinal(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.RISK);
			IAppObjQuery query = facade.createQuery();
			String ra_result = "";
			
			query.addRestriction(QueryRestriction.eq(IRiskAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj risk = iterator.next();
				IOVID riskOVID = risk.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(riskOVID, true);
				
				ra_result = lastRisk.getAttribute(IRiskAttributeTypeCustom.ATTR_RA_CONTROLFINAL).getRawValue();
				
			}
			query.release();
			
			//facade.load(paramIOVID, paramBoolean)
			
			//String ra_result = sourceRec.getString("ra_result");
			targetRec.setString(columnMap.getSource(), ra_result);
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}finally{
			//query
		}
	}
	
	public void setstatuscontrol(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		
		try{
			IUserContext fullReadCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(fullReadCtx, ObjectType.CONTROL);
			IAppObjQuery query = facade.createQuery();
			String statusControl = "";
			
			query.addRestriction(QueryRestriction.eq(IControlAttributeType.BASE_ATTR_GUID, sourceRec.getString("guid")));
			
			IAppObjIterator iterator =  query.getResultIterator();
			
			while(iterator.hasNext()){
				
				IAppObj control = iterator.next();
				IOVID controlOVID = control.getVersionData().getHeadOVID();
				IAppObj lastRisk = facade.load(controlOVID, true);
				
				statusControl = lastRisk.getAttribute(IControlAttributeTypeCustom.ATTR_CUSTOM_STATUS).getRawValue();
				
				targetRec.setString(columnMap.getSource(), statusControl);
				
			}
			query.release();
			
		
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}
		
	}
	
	//Inicio REO 04.08.2017 - Riscos Corporativos
	public void setcategory(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			
			if(sourceRec.containsField(columnMap.getSource())){
				Boolean corprisk = (Boolean)sourceRec.getField(columnMap.getSource());
				if(corprisk)
					targetRec.setString(columnMap.getTarget(), "Risco Corporativo");
			}
			
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}
	}
	
	public void settype(IMigrationRecord sourceRec, IMigrationRecord targetRec, IColumnMap columnMap)
			throws MigrationException {
		try{
			Long intType = new Long(0);
			if(sourceRec.containsField(columnMap.getSource())){
				Boolean corprisk = (Boolean)sourceRec.getField(columnMap.getSource());
				if(corprisk){
					intType = Long.parseLong("12");
				}else{
					intType = (Long)sourceRec.getField("type");
				}
				targetRec.setLong(columnMap.getTarget(), intType);
			}else{
				intType = (Long)sourceRec.getField("type");
				targetRec.setLong(columnMap.getTarget(), intType);
			}
		}catch(Exception e){
			throw new MigrationException(MigrationException.EX_MIGRATION_STEPS);
		}
	}
	//Fim REO 04.08.2017 - Riscos Corporativos
	
}
