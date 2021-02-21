package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import com.tfit.BdBiProcSrvShEduOmc.dto.NoExeInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserInfo;

public class TodoListStat {
	int complNoProcNum;              //投诉待处理数量
	int warnNoProcNum;               //预警未消除数量
	int sysNotiNum;                  //系统通知数量
	NoExeInfo warnNoProcInfo;        //预警未消除信息
	NoExeInfo noDishSchInfo;         //未排菜学校信息
	NoExeInfo noAcceptPlanInfo;      //未验收计划信息
	NoExeInfo dishNoRetInfo;         //未留样菜品信息	
	NoExeInfo noAcceptSchInfo;       //未验收学校数信息	
	NoExeInfo dishNoRetSchInfo;      //未留样学校数信息	
	UserInfo userInfo;               //用户信息
	
	public int getComplNoProcNum() {
		return complNoProcNum;
	}
	public void setComplNoProcNum(int complNoProcNum) {
		this.complNoProcNum = complNoProcNum;
	}
	public int getWarnNoProcNum() {
		return warnNoProcNum;
	}
	public void setWarnNoProcNum(int warnNoProcNum) {
		this.warnNoProcNum = warnNoProcNum;
	}
	public int getSysNotiNum() {
		return sysNotiNum;
	}
	public void setSysNotiNum(int sysNotiNum) {
		this.sysNotiNum = sysNotiNum;
	}
	public NoExeInfo getWarnNoProcInfo() {
		return warnNoProcInfo;
	}
	public void setWarnNoProcInfo(NoExeInfo warnNoProcInfo) {
		this.warnNoProcInfo = warnNoProcInfo;
	}
	public NoExeInfo getNoDishSchInfo() {
		return noDishSchInfo;
	}
	public void setNoDishSchInfo(NoExeInfo noDishSchInfo) {
		this.noDishSchInfo = noDishSchInfo;
	}
	public NoExeInfo getNoAcceptPlanInfo() {
		return noAcceptPlanInfo;
	}
	public void setNoAcceptPlanInfo(NoExeInfo noAcceptPlanInfo) {
		this.noAcceptPlanInfo = noAcceptPlanInfo;
	}
	public NoExeInfo getDishNoRetInfo() {
		return dishNoRetInfo;
	}
	public void setDishNoRetInfo(NoExeInfo dishNoRetInfo) {
		this.dishNoRetInfo = dishNoRetInfo;
	}	
	public NoExeInfo getNoAcceptSchInfo() {
		return noAcceptSchInfo;
	}
	public void setNoAcceptSchInfo(NoExeInfo noAcceptSchInfo) {
		this.noAcceptSchInfo = noAcceptSchInfo;
	}
	public NoExeInfo getDishNoRetSchInfo() {
		return dishNoRetSchInfo;
	}
	public void setDishNoRetSchInfo(NoExeInfo dishNoRetSchInfo) {
		this.dishNoRetSchInfo = dishNoRetSchInfo;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
