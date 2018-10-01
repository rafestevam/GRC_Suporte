package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;

public interface IQuotationAttributeTypeCustom extends IQuotationAttributeType {
	
	public static final String STR_HEIGHT = "height";
	public static final IDoubleAttributeType ATTR_HEIGHT = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_HEIGHT);

}
