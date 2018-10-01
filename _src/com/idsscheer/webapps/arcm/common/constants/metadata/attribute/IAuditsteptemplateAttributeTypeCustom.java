package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;

public interface IAuditsteptemplateAttributeTypeCustom extends IAuditsteptemplateAttributeType {
	
	public static final String STR_AREA = "area";
	public static final IListAttributeType LIST_AREA = (IListAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_AREA);
	
	public static final String STR_PROCESS = "process";
	public static final IListAttributeType LIST_PROCESS = (IListAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_PROCESS);
	
	public static final String STR_SUBPROCESS = "subprocess";
	public static final IListAttributeType LIST_SUBPROCESS = (IListAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_SUBPROCESS);

}
