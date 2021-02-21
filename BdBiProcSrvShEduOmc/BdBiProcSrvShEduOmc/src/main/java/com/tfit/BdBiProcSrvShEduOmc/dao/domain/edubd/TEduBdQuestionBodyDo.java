package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

public class TEduBdQuestionBodyDo {
	String id;                               //试题ID
	String createTime;                       //创建时间，格式：xxxx-xx-xx xx:xx:xx
	String body;                             //试题题干
	Integer questionType;                    //试题类型，0:判断题，1:单选题，2:多选题，3:填空题，4:问答题
	String lastUpdateTime;                   //最后更新时间
	Integer stat;                            //有效标识，0:无效，1:有效	
	
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
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
