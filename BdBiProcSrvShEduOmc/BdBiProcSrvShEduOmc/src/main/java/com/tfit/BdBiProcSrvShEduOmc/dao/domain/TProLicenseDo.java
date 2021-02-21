package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Date;
import java.sql.Timestamp;

public class TProLicenseDo {
	String id;                                 //主键ID
	String licName;                            //证书名称
	String writtenName;                        //证书上面人的名字
	String licNo;                              //证书号码
	Integer licType;                           //0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证 7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证   				0	0
	Date licStartDate;                         //证书有效期开始日期
	Date licEndDate;                           //证书有效期截止日
	String licPic;                             //证书图片
	String supplierId;                         //关联的供应商Id
	Integer cerSource;                         //证书来源，0：供应商-，1：从业人员-雇员，2：商品，3：食堂--教委web学校的法人证
	String relationId;                         //关联ID(团餐公司ID或员工ID)
	Integer reviewed;                          //是否通过审核 0未审核 1 审核通过 2 拒绝
	Integer dataMatching;                      //数据匹配 0 失败 1 成功
	Integer useing;                            //0 停用 1 启用
	String occupationRange;                    //从业范围
	String healthexamineOrganization;          //健康检查单位
	String jobOrganization;                    //工作单位
	String inspectInstitution;                 //检查机构
	Integer certificateType;                   //证件类型：1-身份证；2-军官证；3-护照
	String certificateNo;                      //证件号码
	String operation;                          //发证机关
	Date giveLicDate;                          //发证日期
	String creator;                            //
	Timestamp createTime;                      //创建时间
	String updater;                            //
	Timestamp lastUpdateTime;                  //最后更新时间
	Integer stat;                              //是否有效:0-无效,1-有效
	Integer warn_flag;                         //1默认 2即将过期 3已过期
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLicName() {
		return licName;
	}
	public void setLicName(String licName) {
		this.licName = licName;
	}
	public String getWrittenName() {
		return writtenName;
	}
	public void setWrittenName(String writtenName) {
		this.writtenName = writtenName;
	}
	public String getLicNo() {
		return licNo;
	}
	public void setLicNo(String licNo) {
		this.licNo = licNo;
	}
	public Integer getLicType() {
		return licType;
	}
	public void setLicType(Integer licType) {
		this.licType = licType;
	}
	public Date getLicStartDate() {
		return licStartDate;
	}
	public void setLicStartDate(Date licStartDate) {
		this.licStartDate = licStartDate;
	}
	public Date getLicEndDate() {
		return licEndDate;
	}
	public void setLicEndDate(Date licEndDate) {
		this.licEndDate = licEndDate;
	}
	public String getLicPic() {
		return licPic;
	}
	public void setLicPic(String licPic) {
		this.licPic = licPic;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public Integer getCerSource() {
		return cerSource;
	}
	public void setCerSource(Integer cerSource) {
		this.cerSource = cerSource;
	}
	public String getRelationId() {
		return relationId;
	}
	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
	public Integer getReviewed() {
		return reviewed;
	}
	public void setReviewed(Integer reviewed) {
		this.reviewed = reviewed;
	}
	public Integer getDataMatching() {
		return dataMatching;
	}
	public void setDataMatching(Integer dataMatching) {
		this.dataMatching = dataMatching;
	}
	public Integer getUseing() {
		return useing;
	}
	public void setUseing(Integer useing) {
		this.useing = useing;
	}
	public String getOccupationRange() {
		return occupationRange;
	}
	public void setOccupationRange(String occupationRange) {
		this.occupationRange = occupationRange;
	}
	public String getHealthexamineOrganization() {
		return healthexamineOrganization;
	}
	public void setHealthexamineOrganization(String healthexamineOrganization) {
		this.healthexamineOrganization = healthexamineOrganization;
	}
	public String getJobOrganization() {
		return jobOrganization;
	}
	public void setJobOrganization(String jobOrganization) {
		this.jobOrganization = jobOrganization;
	}
	public String getInspectInstitution() {
		return inspectInstitution;
	}
	public void setInspectInstitution(String inspectInstitution) {
		this.inspectInstitution = inspectInstitution;
	}
	public Integer getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(Integer certificateType) {
		this.certificateType = certificateType;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Date getGiveLicDate() {
		return giveLicDate;
	}
	public void setGiveLicDate(Date giveLicDate) {
		this.giveLicDate = giveLicDate;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
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
	public Integer getWarn_flag() {
		return warn_flag;
	}
	public void setWarn_flag(Integer warn_flag) {
		this.warn_flag = warn_flag;
	}
}
