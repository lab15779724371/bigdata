package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

public class CaMatSupStats {
	int sn;
	String standardName;
	int matCategory;
	String matClassify;
	float actualQuan;
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getStandardName() {
		return standardName;
	}
	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}
	public int getMatCategory() {
		return matCategory;
	}
	public void setMatCategory(int matCategory) {
		this.matCategory = matCategory;
	}
	public String getMatClassify() {
		return matClassify;
	}
	public void setMatClassify(String matClassify) {
		this.matClassify = matClassify;
	}
	public float getActualQuan() {
		return actualQuan;
	}
	public void setActualQuan(float actualQuan) {
		this.actualQuan = actualQuan;
	}
}
