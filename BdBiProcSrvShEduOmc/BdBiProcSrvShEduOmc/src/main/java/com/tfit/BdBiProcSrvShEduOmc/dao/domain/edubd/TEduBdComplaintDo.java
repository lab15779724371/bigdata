package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdComplaintDo {
	String id;                     //投诉举报ID
	String createTime;             //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String subDate;                //提交日期
	String schoolId;               //学校ID
	String title;                  //投诉主题
	byte[] content;                //投诉举报内容
	String cptName;                //投诉人姓名
	String contactNo;              //联系电话
	String contractor;             //承办人
	Integer cpStatus;              //状态，0:待处理，1:已办结
	String feedBack;               //办结反馈
	String finishDate;             //办结日期，格式：xxxx-xx-xx xx:xx:xx
	String userName;               //用户名
	String lastUpdateTime;         //最后更新时间	
	Integer stat;                  //有效标识，0:无效，1:有效
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getSubDate() {
		return subDate;
	}
	public void setSubDate(String subDate) {
		this.subDate = subDate;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getCptName() {
		return cptName;
	}
	public void setCptName(String cptName) {
		this.cptName = cptName;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getContractor() {
		return contractor;
	}
	public void setContractor(String contractor) {
		this.contractor = contractor;
	}
	public Integer getCpStatus() {
		return cpStatus;
	}
	public void setCpStatus(Integer cpStatus) {
		this.cpStatus = cpStatus;
	}
	public String getFeedBack() {
		return feedBack;
	}
	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}
	public String getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
