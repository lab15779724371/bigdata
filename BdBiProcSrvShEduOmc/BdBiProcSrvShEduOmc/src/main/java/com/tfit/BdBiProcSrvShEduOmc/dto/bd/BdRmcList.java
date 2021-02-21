package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

public class BdRmcList {
	String compName;                     //企业名称
	String location;                     //所在地
	String detailAddr;                   //详细地址
	String email;                        //电子邮箱
	String contact;                      //联系人
	String mobilePhone;                  //手机号
	String qaLeader;                     //质量负责人
	String contactNo;                    //联系电话
	Integer relPpNum;                        //关联项目点数量
	String licName;                      //证照名称
	String licPic;                       //证照图片
	String licOptUnit;                   //经营单位
	String licNo;                        //许可证号
	String licIssueDate;                 //发证日期
	String licValidDate;                 //有效日期
	String uscc;                         //统一社会信用代码
	String usccPic;                      //证照图片
	String usccOptUnit;                  //经营单位
	String optScope;                     //经营范围
	String regAddr;                      //注册地址
	String udetailAddr;                  //详细地址
	String regCapital;                   //注册资本，单位：万元
	String corpRepName;                  //法人代表
	String ucssIssueAuth;                //发证机关
	String ucssIssueDate;                //发证日期
	String ucssValidDate;                //有效日期
	String fhlName;                      //食品卫生许可证
	String fhlPic;                       //证照图片
	String fhlOptUnit;                   //经营单位
	String fhlNo;                        //许可证号
	String fhlIssueDate;                 //发证日期
	String fhlValidDate;                 //有效日期
	String tplName;                      //运输许可证
	String tplPic;                       //证照图片
	String tplOptUnit;                   //经营单位
	String tplNo;                        //许可证号
	String tplIssueDate;                 //发证日期
	String tplValidDate;                 //有效日期
	String iosName;                      //IOS认证证书
	String iosPic;                       //证照图片
	String iosOptUnit;                   //经营单位
	String iosNo;                        //许可证号
	String iosIssueDate;                 //发证日期
	String iosValidDate;                 //有效日期
	
	public BdRmcList(){
		compName = "-";                     //企业名称
		location = "-";                     //所在地
		detailAddr = "-";                   //详细地址
		email = "-";                        //电子邮箱
		contact = "-";                      //联系人
		mobilePhone = "-";                  //手机号
		qaLeader = "-";                     //质量负责人
		contactNo = "-";                    //联系电话
		relPpNum = 0;                        //关联项目点数量
		licName = "-";                      //证照名称
		licPic = "-";                       //证照图片
		licOptUnit = "-";                   //经营单位
		licNo = "-";                        //许可证号
		licIssueDate = "-";                 //发证日期
		licValidDate = "-";                 //有效日期
		uscc = "-";                         //统一社会信用代码
		usccPic = "-";                      //证照图片
		usccOptUnit = "-";                  //经营单位
		optScope = "-";                     //经营范围
		regAddr = "-";                      //注册地址
		udetailAddr = "-";                  //详细地址
		regCapital = "-";                   //注册资本，单位：万元
		corpRepName = "-";                  //法人代表
		ucssIssueAuth = "-";                //发证机关
		ucssIssueDate = "-";                //发证日期
		ucssValidDate = "-";                //有效日期
		fhlName = "-";                      //食品卫生许可证
		fhlPic = "-";                       //证照图片
		fhlOptUnit = "-";                   //经营单位
		fhlNo = "-";                        //许可证号
		fhlIssueDate = "-";                 //发证日期
		fhlValidDate = "-";                 //有效日期
		tplName = "-";                      //运输许可证
		tplPic = "-";                       //证照图片
		tplOptUnit = "-";                   //经营单位
		tplNo = "-";                        //许可证号
		tplIssueDate = "-";                 //发证日期
		tplValidDate = "-";                 //有效日期
		iosName = "-";                      //IOS认证证书
		iosPic = "-";                       //证照图片
		iosOptUnit = "-";                   //经营单位
		iosNo = "-";                        //许可证号
		iosIssueDate = "-";                 //发证日期
		iosValidDate = "-";                 //有效日期
	}
	
