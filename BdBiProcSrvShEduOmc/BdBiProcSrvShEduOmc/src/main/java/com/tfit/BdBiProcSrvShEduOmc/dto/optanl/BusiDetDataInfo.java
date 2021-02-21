package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 留样
 * @author fengyang_xie
 * @date 2019/01/25
 *
 */
@Data
public class BusiDetDataInfo {
	
	public BusiDetDataInfo() {
		/**
		 * 学校总数
		 */
		this.totalSchNum = 0;
		/**
		 * 学校总数
		 */
		this.totalSchNum = 0;
		
		/**
		 * 未排菜学校数
		 */
		this.noDishSchNum = 0;
		/**
		 * 已排菜学校数
		 */
		this.dishSchNum = 0;
		
		/**
		 * 应排菜天数，即供餐天数
		 */
		this.mealSchNum = 0;
		
		/**
		 * 未排菜天数
		 */
		this.noDishDayNum = 0;
		
		/**
		 * 排菜率
		 */
		this.dishRate = 0F;
		
		/**
		 * 应验收学校数
		 */
		this.shouldAcceptSchNum = 0;
		
		/**
		 * 已验收学校数
		 */
		this.acceptSchNum = 0;
		
		/**
		 * 未验收学校数
		 */
		this.noAcceptSchNum = 0;
		
		/**
		 * 学校验收率
		 */
		this.schAcceptRate = 0F;
		
		/**
		 * 配货计划数
		 */
		this.gsPlanNum = 0;
		/**
		 * 已验收计划数
		 */
		this.acceptPlanNum = 0;
		/**
		 * 未验收计划数
		 */
		this.noAcceptPlanNum = 0;
		/**
		 * 验收率
		 */
		this.acceptRate = 0F;

		 /**
		  * 应留样学校数
		 */
		this.shouldRsSchNum = 0;
		/**
		 * 已留样学校数
		 */
		this.rsSchNum = 0;
		/**
		 * 未留样学校数
		 */
		this.noRsSchNum = 0;
		/**
		 * 菜品数量
		 */
		this.dishNum = 0;
		/**
		 * 已未留样菜品
		 */
		this.rsDishNum = 0;
		/**
		 * 学校留样率
		 */
		this.schRsRate = 0F;
		
		/**
		 * 未留样菜品
		 */
		this.noRsDishNum = 0;
		/**
		 * 留样率
		 */
		this.rsRate = 0F;
		/**
		 * 未处理单位数
		 */
		this.noProcUnitNum = 0;
		/**
		 * 预警数
		 */
		this.warnNum = 0;
		/**
		 * 未处理预警数
		 */
		this.noProcWarnNum = 0;
		/**
		 * 处理率
		 */
		this.procRate = 0F;
		this.elimWarnNum = 0;
		/**
		 * 餐厨垃圾学校回收桶数
		 */
		this.kwSchRecNum = 0F;
		/**
		 * 餐厨垃圾团餐公司回收桶数
		 */
		this.kwRmcRecNum = 0F;
		/**
		 * 餐厨垃圾回收合计
		 */
		this.kwTotalRecNum = 0F;
		/**
		 * 废弃油脂学校回收桶数
		 */
		this.woSchRecNum = 0F;
		/**
		 * 废弃油脂团餐公司回收桶数
		 */
		this.woRmcRecNum = 0F;
		/**
		 * 废弃油脂回收合计
		 */
		this.woTotalRecNum = 0F;
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
	 * 学校总数
	 */
	private Integer totalSchNum;
	
	/**
	 * 已排菜学校数
	 */
	private Integer dishSchNum;
	
	/**
	 * 未排菜学校数
	 */
	private Integer noDishSchNum;
	
	/**
	 * 应排菜天数，即供餐天数
	 */
	private Integer mealSchNum;
	
	/**
	 * 未排菜天数
	 */
	private Integer noDishDayNum;
	
	/**
	 * 排菜率
	 */
	private Float dishRate;
	
	/**
	 * 应验收学校数
	 */
	private Integer shouldAcceptSchNum;
	
	/**
	 * 已验收学校数
	 */
	private Integer acceptSchNum;
	
	/**
	 * 未验收学校数
	 */
	private Integer noAcceptSchNum;
	/**
	 * 学校验收率
	 */
	private Float schAcceptRate;
	/**
	 * 配货计划数
	 */
	private Integer gsPlanNum;
	/**
	 * 已验收计划数
	 */
	private Integer acceptPlanNum;
	/**
	 * 未验收计划数
	 */
	private Integer noAcceptPlanNum;
	/**
	 * 验收率
	 */
	private Float acceptRate;
	
	/**
	 * 应留样学校数
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
	 * 学校留样率
	 */
	private Float schRsRate;
	/**
	 * 菜品数量
	 */
	private Integer dishNum;
	/**
	 * 已留样菜品
	 */
	private Integer rsDishNum;
	/**
	 * 未留样菜品
	 */
	private Integer noRsDishNum;
	/**
	 * 留样率
	 */
	private Float rsRate;
	
	/**
	 * 未处理单位数
	 */
	private Integer noProcUnitNum;
	/**
	 * 预警数
	 */
	private Integer warnNum;
	/**
	 * 未处理预警数
	 */
	private Integer noProcWarnNum;
	/**
	 * 处理率
	 */
	private Float procRate;
	
	/**
	 * 已处理预警
	 */
	int elimWarnNum;
	
	/**
	 * 餐厨垃圾学校回收桶数
	 */
	private Float kwSchRecNum;
	/**
	 * 餐厨垃圾团餐公司回收桶数
	 */
	private Float kwRmcRecNum;
	/**
	 * 餐厨垃圾回收合计
	 */
	private Float kwTotalRecNum;
	/**
	 * 废弃油脂学校回收桶数
	 */
	private Float woSchRecNum;
	/**
	 * 废弃油脂团餐公司回收桶数
	 */
	private Float woRmcRecNum;
	/**
	 * 废弃油脂回收合计
	 */
	private Float woTotalRecNum;
}
