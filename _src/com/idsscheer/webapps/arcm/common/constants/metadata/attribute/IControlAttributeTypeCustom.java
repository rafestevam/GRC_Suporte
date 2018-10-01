package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IControlAttributeTypeCustom extends IControlAttributeType {
	
	public final static String STR_CUSTOM_STATUS = "custom_status_control";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS);
	
	//Inicio REO - 07.02.2018 - EV133332
	public final static String STR_CUSTOM_STATUS_1LINE = "custom_st_1line";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS_1LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS_1LINE);
	
	public final static String STR_CUSTOM_STATUS_2LINE = "custom_st_2line";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS_2LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS_2LINE);
	
	public final static String STR_CUSTOM_STATUS_3LINE = "custom_st_3line";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS_3LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS_3LINE);
	
	public final static String STR_CUSTOM_STATUS_FINAL = "custom_st_final";
	public final static IStringAttributeType ATTR_CUSTOM_STATUS_FINAL = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_STATUS_FINAL);
	//Fim REO - 07.02.2018 - EV133332
	
	public final static String STR_CUSTOM_OVID = "custom_ovid";
	public final static IStringAttributeType ATTR_CUSTOM_OVID = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_CUSTOM_OVID);

}
