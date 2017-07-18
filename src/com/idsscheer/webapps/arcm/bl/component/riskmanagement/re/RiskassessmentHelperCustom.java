package com.idsscheer.webapps.arcm.bl.component.riskmanagement.re;

import java.util.Date;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.re.REKey;
import com.idsscheer.webapps.arcm.bl.re.ext.CollectiveHelper;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class RiskassessmentHelperCustom extends CollectiveHelper {

	public static void calculateOverdueAssessment(){
		
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj ra = env.getRuleAppObj();
		REKey plannedbeforeKey = ra.createAtomicKey("plannedenddate");
		REKey stateTimeKey = ra.createAtomicKey("progress");
		
		Date date = (Date) ra.getRawValue(plannedbeforeKey.getString());
		if(date != null){
			Date plannedEndDate = DateUtils.normalizeLocalDate(date, DateUtils.Target.END_OF_DAY);
			if(plannedEndDate != null){
				Date today = DateUtils.normalizeLocalDate(DateUtils.today(), DateUtils.Target.END_OF_DAY);
				if (!(plannedEndDate.before(today))) {
					String key = stateTimeKey.getString();
					List items = ARCMCollections.createList(new IEnumerationItem[] { EnumerationsCustom.CUSTOM_PROGRESS.ON_TIME });
					ra.setRawValue(key, items);
				} else{
					String key = stateTimeKey.getString();
					List items = ARCMCollections.createList(new IEnumerationItem[] { EnumerationsCustom.CUSTOM_PROGRESS.OVERDUE });
					ra.setRawValue(key, items);
				}/* else if (controlexecution.isDirty(stateTimeKey.getString())) {
					controlexecution.resetValue(stateTimeKey.getString());
				}*/
				
			}
		}
		
	}
	
}
