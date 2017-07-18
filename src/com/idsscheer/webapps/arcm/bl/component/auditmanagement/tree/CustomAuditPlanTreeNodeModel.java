package com.idsscheer.webapps.arcm.bl.component.auditmanagement.tree;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.framework.tree.DefaultTreeNodeModel;
import com.idsscheer.webapps.arcm.common.util.Factory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

import java.util.Date;

/**
 *  
 * @since 4.0 (08.04.2011, 12:42:35)
 */
public class CustomAuditPlanTreeNodeModel extends DefaultTreeNodeModel {

    public static final ModelKey<Date> AUDITSTARTDATE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<Date> AUDITENDDATE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<Date> ACTUALSTARTDATE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<Date> ACTUALENDDATE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<String> OWNER_GROUP = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<String> SCOPE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<IEnumerationItem> OWNER_STATUS = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<IEnumerationItem> REVIEWER_STATUS = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<Long> SCOPE_ID = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<IEnumerationItem> SCOPE_TYPE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<String> DESCRIPTION = ModelKey.createInstance(ModelKey.Type.OBJECT);
    public static final ModelKey<Object> OBJECT_TYPE = ModelKey.createInstance(ModelKey.Type.OBJECT);
    
    public static final ModelKey<Long> PERCENTAGE_COMPLETED = ModelKey.createInstance(ModelKey.Type.OBJECT);
    
    static {
        registerModelKeys(CustomAuditPlanTreeNodeModel.class);
    }

    /* DO NOT USE DIRECTLY - Use #createInstance() instead */
    protected CustomAuditPlanTreeNodeModel() {}

    /**
     * Use this method in oder to obtain a new instance of this class
     *
     * @return Returns a new instance of this class
     */
    public static CustomAuditPlanTreeNodeModel createInstance() {
        return Factory.createInstance(CustomAuditPlanTreeNodeModel.class);
    }
}
