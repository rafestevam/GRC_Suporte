package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import com.idsscheer.batchserver.logging.Logger;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.authorization.license.LicenseService;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.component.common.support.ObjectContainerHelper;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IOVIDIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsCustom;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IIssueAttributeTypeCustom;
import com.idsscheer.webapps.arcm.common.license.LicensedComponent;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

public class CustomCreateIssueCommand implements ICommand {

	@ConfigParameter(value = "rootObjectType", optional = true)
	public static final String Parameter_rootObjectType = "rootObjectType";
	public static final String WORKOBJECT_CREATED_ISSUE = "created_issue";
	
	final Logger log = new Logger();
	
	public CommandResult execute(CommandContext cc) throws BLException {
		IAppObj rootObject = cc.getChainContext().getTriggeringAppObj();
		IUserContext userContext = cc.getChainContext().getUserContext();
		IUserContext fullGrantUserCtx = ContextFactory.createFullGrantUserContext(userContext);
		
		log.info(this.getClass().getName(), "Executando comando CustomCreateIssueCommand");
		
		CreateIssueCommandRootObjectDataProvider createIssueCommandRootObjectDataProvider = createRootObjectDataProvider(
				rootObject, cc);

		if (createIssueCommandRootObjectDataProvider.measureIsNotSetToIssue()) {
			return new CommandResult(CommandResult.STATUS.OK);
		}

		IAppObjQuery query = cc.getChainContext().createAppObjQuery(ObjectType.ISSUE);
		try {
			query.addRestriction(QueryRestriction.eq(createIssueCommandRootObjectDataProvider.getIssueRelationType(),
					Long.valueOf(rootObject.getObjectId())));
			IOVIDIterator iterator = query.getResultIDIterator();
			if (0 < iterator.getSize()) {
				CommandResult localCommandResult = new CommandResult(CommandResult.STATUS.OK);
				return localCommandResult;
			}
		} finally {
			if (query != null)
				query.release();
		}

		IAppObj client = AppObjUtility.getRelatedClient(rootObject);
		String clientSign = AppObjUtility.getRelatedClientSign(rootObject);

		if (!(LicenseService.getInstance().isLicensed(LicensedComponent.ISSUE_MANAGEMENT, clientSign))) {
			return new CommandResult(CommandResult.STATUS.OK);
		}

		cc.getChainContext().put("issueRelevantObjects", Collections.singletonList(rootObject));

		createIssue(cc, rootObject, client, clientSign, userContext, fullGrantUserCtx,
				createIssueCommandRootObjectDataProvider);

		createIRO(cc, rootObject, userContext);

		return new CommandResult(CommandResult.STATUS.OK);
	}

	private CreateIssueCommandRootObjectDataProvider createRootObjectDataProvider(IAppObj rootObject,
			CommandContext cc) {
		String rootObjectType = cc.getCommandXMLParameter("rootObjectType");
		if (rootObjectType == null) {
			throw new IllegalArgumentException("The rootObjectType parameter may not be null");
		}
		String str1 = rootObjectType;
		int i = -1;
		switch (str1.hashCode()) {
		case 62628795:
			if (str1.equals("AUDIT"))
				i = 0;
			break;
		case -363718974:
			if (str1.equals("TESTCASE"))
				i = 1;
		}
		switch (i) {
		case 0:
			return new CreateIssueCommandAuditStepDataProvider(rootObject);
		case 1:
			return new CreateIssueCommandTestCaseDataProvider(rootObject);
		}
		throw new IllegalArgumentException(
				String.format("The object type '%s' is not supported", new Object[] { rootObjectType }));
	}

