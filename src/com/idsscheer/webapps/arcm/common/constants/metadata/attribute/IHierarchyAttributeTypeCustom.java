package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IHierarchyAttributeTypeCustom extends IHierarchyAttributeType {
	
	public static final String STR_CORPRISK = "corprisk";
	public static final IBooleanAttributeType ATTR_CORPRISK = (IBooleanAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CORPRISK);
	
	public static final String STR_RESIDUAL = "residual";
	public static final IStringAttributeType ATTR_RESIDUAL = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RESIDUAL);

}
