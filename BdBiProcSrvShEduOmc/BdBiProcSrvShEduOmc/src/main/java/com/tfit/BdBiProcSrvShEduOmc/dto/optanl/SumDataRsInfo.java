package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 留样
 * @author fengyang_xie
 * @date 2019/01/25
 *
 */
@Data
public class SumDataRsInfo {
	/**
	 * 应留有学校数
	 */
	private Integer shouldRsSchNum;
	/**
	 * 已留样学校数
	 */
	private Integer rsSchNum;
	/**
	 * 未留样学校数
	 */
	private Integer noRsSchNum;
	/**
	 * 未留样菜品数
	 */
	private Integer noRsDishNum;
	/**
	 * 留样率
	 */
	private Float rsRate;
	/**
	 * 学校留样率
	 */
	private Float schRsRate;
}
