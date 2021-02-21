package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdEtvidLibDo {
	String id;                     //视频ID
	String createTime;             //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String vidName;                //视频名称
	String subTitle;               //副标题
	Integer vidCategory;           //视频分类，0:系统操作，1:食品安全，2:政策法规	
	String thumbUrl;               //缩略图图片URL
	String vidUrl;                 //视频URL
	byte[] vidDescrCont;           //视频描述内容
	String userName;               //用户名
	Integer auditStatus;           //审核状态，0:未审核，1:已审核，2:已驳回
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
	public String getVidName() {
		return vidName;
	}
	public void setVidName(String vidName) {
		this.vidName = vidName;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public Integer getVidCategory() {
		return vidCategory;
	}
	public void setVidCategory(Integer vidCategory) {
		this.vidCategory = vidCategory;
	}
	public String getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public String getVidUrl() {
		return vidUrl;
	}
	public void setVidUrl(String vidUrl) {
		this.vidUrl = vidUrl;
	}
	public byte[] getVidDescrCont() {
		return vidDescrCont;
	}
	public void setVidDescrCont(byte[] vidDescrCont) {
		this.vidDescrCont = vidDescrCont;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
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
