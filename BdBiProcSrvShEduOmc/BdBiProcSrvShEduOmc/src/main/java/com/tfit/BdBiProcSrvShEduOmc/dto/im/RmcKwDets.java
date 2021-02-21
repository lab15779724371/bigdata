package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class RmcKwDets {
	String recDate;
	String distName;
	String rmcName;
	//(改为String类型原因：数据库中存在带单位的数量)
	String recNum;
	String recComany;
	String recPerson;
	Integer recBillNum;
	
	public String getRecDate() {
		return recDate;
	}
	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	public String getRmcName() {
		return rmcName;
	}
	public void setRmcName(String rmcName) {
		this.rmcName = rmcName;
	}
	public String getRecNum() {
		return recNum;
	}
	public void setRecNum(String recNum) {
		this.recNum = recNum;
	}
	public String getRecComany() {
		return recComany;
	}
	public void setRecComany(String recComany) {
		this.recComany = recComany;
	}
	public String getRecPerson() {
		return recPerson;
	}
	public void setRecPerson(String recPerson) {
		this.recPerson = recPerson;
	}
	public Integer getRecBillNum() {
		return recBillNum;
	}
	public void setRecBillNum(Integer recBillNum) {
		this.recBillNum = recBillNum;
	}
}
