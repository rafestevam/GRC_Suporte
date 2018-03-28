package com.idsscheer.webapps.arcm.bl.component.administration.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValue;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.PredefinedFilterValue;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.PredefinedFilterValueComparator;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.ObjectRight;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

public class CustomTaskObjectTypesFilterAttribute extends TaskObjectTypesFilterAttribute {

	private List<String> allTypes = null;

	public CustomTaskObjectTypesFilterAttribute(IUserContext userContext, IFilterAttributeType filterAttributeType) {
		// TODO Auto-generated constructor stub
		super(userContext, filterAttributeType);
	}

	public List<IPredefinedFilterValue> getPredefinedValuesInternal() {
		this.allTypes = new ArrayList(8);

		List values = new ArrayList();
		values.add(new PredefinedFilterValue("-1", "html.all", false));
		for (IObjectType type : Metadata.getMetadata().getNonVirtualObjectTypes()) {
			if (ObjectType.OBJECT.equals(type)) {
				continue;
			}

			if (!(Metadata.getMetadata().getTaskItems(type.getId()).isEmpty()))
				;
			if (this.userContext.getUserComponents().isLicensed(type.getLicensedComponents(), false))
				;
			if (!(this.userContext.getUserRights().hasRight(ObjectRight.READ, type))) 
				continue;
			this.allTypes.add(type.getId());
			if (type.getId().equals("ISSUE")) {
				values.add(new PredefinedFilterValue(type.getId(), "list.Issues2.DBI", false));
			} else {
				values.add(new PredefinedFilterValue(type.getId(), type.getPropertyKey(), false));	
			}
		}
		
//		id: RA_IMPACTTYPE, value: objectType.RA_IMPACTTYPE.DBI
//		values.add(new PredefinedFilterValue("id: ACTIONPLAN", "value: list.Issues2.DBI", false));
//		this.allTypes.add("ACTIONPLAN");
		
		Collections.sort(values, new PredefinedFilterValueComparator(this.userContext.getLanguage()));
		return values;
	}

	public boolean isEmpty() {
		return false;
	}

	public List<IFilterCriteria> getFilterCriteria() {
		String viewColumnAttributeAlias = getViewColumnAttributeAlias();
		String rawValue = getRawValue(viewColumnAttributeAlias).toString();
		if ("-1".equals(rawValue)) {
			IFilterCriteria filterCriteria = filterFactory.getSimpleFilterCriteria(viewColumnAttributeAlias,
					this.allTypes);
			return Collections.singletonList(filterCriteria);
		}
		return super.getFilterCriteria();
	}
}
