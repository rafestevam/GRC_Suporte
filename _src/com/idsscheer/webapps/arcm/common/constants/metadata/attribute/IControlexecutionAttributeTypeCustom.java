package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IControlexecutionAttributeTypeCustom extends IControlexecutionAttributeType {

	public final static String STR_CUSTOMCTRLEXECSTATUS = "custom_controlexecstatus";
	public final static String STR_CONTROL_ID = "control_id";
	
	public final static IEnumAttributeType ATTR_CUSTOMCTRLEXECSTATUS = (IEnumAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOMCTRLEXECSTATUS);
	public final static IStringAttributeType ATTR_CONTROL_ID = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CONTROL_ID);
	
}
