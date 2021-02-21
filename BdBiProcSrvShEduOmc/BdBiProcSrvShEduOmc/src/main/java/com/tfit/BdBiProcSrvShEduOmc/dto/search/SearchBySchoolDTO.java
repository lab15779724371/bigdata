package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class SearchBySchoolDTO {
	String time;
	PageInfo pageInfo;
	Integer distCount;
	List<SearchBySchoolDetail> schoolDetails;
	long msgId;
	
}
