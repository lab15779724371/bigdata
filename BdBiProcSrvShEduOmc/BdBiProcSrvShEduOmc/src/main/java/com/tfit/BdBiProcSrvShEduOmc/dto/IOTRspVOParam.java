package com.tfit.BdBiProcSrvShEduOmc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IOTRspVOParam {

	@JsonProperty("msg")
	private String msg;
	
	@JsonProperty("code")
	private Integer code;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("data")
	private Object dataMap;

	public IOTRspVOParam() {
	}

	public IOTRspVOParam(String msg, Integer code) {
		this.msg = msg;
		this.code = code;
	}

	public IOTRspVOParam(IOTRspType rspType) {
		this.setMsg(rspType.getMsg());
		this.setCode(rspType.getCode());
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getDataMap() {
		return dataMap;
	}

	public void setDataMap(Object dataMap) {
		this.dataMap = dataMap;
	}
}
