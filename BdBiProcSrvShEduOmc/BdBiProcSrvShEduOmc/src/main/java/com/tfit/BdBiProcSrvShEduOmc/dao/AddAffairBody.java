package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.RbSchoolAttachment;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;

public class AddAffairBody {
	String id;
	String time;
	String type;
	String title;
	String content;
	List<RbSchoolAttachment> schoolInfos;
	List<RbUlAttachment> amInfos;
	String releaseUnit;
	String releaser;
	String releaserName;
	String status;
	String completionFeedback;
	String completionDate;
	String operationUnit;
	String operator;
	String operatorName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<RbSchoolAttachment> getSchoolInfos() {
		return schoolInfos;
	}
	public void setSchoolInfos(List<RbSchoolAttachment> schoolInfos) {
		this.schoolInfos = schoolInfos;
	}
	public List<RbUlAttachment> getAmInfos() {
		return amInfos;
	}
	public void setAmInfos(List<RbUlAttachment> amInfos) {
		this.amInfos = amInfos;
	}
	public String getReleaseUnit() {
		return releaseUnit;
	}
	public void setReleaseUnit(String releaseUnit) {
		this.releaseUnit = releaseUnit;
	}
	public String getReleaser() {
		return releaser;
	}
	public void setReleaser(String releaser) {
		this.releaser = releaser;
	}
	public String getReleaserName() {
		return releaserName;
	}
	public void setReleaserName(String releaserName) {
		this.releaserName = releaserName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCompletionFeedback() {
		return completionFeedback;
	}
	public void setCompletionFeedback(String completionFeedback) {
		this.completionFeedback = completionFeedback;
	}
	public String getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}
	public String getOperationUnit() {
		return operationUnit;
	}
	public void setOperationUnit(String operationUnit) {
		this.operationUnit = operationUnit;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
}
