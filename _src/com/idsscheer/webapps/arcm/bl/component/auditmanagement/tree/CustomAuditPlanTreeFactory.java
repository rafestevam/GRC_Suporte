package com.idsscheer.webapps.arcm.bl.component.auditmanagement.tree;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.tree.AbstractServerTreeFactory;
import com.idsscheer.webapps.arcm.bl.framework.tree.CommitPerformedEvent;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTree;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeNode;
import com.idsscheer.webapps.arcm.bl.framework.tree.IServerTreeNodeFilter;
import com.idsscheer.webapps.arcm.bl.framework.tree.ITreeNodeModel;
import com.idsscheer.webapps.arcm.bl.framework.tree.ServerTreeLogic;
import com.idsscheer.webapps.arcm.bl.framework.tree.ServerTreeNodeObjectIDFilter;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IClientAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IValueAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditscopeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.Factory;
import com.idsscheer.webapps.arcm.common.util.ParameterList;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.tree.INodeType;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.IOrderCriteria;

import java.util.*;

/**
 * 
 * @author DAOL
 * @since 4.0 (11.04.11, 14:33)
 */
public class CustomAuditPlanTreeFactory extends AbstractServerTreeFactory {

    protected String VIEW_ID = "";
    protected String OBJ_TYPE = "";

	protected static final String PARENT_NAME = "parent_name";
	protected static final String PARENT_ID = "parent_id";
	protected static final String PARENT_VERSION__NUMBER = "parent_version_number";
	protected static final String PARENT_DEACTIVATED = "parent_deactivated";
	protected static final String PARENT_CLIENT_SIGN = "parent_client_sign";
	protected static final String CHILD_ID = "child_id";
	protected static final String CHILD_SEQUENCE = "child_sequence";
	protected static final String ARTIFICIAL_ROOT_KEY = "artificial.root.ZZZ";

    protected static final INodeType AUDIT         = new INodeType.ObjectTypeNodeType(ObjectType.AUDIT);
    protected static final INodeType AUDITTEMPLATE = new INodeType.ObjectTypeNodeType(ObjectType.AUDITTEMPLATE);

    public static CustomAuditPlanTreeFactory createInstance() {
		return Factory.createInstance(CustomAuditPlanTreeFactory.class);
    }

    protected void createTreeInternal(IServerTree serverTree, ParameterList params) {
        createTreeInternal(serverTree, params, new HashMap<Long, IServerTreeNode>());
    }

