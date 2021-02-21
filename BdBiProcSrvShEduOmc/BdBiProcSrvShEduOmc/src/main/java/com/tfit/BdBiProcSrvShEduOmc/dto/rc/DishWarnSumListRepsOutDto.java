package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.OutCommonDto;

import lombok.Data;

@Data
public class DishWarnSumListRepsOutDto extends OutCommonDto{
	PageInfo pageInfo;
	List<DishWarnSumListRepsDto> dataList;
}
