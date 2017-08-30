package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;

public interface IHierarchyAttributeTypeCustom extends IHierarchyAttributeType {
	
	public static final String STR_CORPRISK = "corprisk";
	public static final IBooleanAttributeType ATTR_CORPRISK = (IBooleanAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CORPRISK);

}
