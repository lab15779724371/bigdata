package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.3.	异常情况学校列表模型
 * @author Administrator
 *
 */
@Data
public class AbnCondSchList {
	/**
	 * 序号
	 */
	Integer sn;
	/**
	 * 学校名称
	 */
	String schName;
	/**
	 * 所属
	 */
	String subLevel;
	/**
	 * 主管部门
	 */
	String compDep;
	/**
	 * 所属区域名称
	 */
	String subDistName;
	/**
	 * 食品经营许可证主体
	 */
	String fblMb;
	/**
	 * 区域名称
	 */
	String distName;
	/**
	 * 学校类型（学制）
	 */
	String schType;
	/**
	 * 学校性质
	 */
	String schProp;
	/**
	 * 是否供餐，0:否，1:是
	 */
	String mealFlag;
	/**
	 * 经营模式
	 */
	String optMode;
	/**
	 * 团餐公司名称
	 */
	String rmcName;
	/**
	 * 近况
	 */
	String recentStatus;

}
