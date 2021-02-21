package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class GsPlanOptDets {
	String distrDate;
	String distrBatNumber;
	String ppName;	
	String schGenBraFlag;
	int braCampusNum;
	String relGenSchName;
	String subLevel;
	String compDep;
	String subDistName;
	String fblMb;
	String schProp;	
	String schType;
	String distName;
	String rmcName;
	String dispType;
	String dispMode;
	int assignStatus;
	int dispStatus;
	int acceptStatus;
	int sendFlag;
	
	//验收时间
	String acceptTime;
	
	//操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	String plaStatus;
	//管理部门编号
	String departmentId;
	
	
	
	public String getPlaStatus() {
		return plaStatus;
	}
	public void setPlaStatus(String plaStatus) {
		this.plaStatus = plaStatus;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDistrDate() {
		return distrDate;
	}
	public void setDistrDate(String distrDate) {
		this.distrDate = distrDate;
	}
	public String getDistrBatNumber() {
		return distrBatNumber;
	}
	public void setDistrBatNumber(String distrBatNumber) {
		this.distrBatNumber = distrBatNumber;
	}
	public String getPpName() {
		return ppName;
	}
	public void setPpName(String ppName) {
		this.ppName = ppName;
	}
	public String getSchGenBraFlag() {
		return schGenBraFlag;
	}
	public void setSchGenBraFlag(String schGenBraFlag) {
		this.schGenBraFlag = schGenBraFlag;
	}
	public int getBraCampusNum() {
		return braCampusNum;
	}
	public void setBraCampusNum(int braCampusNum) {
		this.braCampusNum = braCampusNum;
	}
	public String getRelGenSchName() {
		return relGenSchName;
	}
	public void setRelGenSchName(String relGenSchName) {
		this.relGenSchName = relGenSchName;
	}
	public String getSubLevel() {
		return subLevel;
	}
	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
	}
	public String getCompDep() {
		return compDep;
	}
	public void setCompDep(String compDep) {
		this.compDep = compDep;
	}
	public String getSubDistName() {
		return subDistName;
	}
	public void setSubDistName(String subDistName) {
		this.subDistName = subDistName;
	}
	public String getFblMb() {
		return fblMb;
	}
	public void setFblMb(String fblMb) {
		this.fblMb = fblMb;
	}
	public String getSchProp() {
		return schProp;
	}
	public void setSchProp(String schProp) {
		this.schProp = schProp;
	}
	public String getSchType() {
		return schType;
	}
	public void setSchType(String schType) {
		this.schType = schType;
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
	public String getDispType() {
		return dispType;
	}
	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	public String getDispMode() {
		return dispMode;
	}
	public void setDispMode(String dispMode) {
		this.dispMode = dispMode;
	}
	public int getAssignStatus() {
		return assignStatus;
	}
	public void setAssignStatus(int assignStatus) {
		this.assignStatus = assignStatus;
	}
	public int getDispStatus() {
		return dispStatus;
	}
	public void setDispStatus(int dispStatus) {
		this.dispStatus = dispStatus;
	}
	public int getAcceptStatus() {
		return acceptStatus;
	}
	public void setAcceptStatus(int acceptStatus) {
		this.acceptStatus = acceptStatus;
	}
	public int getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}
	
	
}
