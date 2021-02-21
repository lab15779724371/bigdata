package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

import lombok.Data;

@Data
public class PpMatConfirmWeekOutDto {
	String time;
	PageInfo pageInfo;
	List<PpMatConfirmWeekDto> ppMatConfirmWeekList;
	long msgId;
}
