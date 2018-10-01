#### Riskassessment specific items
[condition][]no risk assessment impact type assessed=eval( !RiskassessmentHelper.hasAtLeastOneAssessedImpactType() )
[condition][]at least one impact type has type quantitative=eval( RiskassessmentHelper.hasAtLeastOneQuantitativeImpactType() )
[consequence][]recalculate assessment overdue=RiskassessmentHelperCustom.calculateOverdueAssessment();