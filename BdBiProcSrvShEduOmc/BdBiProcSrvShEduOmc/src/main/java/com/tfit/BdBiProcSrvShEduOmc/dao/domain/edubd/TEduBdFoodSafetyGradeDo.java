package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdFoodSafetyGradeDo {
	Long id;                  //记录ID	
	String distName;          //区域名称
	String ppName;            //项目点名称（单位名称）
	String licNo;             //许可证号
	String ppAddress;         //项目点地址（单位地址）
	String inspDate;          //检测日期，格式：xxxx-xx-xx
	String levelName;         //等级名称，如良好、一般、较差
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getLicNo() {
		return licNo;
	}
	public void setLicNo(String licNo) {
		this.licNo = licNo;
	}
	public String getPpAddress() {
		return ppAddress;
	}
	public void setPpAddress(String ppAddress) {
		this.ppAddress = ppAddress;
	}
	public String getInspDate() {
		return inspDate;
	}
	public void setInspDate(String inspDate) {
		this.inspDate = inspDate;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
}
