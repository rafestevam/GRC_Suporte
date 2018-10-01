package com.idsscheer.webapps.arcm.bl.component.auditmanagement.evaluation.chart.gantt;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.component.auditmanagement.tree.AuditPlanTreeNodeModel;
import com.idsscheer.webapps.arcm.bl.component.auditmanagement.tree.CustomAuditPlanTreeNodeModel;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.ITreeNodeModel;
import com.idsscheer.webapps.arcm.bl.framework.tree.ITreeNodeModel.ModelKey;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditstepAttributeType;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author mime
 * @since 4.0 (26.04.2011, 12:56:56)
 */


class CustomActivityData implements Comparable {

    private final String id;
    
    private final String objectType;
    private final String tile;
    private final String tileLabel;
    private final String description;
    private final String descriptionLabel;
    
    private final Date plannedStart;
    private final String plannedStartLabel;    
    private final Date plannedEnd;
    private final String plannedEndLabel;
    
    private final Date actualStart;
    private final String actualStartLabel;
    private Date actualEnd;
    private final String actualEndLabel;  
    
    private IEnumerationItem ownerState;
    private IEnumerationItem reviewerStatus;
    
    private final String ownerStateLabel;
    private final String reviewerStateLabel;
    
    private final boolean isFinished;
    
    private  Long percentage_completed;
    private  String percentage_completedLabel;
    
    private IClientTreeNode parent = null;
    private List<Long> subActivities;    


    public CustomActivityData(IClientTreeNode node, IClientTreeNode parent, Locale locale) {
    	
        IResourceBundle bundle = ResourceBundleFactory.getBundle(locale);

        this.parent = parent;    
        this.id= String.valueOf(node.getModel().getValue(CustomAuditPlanTreeNodeModel.OBJECT_ID).getId());
        this.tile = node.getName();       
        this.description = node.getModel().getValue(CustomAuditPlanTreeNodeModel.DESCRIPTION);
        this.plannedStart = node.getModel().getValue(CustomAuditPlanTreeNodeModel.AUDITSTARTDATE);
        this.plannedEnd = node.getModel().getValue(CustomAuditPlanTreeNodeModel.AUDITENDDATE);
        this.actualStart = node.getModel().getValue(CustomAuditPlanTreeNodeModel.ACTUALSTARTDATE);
        this.actualEnd = node.getModel().getValue(CustomAuditPlanTreeNodeModel.ACTUALENDDATE);
        this.ownerState = node.getModel().getValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS);
        this.reviewerStatus = node.getModel().getValue(CustomAuditPlanTreeNodeModel.REVIEWER_STATUS);
        this.percentage_completed = node.getModel().getValue(CustomAuditPlanTreeNodeModel.PERCENTAGE_COMPLETED);
        

        if (actualEnd == null && actualStart != null) {
            actualEnd = DateUtils.normalizeLocalDate(DateUtils.today(), DateUtils.Target.END_OF_DAY);
            isFinished = false;
        } else {
            isFinished = true;
        }

