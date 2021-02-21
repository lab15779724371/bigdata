package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class PpDishWeekOutDto {
	String time;
	PageInfo pageInfo;
	List<PpDishWeekDto> ppDishWeekList;
	long msgId;
}
