#### Issue specific items
[condition][]user is systemadmin=eval( IssueHelperCustom.isUserSysAdmin() )
[condition][]is_not in workflow state "{stateId}"=eval( !CollectiveHelper.isInWorkflowState("{stateId}") )
[consequence][]recalculate time dependent state=IssueHelper.recalculateTimeDependentState();
[consequence][]set risk classification=IssueHelperCustom.setRiskAndProcessClassification();