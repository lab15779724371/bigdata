package com.tfit.BdBiProcSrvShEduOmc.dto.search;


public class SearchLicense {
	//证书编号
	String licId;
	//证书名称
	String licName;
	//关联编号(指代团餐公司的uuid)
	String relationId;
	//食品经营许可证
	String licNo;
	//证书类型0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 
	//6：税务登记证 7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 14:运输许可证 
	//15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证 
	Integer licType;
	//证书有效期开始日期
	String licStartDate;
	//证书有效期截止日
	String licEndDate;
	
	//人员证照特有属性
	//证书上面人的名字
	String writtenName;
	//状态：是否有效:0-无效,1-有效
	String stat;
	//团餐公司编号
	String supplierId;
	//团餐公司名称
	String supplierName;
	
	//关联编号和证书编号组合
	String relationIdAndlicType;
	
	//学校和团餐公司关联关系编号
    String schoolSupplierId;
	

	public String getLicId() {
		return licId;
	}

	public void setLicId(String licId) {
		this.licId = licId;
	}

	public String getLicName() {
		return licName;
	}

	public void setLicName(String licName) {
		this.licName = licName;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
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

	public String getRelationIdAndlicType() {
		return this.relationId+"_"+this.licType;
	}

	public void setRelationIdAndlicType(String relationIdAndlicType) {
		this.relationIdAndlicType = relationIdAndlicType;
	}

	public String getLicStartDate() {
		return licStartDate;
	}

	public void setLicStartDate(String licStartDate) {
		this.licStartDate = licStartDate;
	}

	public String getLicEndDate() {
		return licEndDate;
	}

	public void setLicEndDate(String licEndDate) {
		this.licEndDate = licEndDate;
	}

	public String getWrittenName() {
		return writtenName;
	}

	public void setWrittenName(String writtenName) {
		this.writtenName = writtenName;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
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

	public String getSchoolSupplierId() {
		return schoolSupplierId;
	}

	public void setSchoolSupplierId(String schoolSupplierId) {
		this.schoolSupplierId = schoolSupplierId;
	}

	
	
}
