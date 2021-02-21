package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdNoticeStatusDo {
	Integer id;                     //记录ID
	String createTime;              //创建时间
	String bulletinId;              //通知ID，即t_edu_bd_msg_notice表中的id
	String ownerUserName;           //所属用户名
	Integer readCount;              //阅读次数，0表示没有阅读
	Integer signFlag;               //签到标识，0:未签到，1:已签到
	String rcvUserName;             //接收用户名	
	String lastUpdateTime;          //最后更新时间
	Integer stat;                   //0:无效，1:有效
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getBulletinId() {
		return bulletinId;
	}
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}
	public String getOwnerUserName() {
		return ownerUserName;
	}
	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}
	public Integer getReadCount() {
		return readCount;
	}
	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
	public Integer getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(Integer signFlag) {
		this.signFlag = signFlag;
	}
	public String getRcvUserName() {
		return rcvUserName;
	}
	public void setRcvUserName(String rcvUserName) {
		this.rcvUserName = rcvUserName;
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
