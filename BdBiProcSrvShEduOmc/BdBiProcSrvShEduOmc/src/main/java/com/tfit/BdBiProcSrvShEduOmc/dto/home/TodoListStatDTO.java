package com.tfit.BdBiProcSrvShEduOmc.dto.home;

public class TodoListStatDTO {
	String time;
	TodoListStat todoListStat;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public TodoListStat getTodoListStat() {
		return todoListStat;
	}
	public void setTodoListStat(TodoListStat todoListStat) {
		this.todoListStat = todoListStat;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
