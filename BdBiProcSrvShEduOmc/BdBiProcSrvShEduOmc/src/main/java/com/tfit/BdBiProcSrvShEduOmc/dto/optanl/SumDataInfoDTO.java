package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.1.	汇总数据信息模型
 * @author Administrator
 *
 */
@Data
public class SumDataInfoDTO {
	String time;
	SumDataInfo sumDataInfo;
	Long msgId;
}
