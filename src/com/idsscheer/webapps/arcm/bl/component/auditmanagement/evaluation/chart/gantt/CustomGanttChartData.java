package com.idsscheer.webapps.arcm.bl.component.auditmanagement.evaluation.chart.gantt;

import com.idsscheer.annotations.Since;
import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.component.auditmanagement.support.AuditTreeUtility;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.util.ChartUtility;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeLogic;
import com.idsscheer.webapps.arcm.bl.framework.tree.ClientTreeNodeGatherer;
import com.idsscheer.webapps.arcm.bl.framework.tree.DescendantFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IClientTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.ITreeNodeModel;
import com.idsscheer.webapps.arcm.bl.models.filter.IFilterAttributeSet;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.common.constants.Versions;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditstepAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditsteptemplateAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAudittemplateAttributeType;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDateAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.StringAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.tree.INodeType;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;

import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author mime
 * @since 4.0 (26.04.2011, 12:55:29)
 */
@Since(Versions.ARCM_4_0_0) 
class CustomGanttChartData {

    enum Type {
        AUDIT,
        TEMPLATE,
        CROSS_AUDIT
    }
        
    private static final String VIEW = "crossAuditGanttChart";
    
    private final IUserContext fullReadCtx;
    private final IClientTree tree;        
    private final IAppObj auditObj;
    private final Locale locale;
    
    private List<CustomActivityData> activities = new ArrayList<CustomActivityData>();
    private Map<Long, CustomActivityData> activityMap = new HashMap<Long, CustomActivityData>();
    private String title; 
    private String description;    
    
    private String[] aIDs;
    private String[] aTitles;
    private long rangeInDays = -1;
    
    private Date startDate;
    private Date endDate;
    private Boolean leftMarginRequired = null;

    
    public CustomGanttChartData(IAppObj auditObj, Date plannedStartDate, Date plannedEndDate, Locale locale) {
    	
        if (auditObj == null) {
            throw new IllegalArgumentException("auditObj must not be null");
        }
        this.auditObj = auditObj;
        this.locale = locale;
        this.fullReadCtx = ContextFactory.getFullReadAccessUserContext(locale);      
                
        tree = AuditTreeUtility.getAuditTree(auditObj);
        
        IClientTreeNode root = AuditTreeUtility.getAuditTreeRoot(tree, auditObj);
        if (root != null && root.getId() != null) {
            tree.setFilter(new DescendantFilter(root.getId(), true));
        } 
        initData(plannedStartDate, plannedEndDate, locale);
        initArrays();
    }

    public CustomGanttChartData(IFilterAttributeSet filterAttributeSet, Locale locale) {
    	
        this.auditObj = null;
        this.locale = locale;
        this.fullReadCtx = ContextFactory.getFullReadAccessUserContext(locale);        
        tree = null;
        initCrossClientAuditData(filterAttributeSet);
        initArrays();
    }

    public boolean isChartDataComplete() {
        if (auditObj == null) {
            return true;
        }
        if (AuditTreeUtility.getAuditTreeRoot(tree, auditObj) == null) {
            return false;
        }
        return true;
    }
    
    public Type getType() {
        if (auditObj == null) {
            return Type.CROSS_AUDIT;
        }
        if (auditObj.getObjectType().equals(ObjectType.AUDIT)) {            
            return Type.AUDIT;
        } 
        return Type.TEMPLATE;
    }
    
    private void initCrossClientAuditData(IFilterAttributeSet filterAttributeSet) {
        Map<String, Object> hashMap = new HashMap<String, Object>();

        IViewQuery query = QueryFactory.createQuery(fullReadCtx, VIEW, filterAttributeSet, hashMap);
        try {
            Iterator<IViewObj> objIterator = query.getResultIterator();
            while (objIterator.hasNext()) {
                IViewObj viewObj = objIterator.next();
                activities.add( new CustomActivityData(viewObj, locale) );
            }
        } finally {
            query.release();
        }
    }

