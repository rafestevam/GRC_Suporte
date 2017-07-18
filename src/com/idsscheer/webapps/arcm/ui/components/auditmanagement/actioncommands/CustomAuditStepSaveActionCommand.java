package com.idsscheer.webapps.arcm.ui.components.auditmanagement.actioncommands;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;

import com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands.CreateIssueCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.models.form.IFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IBreadcrumb;
import com.idsscheer.webapps.arcm.bl.navigation.stack.IFormBreadcrumb;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IAuditstepAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.license.LicensedComponent;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ILongAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandIds;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseSaveActionCommand;
import com.idsscheer.webapps.arcm.ui.framework.common.IRequestContext;
import com.idsscheer.webapps.arcm.ui.framework.dialog.ForwardDialog;
import com.idsscheer.webapps.arcm.ui.framework.support.ActionCommandUtils;
import com.idsscheer.webapps.arcm.ui.framework.support.IRequestKeys;

/**
 * @since 4.0 (27.09.11) 
 */
public class CustomAuditStepSaveActionCommand extends BaseSaveActionCommand {

    final List<IObjectType> objTypes = Arrays.asList(ObjectType.AUDIT, ObjectType.AUDITSTEP, ObjectType.AUDITTEMPLATE, ObjectType.AUDITSTEPTEMPLATE);

    long sumOfPercCompl = 0;
    int numOfSteps = 0;
    Logger logger = Logger.getLogger(this.getClass());
    
    @Override
	protected void afterExecute() {

        final boolean hasFloatingRelation = hasFloatingRelation(formModel.getAppObj());
        super.afterExecute();
        if (!isChangedSuccessful()) {
            return;
        }
        final IAppObj step = formModel.getAppObj();
        
        long percentageCompletedAudit = 0;
        IAppObj parentAuditObj = getParentAuditObj(step);
        getPercentageOfAuditSteps(parentAuditObj);
        percentageCompletedAudit = sumOfPercCompl / numOfSteps;
        parentAuditObj.getAttribute((ILongAttributeType)step.getAttributeType("percentage_completed")).setRawValue(percentageCompletedAudit);
        
        try {
	        IAppObjFacade issueFacade = this.environment.getAppObjFacade(ObjectType.AUDIT);
	        issueFacade.allocateWriteLock(parentAuditObj.getVersionData().getHeadOVID());
	        issueFacade.save(parentAuditObj, this.getDefaultTransaction(), true);
	        issueFacade.releaseLock(parentAuditObj.getVersionData().getHeadOVID());
        } catch (Exception e) {
        	
        }
        
       
        
        final IListAttributeType attributeType = step.getAttributeType(IAuditstepAttributeType.STR_STEPS);
        final IListAttribute attribute = step.getAttribute(attributeType);
        for (final IAppObj steps : attribute.getElements(environment.getUserContext())) {
            refreshObjectOnBreadcrumb(steps);
            refreshRelatedObjectOnBreadcrumb(steps);
        }
        if(step.getObjectType().equals(ObjectType.AUDITSTEP) && hasFloatingRelation) {
            final List<IBreadcrumb> breadcrumbs = environment.getBreadcrumbStack().getBreadcrumbs();
            final IFormBreadcrumb iBreadcrumb = (IFormBreadcrumb) breadcrumbs.get(breadcrumbs.size() - 2);
            if(iBreadcrumb.getModel().getAppObj().isDirty()) {
                try {
                    environment.getWorkflowProcessor().update(iBreadcrumb.getModel(), getDefaultTransaction());
                } catch (Exception e) {}
            }
        }
	}

    protected void getPercentageOfAuditSteps(IAppObj parentObj) {

    	IListAttribute auditSteps;
    	if (parentObj.getObjectType().getId().equals("AUDIT"))
    		auditSteps = parentObj.getAttribute(IAuditAttributeType.LIST_STEPS);
    	else
    		auditSteps = parentObj.getAttribute(IAuditstepAttributeType.LIST_STEPS);
    	
    	List<IOVID> ids = auditSteps.getElementIds();

    	for (IOVID id : ids) {

    		IAppObj step = auditSteps.getElement(id, this.getFullGrantUserContext());
    		sumOfPercCompl += step.getAttribute((ILongAttributeType)step.getAttributeType("percentage_completed")).getRawValue().longValue();
    		numOfSteps++;
    		
    		IListAttribute steps = step.getAttribute(IAuditstepAttributeType.LIST_STEPS);
        	
        	List<IOVID> subIds = steps.getElementIds();
        	if (subIds.size() > 0) {
        		getPercentageOfAuditSteps(step);
        	}
    	}
    	
    	
    }
    
