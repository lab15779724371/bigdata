package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class MatSumInfo {
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	/**
	 * 应确认学校
	 */
	private Integer shouldAccSchNum;
	/**
	 * 已确认用料学校
	 */
	private Integer conMatSchNum;
	
	/**
	 * 未确认用料学校
	 */
	private Integer noConMatSchNum;
	
	/**
	 * 用料计划总数
	 */
	private Integer totalMatPlanNum;
	
	/**
	 * 已确认用料计划数
	 */
	private Integer conMatPlanNum;
	
	/**
	 * 未确认用料计划数
	 */
	private Integer noConMatPlanNum;
	
	/**
	 * 确认率，保留小数点有效数字两位
	 */
	private Float matConRate;
	
	
}
