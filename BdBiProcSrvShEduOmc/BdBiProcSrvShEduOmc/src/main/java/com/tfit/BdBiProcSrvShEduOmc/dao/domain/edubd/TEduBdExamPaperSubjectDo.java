package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdExamPaperSubjectDo {
	String id;                         //主题记录ID
	String createTime;                 //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String subjectDescr;               //主题描述
	String epId;                       //试卷ID
	Integer questionType;              //试题类型，0:判断题，1:单选题，2:多选题，3:填空题，4:问答题
	String lastUpdateTime;             //最后更新时间
	Integer stat;                      //有效标识，0:无效，1:有效
	
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
	public String getSubjectDescr() {
		return subjectDescr;
	}
	public void setSubjectDescr(String subjectDescr) {
		this.subjectDescr = subjectDescr;
	}
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public Integer getQuestionType() {
		return questionType;
	}
	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
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