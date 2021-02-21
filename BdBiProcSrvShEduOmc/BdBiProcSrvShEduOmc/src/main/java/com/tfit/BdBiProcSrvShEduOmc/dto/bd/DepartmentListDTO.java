package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class DepartmentListDTO {
	String time;
	PageInfo pageInfo;
	List<DepartmentList> departmentList;
	long msgId;
}
