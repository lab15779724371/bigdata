package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 厨房垃圾/废弃油脂
 * @author fengyang_xie
 * @date 2019/01/25
 *
 */
@Data
public class SumDataKwInfo {
	/**
	 * 学校回收桶数
	 */
	private Float schRecNum;
	/**
	 * 团餐公司回收桶数
	 */
	private Float rmcRecNum;
	/**
	 * 合计回收桶数
	 */
	private Float totalRec;
}
