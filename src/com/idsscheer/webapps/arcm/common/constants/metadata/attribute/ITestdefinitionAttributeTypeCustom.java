package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;

public interface ITestdefinitionAttributeTypeCustom extends ITestdefinitionAttributeType {

	public static final String STR_ORIGEMTESTE = "origemteste";
	public static final IEnumAttributeType ATTR_ORIGEMTESTE = (IEnumAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_ORIGEMTESTE);
	
}
