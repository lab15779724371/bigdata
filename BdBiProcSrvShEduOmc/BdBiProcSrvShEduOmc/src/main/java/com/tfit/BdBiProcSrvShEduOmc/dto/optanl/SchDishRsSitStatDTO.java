package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import lombok.Data;

/**
 * 3.2.26.	学校确认用料计划情况统计
 * @author Administrator
 *
 */
@Data
public class SchDishRsSitStatDTO {
	String time;
	List<SchDishRsSitStat> schDishRsSitStat;
	Long msgId;
}
