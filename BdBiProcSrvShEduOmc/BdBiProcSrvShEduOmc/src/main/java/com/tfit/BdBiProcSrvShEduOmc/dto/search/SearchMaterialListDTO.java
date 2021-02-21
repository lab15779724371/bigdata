package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class SearchMaterialListDTO {
	String time;
	PageInfo pageInfo;
	//包含区的个数
	Integer distCount;
	//包含学校个数
	Integer schoolCount;
	//原料关联学校列表
	List<MaterialList> materialList;
	//学校列表
	List<SearchSch> schoolList;
	long msgId;
	
}
