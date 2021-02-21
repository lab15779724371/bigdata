package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchGsAcceptSitStat {
	
	public SchGsAcceptSitStat() {
		
		/**
		 * 已排菜学校数量
		 */
		this.dishSchNum = 0;
		
		/**
		 * 已排菜学校数量
		 */
		this.dishSchNum = 0;
		/**
		 * 已确认用料学校
		 */
		this.conMatSchNum = 0;
		
		/**
		 * 应验收学校数
		 */
		this.shouldAccSchNum = 0;
		
		/**
		 * 未验收学校
		 */
		this.noAcceptSchNum = 0;
		
		/**
		 * 已验收学校
		 */
		this.acceptSchNum = 0;
		
		
		/**
		 * 配货计划总数
		 */
		this.totalGsPlanNum = 0;
		
		/**
		 * 已验收配货单
		 */
		this.acceptGsNum = 0;
		
		/**
		 * 未验收配货单
		 */
		this.noAcceptGsNum = 0;
		
		/**
		 * 验收率，保留小数点有效数字两位
		 */
		this.acceptRate = 0F;
		
		/**
		 * 学校验收率
		 */
		this.schAcceptRate = 0F;
		
		//1 表示规范录入
		this.schStandardNum = 0;
		//2 表示补录
		this.schSupplementNum = 0;
		//3 表示逾期补录
		this.schBeOverdueNum = 0;
		//4 表示无数据
		this.schNoDataNum = 0;
		//5 标准验收率
		this.schStandardRate = 0F;
	
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
	 * 已确认用料学校
	 */
	private Integer conMatSchNum;
	
	/**
	 * 应验收学校数
	 */
	private Integer shouldAccSchNum;
	
	/**
	 * 未验收学校
	 */
	private Integer noAcceptSchNum;
	
	/**
	 * 已验收学校
	 */
	private Integer acceptSchNum;
	
	
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
	
	//1 表示规范录入
	int schStandardNum;
	//2 表示补录
	int schSupplementNum;
	//3 表示逾期补录
	int schBeOverdueNum;
	//4 表示无数据
	int schNoDataNum;
	
	//5 标准规范学校验收率
	float schStandardRate;
	
	
}
