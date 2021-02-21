package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TProRecyclerWasteDo {
	String id;                                          //主键
	String sourceId;                                   //团餐公司Id或者学校Id
	String districtId;                                 //区县ID
	Integer type;                                       //1餐厨垃圾，2废弃油脂	
	Integer secontType;                                //针对大类 2(1废油，2 含油废水)  默认为0(无特别含义)
	String recyclerId;                                 //回收单位Id
	String level;                                       //0 幼儿园，1 小学，2 初中，3 高中，4 大学 ,5 企业，6中职校 7托儿所  9其他'
	String recyclerName;                               //回收单位
	String recyclerNumber;                             //回收数量 餐厨垃圾默认单位桶 废弃油脂 默认单位 公斤
	Integer recyclerDocuments;                         //回收单据数量	
	String documentsPictures;                          //回收单据图片 json 方式 存取
	Timestamp recyclerDate;                            //回收日期
	String contact;                                     //回收人
	String creator;                                     //创建者	
	String updater;                                     //更新人	
	Timestamp createTime;
	Timestamp lastUpdateTime;
	Integer stat;                                       //记录有效标识，0：无效，1：有效
	Integer platformType;                               //1为教委端 2为团餐端
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSecontType() {
		return secontType;
	}
	public void setSecontType(Integer secontType) {
		this.secontType = secontType;
	}
	public String getRecyclerId() {
		return recyclerId;
	}
	public void setRecyclerId(String recyclerId) {
		this.recyclerId = recyclerId;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRecyclerName() {
		return recyclerName;
	}
	public void setRecyclerName(String recyclerName) {
		this.recyclerName = recyclerName;
	}
	public String getRecyclerNumber() {
		return recyclerNumber;
	}
	public void setRecyclerNumber(String recyclerNumber) {
		this.recyclerNumber = recyclerNumber;
	}
	public Integer getRecyclerDocuments() {
		return recyclerDocuments;
	}
	public void setRecyclerDocuments(Integer recyclerDocuments) {
		this.recyclerDocuments = recyclerDocuments;
	}
	public String getDocumentsPictures() {
		return documentsPictures;
	}
	public void setDocumentsPictures(String documentsPictures) {
		this.documentsPictures = documentsPictures;
	}
	public Timestamp getRecyclerDate() {
		return recyclerDate;
	}
	public void setRecyclerDate(Timestamp recyclerDate) {
		this.recyclerDate = recyclerDate;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
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
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
}
