package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;
/**
 * 排菜
 * @author Administrator
 * @date 2019/01/25
 */
@Data
public class SumDataDishInfo {
	/**
	 * 应供餐学校数量
	 */
	private Integer mealSchNum;
	
	/**
	 * 未排菜学校数量
	 */
	private Integer noDishSchNum;
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	/**
	 * 未排菜天数
	 */
	private Integer noDishDayNum;
	/**
	 * 排菜率
	 */
	private Float dishRate;
}
