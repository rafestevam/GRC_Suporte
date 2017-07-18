package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IRiskassessmentAttributeTypeCustom extends IRiskassessmentAttributeType {

	public static final String STR_RESULT_ASSESSMENT = "result_assessment";
	public static final String STR_HEIGHT = "height";
	public static final String STR_PROGRESS = "progress";
	public static final String STR_RISK_ID = "risk_id";
	
	public static final IStringAttributeType ATTR_RESULT_ASSESSMENT = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RESULT_ASSESSMENT);
	public static final IDoubleAttributeType ATTR_HEIGHT = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_HEIGHT);
	public static final IEnumAttributeType ATTR_PROGRESS = (IEnumAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_PROGRESS);
	public static final IStringAttributeType ATTR_RISK_ID = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RISK_ID);
	
}