    protected void initData(Date plannedStartDate, Date plannedEndDate, Locale locale) {

        ClientTreeNodeGatherer gatherer = new ClientTreeNodeGatherer();        
        IClientTreeNode root = getRoot();            
        ClientTreeLogic.getInstance().processNodes(root, gatherer, ClientTreeLogic.Mode.DOWN_SORTED);

        // add root without any restriction
        CustomActivityData rootData = new CustomActivityData(root, null, locale);
        activities.add(rootData);
        activityMap.put(root.getModel().getValue(ITreeNodeModel.OBJECT_ID).getId(), rootData);

        List<IClientTreeNode> nodes = gatherer.getNodes();
        // do not root twice
        nodes.remove(root);
       
        for (IClientTreeNode node : nodes) {

            long id = node.getModel().getValue(ITreeNodeModel.OBJECT_ID).getId();
            if (auditObj.getObjectId() != id) {
            
            	
            	if (this.getRoot().getId().equals(node.getParent().getId())) {
            		CustomActivityData data = new CustomActivityData(node, node.getParent(), locale);
            		if (isInDateRange(data, plannedStartDate, plannedEndDate)) {
            			activities.add(data); 
            			activityMap.put(id, data);
            		}
            	}
         
            	/*Código comentado em 07/05/2017 ****( INICIO )***   */
  /*    
            	if (rootData.getObjectType().equals("AUDIT") || rootData.getObjectType().equals("AUDITTEMPLATE")) {
            		if (node.getParent().getNodeType().getName().equals("AUDITSTEP") || node.getParent().getNodeType().getName().equals("AUDITSTEPTEMPLATE")) {

            			// do nothing, will only show first lvl of audit step
            		} else {
            			CustomActivityData data = new CustomActivityData(node, node.getParent(), locale);
        	            if (isInDateRange(data, plannedStartDate, plannedEndDate)) {
        	               activities.add(data); 
        	               activityMap.put(id, data);
        	            }
            		}
            	} else {
	            	CustomActivityData data = new CustomActivityData(node, node.getParent(), locale);
		            if (isInDateRange(data, plannedStartDate, plannedEndDate)) {
		               activities.add(data); 
		               activityMap.put(id, data);
		            }
            }
 	*/
            	/*Código comentado em 07/05/2017 ****( FIM )***   */ 	            	
            	
            }
            	
            	
        }
    }
    
    public List<CustomActivityData> getSubActivities(CustomActivityData activity) {
        List<CustomActivityData> subActivities = new ArrayList<CustomActivityData>();
        for (long activityId : activity.getSubActivities()) {
            subActivities.add(activityMap.get(activityId));
        }
        return subActivities;
    }
    
    public boolean hasSubActivities(CustomActivityData activity) {
        return !activity.getSubActivities().isEmpty();
    }
    
    public IClientTreeNode getRoot() {
        IClientTreeNode root = tree.getRoot();
        if (auditObj == null) {
            return root;
        }
        if (root.getNodeType().equals(INodeType.Access.ARTIFICIAL)) {
            List<IClientTreeNode> children = root.getChildren();
            if (children.size() == 1) {
                return children.get(0); 
            }
        }          
        return root;
    }
        
    private boolean isInDateRange(CustomActivityData activity, Date plannedStartDate, Date plannedEndDate) {
        if (plannedStartDate != null && activity.getPlannedStart().before(plannedStartDate)) {
            return false;
        }
        if (plannedEndDate != null && activity.getPlannedEnd().after(plannedEndDate)) {
            return false;
        }
        return true;
    }

    public boolean isTemplate() {
        if (auditObj == null) {
            return false;
        }
        if (ObjectType.AUDITTEMPLATE.equals(auditObj.getObjectType())
                || ObjectType.AUDITSTEPTEMPLATE.equals(auditObj.getObjectType())) {
            return true;
        }
        return false;
    }
    
    public int getActivityCount() {
        return activities.size();
    }
    
    public List<CustomActivityData> getActivities() {
        return activities;
    }
    
    public void initArrays() {        
        aIDs = new String[activities.size()];
        aTitles = new String[activities.size()];
        for (int i = 0; i < activities.size(); i++) {
            CustomActivityData activity = activities.get(i);
            aIDs[i] = activity.getId();
            aTitles[i] = activity.getTitle();                  
        }
    }

    public String[] getActivityIDs() {
        return aIDs;
    }
    
