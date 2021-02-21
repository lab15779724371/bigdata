package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class GoodsOptRateDTO {
	String time;
	//配货单指派率
	float curAssignRate;
	//配货单派送率
	float curDispRate;
	//配货单验收率
	float curAcceptRate;
	//学校指派率
	float curSchAssignRate;
	//学校派送率
	float curSchDispRate;
	//学校验收率
	float curSchAcceptRate;
	List<GoodsOptRate> goodsOptRate;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getCurAssignRate() {
		return curAssignRate;
	}
	public void setCurAssignRate(float curAssignRate) {
		this.curAssignRate = curAssignRate;
	}
	public float getCurDispRate() {
		return curDispRate;
	}
	public void setCurDispRate(float curDispRate) {
		this.curDispRate = curDispRate;
	}
	public float getCurAcceptRate() {
		return curAcceptRate;
	}
	public void setCurAcceptRate(float curAcceptRate) {
		this.curAcceptRate = curAcceptRate;
	}
	public List<GoodsOptRate> getGoodsOptRate() {
		return goodsOptRate;
	}
	public void setGoodsOptRate(List<GoodsOptRate> goodsOptRate) {
		this.goodsOptRate = goodsOptRate;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public float getCurSchAssignRate() {
		return curSchAssignRate;
	}
	public void setCurSchAssignRate(float curSchAssignRate) {
		this.curSchAssignRate = curSchAssignRate;
	}
	public float getCurSchDispRate() {
		return curSchDispRate;
	}
	public void setCurSchDispRate(float curSchDispRate) {
		this.curSchDispRate = curSchDispRate;
	}
	public float getCurSchAcceptRate() {
		return curSchAcceptRate;
	}
	public void setCurSchAcceptRate(float curSchAcceptRate) {
		this.curSchAcceptRate = curSchAcceptRate;
	}
	
}
