package com.tfit.BdBiProcSrvShEduOmc.dto;

import lombok.Data;
@Data
public class NameCode {
	String name;
	String code;
	String doubleCode;
	String doubleName;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDoubleCode() {
		return doubleCode;
	}
	public void setDoubleCode(String doubleCode) {
		this.doubleCode = doubleCode;
	}
	public String getDoubleName() {
		return doubleName;
	}
	public void setDoubleName(String doubleName) {
		this.doubleName = doubleName;
	}
}
