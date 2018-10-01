package com.idsscheer.webapps.arcm.bl.component.auditmanagement.evaluation.chart.gantt;

import ChartDirector.BaseChart;
import ChartDirector.BoxWhiskerLayer;
import ChartDirector.Chart;
import ChartDirector.LegendBox;
import ChartDirector.TextBox;
import ChartDirector.XYChart;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.context.ChartContextItemFactory;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.context.IChartDataContextItem;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.util.ChartIconUtility;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.util.ChartTextUtility;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.chart.util.ChartUtility;
import com.idsscheer.webapps.arcm.bl.framework.evaluation.style.EvaluationColor;
import com.idsscheer.webapps.arcm.bl.models.filter.IFilterAttributeSet;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.util.StringUtility;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.web.support.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author mime
 * @since 4.0 (26.04.2011, 13:32:13)
 */


class CustomGanttChart {                  
    
    private static final long SECONDS_PER_DAY = 24 * 60 * 60;

    private final Font ACTIVITY_LABEL_FONT = new Font("Arial Unicode MS", Font.PLAIN, 8);

    protected final Color VERTICAL_GRID_1    = new Color(248, 248, 248);
    protected final Color VERTICAL_GRID_2    = new Color(255, 255, 255);

    protected final Color ACTIVITY_AUDIT     = new Color(  0, 148, 233);
    protected final Color ACTIVITY_TEMPLATE  = new Color(162, 196,   0);
    
    protected final int WHITE = 0xffffff;
    protected final int R_E_D = 0xff0000;
                 
    protected final Color CONNECTION_ARROW_COLOR = new Color(144, 144, 144);
    protected final Color CONNECTION_LINE_COLOR  = new Color(144, 144, 144);
    
    protected final Color NOW_MARK  = new Color(255, 68, 68);
   
    private final CustomGanttChartData data;
    private final IFilterAttributeSet filterAttributeSet;
    private final HttpServletRequest request;
    private final int chartHeight;
    private final int chartWidth;
    

    public CustomGanttChart(CustomGanttChartData data,
                      IFilterAttributeSet filterAttributeSet,
                      HttpServletRequest request,
                      Dimension preferredSize)
    {

        this.data = data;
        this.filterAttributeSet = filterAttributeSet;
        this.request = request;
        chartWidth = (int)preferredSize.getWidth();
        chartHeight = (int)preferredSize.getHeight();
    }
    
    public final BaseChart createChart(Locale locale, boolean useWhiteBackground)
    {
    	    	
        int calculatedHeight = Math.max(chartHeight, data.getActivityCount() * 52 + 120);

        XYChart chart = ChartUtility.createXYChart(new Dimension(chartWidth, calculatedHeight), data.getTitle(), null, useWhiteBackground);
       // LegendBox legendBox = ChartUtility.addLegend(chart, null);
       LegendBox legendBox = chart.addLegend2(0, 0, -2, ChartTextUtility.getContentFont(), 8);
       legendBox.setKeySize(24, 18);
		        
        boolean hasActivities = data.getActivityIDs().length > 0;
        int filterWidth = addFilter(chart, locale);
        int yLegendPos = 50;
        if (hasActivities) {
            int yLabelWidth = addAxisLabels(chart, locale);
            yLegendPos = yLegendPos + yLabelWidth;
            addPlotArea(chart, yLabelWidth, filterWidth, locale);       
            addActivities(chart, locale);
        } 
        else {
            int maxTextWidth = chartWidth - 80 - filterWidth;
            ChartUtility.addNoDataFoundMessage(chart, 50, 80, maxTextWidth, locale);   
        }
        if (!data.isTemplate()) {
            IResourceBundle bundle = ResourceBundleFactory.getBundle(locale);
            legendBox.addKey(bundle.getString("evaluation.audit.chart.gantt.legend.actual.DBI"), getActualTimeColor(chart));                 
            int nowColor = getTodayMarkColor(chart);
            legendBox.addKey(bundle.getString("evaluation.audit.chart.gantt.legend.today.DBI"), nowColor);
        }
        
        chart.layoutLegend();
        int legendHeight = legendBox.getHeight();
        chart.setSize(chartWidth, calculatedHeight + legendHeight - 35);
        legendBox.setPos(yLegendPos, calculatedHeight - legendHeight);
        legendBox.setBackground(Chart.CColor(EvaluationColor.CHART_TITLE_BG_COLOR));
        legendBox.setWidth(chartWidth);
        
        chart.layout();
        if (hasActivities) {
            addActivityConnections(chart);
        }
                         
        return chart;
    }

