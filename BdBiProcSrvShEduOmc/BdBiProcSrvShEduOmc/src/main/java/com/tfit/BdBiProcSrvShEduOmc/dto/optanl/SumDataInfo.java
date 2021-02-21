package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SumDataInfo {
	/**
	 * 监管学校数量
	 */
	private Integer supSchNum;
	/**
	 * 排菜汇总
	 */
	private SumDataDishInfo dishInfo;
	
	/**
	 * 验收汇总
	 */
	private SumDataAcceptInfo acceptInfo;
	
	/**
	 * 留样汇总
	 */
	private SumDataRsInfo rsInfo;
	
	/**
	 * 预警汇总
	 */
	private SumDataWarnInfo warnInfo;
	
	/**
	 * 餐厨垃圾汇总
	 */
	private SumDataKwInfo kwInfo;
	
	/**
	 * 废弃油脂汇总
	 */
	private SumDataKwInfo woInfo;
}
