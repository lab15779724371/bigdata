package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

public class OeExamTopics {
	String subject;
	List<OeExamQuestion> questions;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<OeExamQuestion> getQuestions() {
		return questions;
	}
	public void setQuestions(List<OeExamQuestion> questions) {
		this.questions = questions;
	}
}
