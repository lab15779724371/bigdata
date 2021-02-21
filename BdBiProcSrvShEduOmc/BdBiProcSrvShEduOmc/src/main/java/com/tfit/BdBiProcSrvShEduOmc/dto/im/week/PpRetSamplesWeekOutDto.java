package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class PpRetSamplesWeekOutDto {
	String time;
	PageInfo pageInfo;
	List<PpRetSamplesWeekDto> ppRetSamplesWeekList;
	long msgId;
}
