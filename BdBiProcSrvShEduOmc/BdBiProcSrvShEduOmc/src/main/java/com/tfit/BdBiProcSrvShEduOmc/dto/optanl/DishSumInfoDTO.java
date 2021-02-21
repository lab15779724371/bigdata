package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.9.	排菜汇总信息模型
 * @author Administrator
 *
 */
@Data
public class DishSumInfoDTO {
	String time;
	DishSumInfo dishSumInfo;
	Long msgId;
}
