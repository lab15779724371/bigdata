package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrList {
	String subDate;
	int crNum;
	int noProcCrNum;
	int assignCrNum;
	int procCrNum;
	float procCrRate;
	
	public String getSubDate() {
		return subDate;
	}
	public void setSubDate(String subDate) {
		this.subDate = subDate;
	}
	public int getCrNum() {
		return crNum;
	}
	public void setCrNum(int crNum) {
		this.crNum = crNum;
	}
	public int getNoProcCrNum() {
		return noProcCrNum;
	}
	public void setNoProcCrNum(int noProcCrNum) {
		this.noProcCrNum = noProcCrNum;
	}
	public int getAssignCrNum() {
		return assignCrNum;
	}
	public void setAssignCrNum(int assignCrNum) {
		this.assignCrNum = assignCrNum;
	}
	public int getProcCrNum() {
		return procCrNum;
	}
	public void setProcCrNum(int procCrNum) {
		this.procCrNum = procCrNum;
	}
	public float getProcCrRate() {
		return procCrRate;
	}
	public void setProcCrRate(float procCrRate) {
		this.procCrRate = procCrRate;
	}
}
