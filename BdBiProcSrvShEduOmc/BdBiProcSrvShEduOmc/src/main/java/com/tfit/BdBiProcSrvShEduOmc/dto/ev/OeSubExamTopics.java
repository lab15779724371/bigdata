package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

public class OeSubExamTopics {
	String subject;
	List<OeSubExamQuestion> questions;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<OeSubExamQuestion> getQuestions() {
		return questions;
	}
	public void setQuestions(List<OeSubExamQuestion> questions) {
		this.questions = questions;
	}
}