    private int addFilter(XYChart chart, Locale locale) {
        if (filterAttributeSet == null) {
            return 0;
        }
        List<IChartDataContextItem> items = ChartContextItemFactory.createDataContextItems(filterAttributeSet, true);
        TextBox filterBox = ChartUtility.addFilterAttributeBox(chart, items, locale);
        if (filterBox == null) {
            return 0;
        }
        return filterBox.getWidth();
    }

    protected int addAxisLabels(XYChart chart, Locale locale) {
        String[] allLabels = wrapLines(data.getActivityTitles());
        int maxWidth = 0;
        String pattern = "<\\*br\\*>";
        for (String string : allLabels) {
            maxWidth = Math.max(maxWidth, StringUtility.getStringWidth(string, ACTIVITY_LABEL_FONT, pattern));
        }                                                            
        chart.xAxis().setLabels(allLabels);                    
        chart.xAxis().setLabelOffset(0.1);        
        chart.xAxis().setReverse();
        chart.xAxis().setTickOffset(0.5);
        chart.xAxis().setLabelStyle(ChartTextUtility.getContentFont(), 8, ChartTextUtility.getContentColor(), 0);
        ChartUtility.setShortWeekdays(chart, locale);

        Date scaleStartDate = data.getScaleStartDate(locale);
        Date scaleEndDate = data.getScaleEndDate(locale);
                
        
        if (data.getRangeInDays() > 365 * 2) {     
            chart.yAxis().setLabelOffset(SECONDS_PER_DAY * 180);  // to write the label in the middle of the month 
            chart.yAxis().setLabelFormat("{value|yyyy}");                   
            chart.yAxis().setDateScale(scaleStartDate, scaleEndDate, SECONDS_PER_DAY * 360);            
            chart.yAxis().setTickLength(4, 4);    
            if (data.getRangeInDays() < 365 * 4) {
                chart.yAxis2().setLabelGap(3);
                chart.yAxis2().setLabelOffset(SECONDS_PER_DAY * 15 );
                chart.yAxis2().setLabelFormat("{value|mm}");
                chart.yAxis2().setDateScale( scaleStartDate, scaleEndDate, SECONDS_PER_DAY * 30);
                chart.yAxis2().setLabelStyle(ChartTextUtility.getContentFont(), 7, ChartTextUtility.getContentColor(), 0);
            }
        }
        else if (data.getRangeInDays() > 31) {       
            chart.yAxis().setLabelOffset(SECONDS_PER_DAY * 15);  // to write the label in the middle of the month 
            chart.yAxis().setLabelFormat("{value|mmm <*br*>yyyy}");
            chart.yAxis().setDateScale(scaleStartDate, scaleEndDate, SECONDS_PER_DAY * 30);      
            chart.yAxis().setTickLength(4, 0);                  
        } else {
            String dateFormat = ChartUtility.getDateFormat(locale);    
            chart.yAxis().setLabelOffset(SECONDS_PER_DAY * 3.5);  // to write the label in the middle of the week   
            chart.yAxis().setLabelFormat("{value|"+dateFormat+"} - {={value}+86400 * 6|"+dateFormat+"}"); 
            chart.yAxis().setDateScale( scaleStartDate, scaleEndDate, 7 * SECONDS_PER_DAY);
            chart.yAxis().setTickLength(4, 4);
            chart.yAxis2().setLabelGap(3);
            chart.yAxis2().setLabelOffset(SECONDS_PER_DAY / 2 );
            chart.yAxis2().setLabelFormat("{value|dd<*br*>w}");
            chart.yAxis2().setDateScale( scaleStartDate, scaleEndDate, SECONDS_PER_DAY);
            chart.yAxis2().setLabelStyle(ChartTextUtility.getContentFont(), 8, ChartTextUtility.getContentColor(), 0);
        }
        
        if (data.isLeftMarginRequired(locale)) {
            chart.yAxis().setMargin(1, 30);  
            chart.yAxis2().setMargin(1, 30);  
        } else {
            chart.yAxis().setMargin(1, 0);              
            chart.yAxis2().setMargin(1, 0);              
        }
        chart.yAxis().setLabelStyle(ChartTextUtility.getContentFont(), 9, ChartTextUtility.getContentColor(), 0);
        chart.setYAxisOnRight();
        
        return maxWidth + 10;
    }

