package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class LicProcInfo {
	int totalWarnNum;
	int noProcWarnNum;
	int elimWarnNum;
	float warnProcRate;
	
	public int getTotalWarnNum() {
		return totalWarnNum;
	}
	public void setTotalWarnNum(int totalWarnNum) {
		this.totalWarnNum = totalWarnNum;
	}
	public int getNoProcWarnNum() {
		return noProcWarnNum;
	}
	public void setNoProcWarnNum(int noProcWarnNum) {
		this.noProcWarnNum = noProcWarnNum;
	}
	public int getElimWarnNum() {
		return elimWarnNum;
	}
	public void setElimWarnNum(int elimWarnNum) {
		this.elimWarnNum = elimWarnNum;
	}
	public float getWarnProcRate() {
		return warnProcRate;
	}
	public void setWarnProcRate(float warnProcRate) {
		this.warnProcRate = warnProcRate;
	}
}