    public int getIndexOf(CustomActivityData activityData) {
        String[] activityIDs = getActivityIDs();
        int index = 0; 
        for (String id : activityIDs) {
            if (activityData.getId().equals(id)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public String[] getActivityTitles() {
        return aTitles;
    }

    public String getTitle() {
        if (title == null) {
            IResourceBundle bundle = ResourceBundleFactory.getInstance().getResourceBundle(locale);
            if (auditObj != null) {
                if (isTemplate()) {
                    title = bundle.getString("evaluation.audit.chart.gantt.title.audittemplate.DBI");
                } else {
                    title = bundle.getString("evaluation.audit.chart.gantt.title.audit.DBI");
                }
            }
            else {
                title = bundle.getString("evaluation.crossAuditGanttChart.DBI");
            }
        }
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getScaleStartDate(Locale locale) {
        Date startDate = getStartDate();
        Date date;
        long rangeInDays = getRangeInDays();
        if (rangeInDays > 365 * 2) {
            date = ChartUtility.getFirstOfYear(startDate);
        }
        else if (rangeInDays > 31) {    
            date = ChartUtility.getFirstOfMonth(startDate);            
        }
        else {
            date = ChartUtility.getFirstOfWeek(startDate, locale);
        }
        int gapInDays = getGapInDays(startDate, date);
        long requiredOffSetInDays = rangeInDays / 20;
        leftMarginRequired = gapInDays < requiredOffSetInDays;
        return date;
    }

    public boolean isLeftMarginRequired(Locale locale) {
        if (leftMarginRequired == null) {
            getScaleStartDate(locale);
        }
        return leftMarginRequired;
    }

    private int getGapInDays(Date later, Date earlier) {
        long gap = later.getTime() - earlier.getTime();
        long gapInDays = gap / DateUtils.MILLIS_PER_DAY; 
        return (int)gapInDays;
    }
    
    public Date getScaleEndDate(Locale locale) {             
        Date endDate = getEndDate();
        if (getRangeInDays() > 31) {
            return ChartUtility.getLastOfMonth(endDate);
        }
        else {
            return ChartUtility.getLastOfWeek(endDate, locale);
        }
    }
    
    public Date getStartDate() {
        if (startDate == null) {
            Date start = null;
            if (auditObj != null) {
                start = auditObj.getAttribute(getStartDateAttributeId(auditObj.getObjectType())).getRawValue();
            }
            for (CustomActivityData activityData : activities) {
                Date min = getMin(activityData.getActualStart(), activityData.getPlannedStart());
                start = getMin(min, start);
            }
            if (start == null) {
                start = new Date();
            }
            startDate = start;
        }
        return startDate;
    }

    public Date getEndDate() {
        if (endDate == null) {
            Date end = null;
            if (auditObj != null) {
                end = auditObj.getAttribute(getEndDateAttributeId(auditObj.getObjectType())).getRawValue();
            }
            for (CustomActivityData activityData : activities) {
                Date min = getMax(activityData.getActualEnd(), activityData.getPlannedEnd());
                end = getMax(min, end);
            }
            if (end == null) {
                end = new Date();
            }
            endDate = end;
        }
        return endDate;
    }
    
    private IDateAttributeType getStartDateAttributeId(IObjectType objectType) {
        if (ObjectType.AUDIT.equals(objectType)) {
            //return IAuditAttributeType.ATTR_ACTUALSTARTDATE;
            return IAuditAttributeType.ATTR_AUDITSTARTDATE;
        }
        if (ObjectType.AUDITSTEP.equals(objectType)) {
            return IAuditstepAttributeType.ATTR_ACTUALSTARTDATE;
        }
        if (ObjectType.AUDITTEMPLATE.equals(objectType)) {
            return IAudittemplateAttributeType.ATTR_PLANNEDSTARTDATE;
        }
        if (ObjectType.AUDITSTEPTEMPLATE.equals(objectType)) {
            return IAuditsteptemplateAttributeType.ATTR_PLANNEDSTARTDATE;
        }
        throw new IllegalStateException(objectType+" not expected object type at getStartDateAttributeId");
    }
    
    private IDateAttributeType getEndDateAttributeId(IObjectType objectType) {
        if (ObjectType.AUDIT.equals(objectType)) {
            //return IAuditAttributeType.ATTR_ACTUALENDDATE;
            return IAuditAttributeType.ATTR_AUDITENDDATE;
        }
        if (ObjectType.AUDITSTEP.equals(objectType)) {
            return IAuditstepAttributeType.ATTR_ACTUALENDDATE;
        }
        if (ObjectType.AUDITTEMPLATE.equals(objectType)) {
            return IAudittemplateAttributeType.ATTR_PLANNEDENDDATE;
        }
        if (ObjectType.AUDITSTEPTEMPLATE.equals(objectType)) {
            return IAuditsteptemplateAttributeType.ATTR_PLANNEDENDDATE;
        }
        throw new IllegalStateException(objectType+" not expected object type at getEndDateAttributeId");
    }

    private Date getMin(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a.before(b)) {
            return a;
        }
        return b;
    }
    
    private Date getMax(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a.after(b)) {
            return a;
        }
        return b;
    }
    
    
    public CustomActivityData getActivity(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        for (CustomActivityData activity : activities) {
            if (id.equalsIgnoreCase(activity.getId())) {
                return activity;
            }
        }        
        throw new IllegalArgumentException("there is no 'activity' with id "+id);        
    }

    public long getRangeInDays() {
        if (rangeInDays < 0) {
            long range = getEndDate().getTime() - getStartDate().getTime();
            rangeInDays = (range/DateUtils.MILLIS_PER_DAY);
        }
        return rangeInDays;
    }
}
