package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class AppCommonDao {
	private LinkedHashMap<String, Object> commonMap;
	
	public AppCommonDao() {
		
	}
	public AppCommonDao(LinkedHashMap<String, Object> commonMap) {
		this.commonMap=commonMap;
	}
	
	public LinkedHashMap<String, Object> getCommonMap() {
		return commonMap;
	}

	public void setCommonMap(LinkedHashMap<String, Object> commonMap) {
		this.commonMap = commonMap;
	}
	
}
