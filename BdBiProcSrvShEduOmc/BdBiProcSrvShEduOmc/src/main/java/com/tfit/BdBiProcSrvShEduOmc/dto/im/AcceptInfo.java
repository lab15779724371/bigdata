package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class AcceptInfo {
	int totalGsPlanNum;
	int noAcceptGsPlanNum;
	int acceptGsPlanNum;
	float acceptRate;
	
	public int getTotalGsPlanNum() {
		return totalGsPlanNum;
	}
	public void setTotalGsPlanNum(int totalGsPlanNum) {
		this.totalGsPlanNum = totalGsPlanNum;
	}
	public int getNoAcceptGsPlanNum() {
		return noAcceptGsPlanNum;
	}
	public void setNoAcceptGsPlanNum(int noAcceptGsPlanNum) {
		this.noAcceptGsPlanNum = noAcceptGsPlanNum;
	}
	public int getAcceptGsPlanNum() {
		return acceptGsPlanNum;
	}
	public void setAcceptGsPlanNum(int acceptGsPlanNum) {
		this.acceptGsPlanNum = acceptGsPlanNum;
	}
	public float getAcceptRate() {
		return acceptRate;
	}
	public void setAcceptRate(float acceptRate) {
		this.acceptRate = acceptRate;
	}
}
