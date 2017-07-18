package com.idsscheer.webapps.arcm.ui.components.issuemanagement.actioncommands;

public class CustomRiskObj {

	private long r_Id = 0;
	private String h_Name ;
	
	public void setr_Id(long id){
		this.r_Id = id;
	}
	
	public Long getr_Id(){
		return this.r_Id;
	}
	
	public void seth_Name(String name){
		this.h_Name = name;
	}
	
	public String geth_Name(){
		return this.h_Name;
	}
}
