package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 验收
 * @author fengyang_xie
 * @date 2019/01/25
 *
 */
@Data
public class SumDataAcceptInfo {
	/**
	 * 应验收学校数
	 */
	private Integer shouldAccSchNum;
	/**
	 * 未验收学校数
	 */
	private Integer noAccSchNum;
	/**
	 * 已验收学校数
	 */
	private Integer accSchNum;
	/**
	 * 未验收配货单数
	 */
	private Integer noAccDistrNum;
	/**
	 * 验收率
	 */
	private Float acceptRate;
	
	/**
	 * 学校验收率
	 */
	private Float schAcceptRate;
}
