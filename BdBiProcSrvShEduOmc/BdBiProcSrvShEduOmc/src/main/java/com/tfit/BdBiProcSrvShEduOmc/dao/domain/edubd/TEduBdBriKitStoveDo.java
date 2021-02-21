package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdBriKitStoveDo {
	Long id;                              //记录ID
	String schoolId;                      //学校ID，即学校唯一标识
	String schoolName;                    //学校名称
	String regionId;                      //区域ID
	String cameraId;                      //摄像头ID，摄像头唯一标识
	String vidSrcName;                    //视频源名称，如上海市天山中学（食堂）-厨房 02	
	String camRtspInnerNetIp;             //摄像头rtsp视频内网IP，带端口格式为xx.xx.xx.xx:xxxx
	String camRtspOuterNetIp;             //摄像头rtsp视频外网IP，带端口格式为xx.xx.xx.xx:xxxx	
	String vidInnerNetRtspUrl;            //RTSP视频内网URL
	String vidOuterNetRtspUrl;            //RTSP视频外网URL	
	String camRtmpInnerNetIp;             //摄像头rtmp视频内网IP，带端口格式为xx.xx.xx.xx:xxxx
	String camRtmpOuterNetIp;             //摄像头rtmp视频外网IP，带端口格式为xx.xx.xx.xx:xxxx	
	String vidInnerNetRtmpUrl;            //RTMP视频内网URL
	String vidOuterNetRtmpUrl;            //RTMP视频外网URL
	Integer stat;                         //有效标识，0:无效，1:有效
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	public String getVidSrcName() {
		return vidSrcName;
	}
	public void setVidSrcName(String vidSrcName) {
		this.vidSrcName = vidSrcName;
	}
	public String getCamRtspInnerNetIp() {
		return camRtspInnerNetIp;
	}
	public void setCamRtspInnerNetIp(String camRtspInnerNetIp) {
		this.camRtspInnerNetIp = camRtspInnerNetIp;
	}
	public String getCamRtspOuterNetIp() {
		return camRtspOuterNetIp;
	}
	public void setCamRtspOuterNetIp(String camRtspOuterNetIp) {
		this.camRtspOuterNetIp = camRtspOuterNetIp;
	}
	public String getVidInnerNetRtspUrl() {
		return vidInnerNetRtspUrl;
	}
	public void setVidInnerNetRtspUrl(String vidInnerNetRtspUrl) {
		this.vidInnerNetRtspUrl = vidInnerNetRtspUrl;
	}
	public String getVidOuterNetRtspUrl() {
		return vidOuterNetRtspUrl;
	}
	public void setVidOuterNetRtspUrl(String vidOuterNetRtspUrl) {
		this.vidOuterNetRtspUrl = vidOuterNetRtspUrl;
	}
	public String getCamRtmpInnerNetIp() {
		return camRtmpInnerNetIp;
	}
	public void setCamRtmpInnerNetIp(String camRtmpInnerNetIp) {
		this.camRtmpInnerNetIp = camRtmpInnerNetIp;
	}
	public String getCamRtmpOuterNetIp() {
		return camRtmpOuterNetIp;
	}
	public void setCamRtmpOuterNetIp(String camRtmpOuterNetIp) {
		this.camRtmpOuterNetIp = camRtmpOuterNetIp;
	}
	public String getVidInnerNetRtmpUrl() {
		return vidInnerNetRtmpUrl;
	}
	public void setVidInnerNetRtmpUrl(String vidInnerNetRtmpUrl) {
		this.vidInnerNetRtmpUrl = vidInnerNetRtmpUrl;
	}
	public String getVidOuterNetRtmpUrl() {
		return vidOuterNetRtmpUrl;
	}
	public void setVidOuterNetRtmpUrl(String vidOuterNetRtmpUrl) {
		this.vidOuterNetRtmpUrl = vidOuterNetRtmpUrl;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
