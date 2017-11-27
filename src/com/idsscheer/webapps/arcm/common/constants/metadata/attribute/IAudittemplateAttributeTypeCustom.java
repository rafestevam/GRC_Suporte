package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

public interface IAudittemplateAttributeTypeCustom extends IAudittemplateAttributeType {
	
	public static final String STR_RISK = "risk";
	public static final IListAttributeType LIST_RISK = (IListAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RISK);

}
