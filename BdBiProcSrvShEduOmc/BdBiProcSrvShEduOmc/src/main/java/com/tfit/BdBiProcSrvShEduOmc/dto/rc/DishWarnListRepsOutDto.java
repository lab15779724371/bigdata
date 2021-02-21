package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.OutCommonDto;

import lombok.Data;

@Data
public class DishWarnListRepsOutDto extends OutCommonDto{
	PageInfo pageInfo;
	List<DishWarnListRepsDto> dataList;
}
