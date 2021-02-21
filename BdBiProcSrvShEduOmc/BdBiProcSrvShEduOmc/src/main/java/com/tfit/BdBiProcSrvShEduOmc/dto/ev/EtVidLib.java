package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidLib {
	String vidId;
	String vidName;
	String subTitle;
	int vidCategory;
	int playCount;
	String uploadTime;
	int likeCount;
	String vidThumbUrl;
	String vidUrl;
	
	public String getVidId() {
		return vidId;
	}
	public void setVidId(String vidId) {
		this.vidId = vidId;
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
	public int getVidCategory() {
		return vidCategory;
	}
	public void setVidCategory(int vidCategory) {
		this.vidCategory = vidCategory;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getVidThumbUrl() {
		return vidThumbUrl;
	}
	public void setVidThumbUrl(String vidThumbUrl) {
		this.vidThumbUrl = vidThumbUrl;
	}
	public String getVidUrl() {
		return vidUrl;
	}
	public void setVidUrl(String vidUrl) {
		this.vidUrl = vidUrl;
	}
}
