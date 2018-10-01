package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDoubleAttribute;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;

public interface IRiskAttributeTypeCustom extends IRiskAttributeType {
	
	public static final String STR_RA_RESULT = "ra_result";
	public static final String STR_RA_CONTROL1LINE = "ra_control1line";
	public static final String STR_RA_CONTROL2LINE = "ra_control2line";
	public static final String STR_RA_CONTROL3LINE = "ra_control3line";
	public static final String STR_RA_CONTROLFINAL = "ra_controlfinal";
	public static final String STR_RA_RESIDUAL1LINE = "ra_residual1line";
	public static final String STR_RA_RESIDUAL2LINE = "ra_residual2line";
	public static final String STR_RA_RESIDUAL3LINE = "ra_residual3line";
	public static final String STR_RA_RESIDUALFINAL = "ra_residualfinal";
	public static final String STR_RA_INEF1LINE = "ra_inef1line";
	public static final String STR_RA_FINAL1LINE = "ra_final1line";
	public static final String STR_RA_INEF2LINE = "ra_inef2line";
	public static final String STR_RA_FINAL2LINE = "ra_final2line";
	public static final String STR_RA_INEF3LINE = "ra_inef3line";
	public static final String STR_RA_FINAL3LINE = "ra_final3line";
	
	public static final IStringAttributeType ATTR_RA_RESULT = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_RESULT);
	public static final IStringAttributeType ATTR_RA_CONTROL1LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_CONTROL1LINE);
	public static final IStringAttributeType ATTR_RA_CONTROL2LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_CONTROL2LINE);
	public static final IStringAttributeType ATTR_RA_CONTROL3LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_CONTROL3LINE);
	public static final IStringAttributeType ATTR_RA_CONTROLFINAL = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_CONTROLFINAL);
	public static final IStringAttributeType ATTR_RA_RESIDUAL1LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_RESIDUAL1LINE);
	public static final IStringAttributeType ATTR_RA_RESIDUAL2LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_RESIDUAL2LINE);
	public static final IStringAttributeType ATTR_RA_RESIDUAL3LINE = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_RESIDUAL3LINE);
	public static final IStringAttributeType ATTR_RA_RESIDUALFINAL = (IStringAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_RESIDUALFINAL);
	public static final IDoubleAttributeType ATTR_RA_INEF1LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_INEF1LINE);
	public static final IDoubleAttributeType ATTR_RA_FINAL1LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_FINAL1LINE);
	public static final IDoubleAttributeType ATTR_RA_INEF2LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_INEF2LINE);
	public static final IDoubleAttributeType ATTR_RA_FINAL2LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_FINAL2LINE);
	public static final IDoubleAttributeType ATTR_RA_INEF3LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_INEF3LINE);
	public static final IDoubleAttributeType ATTR_RA_FINAL3LINE = (IDoubleAttributeType)MetadataConstantsUtil.create(OBJECT_TYPE, STR_RA_FINAL3LINE);

}
