package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.32.	配货验收汇总信息模型
 * @author Administrator
 *
 */
@Data
public class GsAcceptSumInfoDTO {
	String time;
	GsAcceptSumInfo gsAcceptSumInfo;
	Long msgId;
}
