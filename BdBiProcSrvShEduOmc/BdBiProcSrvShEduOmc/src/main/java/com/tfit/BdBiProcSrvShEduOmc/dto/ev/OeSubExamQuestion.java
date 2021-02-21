package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class OeSubExamQuestion {
	String bodyId;
	String body;
	String candAns;
	String candAnsId;
	String eeAns;
	int eeAnsJuge;
	String stdAns;
	
	public String getBodyId() {
		return bodyId;
	}
	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCandAns() {
		return candAns;
	}
	public void setCandAns(String candAns) {
		this.candAns = candAns;
	}
	public String getCandAnsId() {
		return candAnsId;
	}
	public void setCandAnsId(String candAnsId) {
		this.candAnsId = candAnsId;
	}
	public String getEeAns() {
		return eeAns;
	}
	public void setEeAns(String eeAns) {
		this.eeAns = eeAns;
	}
	public int getEeAnsJuge() {
		return eeAnsJuge;
	}
	public void setEeAnsJuge(int eeAnsJuge) {
		this.eeAnsJuge = eeAnsJuge;
	}
	public String getStdAns() {
		return stdAns;
	}
	public void setStdAns(String stdAns) {
		this.stdAns = stdAns;
	}
}
