package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;

public interface IControlexecutionAttributeTypeCustom extends IControlexecutionAttributeType {

	public final static String STR_CUSTOMCTRLEXECSTATUS = "custom_controlexecstatus";
	public final static IEnumAttributeType ATTR_CUSTOMCTRLEXECSTATUS = (IEnumAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOMCTRLEXECSTATUS);
	
}
