package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;

import lombok.Data;

@Data
public class SearchSupplyMatSup {
	//供应商名称
	String supplierName;
	//原料个数
	Integer matSupCount;
	//6.配送单明细列表
	List<CaMatSupDets> caMatSupDets;
}
