package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IControlExecutionTaskAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditsteptemplateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditsteptemplateAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAudittemplateAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeCustom;
import com.idsscheer.webapps.arcm.dl.framework.BusException;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;

public class CustomAuditProcessSelectionViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException {
		
		List<Integer> processIDList = new ArrayList();
		
		// TODO Auto-generated method stub
		try {
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			if(currentObject.getObjMetaData().getObjectType().getId().equalsIgnoreCase("AUDITSTEPTEMPLATE")){
				IAppObj astAppObj = facade.load(currentObject.getHeadObjId(), true);
				
				List<IAppObj> areaAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_AREA).getElements(userCtx);
				List<IAppObj> atAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeType.LIST_AUDITTEMPLATE).getElements(userCtx);
				
				if (areaAppList.size() > 0) {
					for (IAppObj atAppObj : atAppList) {
						
						for(IAppObj riskAppObj : atAppObj.getAttribute(IAudittemplateAttributeTypeCustom.LIST_RISK).getElements(userCtx)){
							
							for(IAppObj processAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){
								
								for(IAppObj controlAppObj : riskAppObj.getAttribute(IRiskAttributeTypeCustom.LIST_CONTROLS).getElements(userCtx)){
									
									List<IAppObj> cetAppList = controlAppObj.getAttribute(IControlAttributeTypeCustom.LIST_CONTROLEXECUTIONTASKS).getElements(userCtx);
									for (IAppObj cetAppObj : cetAppList) {
										
										List<IAppObj> orgUnitAppList = cetAppObj.getAttribute(IControlExecutionTaskAppObj.LIST_AFFECTED_ORGUNIT).getElements(userCtx);
										for (IAppObj orgUnitAppObj : orgUnitAppList) {
											
											for (IAppObj astAreaAppObj : areaAppList) {
												if (astAreaAppObj.equals(orgUnitAppObj)) {
													processIDList.add((int)processAppObj.getObjectId());
												}
											}
											
										}
									}
								
								}
								
							}
							
						}
						
					}
				} else {
					for (IAppObj atAppObj : atAppList) {
						
						for(IAppObj riskAppObj : atAppObj.getAttribute(IAudittemplateAttributeTypeCustom.LIST_RISK).getElements(userCtx)){
							
							for(IAppObj processAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){
								System.out.println(processAppObj.getAttribute(IHierarchyAttributeType.ATTR_NAME).getRawValue());
								processIDList.add((int)processAppObj.getObjectId());
							}
							
						}
						
					}
				}
			}
			
			if(processIDList.size() > 0)
				filters.add(new SimpleFilterCriteria("process_id", DataLayerComparator.IN, processIDList));
			
		} catch (RightException | BusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
