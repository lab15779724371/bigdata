package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TProWarningDo {
	String id;	                        //varchar，主键id
	String title;	                    //varchar，标题
	String levelId;	                    //varchar，严重等级ID
	Integer warningStat;	            //int，预警状态 1待处理、2驳回、3处理中、4已处理
	Integer matchStat;	                //int，数据匹配:1 未匹配 2 匹配成功 3 匹配失败
	String reportObj;	                //varchar，上报对象
	Integer reportStat;	                //int，上报状态 0未上报 1已上报
	String dismissReason;	            //varchar，驳回理由
	Timestamp loseStartTime;	        //datetime，证照开始时间
	Timestamp warnTime;	                //datetime，预警时间
	Timestamp loseTime;	                //datetime，证照失效时间
	Integer remainTime;	                //int，失效剩余时间(天)
	String licenseId;	                //varchar，证件Id
	String licNo;	                    //varchar，证件号码
	String licNoNew;	                //varchar，更新后证件号码
	String license;	                    //varchar，证件名称
	Integer licType;	                //int，0:餐饮服务许可证、1:食品经营许可证、2:食品流通许可证、3:食品生产许可证、4:营业执照 5：组织机构代码6：税务登记证 7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证
	String licPic;	                    //varchar，证书图片
	String licPicNew;	                //varchar，更新后证书图片
	String writtenName;	                //varchar，证书上人的名字
	String districtId;	                //varchar，区县ID
	String districtName;	            //varchar，区县
	String sourceId;	                //varchar，学校ID
	String projId;	                    //varchar，项目点ID
	String projName;	                //varchar，项目点名称
	Integer projType;	                //int，项目点类型 1：学校项目点 2：社会项目点
	String supplierId;	                //varchar，团餐公司id
	String supplierName;	            //varchar，团餐公司名
	Integer readStat;	                //int，0:未读;1:已读
	String createAdminId;	            //varchar，创建者id
	String createAdminName;	            //varchar，创建者姓名
	Timestamp createTime;	            //datetime，创建时间
	String updater;	                    //varchar，更新人
	Timestamp lastUpdateTime;	        //datetime，最后更新时间
	Integer stat;	                    //int，状态
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLevelId() {
		return levelId;
	}
	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}
	public Integer getWarningStat() {
		return warningStat;
	}
	public void setWarningStat(Integer warningStat) {
		this.warningStat = warningStat;
	}
	public Integer getMatchStat() {
		return matchStat;
	}
	public void setMatchStat(Integer matchStat) {
		this.matchStat = matchStat;
	}
	public String getReportObj() {
		return reportObj;
	}
	public void setReportObj(String reportObj) {
		this.reportObj = reportObj;
	}
	public Integer getReportStat() {
		return reportStat;
	}
	public void setReportStat(Integer reportStat) {
		this.reportStat = reportStat;
	}
	public String getDismissReason() {
		return dismissReason;
	}
	public void setDismissReason(String dismissReason) {
		this.dismissReason = dismissReason;
	}
	public Timestamp getLoseStartTime() {
		return loseStartTime;
	}
	public void setLoseStartTime(Timestamp loseStartTime) {
		this.loseStartTime = loseStartTime;
	}
	public Timestamp getWarnTime() {
		return warnTime;
	}
	public void setWarnTime(Timestamp warnTime) {
		this.warnTime = warnTime;
	}
	public Timestamp getLoseTime() {
		return loseTime;
	}
	public void setLoseTime(Timestamp loseTime) {
		this.loseTime = loseTime;
	}
	public Integer getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(Integer remainTime) {
		this.remainTime = remainTime;
	}
	public String getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}
	public String getLicNo() {
		return licNo;
	}
	public void setLicNo(String licNo) {
		this.licNo = licNo;
	}
	public String getLicNoNew() {
		return licNoNew;
	}
	public void setLicNoNew(String licNoNew) {
		this.licNoNew = licNoNew;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public Integer getLicType() {
		return licType;
	}
	public void setLicType(Integer licType) {
		this.licType = licType;
	}
	public String getLicPic() {
		return licPic;
	}
	public void setLicPic(String licPic) {
		this.licPic = licPic;
	}
	public String getLicPicNew() {
		return licPicNew;
	}
	public void setLicPicNew(String licPicNew) {
		this.licPicNew = licPicNew;
	}
	public String getWrittenName() {
		return writtenName;
	}
	public void setWrittenName(String writtenName) {
		this.writtenName = writtenName;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getProjId() {
		return projId;
	}
	public void setProjId(String projId) {
		this.projId = projId;
	}
	public String getProjName() {
		return projName;
	}
	public void setProjName(String projName) {
		this.projName = projName;
	}
	public Integer getProjType() {
		return projType;
	}
	public void setProjType(Integer projType) {
		this.projType = projType;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public Integer getReadStat() {
		return readStat;
	}
	public void setReadStat(Integer readStat) {
		this.readStat = readStat;
	}
	public String getCreateAdminId() {
		return createAdminId;
	}
	public void setCreateAdminId(String createAdminId) {
		this.createAdminId = createAdminId;
	}
	public String getCreateAdminName() {
		return createAdminName;
	}
	public void setCreateAdminName(String createAdminName) {
		this.createAdminName = createAdminName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
