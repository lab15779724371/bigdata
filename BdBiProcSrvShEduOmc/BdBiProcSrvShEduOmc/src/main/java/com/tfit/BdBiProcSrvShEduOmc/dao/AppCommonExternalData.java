package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.LinkedHashMap;
import java.util.List;

public class AppCommonExternalData {
	private LinkedHashMap<String, Object> data;
	private List<LinkedHashMap<String, Object>> dataList;
	private List<LinkedHashMap<String, Object>> ExternalList;
	public AppCommonExternalData() {
		
	}
	public AppCommonExternalData(LinkedHashMap<String, Object> data,List<LinkedHashMap<String, Object>> dataList,List<LinkedHashMap<String, Object>> ExternalList) {
		this.data=data;
		this.dataList=dataList;
		this.ExternalList=ExternalList;
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
	public List<LinkedHashMap<String, Object>> getExternalList() {
		return ExternalList;
	}
	public void setExternalList(List<LinkedHashMap<String, Object>> externalList) {
		ExternalList = externalList;
	}
}
