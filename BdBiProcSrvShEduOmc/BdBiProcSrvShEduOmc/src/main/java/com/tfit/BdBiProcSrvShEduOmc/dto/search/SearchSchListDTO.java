package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class SearchSchListDTO {
	String time;
	PageInfo pageInfo;
	Integer distCount;
	List<SearchSch> searchSchList;
	long msgId;
	
}