    protected IAppObj getParentAuditObj(IAppObj appObj) {
    	
    	IListAttribute parentListAttr = appObj.getAttribute(IAuditstepAttributeType.LIST_AUDIT);
        IAppObj parentObj = parentListAttr.getElement(parentListAttr.getElementIds().get(0), this.getFullGrantUserContext());
    	
    	//IAppObj parentObj = appObj.getParentAppObj(getFullGrantUserContext(), IAuditstepAttributeType.LIST_AUDIT);
    	while (parentObj.getObjectType().getId().equals("AUDITSTEP")) {
    		//parentObj = parentObj.getParentAppObj(getFullGrantUserContext(), IAuditstepAttributeType.LIST_AUDIT);
    		parentListAttr = appObj.getAttribute(IAuditstepAttributeType.LIST_AUDIT);
            parentObj = parentListAttr.getElement(parentListAttr.getElementIds().get(0), this.getFullGrantUserContext());
    	}
    		
    	return parentObj;
    }
    
    protected void refreshRelatedObjectOnBreadcrumb(final IAppObj p_object) {
		final List<IBreadcrumb> breadcrumbs = environment.getBreadcrumbStack().getBreadcrumbs();
		int index = breadcrumbs.size() - 2;
		while (-1 < index) {
			final IBreadcrumb iBreadcrumb = breadcrumbs.get(index--);
            if (!(iBreadcrumb instanceof IFormBreadcrumb)) {
				continue;
			}
			final IFormModel parentFormModel = (IFormModel) iBreadcrumb.getModel();
            final IAppObj appObj = parentFormModel.getAppObj();

            if(!objTypes.contains(appObj.getObjectType())) {
                continue;
            }
            final IListAttributeType attributeType = appObj.getAttributeType(IAuditstepAttributeType.STR_STEPS);
            final IListAttribute attribute = appObj.getAttribute(attributeType);
            if(!attribute.hasElement(p_object.getVersionData().getHeadOVID())){
                continue;
            }

            try {
                attribute.removeElement(p_object, getFullGrantUserContext());
            } catch (BLException e) {
                throw new ActionCommandException(e);
            }
        }
	}


    @Override
    protected void addForwardDialog() {
        if (null == transition) {
            return;
        }

        IEnumAttribute measure = formModel.getAppObj().getAttribute(IAuditstepAttributeType.ATTR_MEASURE);
        boolean issueWorkflow = measure.hasItem(Enumerations.MEASURE_AUDITSTEP.ISSUE);
        if(!issueWorkflow) {
            return;
        }

        IAppObj relatedObject = null;
        if (issueWorkflow) {
            relatedObject = transition.getWorkAppObj(CreateIssueCommand.WORKOBJECT_CREATED_ISSUE);
            if (null == relatedObject) {
                return;
            }
            //message: The issue with the name '%0' was created.
            notificationDialog.addMessageKey("issue.generated.DBI", relatedObject.getAttribute(IIssueAttributeType.ATTR_NAME).getRawValue());

            //create forward dialog if issue management license is set
            if ( environment.getUserContext().getUserComponents().isLicensed(LicensedComponent.ISSUE_MANAGEMENT) ) {
                //forward option to the created issue
                final ForwardDialog forwardDialog = environment.getDialogManager().createForwardDialog();
                forwardDialog.setHelpId(ForwardDialog.DEFAULT_HELPID);
                forwardDialog.setQuestionKey("forward.how.to.proceed.DBI");

                final ActionForward forward = ActionCommandUtils.getObjectCommandForward(ObjectType.ISSUE.getId(), ActionCommandIds.EDIT, "&" + IRequestKeys.OBJECT + "=" + relatedObject.getVersionData().getHeadOVID().getAsString());
                final ForwardDialog.ForwardOption forwardOption = forwardDialog.addOption(forward);
                forwardOption.setOptionKey("forward.open.issue.DBI");

                final ForwardDialog.ForwardOption forwardOption2 = forwardDialog.addOption(IRequestContext.TOP_HISTORY);
                forwardOption2.setOptionKey("forward.auditstep.stay.at.auditstep.DBI");
            }
        }

    }
}
