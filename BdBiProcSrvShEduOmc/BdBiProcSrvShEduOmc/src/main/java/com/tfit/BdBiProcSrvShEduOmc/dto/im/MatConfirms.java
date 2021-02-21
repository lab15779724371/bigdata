package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class MatConfirms {
	String matUseDate;	
	String subLevel;
	String compDep;	
	String distName;	
	int dishSchNum;
	//应确认学校
	int shouldAccSchNum;
	//未确认学校
	int noConMatSchNum;
	//已确认学校
	int conMatSchNum;	
	int totalMatPlanNum;
	int conMatPlanNum;
	int noConMatPlanNum;
	int expConMatPlanNum;
	float matConRate;
	//管理部门
	String departmentId;
	
	
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getMatUseDate() {
		return matUseDate;
	}
	public void setMatUseDate(String matUseDate) {
		this.matUseDate = matUseDate;
	}
	public String getSubLevel() {
		return subLevel;
	}
	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
	}
	public String getCompDep() {
		return compDep;
	}
	public void setCompDep(String compDep) {
		this.compDep = compDep;
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	public int getDishSchNum() {
		return dishSchNum;
	}
	public void setDishSchNum(int dishSchNum) {
		this.dishSchNum = dishSchNum;
	}
	public int getNoConMatSchNum() {
		return noConMatSchNum;
	}
	public void setNoConMatSchNum(int noConMatSchNum) {
		this.noConMatSchNum = noConMatSchNum;
	}
	public int getConMatSchNum() {
		return conMatSchNum;
	}
	public void setConMatSchNum(int conMatSchNum) {
		this.conMatSchNum = conMatSchNum;
	}
	public int getTotalMatPlanNum() {
		return totalMatPlanNum;
	}
	public void setTotalMatPlanNum(int totalMatPlanNum) {
		this.totalMatPlanNum = totalMatPlanNum;
	}
	public int getConMatPlanNum() {
		return conMatPlanNum;
	}
	public void setConMatPlanNum(int conMatPlanNum) {
		this.conMatPlanNum = conMatPlanNum;
	}
	public int getNoConMatPlanNum() {
		return noConMatPlanNum;
	}
	public void setNoConMatPlanNum(int noConMatPlanNum) {
		this.noConMatPlanNum = noConMatPlanNum;
	}
	public int getExpConMatPlanNum() {
		return expConMatPlanNum;
	}
	public void setExpConMatPlanNum(int expConMatPlanNum) {
		this.expConMatPlanNum = expConMatPlanNum;
	}
	public float getMatConRate() {
		return matConRate;
	}
	public void setMatConRate(float matConRate) {
		this.matConRate = matConRate;
	}
	public int getShouldAccSchNum() {
		return shouldAccSchNum;
	}
	public void setShouldAccSchNum(int shouldAccSchNum) {
		this.shouldAccSchNum = shouldAccSchNum;
	}
	
}
