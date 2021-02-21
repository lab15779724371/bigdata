package com.tfit.BdBiProcSrvShEduOmc.dto.home;

public class DishRsRate {
	String timeCoord;
	//菜品留样率
    float rsRate;
    //学校留样率
    float schRsRate;
    
	public String getTimeCoord() {
		return timeCoord;
	}
	public void setTimeCoord(String timeCoord) {
		this.timeCoord = timeCoord;
	}
	public float getRsRate() {
		return rsRate;
	}
	public void setRsRate(float rsRate) {
		this.rsRate = rsRate;
	}
	public float getSchRsRate() {
		return schRsRate;
	}
	public void setSchRsRate(float schRsRate) {
		this.schRsRate = schRsRate;
	}
	
}
