package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WeeklyOptRepsDTO {
	String time;
	PageInfo pageInfo;
	List<WeeklyOptReps> weeklyOptReps;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<WeeklyOptReps> getWeeklyOptReps() {
		return weeklyOptReps;
	}
	public void setWeeklyOptReps(List<WeeklyOptReps> weeklyOptReps) {
		this.weeklyOptReps = weeklyOptReps;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
