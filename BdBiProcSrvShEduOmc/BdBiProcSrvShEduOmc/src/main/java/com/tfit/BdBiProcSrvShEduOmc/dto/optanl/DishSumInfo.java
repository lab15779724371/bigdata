package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class DishSumInfo {
	/**
	 * 学校数量
	 */
	private Integer totalSchNum;
	/**
	 * 供餐学校数量
	 */
	private Integer mealSchNum;
	
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	
	/**
	 * 未排菜学校数量
	 */
	private Integer noDishSchNum;
	
	/**
	 * 排菜率，保留小数点有效数字两位。
	 */
	private Float dishRate;
	
	
}