    /**
     *
     * @param params     	The parameters used during tree construction; may be null
   	 * @param serverTree	The (empty) tree which should be constructed, i.e. filled with nodes.
     * @param nodes         Empty map in case of initial tree creation, map with all nodes in case of tree update
     * @return true if structure of tree was changed
     */
	protected boolean createTreeInternal(final IServerTree serverTree, final ParameterList params, Map<Long, IServerTreeNode> nodes) {
        		
		final InternalData data = (InternalData) getData(serverTree.getKey());
        if (null == data) {
            throw new IllegalStateException("The tree is not yet created by this factory - notification not allowed");
        }
        if (null == params || !params.containsKey(ParameterKeys.VIEW_ID)) {
             throw new IllegalArgumentException("Not able to create a tree without a given " +
                    "view ID, i.e. missing parameter " + ParameterKeys.VIEW_ID);
        }
        VIEW_ID = params.getString(ParameterKeys.VIEW_ID);
        if (!params.containsKey(ParameterKeys.OBJECT_ID)) {
            throw new IllegalArgumentException("Not able to create a tree without a given " +
                    "object ID, i.e. missing parameter " + ParameterKeys.OBJECT_ID);
        }
         if (!params.containsKey(ParameterKeys.OBJECT_TYPE)) {
            throw new IllegalArgumentException("Not able to create a tree without a given " +
                    "object type, i.e. missing parameter " + ParameterKeys.OBJECT_TYPE);
        }

        Set<Long> inputNodes = new HashSet<>(nodes.keySet());

        final IUserContext ctx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
        final Long objectID = params.getLong(ParameterKeys.OBJECT_ID);
        OBJ_TYPE = params.getString(ParameterKeys.OBJECT_TYPE);
        final IObjectType objectType = Metadata.getMetadata().getObjectType(OBJ_TYPE);

        final IAppObj mainAppObj = loadMainAppObj(ctx, objectID, objectType);
        final long parent_objectId = objectID;

        final IOrderCriteria orderCriteria = null;
        final IFilterCriteria simpleFilterCriteria = filterFactory.getSimpleFilterCriteria("parent_objectID", parent_objectId);

        final IViewQuery viewQuery = QueryFactory.createQuery(ctx, getViewName(),
                                                              Arrays.asList(simpleFilterCriteria),
                                                              Collections.singletonList(orderCriteria), null);
        viewQuery.setEnablePaging(false, false);

        try {
            IServerTreeNode root = nodes.get(0L);
            if (root == null) {
                root = defineRootNode(serverTree);
            }

			//
            // Construct the audit template node
            //

            IServerTreeNode templateNode = nodes.get(objectID);
            if (templateNode == null) {
                templateNode = createServerTreeNode(serverTree);
            }
            defineTreeNodeModel(templateNode);
	        root.addChild(templateNode);
	        templateNode.setParent(root);
            templateNode.setNodeType(INodeType.Access.get(mainAppObj.getObjectType().getId()));
            templateNode.setSequence(1 /* since there is in each case exactly one root node, this node has always sequence 1 */);
            templateNode.setName((String) getValueAttribute(mainAppObj, IAuditAttributeType.STR_NAME).getPersistentRawValue());
	        templateNode.setActive(!(Boolean)getValueAttribute(mainAppObj, IAuditAttributeType.STR_DEACTIVATED).getPersistentRawValue());
            final ITreeNodeModel templateModel = templateNode.getModel();
            final IClientAppObj client = (IClientAppObj) getValueAttribute(mainAppObj, IAuditAttributeType.STR_RELATED_CLIENT).getPersistentRawValue();
            templateModel.setValue(CustomAuditPlanTreeNodeModel.OBJECT_ID, mainAppObj.getVersionData().getHeadOVID());
            templateModel.setValue(CustomAuditPlanTreeNodeModel.CLIENT_SIGN, client.getAttribute(IClientAppObj.ATTR_SIGN).getRawValue());
            templateModel.setValue(CustomAuditPlanTreeNodeModel.OBJECT_TYPE, mainAppObj.getObjectType().getId());
            templateModel.setValue(CustomAuditPlanTreeNodeModel.PERCENTAGE_COMPLETED,  (Long) getValueAttribute(mainAppObj, "percentage_completed").getPersistentRawValue());

            if(OBJ_TYPE.contains("STEP")) {
                templateModel.setValue(CustomAuditPlanTreeNodeModel.AUDITSTARTDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_PLANNEDSTARTDATE).getPersistentRawValue());
                templateModel.setValue(CustomAuditPlanTreeNodeModel.AUDITENDDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_PLANNEDENDDATE).getPersistentRawValue());
            } else {
                templateModel.setValue(CustomAuditPlanTreeNodeModel.AUDITSTARTDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_AUDITSTARTDATE).getPersistentRawValue());
                templateModel.setValue(CustomAuditPlanTreeNodeModel.AUDITENDDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_AUDITENDDATE).getPersistentRawValue());
            }
            IListAttribute attribute = getListAttribute(mainAppObj, IAuditAttributeType.STR_OWNER_GROUP);
            IAppObj group = ARCMCollections.extractSingleEntry(attribute.getElements(ctx), false);
            // owner group can be null if audit template is in preparation status
            if (group != null) {
                templateModel.setValue(CustomAuditPlanTreeNodeModel.OWNER_GROUP, AppObjUtility.getLocalisedAppObjName(group, ctx.getLanguage(), false));
            }templateModel.setValue(CustomAuditPlanTreeNodeModel.DESCRIPTION, (String) getValueAttribute(mainAppObj, IAuditAttributeType.STR_DESCRIPTION).getPersistentRawValue());
            templateModel.setValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS, null);
            templateModel.setValue(CustomAuditPlanTreeNodeModel.REVIEWER_STATUS, null);

            if(mainAppObj.hasAttributeType(IAuditAttributeType.STR_OWNER_STATUS)) {
                final IAttributeType attributeType = mainAppObj.getAttributeType(IAuditAttributeType.STR_OWNER_STATUS);
                final List<IEnumerationItem> owner_status = ((IEnumAttribute)mainAppObj.getAttribute(attributeType)).getPersistentRawValue();
                if(null != owner_status && !owner_status.isEmpty()) {
                    final IEnumerationItem status = owner_status.get(0);
                    templateModel.setValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS, status);
                }
            }
            if(mainAppObj.getObjectType().equals(ObjectType.AUDIT)
                    && mainAppObj.hasAttributeType(IAuditAttributeType.STR_REVIEWER_STATUS)) {
                final IAttributeType attributeType = mainAppObj.getAttributeType(IAuditAttributeType.STR_REVIEWER_STATUS);
                final List<IEnumerationItem> reviewer_status = ((IEnumAttribute)mainAppObj.getAttribute(attributeType)).getPersistentRawValue();
                if(null != reviewer_status && !reviewer_status.isEmpty()) {
                    final IEnumerationItem status = reviewer_status.get(0);
                    templateModel.setValue(CustomAuditPlanTreeNodeModel.REVIEWER_STATUS, status);
                }
            }
            if(mainAppObj.hasAttributeType(IAuditAttributeType.STR_ACTUALSTARTDATE)) {
                templateModel.setValue(CustomAuditPlanTreeNodeModel.ACTUALSTARTDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_ACTUALSTARTDATE).getPersistentRawValue());
                templateModel.setValue(CustomAuditPlanTreeNodeModel.ACTUALENDDATE,  (Date) getValueAttribute(mainAppObj, IAuditAttributeType.STR_ACTUALENDDATE).getPersistentRawValue());
            }

	        final List<IAppObj> elements = mainAppObj.getAttribute((IListAttributeType)mainAppObj.getAttributeType(IAuditAttributeType.STR_SCOPE)).getElements(ctx);
	        if(elements.isEmpty()) {
	            templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE, null);
	            templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_ID, null);
	            templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_TYPE, null);
	        } else {
                if(OBJ_TYPE.contains("TEMPLATE")) {
                    templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE, elements.get(0).getRawValue(IHierarchyAttributeType.ATTR_NAME));
	                templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_ID, elements.get(0).getObjectId());
	                templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_TYPE, elements.get(0).getAttribute(IHierarchyAttributeType.ATTR_TYPE).getRawValue().get(0));
                } else {
		            templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE, elements.get(0).getRawValue(IAuditscopeAttributeType.ATTR_OBJECT_NAME));
	                templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_ID, elements.get(0).getObjectId());
	                templateModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_TYPE, elements.get(0).getAttribute(IAuditscopeAttributeType.ATTR_TYPE).getRawValue().get(0));
                }
	        }

			final List<IOVID> rootOVIDs = mainAppObj.getAttribute((IListAttributeType)mainAppObj.getAttributeType(IAuditAttributeType.STR_STEPS)).getPersistentRawValue();

			//
            // Retrieve the step nodes by executing the view
            //
            createStepNodes(serverTree, viewQuery, nodes);


            final Set<IServerTreeNode> parentlessNodes = new HashSet<IServerTreeNode>();

            for (final IServerTreeNode node : nodes.values()) {
                final ITreeNodeModel model = node.getModel();
                if (node.getNodeType().equals(INodeType.Access.ARTIFICIAL)) {
                    continue;
                }
                final IOVID headRevision = OVIDFactory.getOVID(model.getValue(CustomAuditPlanTreeNodeModel.OBJECT_ID).getId());
                model.setValue(CustomAuditPlanTreeNodeModel.OBJECT_ID, headRevision);
                final IServerTreeNode parent = node.getParent();
                if (parent == null
                        || AUDITTEMPLATE.equals(parent.getNodeType())
                        || AUDIT.equals(parent.getNodeType())) {

                    // [TANR 490619] add this parent less node only if is referenced by the audit template this
                    // tree should be constructed for, i.e. if the parent less node is a direct child of the template
                    // itself:
                    if (OVIDUtility.contains(rootOVIDs, headRevision, true)) {
                        parentlessNodes.add(node);
                    }
                }
            }
            if (parentlessNodes.size()!=rootOVIDs.size()) {
                throw new IllegalStateException("size of relevant collections differ concerning mainAppObj #" +
                    mainAppObj + ": parentlessNodes.size()=" +
                    parentlessNodes.size() + ", rootSectionOVIDs.size()=" + rootOVIDs.size());
            }
            int firstGenerationSequence = 1;
            boolean found;
            for (final IOVID rootSectionOVID : rootOVIDs) {
                final Long rootSectionID = rootSectionOVID.getId();
                found = false;
                for (final IServerTreeNode parentlessNode : parentlessNodes) {
                    final ITreeNodeModel model = parentlessNode.getModel();
                    final Long nodeObjectID = model.getValue(ITreeNodeModel.OBJECT_ID).getId();
                    if (nodeObjectID.equals(rootSectionID)) {
                        //
                        // update sequence information at first generation level...
                        //
                        parentlessNode.setSequence(firstGenerationSequence++);
                        //
                        // ... and btw also the parent<->child relationship
                        //
                        parentlessNode.setParent(templateNode);
                        templateNode.addChild(parentlessNode);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalStateException("no first generation node for object ID " + rootSectionID + " found");
                }
            }

            if (inputNodes.size() == nodes.keySet().size()) {
                return true; // any node added or removed
            }
            //noinspection RedundantIfStatement
            if (inputNodes.containsAll(nodes.keySet())) {
                return false; // no node added or removed
            }
            return true;

        } finally {
            viewQuery.release();
            nodes.clear();
        }
    }

    private void createStepNodes(IServerTree serverTree, IViewQuery viewQuery, Map<Long, IServerTreeNode> nodes) {
        for (Iterator<IViewObj> iter = viewQuery.getResultIterator(); iter.hasNext();) {
            final IViewObj viewObj = iter.next();
            final String clientSign = String.valueOf(viewObj.getRawValue(PARENT_CLIENT_SIGN));
            //
            // (1) parent node
            //
            final Long parentId = (Long) viewObj.getRawValue(PARENT_ID);
            IServerTreeNode parent = nodes.get(parentId);
            if (null == parent) {
                parent = createServerTreeNode(serverTree);
                defineTreeNodeModel(parent);
                nodes.put(parentId, parent);
                if (log.isDebugEnabled()) {
                    log.debug("Created: parent #" + parent.getID());
                }
            }
            final Long parentVersionNumber = (Long) viewObj.getRawValue(PARENT_VERSION__NUMBER);
            final boolean updateVersion = updateVersion(parent, parentVersionNumber);
            final boolean updateSequence = updateVersion || updateSequence(parent, parentVersionNumber);
            if (updateVersion) {
                final IOVID mainObjectOVID = OVIDFactory.getOVID(parentId, parentVersionNumber);
                createTreeInternalSetValues(parent, mainObjectOVID, clientSign, viewObj);
                if (log.isDebugEnabled()) {
                    log.debug("Updated: parent #" + parent.getID() + " - (version " + parentVersionNumber +
                            "): " + IServerTreeNode.ToString.serverTreeNode(parent));
                }
            }
            //
            // (2) child node
            //
            final Long childId = (Long) viewObj.getRawValue(CHILD_ID);
            if (null != childId) {
                IServerTreeNode child = nodes.get(childId);
                if (null == child) {
                    child = createServerTreeNode(serverTree);
                    defineTreeNodeModel(child);
                    nodes.put(childId, child);
                    if (log.isDebugEnabled()) {
                        log.debug("Created: child #" + child.getID());
                    }
                }
                parent.addChild(child);
                final IServerTreeNode childsParent = child.getParent();
                if (null != childsParent && childsParent != parent) {
                    final String msg = "Parent (" + IServerTreeNode.ToString.serverTreeNode(childsParent) +
                            ") of node (" + IServerTreeNode.ToString.serverTreeNode(child) +
                            ") is replaced by new parent (" + IServerTreeNode.ToString.serverTreeNode(parent) +
                            ") - this maybe a data inconsistence";
                    log.warn(msg);
                }
                child.setParent(parent);
                if (updateSequence) {
                    final Object value = viewObj.getRawValue(CHILD_SEQUENCE);
                    if (null == value) {
                        throw new IllegalStateException();
                    }
                    final long sequence = (Long) value + 1; // align sequence from database (which starts from 0 - we needed starting from 1)
                    child.setSequence(sequence);
                }
            } // else: this is a row containing no child, i.e. we have only the parent filled in, which means,
            // that we have a leaf node, and so we do not have anything to do here
        }
    }

    @Override
	protected boolean commitPerformedInternal(final IServerTree serverTree, final ParameterList params, final CommitPerformedEvent event) {
		final IOVID id = event.getAppObj().getVersionData().getHeadOVID();
		final ServerTreeNodeObjectIDFilter filter = new ServerTreeNodeObjectIDFilter(id, ServerTreeNodeObjectIDFilter.Mode.ID_ONLY);
		final IServerTreeNode node = ServerTreeLogic.getInstance().findFirst(serverTree.getRoot(), filter);
        boolean changed;
        if(node != null) {
            Collection<IServerTreeNode> all = ServerTreeLogic.getInstance().findAll(serverTree.getRoot(), IServerTreeNodeFilter.TRUE);
            Map<Long, IServerTreeNode> nodes = new HashMap<>();
            for (IServerTreeNode serverTreeNode : all) {
                IOVID mainObjectOVID = serverTreeNode.getModel().getValue(CustomAuditPlanTreeNodeModel.OBJECT_ID);
                Collection<IServerTreeNode> children = new ArrayList<>(serverTreeNode.getChildren());
                for (IServerTreeNode child : children) {
                    serverTreeNode.removeChild(child);
                }
                serverTreeNode.setParent(null);
                if (mainObjectOVID == null) {
                    nodes.put(0L, serverTreeNode);
                } else {
                    nodes.put(mainObjectOVID.getId(), serverTreeNode);
                }
            }
            changed = createTreeInternal(serverTree, params, nodes);
        } else {
            changed = false;
        }
		return changed;
	}

    protected IServerTreeNode defineRootNode(final IServerTree serverTree) {
    	
		final IServerTreeNode root = createServerTreeNode(serverTree);
		defineTreeNodeModel(root);
		serverTree.setRoot(root);
		root.setNameKey(ARTIFICIAL_ROOT_KEY);
		root.setDescriptionKey(ARTIFICIAL_ROOT_KEY);
		root.setNodeType(INodeType.Access.ARTIFICIAL);
		root.setActive(true);
		final ITreeNodeModel rootModel = root.getModel();
		rootModel.setValue(CustomAuditPlanTreeNodeModel.OBJECT_ID, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.CLIENT_SIGN, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.AUDITSTARTDATE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.AUDITENDDATE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.ACTUALSTARTDATE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.ACTUALENDDATE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.OWNER_GROUP, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_ID, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.SCOPE_TYPE, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.DESCRIPTION, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.REVIEWER_STATUS, null);
		rootModel.setValue(CustomAuditPlanTreeNodeModel.PERCENTAGE_COMPLETED, null);
		return root;
	}

	protected static boolean updateVersion(IServerTreeNode parent, long parentVersionNumber) {
		   if (parent == null) {
			   throw new IllegalArgumentException("parent must not be null");
		   }
		   if (parentVersionNumber == IOVID.HEAD_REV) {
			   throw new IllegalArgumentException("parentVersionNumber==IOVID.HEAD_REV not supported");
		   }
		   ITreeNodeModel model = parent.getModel();
		   if (model == null) {
			   throw new IllegalStateException("model of node " + IServerTreeNode.ToString.serverTreeNode(parent) + " is null");
		   }
		   IOVID objectID;
		   try {
			   objectID = model.getValue(CustomAuditPlanTreeNodeModel.OBJECT_ID);
		   } catch (UnsupportedOperationException ex) {
			   throw new IllegalStateException("model does not support object IDs");
		   }
		   return objectID == null || objectID.getVersion() < parentVersionNumber;
	   }

	protected static boolean updateSequence(IServerTreeNode parent, long parentVersionNumber) {
		   if (parent == null) {
			   throw new IllegalArgumentException("parent must not be null");
		   }
		   if (parentVersionNumber == IOVID.HEAD_REV) {
			   throw new IllegalArgumentException("parentVersionNumber==IOVID.HEAD_REV not supported");
		   }
		   ITreeNodeModel model = parent.getModel();
		   if (model == null) {
			   throw new IllegalStateException("model of node " + IServerTreeNode.ToString.serverTreeNode(parent) + " is null");
		   }
		   IOVID objectID;
		   try {
			   objectID = model.getValue(CustomAuditPlanTreeNodeModel.OBJECT_ID);
		   } catch (UnsupportedOperationException ex) {
			   throw new IllegalStateException("model does not support object IDs");
		   }
		   return objectID == null || objectID.getVersion() <= /* Note the allowed equality - necessary for sequence update! */ parentVersionNumber;
	   }

	protected boolean hasFixedStructure(final IAppObj appObj) {
		return true;
	}

	protected void defineTreeNodeModel(final IServerTreeNode node) {
		node.setModel(CustomAuditPlanTreeNodeModel.createInstance());
	}

	protected static final class InternalData extends Data {
        protected void reset() {
        }
    }

	protected AbstractServerTreeFactory.Data createData() {
        return new InternalData();
    }

    protected IAppObj loadMainAppObj(final IUserContext ctx, final Long p_id, final IObjectType p_objectType) {
		//
		// Read the whole questionnaire template for later use
		//
		final IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(ctx, p_objectType);
		final IOVID id = OVIDFactory.getOVID(p_id);
		try {
			return facade.load(id, false);
		} catch (RightException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
	protected void createTreeInternalSetValues(final IServerTreeNode node, final IOVID mainObjectOVID, final String clientSign, final IViewObj viewObj) {
		    	
    	node.setActive(!(Boolean) viewObj.getRawValue(PARENT_DEACTIVATED));
        node.setName((String) viewObj.getRawValue(PARENT_NAME));       
        final ITreeNodeModel model = node.getModel();
        if(OBJ_TYPE.contains("TEMPLATE")) {
            node.setNodeType(INodeType.Access.get("AUDITSTEPTEMPLATE"));
            model.setValue(CustomAuditPlanTreeNodeModel.OBJECT_TYPE, ObjectType.AUDITSTEPTEMPLATE.getId());
        } else {
            node.setNodeType(INodeType.Access.get("AUDITSTEP"));
            model.setValue(CustomAuditPlanTreeNodeModel.OBJECT_TYPE, ObjectType.AUDITSTEP.getId());
        }
        // do not touch the sequence slot here
        // node.setSequence(...);

        model.setValue(CustomAuditPlanTreeNodeModel.OBJECT_ID, mainObjectOVID);
        model.setValue(CustomAuditPlanTreeNodeModel.CLIENT_SIGN, clientSign);
        model.setValue(CustomAuditPlanTreeNodeModel.AUDITSTARTDATE, (Date) viewObj.getRawValue("plannedstartdate"));
        model.setValue(CustomAuditPlanTreeNodeModel.AUDITENDDATE, (Date) viewObj.getRawValue("plannedenddate"));
        model.setValue(CustomAuditPlanTreeNodeModel.OWNER_GROUP, (String)viewObj.getRawValue("owner_group"));
        model.setValue(CustomAuditPlanTreeNodeModel.SCOPE, (String)viewObj.getRawValue("scope"));
        model.setValue(CustomAuditPlanTreeNodeModel.SCOPE_ID, (Long)viewObj.getRawValue("scope_id"));
        
        model.setValue(CustomAuditPlanTreeNodeModel.PERCENTAGE_COMPLETED, (Long)viewObj.getRawValue("percentage_completed"));
        
        String description = (String) viewObj.getRawValue("description");
        model.setValue(CustomAuditPlanTreeNodeModel.DESCRIPTION, description);
        node.setDescription(description);
        if(viewObj.hasAttributeType("actualstartdate")) {
            model.setValue(CustomAuditPlanTreeNodeModel.ACTUALSTARTDATE, (Date) viewObj.getRawValue("actualstartdate"));
            model.setValue(CustomAuditPlanTreeNodeModel.ACTUALENDDATE, (Date) viewObj.getRawValue("actualenddate"));
        } else {
            model.setValue(CustomAuditPlanTreeNodeModel.ACTUALSTARTDATE, null);
            model.setValue(CustomAuditPlanTreeNodeModel.ACTUALENDDATE, null);
        }
        model.setValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS, null);
        if(viewObj.hasAttributeType("owner_status")) {
            IEnumerationItem status = viewObj.getEnumItem("owner_status");
            model.setValue(CustomAuditPlanTreeNodeModel.OWNER_STATUS, status);
        }  
        if(viewObj.hasAttributeType("scope_type")) {
            IEnumerationItem scope_type = viewObj.getEnumItem("scope_type");
            model.setValue(CustomAuditPlanTreeNodeModel.SCOPE_TYPE, scope_type);
        }       
	}

	protected String getViewName() {
        return VIEW_ID;
    }

	public Collection<IObjectType> getRelevantObjectTypes() {
		return ARCMCollections.createSet(ObjectType.AUDITTEMPLATE, ObjectType.AUDITSTEPTEMPLATE, ObjectType.AUDIT, ObjectType.AUDITSTEP);
	}

    public IValueAttribute getValueAttribute(final IAppObj p_appObj , final String p_attrName) {
        return (IValueAttribute) p_appObj.getAttribute((IAttributeType)p_appObj.getAttributeType(p_attrName));
    }     

    private IListAttribute getListAttribute(IAppObj obj, String attributeId) {
        return (IListAttribute) obj.getAttribute((IAttributeType)obj.getAttributeType(attributeId));
    }

    protected boolean reorganizeTreeForClient(final IServerTree oldTree, final ParameterList params, final String clientSign) {
        // a questionnaire template tree is affected by a client related import only if the tree 'lives' on this client
        boolean changed;
        if (isRelatedToClient(oldTree, clientSign)) {
            reorganizeTree(oldTree, params);
            changed = true;
        } else {
            changed = false;
        }
        return changed;
    }
}
