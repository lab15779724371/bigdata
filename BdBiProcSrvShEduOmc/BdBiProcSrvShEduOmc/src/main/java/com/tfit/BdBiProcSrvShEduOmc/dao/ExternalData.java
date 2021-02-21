package com.tfit.BdBiProcSrvShEduOmc.dao;

import java.util.LinkedHashMap;
import java.util.List;

public class ExternalData {
	private LinkedHashMap<String, Object> data;
	private List<LinkedHashMap<String, Object>> amInfos;
	private List<LinkedHashMap<String, Object>> schoolInfos;
	public ExternalData() {
		
	}
	public ExternalData(LinkedHashMap<String, Object> data,List<LinkedHashMap<String, Object>> dataList,List<LinkedHashMap<String, Object>> ExternalList) {
		this.data=data;
		this.amInfos=dataList;
		this.schoolInfos=ExternalList;
	}
	public LinkedHashMap<String, Object> getData() {
		return data;
	}
	public void setData(LinkedHashMap<String, Object> data) {
		this.data = data;
	}
	public List<LinkedHashMap<String, Object>> getDataList() {
		return amInfos;
	}
	public void setDataList(List<LinkedHashMap<String, Object>> dataList) {
		this.amInfos = dataList;
	}
	public List<LinkedHashMap<String, Object>> getExternalList() {
		return schoolInfos;
	}
	public void setExternalList(List<LinkedHashMap<String, Object>> externalList) {
		schoolInfos = externalList;
	}
}
