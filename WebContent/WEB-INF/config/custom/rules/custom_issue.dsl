#### Issue specific items
#DMM - BOF- Revis�o da sprint - 14/05/2018
[condition][]user is systemadmin=eval( IssueHelperCustom.isUserSysAdmin() )
[condition][]is_not in workflow state "{stateId}"=eval( !CollectiveHelper.isInWorkflowState("{stateId}") )
#DMM - EOF - Revis�o da sprint - 14/05/2018
[condition][]is issue earlier than first version=eval( IssueHelperCustom.isLateThanFirstDate() )
[consequence][]recalculate time dependent state=IssueHelper.recalculateTimeDependentState();
[consequence][]set risk classification=IssueHelperCustom.setRiskAndProcessClassification();
[consequence][]set issue delay=IssueHelperCustom.setIssueDelay();
