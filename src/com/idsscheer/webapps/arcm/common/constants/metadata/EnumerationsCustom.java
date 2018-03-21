package com.idsscheer.webapps.arcm.common.constants.metadata;

import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationWrapper;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumeration;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.MessageTemplateEnumerationItem;

public class EnumerationsCustom extends Enumerations {

	/*private static <T extends IEnumeration> T lookupEnumeration(String enumerationId) {
		return Metadata.getMetadata().getEnumeration(enumerationId);
	}*/
	
	private static <T extends IEnumerationItem> T lookupEnumerationItem(IEnumeration enumeration, String itemId) {
		return enumeration.getItemById(itemId);
	}
	
	public static abstract interface CENUM_IS_CREATOR_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_is_creator_status");
		public static final IEnumerationItem PLEASE_SELECT = EnumerationsCustom.lookupEnumerationItem(ENUM, "please_select");
		public static final IEnumerationItem NEW = EnumerationsCustom.lookupEnumerationItem(ENUM, "new");
		public static final IEnumerationItem IN_REVIEW = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_review");
	}
	
	public static abstract interface CENUM_IS_OWNER_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_is_owner_status");
		public static final IEnumerationItem IN_REVIEW = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_review");
		public static final IEnumerationItem OPEN = EnumerationsCustom.lookupEnumerationItem(ENUM, "open");
		public static final IEnumerationItem IN_PROGRESS = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_progress");
		public static final IEnumerationItem FUP = EnumerationsCustom.lookupEnumerationItem(ENUM, "fup");
		public static final IEnumerationItem OPEN_FOR_EXECUTION = EnumerationsCustom.lookupEnumerationItem(ENUM, "open_for_execution");
		public static final IEnumerationItem ATTENDED = EnumerationsCustom.lookupEnumerationItem(ENUM, "attended");
		public static final IEnumerationItem RISK_ASSUMED = EnumerationsCustom.lookupEnumerationItem(ENUM, "risk_assumed");
		public static final IEnumerationItem SETTLED = EnumerationsCustom.lookupEnumerationItem(ENUM, "settled");
		public static final IEnumerationItem TO_BE_REVIEWED = EnumerationsCustom.lookupEnumerationItem(ENUM, "to_be_reviewed");
	}
	
	public static abstract interface CENUM_IS_REVIEWER_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_is_reviewer_status");
		public static final IEnumerationItem TO_BE_REVIEWED = EnumerationsCustom.lookupEnumerationItem(ENUM, "to_be_reviewed");
		public static final IEnumerationItem IN_PROGRESS = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_progress");
		public static final IEnumerationItem ATTENDED = EnumerationsCustom.lookupEnumerationItem(ENUM, "attended");
		public static final IEnumerationItem RISK_ASSUMED = EnumerationsCustom.lookupEnumerationItem(ENUM, "risk_assumed");
		public static final IEnumerationItem SETTLED = EnumerationsCustom.lookupEnumerationItem(ENUM, "settled");
		public static final IEnumerationItem FUP = EnumerationsCustom.lookupEnumerationItem(ENUM, "fup");
	}
	
	public static abstract interface CENUM_AP_CREATOR_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_ap_creator_status");
		public static final IEnumerationItem PLEASE_SELECT = EnumerationsCustom.lookupEnumerationItem(ENUM, "please_select");
		public static final IEnumerationItem NEW = EnumerationsCustom.lookupEnumerationItem(ENUM, "new");
		public static final IEnumerationItem PENDING = EnumerationsCustom.lookupEnumerationItem(ENUM, "pending");
		public static final IEnumerationItem IN_PROGRESS = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_progress");
		public static final IEnumerationItem FUP = EnumerationsCustom.lookupEnumerationItem(ENUM, "fup");
		public static final IEnumerationItem RISK_ASSUMED = EnumerationsCustom.lookupEnumerationItem(ENUM, "risk_assumed");
	}
	
	public static abstract interface CENUM_AP_OWNER_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_ap_owner_status");
		public static final IEnumerationItem IN_PROGRESS = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_progress");
		public static final IEnumerationItem PENDING = EnumerationsCustom.lookupEnumerationItem(ENUM, "pending");
		public static final IEnumerationItem FUP = EnumerationsCustom.lookupEnumerationItem(ENUM, "fup");
		public static final IEnumerationItem RISK_ASSUMED = EnumerationsCustom.lookupEnumerationItem(ENUM, "risk_assumed");
	}
	
	public static abstract interface CENUM_AP_REVIEWER_STATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("cenum_ap_reviewer_status");
		public static final IEnumerationItem IN_PROGRESS = EnumerationsCustom.lookupEnumerationItem(ENUM, "in_progress");
		public static final IEnumerationItem FUP = EnumerationsCustom.lookupEnumerationItem(ENUM, "fup");
		public static final IEnumerationItem NOT_APPROVED = EnumerationsCustom.lookupEnumerationItem(ENUM, "not_approved");
		public static final IEnumerationItem SETTLED = EnumerationsCustom.lookupEnumerationItem(ENUM, "settled");
		public static final IEnumerationItem ATTENDED = EnumerationsCustom.lookupEnumerationItem(ENUM, "attended");
		public static final IEnumerationItem RISK_ASSUMED = EnumerationsCustom.lookupEnumerationItem(ENUM, "risk_assumed");
	}
	
	public static abstract interface CUSTOM_ENUMACTIONTYPE{
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("custom_enumactiontype");
		public static final IEnumerationItem ACTIONPLAN = EnumerationsCustom.lookupEnumerationItem(ENUM, "actionplan");
		public static final IEnumerationItem ISSUE = EnumerationsCustom.lookupEnumerationItem(ENUM, "issue");
	}
	
	public static abstract interface CUSTOM_JOBS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("jobs");
		public static final IEnumerationItem ISSUE_PENDING = EnumerationsCustom.lookupEnumerationItem(ENUM, "updateIssuesJob");
		public static final IEnumerationItem ISSUE_SLA = EnumerationsCustom.lookupEnumerationItem(ENUM, "updateIssueSLAUpdateJob");
		public static final IEnumerationItem RA_MAIL = EnumerationsCustom.lookupEnumerationItem(ENUM, "RiskassessmentDelayMailJob");
		public static final IEnumerationItem CR_FIXING = EnumerationsCustom.lookupEnumerationItem(ENUM, "CorpRiskFixingJob");
		public static final IEnumerationItem CONTROL_A1L = EnumerationsCustom.lookupEnumerationItem(ENUM, "AdjustControl1Line");
		public static final IEnumerationItem CONTROL_AOL = EnumerationsCustom.lookupEnumerationItem(ENUM, "AdjustControlOtherLines");
		public static final IEnumerationItem CONTROL_RSK = EnumerationsCustom.lookupEnumerationItem(ENUM, "AdjustRisks");
		// EV131854 - Job para readequar taskitens de action plans já existentes.
		public static final IEnumerationItem ACTIONTASKITEM = EnumerationsCustom.lookupEnumerationItem(ENUM, "ActionPlanTaskItemJob");
	}
	
	public static abstract interface CUSTOM_PROGRESS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("custom_progress");
		public static final IEnumerationItem ON_TIME = EnumerationsCustom.lookupEnumerationItem(ENUM, "on_time");
		public static final IEnumerationItem OVERDUE = EnumerationsCustom.lookupEnumerationItem(ENUM, "overdue");
	}
	
	public static abstract interface CUSTOM_INITIATORS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("initiators");
		public static final MessageTemplateEnumerationItem RISKOWNER_NOTIFICATION = (MessageTemplateEnumerationItem) EnumerationsCustom.lookupEnumerationItem(ENUM, "riskowner_notification");
	}
	

	public static abstract interface CUSTOM_CUSTOMCTRLEXECSTATUS {
		public static final EnumerationWrapper ENUM = new EnumerationWrapper("custom_controlexecstatus");
		public static final IEnumerationItem EFFECTIVE = (MessageTemplateEnumerationItem) EnumerationsCustom.lookupEnumerationItem(ENUM, "effective");
		public static final IEnumerationItem INEFFECTIVE = (MessageTemplateEnumerationItem) EnumerationsCustom.lookupEnumerationItem(ENUM, "ineffective");
	}
	
}