	protected void createIssue(CommandContext cc, IAppObj issueRelevantObject, IAppObj client, String clientSign,
			IUserContext userContext, IUserContext fullGrantUserCtx,
			CreateIssueCommandRootObjectDataProvider createIssueCommandRootObjectDataProvider) throws BLException {
		IAppObj issue = cc.getChainContext().create(Metadata.getMetadata().getObjectType("ISSUE"), true);

		Locale locale = AppObjUtility.getLocale(client);
		issue.getAttribute(IIssueAttributeType.ATTR_NAME)
				.setRawValue(clientSign + "_" + issueRelevantObject.getObjectId());

		IResourceBundle resourceBundle = ResourceBundleFactory.getInstance().getResourceBundle(locale);

		issue.getAttribute(IIssueAttributeType.ATTR_DESCRIPTION).setRawValue(
				resourceBundle.getString(createIssueCommandRootObjectDataProvider.getGeneratedIssueDesription(),
						new String[] { issueRelevantObject.getVersionData().getOVID().getId() + "" }));

		issue.getAttribute(IIssueAttributeType.ATTR_REMEDIATIONMEASURE)
				.setRawValue(resourceBundle.getString(
						createIssueCommandRootObjectDataProvider.getGeneratedIssueRemediationMeasure(),
						new String[] { issueRelevantObject.getVersionData().getOVID().getId() + "" }));
		
		//GAP GRC74 - Criação de Apontamento como "Apontamento" a partir de um TestCase
		issue.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE).setRawValue(
				Collections.singletonList(EnumerationsCustom.CUSTOM_ENUMACTIONTYPE.ISSUE)
		);
		
		//GAP GRC74 - Definir Status do Criador do Apontamento para "Em Elaboração"
		issue.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS).setRawValue(
				Collections.singletonList(EnumerationsCustom.CENUM_IS_CREATOR_STATUS.NEW)
		);
		
		//GAP GRC74 - Definir Data do Apontamento
		issue.getAttribute(IIssueAttributeTypeCustom.ATTR_ISSUE_DATE).setRawValue(
			new Date()
		);
		
		//GAP GRC74 - Definir Ano Base do Apontamento
		issue.getAttribute(IIssueAttributeTypeCustom.ATTR_ISSUE_YEAR).setRawValue(
			String.valueOf(Calendar.getInstance().get(Calendar.YEAR))	
		);
				
		issue.getAttribute(createIssueCommandRootObjectDataProvider.getIssueCreatedByAttribute())
				.setRawValue(Boolean.TRUE);
		issue.getAttribute(createIssueCommandRootObjectDataProvider.getIssueRelationType())
				.setRawValue(issueRelevantObject.getObjectId());

		IListAttribute issueCreatorAttribute = issue.getAttribute(IIssueAttributeType.LIST_CREATOR);
		issueCreatorAttribute.addLastElement(userContext.getUser(), fullGrantUserCtx);
		
		log.info(this.getClass().getName(), 
				"Apontamento Criado: " + issue.getAttribute(IIssueAttributeType.BASE_ATTR_ID).getRawValue().toString() +
				" - " + issue.getAttribute(IIssueAttributeType.ATTR_NAME).getRawValue());
		
		IEnumAttribute issueTypeAttr = issue.getAttribute(IIssueAttributeTypeCustom.ATTR_ACTIONTYPE);
		IEnumerationItem issueType = ARCMCollections.extractSingleEntry(issueTypeAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "Tipo: " + issueType.getId());
		
		IEnumAttribute issueCreatorStatusAttr = issue.getAttribute(IIssueAttributeTypeCustom.ATTR_IS_CREATOR_STATUS);
		IEnumerationItem issueCreatorStatus = ARCMCollections.extractSingleEntry(issueCreatorStatusAttr.getRawValue(), true);
		log.info(this.getClass().getName(), "Status do Criador: " + issueCreatorStatus.getId());
		
		cc.getChainContext().save(issue, true);
		log.info(this.getClass().getName(), "Apontamento Salvo");
		
		cc.getChainContext().put("created_issue", issue);
		log.info(this.getClass().getName(), "Apontamento Listado");
	}

	protected void createIRO(CommandContext cc, IAppObj appObj, IUserContext userContext) throws BLException {
		IAppObj iro = cc.getChainContext().create(Metadata.getMetadata().getObjectType("ISSUERELEVANTOBJECT"), true);
		ObjectContainerHelper.fillObjectContainer(userContext, iro, appObj);
		cc.getChainContext().save(iro, true);
	}

}
