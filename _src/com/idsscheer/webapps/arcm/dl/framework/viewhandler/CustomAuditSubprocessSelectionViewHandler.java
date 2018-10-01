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
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlexecutionAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeCustom;
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

public class CustomAuditSubprocessSelectionViewHandler implements IViewHandler {

	@Override
	public void handleView(QueryDefinition query, List<IRightsFilterCriteria> rightsFilters,
			List<IFilterCriteria> filters, IDataLayerObject currentObject,
			QueryDefinition parentQuery) throws BusViewException {
		
		List<Integer> subprocessListID = new ArrayList();
		
		// TODO Auto-generated method stub
		try {
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			if(currentObject.getObjMetaData().getObjectType().getId().equalsIgnoreCase("AUDITSTEPTEMPLATE")){
				IAppObj astAppObj = facade.load(currentObject.getHeadObjId(), true);
				
				// Audit Template List.
				List<IAppObj> atAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeType.LIST_AUDITTEMPLATE).getElements(userCtx);
				// Área List dentro desta ficha de auditoria.
				List<IAppObj> areaAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_AREA).getElements(userCtx);
				// Process List dentro desta ficha de auditoria.
				List<IAppObj> processAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeTypeCustom.LIST_PROCESS).getElements(userCtx);
				
				for (IAppObj atAppObj : atAppList) {
					
					for(IAppObj riskAppObj : atAppObj.getAttribute(IAudittemplateAttributeTypeCustom.LIST_RISK).getElements(userCtx)){
						
						if (processAppList.size() > 0) {
							for(IAppObj processAppObj : processAppList){
								for(IAppObj subProcessAppObj : processAppObj.getAttribute(IHierarchyAttributeTypeCustom.LIST_CHILDREN).getElements(userCtx)){
									subprocessListID.add((int)subProcessAppObj.getObjectId());
								}
							}
						} else {
							if (areaAppList.size() > 0) {
								// Filtro de processo preenchido.
								for (IAppObj controlAppObj : riskAppObj.getAttribute(IRiskAttributeTypeCustom.LIST_CONTROLS).getElements(userCtx)) { // Loop nos controles deste risco.
									for (IAppObj cetAppObj : controlAppObj.getAttribute(IControlAttributeTypeCustom.LIST_CONTROLEXECUTIONTASKS).getElements(userCtx)) { // Loop nas tarefas de execução dos controles associados.
										for (IAppObj controlAreaAppObj : cetAppObj.getAttribute(IControlExecutionTaskAppObj.LIST_AFFECTED_ORGUNIT).getElements(userCtx)) { // Loop nas tarefas de execução dos controles associados.
											for (IAppObj areaAppObj : areaAppList) {
												if (areaAppObj.equals(controlAreaAppObj)) {
													for(IAppObj processAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){															
														for(IAppObj subProcessAppObj : processAppObj.getAttribute(IHierarchyAttributeTypeCustom.LIST_CHILDREN).getElements(userCtx)){
															subprocessListID.add((int)subProcessAppObj.getObjectId());
														}
													}	
												}
											}
										}	
									}
								}	
							} else {
								for(IAppObj processAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_PROCESS).getElements(userCtx)){
									for(IAppObj subProcessAppObj : processAppObj.getAttribute(IHierarchyAttributeTypeCustom.LIST_CHILDREN).getElements(userCtx)){
										subprocessListID.add((int)subProcessAppObj.getObjectId());
									}
								}
							}
						}
					}
				}
			}
			
			if(subprocessListID.size() > 0) {
				filters.add(new SimpleFilterCriteria("subprocess_id", DataLayerComparator.IN, subprocessListID));
			} else {
				// Subprocesso Dummy para não aparecer nenhum subprocesso na lista. 
				subprocessListID.add(999999);  
				filters.add(new SimpleFilterCriteria("subprocess_id", DataLayerComparator.IN, subprocessListID));
			}
				
			
		} catch (RightException | BusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
