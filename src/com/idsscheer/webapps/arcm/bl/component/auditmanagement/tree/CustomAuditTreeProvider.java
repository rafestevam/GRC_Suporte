package com.idsscheer.webapps.arcm.bl.component.auditmanagement.tree;

import com.idsscheer.annotations.Since;
import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.framework.tree.AbstractAppObjTreeProvider;
import com.idsscheer.webapps.arcm.bl.framework.tree.IAppObjTreeProvider;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeFactory;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeNodeFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.SequencedLocationComparator;
import com.idsscheer.webapps.arcm.bl.framework.tree.TreeManager;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.Versions;
import com.idsscheer.webapps.arcm.common.util.ParameterList;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;

import java.util.Comparator;
import java.util.Locale;

/**
 * Provides a audit- or audit-template-client tree, depending on any given audit object (audit, template, step, ...) 
 * and AuditPlanTreeFactory server-tree implementation. 
 * <pre>
 *
 * example tree:
 *
 * -AUDIT
 *  +-AUDITSTEP
 *  | +-AUDITSTEP
 *  +-AUDITSTEP
 *  | +-AUDITSTEP
 *  | +-AUDITSTEP
 *
 * -AUDIT-TEMPLATE
 *  +-AUDITSTEP-TEMPLATE
 *  | +-AUDITSTEP-TEMPLATE
 *  +-AUDITSTEP-TEMPLATE
 *  | +-AUDITSTEP-TEMPLATE
 *  | +-AUDITSTEP-TEMPLATE
 *
 * </pre>
 *
 * @author mime
 * @since 9.8.2
 */


@Since(Versions.ARCM_9_8_2_0)
public class CustomAuditTreeProvider extends AbstractAppObjTreeProvider implements IAppObjTreeProvider {

    public IServerTree.Key registerTree() {
    	    	
        IAppObj rootObject = getRootAppObj();
        long rootObjId;
        String typeOfTree = getObjectTypeOfRoot(rootObject); // 'audit' or 'audit-template'
        if (isRoot(rootObject)) {
            rootObjId = rootObject.getObjectId();
        } else {
            IListAttribute attribute = rootObject.getAttribute((IAttributeType)rootObject.getAttributeType(typeOfTree));
            rootObjId = attribute.getElementIds().get(0).getId();
        }
        ParameterList params = new ParameterList();
            params.setLong(IServerTreeFactory.ParameterKeys.OBJECT_ID, rootObjId);
            params.setString(IServerTreeFactory.ParameterKeys.OBJECT_TYPE, typeOfTree.toUpperCase(Locale.ENGLISH));
            params.setString(IServerTreeFactory.ParameterKeys.VIEW_ID, typeOfTree + "PlanTree");
        
        TreeManager tm = TreeManager.getInstance();
        return tm.registerTree(CustomAuditPlanTreeFactory.createInstance(), params);
    }

    /**
     * @return the object-type of the root-node 
     */
    protected String getObjectTypeOfRoot(IAppObj rootObject) {
        String typeId = rootObject.getObjectType().getId();
        return typeId.toLowerCase(Locale.ENGLISH).replaceAll("step", "");
    }

    /**
     * @return true if the root item of the tree is a audit-step or a audit-step-template  
     */
    protected boolean isRoot(IAppObj rootObject) {
        String typeId = rootObject.getObjectType().getId();
        return !typeId.contains("STEP");
    }

    /**
     * @return filter which accepts: 
     *              active steps of an active audit,
     *              all steps of an deleted audit 
     */
    public IServerTreeNodeFilter getServerTreeNodeFilter() {
        final boolean deleted = getRootAppObj().getVersionData().isDeleted();
        return new IServerTreeNodeFilter() {
            public boolean accept(IServerTreeNode node) {
                if (deleted) {
                    return true;
                }
                return node.isActive();
            }
        };
    }

    /**
     * order of steps is defined manually by user
     */
    public Comparator<IClientTreeNode> getComparator() {
        return SequencedLocationComparator.getInstance(SequencedLocationComparator.Mode.ASC);
    }
}
