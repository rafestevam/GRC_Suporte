package com.idsscheer.webapps.arcm.bl.datatransport.xml.generator;

import com.idsscheer.webapps.arcm.bl.datatransport.xml.ExportModus;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.dl.framework.metadata.IDataLayerAttrMetaDataList;
import com.idsscheer.webapps.arcm.dl.framework.metadata.IDataLayerObjMetaData;

public class CustomRuleGenerator4ARIS extends XMLImportMigrationRuleGenerator4ARIS {
	protected ExportModus getExportModus() {
		return super.getExportModus();
	}
	
	protected void printMerge(IDataLayerObjMetaData smd) {
		super.printMerge(smd);
	}
	
	protected String[] getObjectAndVersionColumnNames() {
		return super.getObjectAndVersionColumnNames();
	}
	
	protected String getCreatorUserFunctionName() {
		return super.getCreatorUserFunctionName();
	}
	
	protected String getChangeUserFunctionName() {
		return super.getChangeTypeFunctionName();
	}
	
	protected String getChangeTypeFunctionName() {
		return super.getChangeTypeFunctionName();
	}	
	
	protected void printCustomColumMaps(IDataLayerObjMetaData smd) {
		super.printCustomColumMaps(smd);
		if (smd.getDataLayerObjectType().getName().equalsIgnoreCase(ObjectType.RISK.getId())){
			printColumnMap("setStartDate", ObjectType.RISK.getId(), "startdate", "startdate");
			printColumnMap("setEndDate", ObjectType.RISK.getId(), "enddate", "enddate");
			printColumnMap("setRAResult", ObjectType.RISK.getId(), "ra_result", "ra_result");
			printColumnMap("setRAControl1Line", ObjectType.RISK.getId(), "ra_control1line", "ra_control1line");
			printColumnMap("setRAControl2Line", ObjectType.RISK.getId(), "ra_control2line", "ra_control2line");
			printColumnMap("setRAControl3Line", ObjectType.RISK.getId(), "ra_control3line", "ra_control3line");
			printColumnMap("setRAControlFinal", ObjectType.RISK.getId(), "ra_controlfinal", "ra_controlfinal");
		}
		
		if (smd.getDataLayerObjectType().getName().equalsIgnoreCase(ObjectType.CONTROL.getId())){
			printColumnMap("setStatusControl", ObjectType.CONTROL.getId(), "custom_status_control", "custom_status_control");

		}
		
		//REO 04.08.2017 - Riscos Corporativos
		if (smd.getDataLayerObjectType().getName().equalsIgnoreCase(ObjectType.HIERARCHY.getId())){
			printColumnMap("setCategory", ObjectType.HIERARCHY.getId(), "corprisk", "rc_category");
			printColumnMap("setType", ObjectType.HIERARCHY.getId(), "corprisk", "rc_csttype");
		}
		
	}
	
	protected void printObjectAndVersionColumMaps(IDataLayerObjMetaData smd) {
		super.printObjectAndVersionColumMaps(smd);
	}
	
	protected void printColumMap(IDataLayerAttrMetaDataList attr, IDataLayerObjMetaData smd) {
		super.printColumMap(attr, smd);
	}	
	
}
