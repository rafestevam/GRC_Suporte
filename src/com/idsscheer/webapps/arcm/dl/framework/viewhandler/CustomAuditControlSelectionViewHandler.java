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
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.BusException;
import com.idsscheer.webapps.arcm.dl.framework.BusViewException;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IDataLayerObject;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IRightsFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.QueryDefinition;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;

public class CustomAuditControlSelectionViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException {
		
		List<Integer> ctrlIDList = new ArrayList();
		
		// TODO Auto-generated method stub
		try {
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			if(currentObject.getObjMetaData().getObjectType().getId().equalsIgnoreCase("AUDITSTEPTEMPLATE")){
				IAppObj astAppObj = facade.load(currentObject.getHeadObjId(), true);
				
				// Audit Template List.
				List<IAppObj> atAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeType.LIST_AUDITTEMPLATE).getElements(userCtx);
				// Area List dentro desta ficha de auditoria.
				List<IAppObj> astAreaAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_AREA).getElements(userCtx);
				// Process List dentro desta ficha de auditoria.
				List<IAppObj> astProcessAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_PROCESS).getElements(userCtx);
				// Process List dentro desta ficha de auditoria.
				List<IAppObj> astSubprocessAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_SUBPROCESS).getElements(userCtx);
				
				for (IAppObj atAppObj : atAppList) {
					
						for(IAppObj riskAppObj : atAppObj.getAttribute(IAudittemplateAttributeTypeCustom.LIST_RISK).getElements(userCtx)){

							if (astSubprocessAppList.size() > 0) {
								for (IAppObj astSubprocessObj : astSubprocessAppList) {
									for(IAppObj riskProcessAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){
										for(IAppObj subProcessAppObj : riskProcessAppObj.getAttribute(IHierarchyAttributeTypeCustom.LIST_CHILDREN).getElements(userCtx)){	
											if (subProcessAppObj.equals(astSubprocessObj)) {
												for(IAppObj ctrlAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx)){
													ctrlIDList.add((int)ctrlAppObj.getObjectId());
												}	
											}
										}
									}								
								}
							} else {
								// EV109172 - ATV3 - Só faz filtro recursivo de processos caso
								// houver processos na ficha de auditoria (Audit Step Template).
								if (astProcessAppList.size() > 0) {
									for (IAppObj astProcessObj : astProcessAppList) {
										for(IAppObj riskProcessAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){
											if (astProcessObj.equals(riskProcessAppObj)) {
												for(IAppObj ctrlAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx)){
													ctrlIDList.add((int)ctrlAppObj.getObjectId());
												}	
											}
										}								
									}
								} else {
									if (astAreaAppList.size() > 0) {
										for(IAppObj ctrlAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx)){
											for (IAppObj cetAppObj : ctrlAppObj.getAttribute(IControlAttributeTypeCustom.LIST_CONTROLEXECUTIONTASKS).getElements(userCtx)) {
												for (IAppObj orgUnitAppObj : cetAppObj.getAttribute(IControlExecutionTaskAppObj.LIST_AFFECTED_ORGUNIT).getElements(userCtx)) {
													for (IAppObj astAreaAppObj : astAreaAppList) {
														if (astAreaAppObj.equals(orgUnitAppObj)) {
															ctrlIDList.add((int)ctrlAppObj.getObjectId());
														}
													}
												}
											}
										}
									} else {
										for(IAppObj ctrlAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx)){
											ctrlIDList.add((int)ctrlAppObj.getObjectId());
										}
									}
								}
							}							
						}
					}
				}
				
			if(ctrlIDList.size() > 0)
				filters.add(new SimpleFilterCriteria("ct_id", DataLayerComparator.IN, ctrlIDList));
			
		} catch (RightException | BusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
