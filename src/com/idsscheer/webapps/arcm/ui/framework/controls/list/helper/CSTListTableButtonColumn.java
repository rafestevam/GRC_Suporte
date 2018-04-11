package com.idsscheer.webapps.arcm.ui.framework.controls.list.helper;

public class CSTListTableButtonColumn extends AbstractListTableHelper {

	protected CSTListTableButtonColumn(String p_instanceName) {
		super(p_instanceName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void render() {
		
		System.out.println(this.context.getCurrentID());
		System.out.println(this.context.getVersionColumn());
		
		
	}

}
