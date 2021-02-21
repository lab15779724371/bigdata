package com.tfit.BdBiProcSrvShEduOmc.dto;

import lombok.Data;

@Data
public class DishEmailDto {
	public DishEmailDto() {
	}
	public DishEmailDto(String departmentName, Integer schTypeId,String schType, String schName, String noDishDate, String closingDate) {
		this.departmentName = departmentName;
		this.schTypeId = schTypeId;
		this.schType = schType;
		this.schName = schName;
		this.noDishDate = noDishDate;
		this.closingDate = closingDate;
	}
	//管理部门编号
	String departmentName;
	//学制编号
	Integer schTypeId;
	//学制
	String schType;
	//学校名称
	String schName;	
	//未排菜日期
	String noDishDate;
	//截止日期
	String closingDate;
	
}
