package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchDishSitStat {
	
	public SchDishSitStat() {
		/**
		 * 学校总数
		 */
		this.totalSchNum = 0;
		
		/**
		 * 未排菜学校数
		 */
		this.noDishSchNum = 0;
		
		/**
		 * 应排菜天数，即供餐天数
		 */
		this.mealSchNum = 0;
		
		/**
		 * 排菜学校
		 */
		this.dishSchNum = 0;
		
		/**
		 * 排菜率
		 */
		this.dishRate = 0F;
		
		//1 表示规范录入
		this.standardNum = 0;
		//2 表示补录
		this.supplementNum = 0;
		//3 表示逾期补录
		this.beOverdueNum = 0;
		//4 表示无数据
		this.noDataNum = 0;
		this.standardRate = 0F;
		
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
	
	//1 表示规范录入
	int standardNum;
	//2 表示补录
	int supplementNum;
	//3 表示逾期补录
	int beOverdueNum;
	//4 表示无数据
	int noDataNum;
	float standardRate;
	
	
}
