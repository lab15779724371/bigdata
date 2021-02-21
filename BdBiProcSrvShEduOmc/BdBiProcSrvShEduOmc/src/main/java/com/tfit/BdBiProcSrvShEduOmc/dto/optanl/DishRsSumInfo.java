package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class DishRsSumInfo {
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	/**
	 * 已留样学校
	 */
	private Integer rsSchNum;
	
	/**
	 * 应留有学校数
	 */
	private Integer shouldRsSchNum;
	
	/**
	 * 未留样学校
	 */
	private Integer noRsSchNum;
	
	/**
	 * 菜品总数
	 */
	private Integer totalDishNum;
	
	/**
	 * 已留样菜品数
	 */
	private Integer rsDishNum;
	
	/**
	 * 未留样菜品数
	 */
	private Integer noRsDishNum;
	
	/**
	 * 留样率，保留小数点有效数字两位
	 */
	private Float rsRate;
	
	/**
	 * 学校留样率
	 */
	private Float schRsRate;
	
	
}