    protected void addPlotArea(XYChart chart, int yLabelWidth, int filterWidth, Locale locale) {
        chart.swapXY();
        int activityCount = data.getActivityCount();
        int activityHeight = activityCount * 50;
        chart.setPlotArea(
                50 + yLabelWidth, 
                70 , 
                chartWidth - 80 - filterWidth - yLabelWidth, 
                Math.max(chartHeight - 150, activityHeight),
                Chart.CColor(VERTICAL_GRID_1),          // bgColor 
                Chart.CColor(VERTICAL_GRID_2),          // altBgColor
                Chart.Transparent,          
                Chart.Transparent,
                chart.dashLineColor(Chart.CColor(EvaluationColor.DOT_LINE_GRID_COLOR), Chart.DotLine)       // vGridColor
        );
    }

    private void addActivities(XYChart chart, Locale locale) {
    	
        int maxLevel = 0;
        String[] ids = data.getActivityIDs();
        int arrayLength = data.getActivityCount();
        for (int index = 0; index < ids.length; index++) {
        	
        	
            String id = ids[index];
            CustomActivityData activity = data.getActivity(id); 
            
            
            String[] ovids = new String[arrayLength];
            String[] objectType = new String[arrayLength];              
            ovids[index] = activity.getId();   
            objectType[index] = activity.getObjectType();
            
            Date[] plannedStart = new Date[arrayLength];
            plannedStart[index] = activity.getPlannedStart();
            Date[] plannedEnd = new Date[arrayLength];
            plannedEnd[index] = activity.getPlannedEnd();

            BoxWhiskerLayer actualBoxLayer = null;
            BoxWhiskerLayer plannedBoxLayer;
            
            if (!data.isTemplate()) {
            	
                /*Date[] actualStart = new Date[arrayLength];
                actualStart[index] = activity.getActualStart();
                Date[] actualEnd = new Date[arrayLength];
                actualEnd[index] = activity.getActualEnd();*/
                                          
                Date plannedStartTemp = activity.getPlannedStart();
                Date plannedEndTemp = activity.getPlannedEnd();
                
                long diff = plannedEndTemp.getTime() - plannedStartTemp.getTime();
                
                Date[] actualStart = new Date[arrayLength];
                Date[] actualEnd = new Date[arrayLength];
 
	            long perc_compl = activity.getPercentageCompleted().longValue();
	            long diffPerc = (long)(diff * (perc_compl / 100.0));
	            Date percEndTemp = new Date(plannedStartTemp.getTime() + diffPerc);

	            actualStart[index] = plannedStartTemp;

	            actualEnd[index] = percEndTemp;

                
                actualBoxLayer = chart.addBoxWhiskerLayer2(
                        Chart.CTime(actualStart),
                        Chart.CTime(actualEnd)
                );

                int[] actualColors = new int[arrayLength];
                int actualColor = getActualTimeColor(chart);
                actualColors[index] = actualColor;
                actualBoxLayer.setBoxColors(actualColors);
                actualBoxLayer.setDataWidth(10);   
                actualBoxLayer.addExtraField(objectType);
                actualBoxLayer.addExtraField(ovids);
                actualBoxLayer.setBorderColor(Chart.Transparent);
            }
                               
            plannedBoxLayer = chart.addBoxWhiskerLayer2(
                    Chart.CTime(plannedStart),
                    Chart.CTime(plannedEnd)                        
            );

            Color[] plannedColors = new Color[arrayLength];              
            plannedColors[index] = getLevelColor(activity.getLevel(), getBoxColor());
            maxLevel = Math.max(activity.getLevel(), maxLevel);
            plannedBoxLayer.setDataWidth(35);                 
            
            plannedBoxLayer.setBoxColors(Chart.CColor(plannedColors));
            plannedBoxLayer.addExtraField(objectType);
            plannedBoxLayer.addExtraField(ovids);
            plannedBoxLayer.setBorderColor(Chart.Transparent);
            
            if (request != null) {
                String[] ttpStrings = createToolTipLink(data, locale, request.getContextPath());
                plannedBoxLayer.addExtraField(ttpStrings);
                if (actualBoxLayer != null) {
                    actualBoxLayer.addExtraField(ttpStrings);                    
                }
            }                   
        }

        if (!data.isTemplate()) {
            // Add a red dash line to represent the current day
            Date now = DateUtils.today();
            if (data.getScaleStartDate(locale).before(now) && data.getScaleEndDate(locale).after(now)) {
                chart.yAxis().addMark(Chart.CTime(now), chart.dashLineColor(Chart.CColor(NOW_MARK), Chart.DashLine));       
            }
        }

        LegendBox legendBox = chart.getLegend();
        IResourceBundle resourceBundle = ResourceBundleFactory.getBundle(locale);
        for (int i = 1; i <= maxLevel; i++) {
            Color color = getLevelColor(i, getBoxColor());
            String labelKey;
            if (i == 1) {
                labelKey = "evaluation.audit.chart.gantt.legend.topLevel.DBI";
            } else {
                labelKey = "evaluation.audit.chart.gantt.legend.subLevel.DBI";
            }
            legendBox.addKey(resourceBundle.getString(labelKey, String.valueOf(i-1)), Chart.CColor(color));
        }
        
    }

