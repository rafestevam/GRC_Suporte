package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

import java.util.Date;

public class CustomIssueObj {
	
	private long objId = 0;
	private Date objDate = new Date();
	
	public void setObjId(long id){
		this.objId = id;
	}
	
	public Long getObjId(){
		return this.objId;
	}
	
	public void setObjDate(Date date){
		this.objDate = date;
	}
	
	public Date getObjDate(){
		return this.objDate;
	}

}
