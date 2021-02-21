package com.tfit.BdBiProcSrvShEduOmc.dto;

import lombok.Data;

@Data
public class ParentObj {
	//省
	protected String province;
	//市
	protected String prefCity;
	//页
	protected String page;
	//页大小
	protected String pageSize;
}