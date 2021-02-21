package com.tfit.BdBiProcSrvShEduOmc.config;

//redis、hive的键和表配置，方便以后统一改动
public class DataKeyConfig {
	

	//学校基础信息
	public static String schoolData = "schoolData";
	//学校基础信息-管理部门
	public static String areaSchoolData = "schoolData_department_";
	
	/**
	 * 排菜
	 */
	//排菜汇总key
	public static String platoonfeedTotal = "_platoonfeed-total";
	//排菜汇总-管理部门key
	public static String departmentPlatoonfeedTotal = "_platoonfeed-total_department_";
	
	//排菜汇总表
	public static String talbePlatoonTotal = "app_t_edu_platoon_total";
	//排菜汇总新表
	public static String talbePlatoonTotalD = "app_t_edu_platoon_total_d";
	
	
	/**
	 * 用料
	 */
	//用料汇总key
	public static String useMaterialPlanTotal = "_useMaterialPlanTotal";
	//用料汇总-管理部门key
	public static String departmentUseMaterialPlanTotal = "_useMaterialPlanTotal_department_";
	
	//用料汇总表
	public static String talbeMaterialTotal = "app_t_edu_material_total";
	//用料汇总新表
	public static String talbeMaterialTotalD = "app_t_edu_material_total_d";
	
	/**
	 * 配货
	 */
	//配送汇总key
	public static String distributionTotal = "_DistributionTotal";
	//配送汇总-管理部门key
	public static String departmentDistributionTotal = "_DistributionTotal_department_";
	
	public static String talbeLedgerMasterTotal = "app_t_edu_ledger_master_total";
	//排菜汇总新表
	public static String talbeLedgerMasterTotalD = "app_t_edu_ledger_master_total_d";
	
	/**
	 * 留样
	 */

	//配送汇总key
	public static String gcRetentiondishtotal = "_gc-retentiondishtotal";
	//配送汇总-管理部门key
	public static String departmentGcRetentiondishtotal = "_gc-retentiondishtotal_department_";
	
	//排菜汇总新表
	public static String talbeReserveTotalD = "app_t_edu_reserve_total_d";
	
	/**
	 * 预警
	 */
}
