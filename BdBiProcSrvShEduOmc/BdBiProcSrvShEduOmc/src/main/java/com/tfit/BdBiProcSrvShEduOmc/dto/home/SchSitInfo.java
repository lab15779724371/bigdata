package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class SchSitInfo {
	int supSchNum;
    List<SchPropClass> schPropClass;
    
	public int getSupSchNum() {
		return supSchNum;
	}
	public void setSupSchNum(int supSchNum) {
		this.supSchNum = supSchNum;
	}
	public List<SchPropClass> getSchPropClass() {
		return schPropClass;
	}
	public void setSchPropClass(List<SchPropClass> schPropClass) {
		this.schPropClass = schPropClass;
	}
}
