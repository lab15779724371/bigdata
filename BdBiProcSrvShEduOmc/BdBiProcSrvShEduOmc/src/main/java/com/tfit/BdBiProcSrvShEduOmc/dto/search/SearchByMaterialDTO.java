package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class SearchByMaterialDTO {
	String time;
	PageInfo pageInfo;
	Integer distCount;
	List<SearchByMaterialDetail> schoolDetails;
	long msgId;
	
}