    private void addActivityConnections(XYChart chart) {
        String[] ids = data.getActivityIDs();        
        drawConnections(chart, ids, true);
        drawConnections(chart, ids, false);
    }

    private void drawConnections(XYChart chart, String[] ids, boolean shadow) {
        Color shadowColor = Color.WHITE;   
        int cArrowColor = shadow ? Chart.CColor(shadowColor) : Chart.CColor(CONNECTION_ARROW_COLOR);                        
        int width = shadow ? 3 : 1;
        
      //  if (data.getActivity(ids[0]).getObjectType().equals("AUDIT") || data.getActivity(ids[0]).getObjectType().equals("AUDITTEMPLATE")) {
        	int gap = 10;
            String id = ids[0];
            CustomActivityData mainActivity = data.getActivity(id);  
           // Color lineColor = shadow ? shadowColor : Color.BLACK; 
           // Color lineColor = shadow ? shadowColor : getLevelColor(mainActivity.getLevel(), getBoxColor());
            Color lineColor = shadow ? shadowColor : CONNECTION_LINE_COLOR;
            int cLineColor = Chart.CColor(lineColor);
            if (data.hasSubActivities(mainActivity)) {
                Date mainStartDate = mainActivity.getPlannedStart();
                double joinFrom = Chart.CTime(mainStartDate);
                List<CustomActivityData> subActivities = data.getSubActivities(mainActivity);
                for (CustomActivityData subActivity : subActivities) { 
                    Date nextStart = subActivity.getPlannedStart();                   
                    double joinTo = Chart.CTime(nextStart);
                    if (joinTo == joinFrom) {                        
                        gap = gap + 7;    
                    }
                }
                for (CustomActivityData subActivity : subActivities) {
                    Date nextStart = subActivity.getPlannedStart();
                    double joinTo = Chart.CTime(nextStart);
                    int fromYCoor = chart.getXCoor(0);
                    int fromXCoor = chart.getYCoor(joinFrom) - 2;
                    int toYCoor = chart.getXCoor(data.getIndexOf(subActivity));
                    int toXCoor = chart.getYCoor(joinTo) - 3;
                    // line                   
                    chart.addLine(fromXCoor, fromYCoor + 10, fromXCoor -gap, fromYCoor + 10, cLineColor, width);
                    chart.addLine(fromXCoor - gap, fromYCoor + 10, fromXCoor - gap, toYCoor - 10, cLineColor, width);
                    chart.addLine(fromXCoor - gap, toYCoor - 10, toXCoor, toYCoor - 10, cLineColor, width);                                             
                    // arrow                      
                    chart.addLine(toXCoor - 7, toYCoor - 13, toXCoor, toYCoor - 10, cArrowColor, width);                    
                    chart.addLine(toXCoor - 6, toYCoor - 12, toXCoor, toYCoor - 10, cArrowColor, width);
                    chart.addLine(toXCoor - 5, toYCoor - 11, toXCoor, toYCoor - 10, cArrowColor, width);
                    chart.addLine(toXCoor - 4, toYCoor - 10, toXCoor + 1, toYCoor - 10, cArrowColor, width);
                    chart.addLine(toXCoor - 5, toYCoor -  9, toXCoor, toYCoor - 10, cArrowColor, width);
                    chart.addLine(toXCoor - 6, toYCoor -  8, toXCoor, toYCoor - 10, cArrowColor, width);
                    chart.addLine(toXCoor - 7, toYCoor -  7, toXCoor, toYCoor - 10, cArrowColor, width);
                }
            }
       /*Código comentado em 07/05/2017 ****( INICIO )***   */  	
       /* } else {
        	for (int index = 0; index < ids.length -1; index++) {
                int gap = 10;
                String id = ids[index];
                CustomActivityData mainActivity = data.getActivity(id);  
               // Color lineColor = shadow ? shadowColor : Color.BLACK; 
               // Color lineColor = shadow ? shadowColor : getLevelColor(mainActivity.getLevel(), getBoxColor());
                Color lineColor = shadow ? shadowColor : CONNECTION_LINE_COLOR;
                int cLineColor = Chart.CColor(lineColor);
                if (data.hasSubActivities(mainActivity)) {
                    Date mainStartDate = mainActivity.getPlannedStart();
                    double joinFrom = Chart.CTime(mainStartDate);
                    List<CustomActivityData> subActivities = data.getSubActivities(mainActivity);
                    for (CustomActivityData subActivity : subActivities) { 
                        Date nextStart = subActivity.getPlannedStart();                   
                        double joinTo = Chart.CTime(nextStart);
                        if (joinTo == joinFrom) {                        
                            gap = gap + 7;    
                        }
                    }
                    for (CustomActivityData subActivity : subActivities) {
                        Date nextStart = subActivity.getPlannedStart();
                        double joinTo = Chart.CTime(nextStart);
                        int fromYCoor = chart.getXCoor(index);
                        int fromXCoor = chart.getYCoor(joinFrom) - 2;
                        int toYCoor = chart.getXCoor(data.getIndexOf(subActivity));
                        int toXCoor = chart.getYCoor(joinTo) - 3;
                        // line                   
                        chart.addLine(fromXCoor, fromYCoor + 10, fromXCoor -gap, fromYCoor + 10, cLineColor, width);
                        chart.addLine(fromXCoor - gap, fromYCoor + 10, fromXCoor - gap, toYCoor - 10, cLineColor, width);
                        chart.addLine(fromXCoor - gap, toYCoor - 10, toXCoor, toYCoor - 10, cLineColor, width);                                             
                        // arrow                      
                        chart.addLine(toXCoor - 7, toYCoor - 13, toXCoor, toYCoor - 10, cArrowColor, width);                    
                        chart.addLine(toXCoor - 6, toYCoor - 12, toXCoor, toYCoor - 10, cArrowColor, width);
                        chart.addLine(toXCoor - 5, toYCoor - 11, toXCoor, toYCoor - 10, cArrowColor, width);
                        chart.addLine(toXCoor - 4, toYCoor - 10, toXCoor + 1, toYCoor - 10, cArrowColor, width);
                        chart.addLine(toXCoor - 5, toYCoor -  9, toXCoor, toYCoor - 10, cArrowColor, width);
                        chart.addLine(toXCoor - 6, toYCoor -  8, toXCoor, toYCoor - 10, cArrowColor, width);
                        chart.addLine(toXCoor - 7, toYCoor -  7, toXCoor, toYCoor - 10, cArrowColor, width);
                    }
                }
            }
        }*/
        /*Código comentado em 07/05/2017 ****( FIM )***   */                
    }

