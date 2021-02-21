package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdQuestionCandAnsDo {
	String id;                             //候选答案ID
	String candAnsDescr;                   //试题候选答案描述
	String questionId;                     //试题ID，表t_edu_bd_question_body中的字段id
	String createTime;                     //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String lastUpdateTime;                 //最后更新时间
	Integer stdAnsFlag;                    //是否为标准答案标识，0:否，1:是
	Integer stat;                          //有效标识，0:无效，1:有效	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCandAnsDescr() {
		return candAnsDescr;
	}
	public void setCandAnsDescr(String candAnsDescr) {
		this.candAnsDescr = candAnsDescr;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStdAnsFlag() {
		return stdAnsFlag;
	}
	public void setStdAnsFlag(Integer stdAnsFlag) {
		this.stdAnsFlag = stdAnsFlag;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
