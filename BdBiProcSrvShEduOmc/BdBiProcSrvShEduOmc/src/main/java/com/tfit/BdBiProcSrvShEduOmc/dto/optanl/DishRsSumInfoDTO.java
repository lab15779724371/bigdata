package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.43.	菜品留样汇总信息模型
 * @author Administrator
 *
 */
@Data
public class DishRsSumInfoDTO {
	String time;
	DishRsSumInfo dishRsSumInfo;
	Long msgId;
}