        IObjectType type = Metadata.getMetadata().getObjectType(node.getNodeType().getName());
        if (ObjectType.AUDIT.equals(type)) {
            this.tileLabel = bundle.getString(IAuditAttributeType.ATTR_NAME.getPropertyKey()); 
            this.descriptionLabel = bundle.getString(IAuditAttributeType.ATTR_DESCRIPTION.getPropertyKey()); 
            this.plannedStartLabel = bundle.getString(IAuditAttributeType.ATTR_PLANNEDSTARTDATE.getPropertyKey()); 
            this.plannedEndLabel = bundle.getString(IAuditAttributeType.ATTR_PLANNEDENDDATE.getPropertyKey()); 
            this.actualStartLabel = bundle.getString(IAuditAttributeType.ATTR_ACTUALSTARTDATE.getPropertyKey()); 
            this.actualEndLabel = bundle.getString(IAuditAttributeType.ATTR_ACTUALENDDATE.getPropertyKey());    
            this.ownerStateLabel = bundle.getString(IAuditAttributeType.ATTR_OWNER_STATUS.getPropertyKey());
            this.reviewerStateLabel = bundle.getString(IAuditAttributeType.ATTR_REVIEWER_STATUS.getPropertyKey());
            this.percentage_completedLabel = bundle.getString("attribute.AUDITSTEP.percentage_completed.DBI");
        } 
        else {
            this.tileLabel = bundle.getString(IAuditstepAttributeType.ATTR_NAME.getPropertyKey());
            this.descriptionLabel = bundle.getString(IAuditstepAttributeType.ATTR_DESCRIPTION.getPropertyKey());
            this.plannedStartLabel = bundle.getString(IAuditstepAttributeType.ATTR_PLANNEDSTARTDATE.getPropertyKey());
            this.plannedEndLabel = bundle.getString(IAuditstepAttributeType.ATTR_PLANNEDENDDATE.getPropertyKey());
            this.actualStartLabel = bundle.getString(IAuditstepAttributeType.ATTR_ACTUALSTARTDATE.getPropertyKey());
            this.actualEndLabel = bundle.getString(IAuditstepAttributeType.ATTR_ACTUALENDDATE.getPropertyKey());   
            this.ownerStateLabel = bundle.getString(IAuditstepAttributeType.ATTR_OWNER_STATUS.getPropertyKey());
            this.reviewerStateLabel = bundle.getString(IAuditstepAttributeType.ATTR_REVIEWER_STATUS.getPropertyKey());
            this.percentage_completedLabel = bundle.getString("attribute.AUDITSTEP.percentage_completed.DBI");
        }
        for (IClientTreeNode child : node.getChildren()) {
            long childId = child.getModel().getValue(ITreeNodeModel.OBJECT_ID).getId();
            addSubActivity(childId);
        }
        objectType = type.getId();              
    }

    public CustomActivityData(IViewObj viewObj, Locale locale) {

         IResourceBundle bundle = ResourceBundleFactory.getBundle(locale);

        this.parent = null;    
        this.id = String.valueOf(viewObj.getRawValue("auditId"));
        this.tile = (String)viewObj.getRawValue("auditName");
        this.description = (String)viewObj.getRawValue("auditDescription");
        
        this.plannedStart = (Date)viewObj.getRawValue("plannedStartDate");
        this.plannedEnd = (Date)viewObj.getRawValue("plannedEndDate");
        this.actualStart = (Date)viewObj.getRawValue("actualStartDate");
        this.actualEnd = (Date)viewObj.getRawValue("actualEndDate");
        this.ownerState = viewObj.getEnumItem("ownerStatus");
        this.reviewerStatus = viewObj.getEnumItem("reviewerStatus");
        
        this.percentage_completed = (Long)viewObj.getRawValue("percentage_completed");
        
        if (actualEnd == null && actualStart != null) {
            actualEnd = DateUtils.normalizeLocalDate(DateUtils.today(), DateUtils.Target.END_OF_DAY);
            isFinished = false;
        } else {
            isFinished = true;
        }
        
        this.tileLabel = bundle.getString(IAuditAttributeType.ATTR_NAME.getPropertyKey()); 
        this.descriptionLabel = bundle.getString(IAuditAttributeType.ATTR_DESCRIPTION.getPropertyKey()); 
        this.plannedStartLabel = bundle.getString(IAuditAttributeType.ATTR_PLANNEDSTARTDATE.getPropertyKey()); 
        this.plannedEndLabel = bundle.getString(IAuditAttributeType.ATTR_PLANNEDENDDATE.getPropertyKey()); 
        this.actualStartLabel = bundle.getString(IAuditAttributeType.ATTR_ACTUALSTARTDATE.getPropertyKey()); 
        this.actualEndLabel = bundle.getString(IAuditAttributeType.ATTR_ACTUALENDDATE.getPropertyKey());
        this.ownerStateLabel = bundle.getString(IAuditAttributeType.ATTR_OWNER_STATUS.getPropertyKey());
        this.reviewerStateLabel = bundle.getString(IAuditAttributeType.ATTR_REVIEWER_STATUS.getPropertyKey());
        this.percentage_completedLabel = bundle.getString("attribute.AUDITSTEP.percentage_completed.DBI");

        objectType = ObjectType.AUDIT.getId();           
    }

    public String getObjectType() {
        return objectType;
    }
        
    public boolean hasParent() {
        return parent != null;
    }
    
    public int getLevel() {
        if (!hasParent()) {
            return 1;
        }
        return parent.getLevel() + 1;
    }
    
    public String getId() {
        return id;
    }

    public String getTitle() {
        return tile;
    }

    public String getDescription() {
        return description;
    }

    public IEnumerationItem getOwnerState() {
        return ownerState;
    }
    
    public IEnumerationItem getReviewerStatus() {
        return reviewerStatus;
    }

    public Date getPlannedStart() {
        return plannedStart;
    }

    public Date getPlannedEnd() {
        return plannedEnd;
    }

    public Date getActualStart() {
        return actualStart;
    }

    public Date getActualEnd() {
        return actualEnd;
    }

    public String getTileLabel() {
        return tileLabel;
    }

    public String getDescriptionLabel() {
        return descriptionLabel;
    }

    public String getOwnerStateLabel() {
        return ownerStateLabel;
    }

    public String getReviewerStateLabel() {
        return reviewerStateLabel;
    }
    
    public Long getPercentageCompleted() {
    	return percentage_completed;
    }
    
    public String getPercentageCompletedLabel() {
    	return percentage_completedLabel;
    }

    public int compareTo(Object other) {
        CustomActivityData otherActivity = (CustomActivityData) other;
        int i = plannedStart.compareTo(otherActivity.plannedStart);
        if (i == 0) {
            i = tile.compareTo(otherActivity.tile);
        }
        return i;
    }

    public void addSubActivity(long childId) {
        if (subActivities == null) {
            subActivities = new ArrayList<Long>();
        }
        subActivities.add(childId);
    }

    public List<Long> getSubActivities() {
        if (subActivities == null) {
            return Collections.emptyList();
        }
        return subActivities;
    }
}
