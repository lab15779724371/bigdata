package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdMsgNoticeDo {
	String id;                   //消息通知ID
	String createTime;           //创建时间
	String receiver;             //接收人，多个时用";"隔开
	String title;                //消息通知标题
	Integer announceType;        //通知类型，0: 通知公示，1: 健康宣教
	byte[] annCont;              //通知内容
	String amInfo;               //附件信息
	String userName;             //用户名
	Integer amFlag;              //是否有附件标识，0:无，1:有
	String lastUpdateTime;       //最后更新时间
	Integer stat;                //0:无效，1:有效
	
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
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getAnnounceType() {
		return announceType;
	}
	public void setAnnounceType(Integer announceType) {
		this.announceType = announceType;
	}
	public byte[] getAnnCont() {
		return annCont;
	}
	public void setAnnCont(byte[] annCont) {
		this.annCont = annCont;
	}
	public String getAmInfo() {
		return amInfo;
	}
	public void setAmInfo(String amInfo) {
		this.amInfo = amInfo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getAmFlag() {
		return amFlag;
	}
	public void setAmFlag(Integer amFlag) {
		this.amFlag = amFlag;
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
