package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.LockInfoFlag;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IQuotationAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.DataLayerObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.ILockService;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.ILockObject;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockServiceException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.lockservice.LockType;

public class CustomSaveDependentObjectsCommand implements ICommand {

	@ConfigParameter(value = "listAttributeID", optional = false)
	public static final String PARAMETER_LISTATTRIBUTEID = "listAttributeID";
	private Log log;

	public CustomSaveDependentObjectsCommand() {
		this.log = LogFactory.getLog(super.getClass());
	}

	public CommandResult execute(CommandContext cc) throws BLException {
		String listAttributeID = cc.getCommandXMLParameter("listAttributeID");

		IAppObj triggeringAppObj = cc.getChainContext().getTriggeringAppObj();
		if (!(triggeringAppObj.hasAttributeType(listAttributeID))) {
			throw new IllegalArgumentException("No attribute found with id '" + listAttributeID + "' at object type '"
					+ triggeringAppObj.getObjectType().getId() + "'");
		}

		IAttributeType attributeType = triggeringAppObj.getAttributeType(listAttributeID);
		if (!(attributeType instanceof IListAttributeType)) {
			throw new IllegalArgumentException("Attribute '" + listAttributeID + "' is no list attribute type");
		}

		IListAttributeType listAttributeType = (IListAttributeType) attributeType;
		IListAttribute listAttribute = triggeringAppObj.getAttribute(listAttributeType);
		ILockService service = ARCMServiceProvider.getInstance().getLockService();
		IUserContext userContext = cc.getChainContext().getUserContext();

		for (IAppObj elementAppObj : listAttribute.getElements(userContext)) {
			/*//REO - EV103278 - Inicio
			//elementAppObj.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL).removeAllElements(userContext);
			IListAttribute lossQualList = elementAppObj.getAttribute(IRa_impacttypeAttributeType.LIST_LOSSQUAL);
			while(lossQualList.getSize() != 1){
				int count = 0;
				for(IAppObj lossQual : lossQualList.getElements(userContext)){
					if(count > 0){
						lossQualList.removeElement(lossQual, userContext);
					}
					count += 1;
				}
			}//REO - EV103278 - Fim */
			if (elementAppObj.isDirty()) {
				try {
					ILockObject lock = service.findLock(elementAppObj.getVersionData().getOVID(),
							userContext.getUserID(), userContext.getRemoteClientID());
					if ((lock != null) && (lock.getLockType().equals(LockType.WRITE))) {
						if (lock.hasInfoFlag(LockInfoFlag.CHECK_OUT)) {
							IOVID lockUserId = lock.getLockUserId();

							ILockObject foundLock = findLockObject(elementAppObj, lockUserId, userContext);

							if (!(switchCheckOutLockToWriteLock(elementAppObj, userContext, foundLock))) {
								return new CommandResult(CommandResult.STATUS.FAILED);
							}
							cc.getChainContext().save(elementAppObj, false);
							if (!(switchWriteLockToCheckOutLock(elementAppObj, userContext, lockUserId, foundLock))) {
								return new CommandResult(CommandResult.STATUS.FAILED);
							}
						} else {
							cc.getChainContext().save(elementAppObj, false);
						}
					} else {
						cc.getChainContext().allocateWriteLock(elementAppObj);
						cc.getChainContext().save(elementAppObj, false);
						cc.getChainContext().releaseWriteLock(elementAppObj);
					}
				} catch (LockServiceException lse) {
					return new CommandResult(CommandResult.STATUS.FAILED);
				}
			}
		}

		return CommandResult.OK;
	}

	private boolean switchCheckOutLockToWriteLock(IAppObj appObj, IUserContext toUserContext, ILockObject foundLock)
			throws LockServiceException {
		ILockService service = ARCMServiceProvider.getInstance().getLockService();
		try {
			if (foundLock == null) {
				this.log.error("Could not switch check out lock to write lock for object '"
						+ AppObjUtility.getLocalisedAppObjName(appObj, toUserContext.getLanguage()) + "'");
				return false;
			}
			service.releaseLock(foundLock.getLockObjectId(), foundLock.getLockUserId(), LockInfoFlag.CHECK_OUT, null);
			service.allocateLock(foundLock.getLockObjectId(), appObj.getObjectType(), LockType.WRITE,
					toUserContext.getUserID(), foundLock.getRemoteClientID(), LockInfoFlag.CLEAN);
		} catch (LockServiceException lse) {
			this.log.error("Could not switch check out lock to write lock for object '"
					+ AppObjUtility.getLocalisedAppObjName(appObj, toUserContext.getLanguage()) + "'");
			return false;
		}
		return true;
	}

	private boolean switchWriteLockToCheckOutLock(IAppObj appObj, IUserContext fromUserContext, IOVID toLockOwnerId,
			ILockObject foundLock) {
		ILockService service = ARCMServiceProvider.getInstance().getLockService();
		IOVID objectId = OVIDFactory.getOVID(appObj.getObjectId());
		IOVID oldLockOwner = fromUserContext.getUserID();
		try {
			service.releaseLock(objectId, oldLockOwner, LockInfoFlag.CLEAN, fromUserContext.getRemoteClientID());
			service.allocateLock(objectId, appObj.getObjectType(), LockType.WRITE, toLockOwnerId,
					foundLock.getRemoteClientID(), LockInfoFlag.CHECK_OUT);
		} catch (LockServiceException lse) {
			this.log.error("Could not switch write lock to check out lock for object '"
					+ AppObjUtility.getLocalisedAppObjName(appObj, fromUserContext.getLanguage()) + "'");
			return false;
		}
		return true;
	}

	private ILockObject findLockObject(IAppObj appObj, IOVID fromLockOwnerId, IUserContext toUserContext)
			throws LockServiceException {
		ILockService service = ARCMServiceProvider.getInstance().getLockService();
		ILockObject lock = service.findLock(OVIDFactory.getOVID(appObj.getObjectId()), fromLockOwnerId,
				toUserContext.getRemoteClientID());
		if (lock == null) {
			lock = service.findLock(OVIDFactory.getOVID(appObj.getObjectId()), toUserContext.getUserID(),
					toUserContext.getRemoteClientID());
		}
		return lock;
	}

}
