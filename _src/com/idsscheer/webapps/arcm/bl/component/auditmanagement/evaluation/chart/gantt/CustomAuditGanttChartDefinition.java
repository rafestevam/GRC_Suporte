package com.idsscheer.webapps.arcm.bl.component.auditmanagement.evaluation.chart.gantt;

import java.awt.Dimension;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLInternalException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.IChartDefinition;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.common.context.IEvaluationContext;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.common.support.EvaluationParameterSupport;
import com.idsscheer.webapps.arcm.bl.models.IBLComponentModel;
import com.idsscheer.webapps.arcm.bl.models.filter.IFilterAttributeSet;
import com.idsscheer.webapps.arcm.bl.models.form.IFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.notification.INotificationList;
import com.idsscheer.webapps.arcm.common.notification.NotificationTypeEnum;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.ui.web.support.RequestUtils;

import ChartDirector.BaseChart;

/**
 * @author mime
 * @since 4.0 (26.04.2011, 11:08:38)
 */


public class CustomAuditGanttChartDefinition extends BaseGanttChartDefinition implements IChartDefinition {
    
    protected IUserContext userCtx;
    protected IAppObj obj;
    private CustomGanttChartData data;
   
    
    public void init(IEvaluationContext evaluationContext, Map<String, String> xmlConfigParameterMap, Object dataSource) { 
    	super.init(evaluationContext, xmlConfigParameterMap, dataSource);
        IBLComponentModel superiorComponentModel = evaluationContext.getSuperiorComponentModel();
        if (superiorComponentModel instanceof IFormModel) {
            obj = ((IFormModel) dataSource).getAppObj();
        }
        else {
            IOVID ovid = EvaluationParameterSupport.getOVID(evaluationContext.getParameterMap());
            if (ovid != null) {
                try {
                    IUserContext readCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
                    IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(readCtx, ObjectType.AUDIT);
                    obj = facade.load(ovid, false);
                } catch (RightException e) {
                    throw new BLInternalException(e);
                }
                // If the obj is null try to get as audit step
                if (obj == null) {
	                try {
	                    IUserContext readCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
	                    IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(readCtx, ObjectType.AUDITSTEP);
	                    obj = facade.load(ovid, false);
	                } catch (RightException e) {
	                    throw new BLInternalException(e);
	                }
                }
             // If the obj is null try to get as audit step
                if (obj == null) {
	                try {
	                    IUserContext readCtx = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
	                    IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(readCtx, ObjectType.AUDITSTEPTEMPLATE);
	                    obj = facade.load(ovid, false);
	                } catch (RightException e) {
	                    throw new BLInternalException(e);
	                }
                }
            }
        }
        if (obj == null) {
            throw new IllegalArgumentException(
                    "superiorComponentModel must be an instance of IFormModel, " +
                    "or the request must contain the object id ");
        }
    }

    public String getHTMLImageMap(BaseChart chartDirectorChart, String applicationContextPath, Locale locale) 
    { 
        String url = "javascript:aam_openPopUp('"+applicationContextPath+"/object.do";
        String parameter = "__commandId=popup&__objectType={field0}&__object={field1}','object')";
        return chartDirectorChart.getHTMLImageMap(url, RequestUtils.addWindowIdIfNecessaryTo(parameter), createToolTipHtml());
    }
    
    public String getChartTitle(Locale locale) {
        return getChartData(locale).getTitle();
    }

    public String getChartSubTitle(Locale locale) {
        return getChartData(locale).getDescription();
    }

    public INotificationList getNotifications() {
        INotificationList notifications = super.getNotifications();
        IAttributeType steps = obj.getObjectType().getAttribute("steps");
        if (obj.getAttribute(steps).isDirty()) {
            notifications.add(
                    NotificationTypeEnum.INFO, 
                    "evaluation.audit.structure.audit.dirty.DBI");
        }
        if (data != null && !data.isChartDataComplete()) {
            notifications.add(
                    NotificationTypeEnum.INFO,
                    "evaluation.audit.structure.step.assignment.dirty.DBI", AppObjUtility.getLocalisedAppObjName(obj, getLocale()));
        }
        return notifications;
    }

    public BaseChart createChart(IFilterAttributeSet filterAttributeSet, Dimension preferredSize, HttpServletRequest request, boolean useWhiteBackground, Locale locale) {
    	
    	data = null;        
        CustomGanttChart chart = new CustomGanttChart(getChartData(locale), filterAttributeSet, request, preferredSize);
        return chart.createChart(locale, useWhiteBackground);
    }

    private CustomGanttChartData getChartData(Locale locale) {
        if (data == null) {
            data = new CustomGanttChartData(obj, null, null, locale);
        }
        return data; 
    }

}
