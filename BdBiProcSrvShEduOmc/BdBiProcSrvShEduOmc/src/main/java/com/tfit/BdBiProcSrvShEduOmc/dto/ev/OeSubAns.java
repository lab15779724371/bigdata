package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class OeSubAns {
	String epId;
	float score;
	int examStatus;
	String epName;
	String epTitel;
	String remark;	
	OeSubExamTopics trueFalseTopics;
	OeSubExamTopics singleChoiceTopic;
	OeSubExamTopics multiChoiceTopic;
	
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public int getExamStatus() {
		return examStatus;
	}
	public void setExamStatus(int examStatus) {
		this.examStatus = examStatus;
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
	public OeSubExamTopics getTrueFalseTopics() {
		return trueFalseTopics;
	}
	public void setTrueFalseTopics(OeSubExamTopics trueFalseTopics) {
		this.trueFalseTopics = trueFalseTopics;
	}
	public OeSubExamTopics getSingleChoiceTopic() {
		return singleChoiceTopic;
	}
	public void setSingleChoiceTopic(OeSubExamTopics singleChoiceTopic) {
		this.singleChoiceTopic = singleChoiceTopic;
	}
	public OeSubExamTopics getMultiChoiceTopic() {
		return multiChoiceTopic;
	}
	public void setMultiChoiceTopic(OeSubExamTopics multiChoiceTopic) {
		this.multiChoiceTopic = multiChoiceTopic;
	}
}
