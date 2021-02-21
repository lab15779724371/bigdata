package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class SchKwDets {
	//回收日期，格式：xxxx/xx/xx
	String recDate;
	//区域名称
	String distName;
	//项目点名称
	String ppName;
	//总分校标识
	String schGenBraFlag;
	//分校数量
	int braCampusNum;
	//关联总校
	String relGenSchName;
	//所属
	String subLevel;
	//主管部门
	String compDep;
	//所属区域名称
	String subDistName;
	//学校类型（学制）
	String schType;
	//学校性质
	String schProp;
	//团餐公司名称
	String rmcName;
	//回收数量，单位：桶(改为String类型原因：数据库中存在带单位的数量)
	String recNum;
	//回收单位
	String recComany;
	//回收人
	String recPerson;
	//回收单据数
	int recBillNum;
	
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
	public String getSchType() {
		return schType;
	}
	public void setSchType(String schType) {
		this.schType = schType;
	}
	public String getSchProp() {
		return schProp;
	}
	public void setSchProp(String schProp) {
		this.schProp = schProp;
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
	public int getRecBillNum() {
		return recBillNum;
	}
	public void setRecBillNum(int recBillNum) {
		this.recBillNum = recBillNum;
	}
}
