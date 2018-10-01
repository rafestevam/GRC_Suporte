package com.idsscheer.webapps.arcm.bl.component.administration.tree;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeNode;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class CustomHierarchyServerTreeFactory extends HierarchyServerTreeFactory{
	
	protected static final Log log = LogFactory.getLog(CustomHierarchyServerTreeFactory.class);
	
	@Override
	protected void addUsergroups(IViewObj viewObj, IServerTreeNode node) {
//		IEnumerationItem enumItem = (IEnumerationItem)ARCMCollections.extractSingleEntry((List)viewObj.getRawValue("parent_tester_role"), true);
//		
//		addEnumItemContainer(viewObj, node, "parent_so_owner_id", "parent_so_owner_role", Enumerations.USERROLE_TYPE.SIGNOFFOWNER);
//		
//		if(enumItem.getValue().equals("tester"))
//			addEnumItemContainer(viewObj, node, "parent_tester_id", "parent_tester_role", Enumerations.USERROLE_TYPE.TESTER);
//		if(enumItem.getValue().equals("tester2l"))
//			addEnumItemContainer(viewObj, node, "parent_tester_id", "parent_tester_role", EnumerationsCustom.CUSTOM_USERROLE_TYPE.TESTER2L);
//		if(enumItem.getValue().equals("tester3l"))
//			addEnumItemContainer(viewObj, node, "parent_tester_id", "parent_tester_role", EnumerationsCustom.CUSTOM_USERROLE_TYPE.TESTER3L);
//		
//		addEnumItemContainer(viewObj, node, "parent_auditor_id", "parent_auditor_role", Enumerations.USERROLE_TYPE.TESTAUDITOR);
	}
	
}