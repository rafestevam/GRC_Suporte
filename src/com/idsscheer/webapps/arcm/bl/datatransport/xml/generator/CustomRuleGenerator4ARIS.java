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
			//Inicio REO - 13.10.2017 - EV115914
			printColumnMap("setFinal1line", ObjectType.RISK.getId(), "ra_final1line", "ra_final1line");
			printColumnMap("setFinal2line", ObjectType.RISK.getId(), "ra_final2line", "ra_final2line");
			printColumnMap("setFinal3line", ObjectType.RISK.getId(), "ra_final3line", "ra_final3line");
			printColumnMap("setInef1line", ObjectType.RISK.getId(), "ra_inef1line", "ra_inef1line");
			printColumnMap("setInef2line", ObjectType.RISK.getId(), "ra_inef2line", "ra_inef2line");
			printColumnMap("setInef3line", ObjectType.RISK.getId(), "ra_inef3line", "ra_inef3line");
			printColumnMap("setResidual1line", ObjectType.RISK.getId(), "ra_residual1line", "ra_residual1line");
			printColumnMap("setResidual2line", ObjectType.RISK.getId(), "ra_residual2line", "ra_residual2line");
			printColumnMap("setResidual3line", ObjectType.RISK.getId(), "ra_residual3line", "ra_residual3line");
			printColumnMap("setResidualfinal", ObjectType.RISK.getId(), "ra_residualfinal", "ra_residualfinal");
			//Fim REO - 13.10.2017 - EV115914
		}
		
		if (smd.getDataLayerObjectType().getName().equalsIgnoreCase(ObjectType.CONTROL.getId())){
			printColumnMap("setStatusControl", ObjectType.CONTROL.getId(), "custom_status_control", "custom_status_control");

		}
		
		//REO 04.08.2017 - Riscos Corporativos
		if (smd.getDataLayerObjectType().getName().equalsIgnoreCase(ObjectType.HIERARCHY.getId())){
			printColumnMap("setCategory", ObjectType.HIERARCHY.getId(), "corprisk", "rc_category");
			printColumnMap("setType", ObjectType.HIERARCHY.getId(), "corprisk", "rc_csttype");
			printColumnMap("setResidualResult", ObjectType.HIERARCHY.getId(), "corprisk", "residual");
		}
		
	}
	
	protected void printObjectAndVersionColumMaps(IDataLayerObjMetaData smd) {
		super.printObjectAndVersionColumMaps(smd);
	}
	
	protected void printColumMap(IDataLayerAttrMetaDataList attr, IDataLayerObjMetaData smd) {
		super.printColumMap(attr, smd);
	}	
	
}
