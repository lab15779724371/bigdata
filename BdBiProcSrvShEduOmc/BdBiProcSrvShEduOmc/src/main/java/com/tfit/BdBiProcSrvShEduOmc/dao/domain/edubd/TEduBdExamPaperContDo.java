package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdExamPaperContDo {
	String id;                        //试卷内容记录ID
	String createTime;                //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String epId;                      //试卷ID
	String questionId;                //试卷问题ID，表t_edu_bd_question_body中的字段id	
	Integer score;                    //试卷试题分值	
	Integer questionType;             //试题类型，0:判断题，1:单选题，2:多选题，3:填空题，4:问答题
	String lastUpdateTime;            //最后更新时间
	Integer stat;                     //有效标识，0:无效，1:有效
	
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
	public String getEpId() {
		return epId;
	}
	public void setEpId(String epId) {
		this.epId = epId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
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
