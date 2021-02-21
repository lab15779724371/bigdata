package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class AppCommonData {
	private LinkedHashMap<String, Object> data;
	private List<LinkedHashMap<String, Object>> dataList;
	public AppCommonData() {
		
	}
	public AppCommonData(LinkedHashMap<String, Object> data,List<LinkedHashMap<String, Object>> dataList) {
		this.data=data;
		this.dataList=dataList;
	}
	public LinkedHashMap<String, Object> getData() {
		return data;
	}
	public void setData(LinkedHashMap<String, Object> data) {
		this.data = data;
	}
	public List<LinkedHashMap<String, Object>> getDataList() {
		return dataList;
	}
	public void setDataList(List<LinkedHashMap<String, Object>> dataList) {
		this.dataList = dataList;
	}
}
