package com.tfit.BdBiProcSrvShEduOmc.dto.home;

public class GoodsOptRate {
	String timeCoord;
	//配货单指派率
    float assignRate;
    //配货单派送率
    float dispRate;
    //配货单验收率
    float acceptRate;
	//学校指派率
    float schAssignRate;
    //学校派送率
    float schDispRate;
    //学校验收率
    float schAcceptRate;
    
	public String getTimeCoord() {
		return timeCoord;
	}
	public void setTimeCoord(String timeCoord) {
		this.timeCoord = timeCoord;
	}
	public float getAssignRate() {
		return assignRate;
	}
	public void setAssignRate(float assignRate) {
		this.assignRate = assignRate;
	}
	public float getDispRate() {
		return dispRate;
	}
	public void setDispRate(float dispRate) {
		this.dispRate = dispRate;
	}
	public float getAcceptRate() {
		return acceptRate;
	}
	public void setAcceptRate(float acceptRate) {
		this.acceptRate = acceptRate;
	}
	public float getSchAssignRate() {
		return schAssignRate;
	}
	public void setSchAssignRate(float schAssignRate) {
		this.schAssignRate = schAssignRate;
	}
	public float getSchDispRate() {
		return schDispRate;
	}
	public void setSchDispRate(float schDispRate) {
		this.schDispRate = schDispRate;
	}
	public float getSchAcceptRate() {
		return schAcceptRate;
	}
	public void setSchAcceptRate(float schAcceptRate) {
		this.schAcceptRate = schAcceptRate;
	}
	
}