	public String getCompName() {
		return compName;
	}
	public void setCompName(String compName) {
		this.compName = compName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDetailAddr() {
		return detailAddr;
	}
	public void setDetailAddr(String detailAddr) {
		this.detailAddr = detailAddr;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getQaLeader() {
		return qaLeader;
	}
	public void setQaLeader(String qaLeader) {
		this.qaLeader = qaLeader;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public Integer getRelPpNum() {
		return relPpNum;
	}
	public void setRelPpNum(Integer relPpNum) {
		this.relPpNum = relPpNum;
	}
	public String getLicName() {
		return licName;
	}
	public void setLicName(String licName) {
		this.licName = licName;
	}
	public String getLicPic() {
		return licPic;
	}
	public void setLicPic(String licPic) {
		this.licPic = licPic;
	}
	public String getLicOptUnit() {
		return licOptUnit;
	}
	public void setLicOptUnit(String licOptUnit) {
		this.licOptUnit = licOptUnit;
	}
	public String getLicNo() {
		return licNo;
	}
	public void setLicNo(String licNo) {
		this.licNo = licNo;
	}
	public String getLicIssueDate() {
		return licIssueDate;
	}
	public void setLicIssueDate(String licIssueDate) {
		this.licIssueDate = licIssueDate;
	}
	public String getLicValidDate() {
		return licValidDate;
	}
	public void setLicValidDate(String licValidDate) {
		this.licValidDate = licValidDate;
	}
	public String getUscc() {
		return uscc;
	}
	public void setUscc(String uscc) {
		this.uscc = uscc;
	}
	public String getUsccPic() {
		return usccPic;
	}
	public void setUsccPic(String usccPic) {
		this.usccPic = usccPic;
	}
	public String getUsccOptUnit() {
		return usccOptUnit;
	}
	public void setUsccOptUnit(String usccOptUnit) {
		this.usccOptUnit = usccOptUnit;
	}
	public String getOptScope() {
		return optScope;
	}
	public void setOptScope(String optScope) {
		this.optScope = optScope;
	}
	public String getRegAddr() {
		return regAddr;
	}
	public void setRegAddr(String regAddr) {
		this.regAddr = regAddr;
	}
	public String getUdetailAddr() {
		return udetailAddr;
	}
	public void setUdetailAddr(String udetailAddr) {
		this.udetailAddr = udetailAddr;
	}	
	public String getRegCapital() {
		return regCapital;
	}
	public void setRegCapital(String regCapital) {
		this.regCapital = regCapital;
	}
	public String getCorpRepName() {
		return corpRepName;
	}
	public void setCorpRepName(String corpRepName) {
		this.corpRepName = corpRepName;
	}
	public String getUcssIssueAuth() {
		return ucssIssueAuth;
	}
	public void setUcssIssueAuth(String ucssIssueAuth) {
		this.ucssIssueAuth = ucssIssueAuth;
	}
	public String getUcssIssueDate() {
		return ucssIssueDate;
	}
	public void setUcssIssueDate(String ucssIssueDate) {
		this.ucssIssueDate = ucssIssueDate;
	}
	public String getUcssValidDate() {
		return ucssValidDate;
	}
	public void setUcssValidDate(String ucssValidDate) {
		this.ucssValidDate = ucssValidDate;
	}
	public String getFhlName() {
		return fhlName;
	}
	public void setFhlName(String fhlName) {
		this.fhlName = fhlName;
	}
	public String getFhlPic() {
		return fhlPic;
	}
	public void setFhlPic(String fhlPic) {
		this.fhlPic = fhlPic;
	}
	public String getFhlOptUnit() {
		return fhlOptUnit;
	}
	public void setFhlOptUnit(String fhlOptUnit) {
		this.fhlOptUnit = fhlOptUnit;
	}
	public String getFhlNo() {
		return fhlNo;
	}
	public void setFhlNo(String fhlNo) {
		this.fhlNo = fhlNo;
	}
	public String getFhlIssueDate() {
		return fhlIssueDate;
	}
	public void setFhlIssueDate(String fhlIssueDate) {
		this.fhlIssueDate = fhlIssueDate;
	}
	public String getFhlValidDate() {
		return fhlValidDate;
	}
	public void setFhlValidDate(String fhlValidDate) {
		this.fhlValidDate = fhlValidDate;
	}
	public String getTplName() {
		return tplName;
	}
	public void setTplName(String tplName) {
		this.tplName = tplName;
	}
	public String getTplPic() {
		return tplPic;
	}
	public void setTplPic(String tplPic) {
		this.tplPic = tplPic;
	}
	public String getTplOptUnit() {
		return tplOptUnit;
	}
	public void setTplOptUnit(String tplOptUnit) {
		this.tplOptUnit = tplOptUnit;
	}
	public String getTplNo() {
		return tplNo;
	}
	public void setTplNo(String tplNo) {
		this.tplNo = tplNo;
	}
	public String getTplIssueDate() {
		return tplIssueDate;
	}
	public void setTplIssueDate(String tplIssueDate) {
		this.tplIssueDate = tplIssueDate;
	}
	public String getTplValidDate() {
		return tplValidDate;
	}
	public void setTplValidDate(String tplValidDate) {
		this.tplValidDate = tplValidDate;
	}
	public String getIosName() {
		return iosName;
	}
	public void setIosName(String iosName) {
		this.iosName = iosName;
	}
	public String getIosPic() {
		return iosPic;
	}
	public void setIosPic(String iosPic) {
		this.iosPic = iosPic;
	}
	public String getIosOptUnit() {
		return iosOptUnit;
	}
	public void setIosOptUnit(String iosOptUnit) {
		this.iosOptUnit = iosOptUnit;
	}
	public String getIosNo() {
		return iosNo;
	}
	public void setIosNo(String iosNo) {
		this.iosNo = iosNo;
	}
	public String getIosIssueDate() {
		return iosIssueDate;
	}
	public void setIosIssueDate(String iosIssueDate) {
		this.iosIssueDate = iosIssueDate;
	}
	public String getIosValidDate() {
		return iosValidDate;
	}
	public void setIosValidDate(String iosValidDate) {
		this.iosValidDate = iosValidDate;
	}
}
