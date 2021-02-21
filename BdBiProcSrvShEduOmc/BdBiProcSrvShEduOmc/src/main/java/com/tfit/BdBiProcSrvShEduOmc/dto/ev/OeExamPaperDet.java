package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class OeExamPaperDet {
	String epId;
	String epName;
	String epTitel;
	String remark;
	OeExamTopics trueFalseTopics;
	OeExamTopics singleChoiceTopic;
	OeExamTopics multiChoiceTopic;
	
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public String getEpName() {
		return epName;
	}
	public void setEpName(String epName) {
		this.epName = epName;
	}
	public String getEpTitel() {
		return epTitel;
	}
	public void setEpTitel(String epTitel) {
		this.epTitel = epTitel;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public OeExamTopics getTrueFalseTopics() {
		return trueFalseTopics;
	}
	public void setTrueFalseTopics(OeExamTopics trueFalseTopics) {
		this.trueFalseTopics = trueFalseTopics;
	}
	public OeExamTopics getSingleChoiceTopic() {
		return singleChoiceTopic;
	}
	public void setSingleChoiceTopic(OeExamTopics singleChoiceTopic) {
		this.singleChoiceTopic = singleChoiceTopic;
	}
	public OeExamTopics getMultiChoiceTopic() {
		return multiChoiceTopic;
	}
	public void setMultiChoiceTopic(OeExamTopics multiChoiceTopic) {
		this.multiChoiceTopic = multiChoiceTopic;
	}
}
