package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.25.	用料汇总信息模型
 * @author Administrator
 *
 */
@Data
public class MatSumInfoDTO {
	String time;
	MatSumInfo matSumInfo;
	Long msgId;
}
