package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IControlAttributeTypeCustom extends IControlAttributeType {
	
	public final static String STR_CUSTOM_STATUS = "custom_status_control";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS);

}
