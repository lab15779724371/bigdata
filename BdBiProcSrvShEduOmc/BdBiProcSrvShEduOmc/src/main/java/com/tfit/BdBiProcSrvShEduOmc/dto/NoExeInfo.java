package com.tfit.BdBiProcSrvShEduOmc.dto;

public class NoExeInfo {
	int noExeNum;      //未执行数量
	int status;        //状态,0:无升降,1:升,2:降
	
	public int getNoExeNum() {
		return noExeNum;
	}
	public void setNoExeNum(int noExeNum) {
		this.noExeNum = noExeNum;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
