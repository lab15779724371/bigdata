package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidLibEditBody {
	String vidId;
	String vidName;
	String subTitle;
	Integer vidCategory;
	String thumbUrl;
	String vidUrl;
	String vidDescrCont;
	
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
	public String getVidDescrCont() {
		return vidDescrCont;
	}
	public void setVidDescrCont(String vidDescrCont) {
		this.vidDescrCont = vidDescrCont;
	}
}
