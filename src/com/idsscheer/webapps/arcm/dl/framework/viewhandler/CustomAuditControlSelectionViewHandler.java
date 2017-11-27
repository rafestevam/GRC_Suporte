package com.idsscheer.webapps.arcm.dl.framework.viewhandler;

import java.util.List;

import org.apache.myfaces.shared.util.LocaleUtils;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditsteptemplateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAudittemplateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAudittemplateAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.BusException;
import com.idsscheer.webapps.arcm.dl.framework.BusObjectException;
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
		
		// TODO Auto-generated method stub
		try {
			IUserContext userCtx = ContextFactory.getFullReadAccessUserContext(LocaleUtils.toLocale("US"));
			IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, currentObject.getObjMetaData().getObjectType());
			if(currentObject.getObjMetaData().getObjectType().getId().equalsIgnoreCase("AUDITSTEPTEMPLATE")){
				IAppObj astAppObj = facade.load(currentObject.getHeadObjId(), true);
				
				List<IAppObj> atAppList = astAppObj.getAttribute(IAuditsteptemplateAttributeType.LIST_AUDITTEMPLATE).getElements(userCtx);
				for (IAppObj atAppObj : atAppList) {
					
					for(IAppObj riskAppObj : atAppObj.getAttribute(IAudittemplateAttributeTypeCustom.LIST_RISK).getElements(userCtx)){
						
						for(IAppObj ctrlAppObj : riskAppObj.getAttribute(IRiskAttributeType.LIST_CONTROLS).getElements(userCtx)){
							System.out.println(ctrlAppObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue());
							filters.add(new SimpleFilterCriteria("ct_id", DataLayerComparator.EQUAL, ctrlAppObj.getObjectId()));
						}
						
					}
					
				}
				
			}
			
			//System.out.println(facade.toString());
			
			for(IFilterCriteria filter : query.getFilters()){
				//query.getFilters().remove(filter);
			}
			
		} catch (RightException | BusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