    private Color getBoxColor() {
        if (data.isTemplate()) {
            return ACTIVITY_TEMPLATE;
        }
        return ACTIVITY_AUDIT;
    }

    private Color getLevelColor(int level, Color color) {
        for (int i = 1; i < level; i++) {
            color = EvaluationColor.brighter(color, 120, 100);
        }
        return color;
    }

    private String[] wrapLines(String[] activityTitles) {
        String[] result = new String[activityTitles.length];
        for (int i = 0; i < activityTitles.length; i++) {
            result[i] = StringUtility.wrapLines(activityTitles[i], ACTIVITY_LABEL_FONT, 100, "<*br*>", 2, "...");
        }
        return result;
    }
         
    protected String[] createToolTipLink(CustomGanttChartData data, Locale locale, String contextPath) 
    {
        java.util.List<String> strings = new ArrayList<>();
        IResourceBundle bundle = ResourceBundleFactory.getBundle(locale);
        
        for (CustomActivityData activity : data.getActivities()) {
            StringBuilder toolTipLink = new StringBuilder();


            StringBuffer url = new StringBuffer();
            
            
            url.append("/evaluation.do")
            	.append("?__commandId=init")
            	.append("&amp;__evaluationId=auditGanttChart")
            	.append("&__objectType=").append(activity.getObjectType())
                .append("&__object=").append(activity.getId())
                .append("&__windowType=popup");
              
            /*
            url.append("/object.do")
                    .append("?__commandId=popup")
                    .append("&__objectType=").append(activity.getObjectType())
                    .append("&__object=").append(activity.getId());
*/
            
            toolTipLink
					.append("<table>")
                    .append("<tr><th colspan=\"2\" align=\"left\">");            
            toolTipLink.append("<a href=").append("javascript:aam_openPopUp(&quot;").append(contextPath)
                    .append(RequestUtils.addWindowIdIfNecessaryTo(url.toString()))
                    .append("&quot;,&quot;object&quot;)")
                    .append(">");
            toolTipLink
                    .append(ChartIconUtility.getHtmlIconString(request, "iconlib/icons/view_show_16.png"))
                    .append(bundle.getString("evaluation.audit.chart.gantt.ttp.open.form.DBI"));			
			toolTipLink
					.append("</a>");

            if (data.getType() == CustomGanttChartData.Type.CROSS_AUDIT) {
                url = new StringBuffer();
                url.append("/evaluation.do")
                        .append("?__commandId=init")
                        .append("&__evaluationId=").append(CrossAuditGanttChartDefinition.DETAIL_GANTT_CHART)
                        .append("&__objectType=").append(activity.getObjectType())
                        .append("&__object=").append(activity.getId())
                        .append("&__windowType=popup");

                toolTipLink.append("<a href=")
                        .append("javascript:aam_openPopUp(&quot;").append(contextPath)
                        .append(RequestUtils.addWindowIdIfNecessaryTo(url.toString()))
                        .append("&quot;)>");
                toolTipLink
                        .append(ChartIconUtility.getHtmlIconString(request, "iconlib/icons/button_gantt_16.png"))
                        .append(bundle.getString("evaluation.audit.chart.gantt.ttp.open.chart.DBI"));			
                toolTipLink
                        .append("</a>")
                        .append("</th>");                        
            } else {
                toolTipLink
                        .append("<th></th>");
            }
            
            toolTipLink.append("</tr>");
            
            String plannedLabel = bundle.getString("form.row.audit.period.DBI");
            String actualLabel = bundle.getString("form.row.audit.period.actual.DBI");

            String plannedRange = ChartUtility.createDateRangeString(activity.getPlannedStart(), activity.getPlannedEnd(), locale);
            String actualRange = ChartUtility.createDateRangeString(activity.getActualStart(), activity.getActualEnd(), locale);

            toolTipLink
					.append("<tr>")
					.append(create(activity.getTileLabel(), activity.getTitle(), false, true))
					.append(create(activity.getDescriptionLabel(), activity.getDescription(), false, true))
					.append("<tr><td></td><td></td></tr>");

            
            toolTipLink.append(create(plannedLabel, plannedRange, false, true));
            
            if (!data.isTemplate()) {
                toolTipLink
                    .append(create(actualLabel, actualRange, false, true)); 
                
                toolTipLink.append(create(activity.getPercentageCompletedLabel(), String.valueOf(activity.getPercentageCompleted()) + "%", false, true));

                IEnumerationItem ownerState = activity.getOwnerState();
                if (ownerState != null) {
                    String ownerStateStr = bundle.getString(ownerState.getPropertyKey());
                    toolTipLink
                         .append(create(activity.getOwnerStateLabel(), ownerStateStr, false, true));
                }
                
                IEnumerationItem reviewerState = activity.getReviewerStatus();
                if (reviewerState != null) {
                    String reviewerStateStr = bundle.getString(reviewerState.getPropertyKey());
                    toolTipLink
                         .append(create(activity.getReviewerStateLabel(), reviewerStateStr, false, true));
                }
            }
            
            toolTipLink
                    .append("</table>");
            
            strings.add(toolTipLink.toString());
        }    
        
        return  strings.toArray(new String[strings.size()]);
    }
    
