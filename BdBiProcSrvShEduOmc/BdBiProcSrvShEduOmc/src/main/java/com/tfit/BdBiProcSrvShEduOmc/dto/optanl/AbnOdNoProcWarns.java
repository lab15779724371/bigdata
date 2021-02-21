package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 3.2.4.	逾期未处理预警列表模型
 * @author Administrator
 *
 */
@Data
public class AbnOdNoProcWarns {
	/**
	 * 序号
	 */
	Integer sn;
	/**
	 * 预警日期
	 */
	String warnDate;
	/**
	 * 区域名称
	 */
	String distName;
	/**
	 * 学校名称
	 */
	String schName;
	/**
	 * 触发预警单位
	 */
	String trigWarnUnit;
	/**
	 * 证件名称
	 */
	String licName;
	/**
	 * 证件号码
	 */
	String licNo;
	/**
	 * 有效日期，格式：xxxx-xx-xx
	 */
	String validDate;
	/**
	 * 证件状况
	 */
	String licStatus;
	/**
	 * 审核状态，即未处理
	 */
	String licAuditStatus;

}
