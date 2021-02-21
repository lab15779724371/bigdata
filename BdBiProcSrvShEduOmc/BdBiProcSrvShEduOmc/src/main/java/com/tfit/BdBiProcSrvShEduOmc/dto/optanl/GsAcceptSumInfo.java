package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class GsAcceptSumInfo {
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	/**
	 * 已确认用料学校
	 */
	private Integer conMatSchNum;
	/**
	 * 应验收学校数
	 */
	private Integer shouldAccSchNum;
	
	/**
	 * 已验收学校
	 */
	private Integer acceptSchNum;
	
	/**
	 * 未验收学校
	 */
	private Integer noAcceptSchNum;
	
	/**
	 * 配货计划总数
	 */
	private Integer totalGsPlanNum;
	
	/**
	 * 已验收配货单
	 */
	private Integer acceptGsNum;
	
	/**
	 * 未验收配货单
	 */
	private Integer noAcceptGsNum;
	
	/**
	 * 验收率，保留小数点有效数字两位
	 */
	private Float acceptRate;
	
	/**
	 * 学校验收率
	 */
	private Float schAcceptRate;
	
	
}