	protected String create(String label, String value, boolean boldLabel, boolean sameLine) {
		StringBuilder tooltipLink = new StringBuilder(200);
		     
		tooltipLink.append("<tr>");
		
        int col = sameLine ? 1 : 2;
        
        String colSpan = " colspan=\""+col+"\"";
        
		if (boldLabel) {
            tooltipLink.append("<td style=\"font-weight:bold;\"").append(colSpan).append(">");
		} else {
            tooltipLink.append("<td").append(colSpan).append(">");			
		}
		        
		tooltipLink.append(ChartTextUtility.escapeForChartTooltip(label+": "));
        if (!sameLine) {
            tooltipLink.append("</td></tr><tr>"); 
        } 
        else {
            tooltipLink.append("</td>");
        }
		
		if (boldLabel) {
            tooltipLink.append("<td style=\"font-weight:bold;\"").append(colSpan).append(" width=\"250\">");
		} else {
            tooltipLink.append("<td").append(colSpan).append(" width=\"250\">");			
		}
		
		tooltipLink.append(ChartTextUtility.escapeForChartTooltip(value)).append("</td></tr>");
		return tooltipLink.toString();
	}
    
    private int getActualTimeColor(XYChart chart) {
        return chart.patternColor(
                new int[]{
                        WHITE, WHITE, WHITE, R_E_D,
                        WHITE, WHITE, R_E_D, WHITE,
                        WHITE, R_E_D, WHITE, WHITE,
                        R_E_D, WHITE, WHITE, WHITE},4
        );
    }
        
    private int getTodayMarkColor(XYChart chart) {
        return chart.patternColor(
                new int[]{
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, R_E_D, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
                        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE},14
        );
    }
}
