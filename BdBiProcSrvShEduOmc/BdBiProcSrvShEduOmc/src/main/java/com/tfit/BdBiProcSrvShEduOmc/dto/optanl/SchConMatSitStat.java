package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchConMatSitStat {
	public SchConMatSitStat() {
		
		/**
		 * 已排菜学校数量
		 */
		this.dishSchNum = 0;
		
		/**
		 * 应确认学校
		 */
		this.shouldAccSchNum = 0;
		
		/**
		 * 未确认用料学校
		 */
		this.noConMatSchNum = 0;
		
		/**
		 * 已确认用料学校
		 */
		this.conMatSchNum = 0;
		
		/**
		 * 用料计划总数
		 */
		this.totalMatPlanNum = 0;
		
		
		/**
		 * 已确认用料计划数
		 */
		this.conMatPlanNum = 0;
		
		/**
		 * 未确认用料计划数
		 */
		this.noConMatPlanNum = 0;
		
		/**
		 * 确认率，保留小数点有效数字两位
		 */
		this.matConRate= 0F;
	}
	
	/**
	 * 统计类别名称，按学校学制统计和按所属主管部门统计有效
	 */
	private String statClassName;
	
	/**
	 * 统计属性名称，具体名称依据统计模式值确定，如统计模式为0则为区域名称
	 */
	private String statPropName;
	
	/**
	 * 已排菜学校数量
	 */
	private Integer dishSchNum;
	
	/**
	 * 应确认学校
	 */
	private Integer shouldAccSchNum;
	
	/**
	 * 未确认用料学校
	 */
	private Integer noConMatSchNum;
	
	/**
	 * 已确认用料学校
	 */
	private Integer conMatSchNum;
	
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
