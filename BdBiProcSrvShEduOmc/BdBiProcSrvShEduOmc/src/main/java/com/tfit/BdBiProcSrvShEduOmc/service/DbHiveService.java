package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;
import java.util.Map;

import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.EduPackage;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplier;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;

/**
 * 菜品、原料以及基础信息先关hive库的查询
 * @author Administrator
 *
 */
public interface DbHiveService {
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据列表
	 * @return
	 */
    List<CaDishSupDets> getCaDishSupDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,List<String> shIdList, String dishName, String rmcName,String distName,
    		String caterType, int schType, int schProp, int optMode, String menuName,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据条数
     * @return
     */
    Integer getCaDishSupDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,List<String> shIdList,String dishName, String rmcName,String distName,
    		String caterType, int schType, int schProp, int optMode, String menuName);
    
	/**
	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据列表
	 * @return
	 */
    List<CaDishSupStats> getCaDishSupStatsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName, String dishName,String distName,String dishType,String caterType,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据条数
     * @return
     */
    Integer getCaDishSupStatsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName, String dishName,String distName,String dishType,String caterType);
    
    /**
	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据列表
	 * @return
	 */
    List<CaDishSupStats> getCaDishSupStatsListFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String dishName,String distName,String dishType,String caterType,
    		Integer startNum,Integer endNum);
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据条数
     * @return
     */
    Integer getCaDishSupStatsCountFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String dishName,String distName,String dishType,String caterType);
    
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询数据列表
  	 */
    public List<CaMatSupDets> getCaMatSupDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,String distName,List<String> schoolIds, String matName,String stdMatName, String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap,String supplierNameLike,String supplierNameAll ) ;
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询数据条数
     */
    public Integer getCaMatSupDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,String distName,List<String> schoolIds, String matName,String stdMatName, String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode,String supplierNameLike,String supplierNameAll);
    
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_total中根据条件查询原料汇总数据列表
  	 */
    public List<CaMatSupStats> getCaMatSupStatsListFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,String matClassify, int matCategory, String matStdName,
    		Integer startNum,Integer endNum);
    
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_total中根据条件查询原料汇总数据条数
     */
    public Integer getCaMatSupStatsCountFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,String matClassify, int matCategory, String matStdName);
    
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询原料汇总数据列表
  	 */
    public List<CaMatSupStats> getCaMatSupStatsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,int schType, String schName,String matClassify, int matCategory, String matStdName,
    		Integer startNum,Integer endNum);
    
    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询原料汇总数据条数
     */
    public Integer getCaMatSupStatsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,int schType, String schName,String matClassify, int matCategory, String matStdName);
    
    /**
     * 获取学校信息
     * @param schName
     * @param distName
     * @param startNum
     * @param endNum
     * @param schoolPropertyMap
     * @return
     */
    public List<SearchSch> getSchList(String schName,String distName,List<String> shIdList,List<String> idList,
    		Integer startNum,Integer endNum);
    
    /**
     * 从数据库app_saas_v1的数据表t_edu_school_new中根据条件查询数据条数
     */
    public Integer getSchCount(String schName,String distName,List<String> shIdList,List<String> idList);
    
    /**
     * 从数据库app_saas_v1的数据表t_pro_license中根据条件查询数据条数
     */
    public List<SearchLicense> getLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum);
    /**
  	 * 从数据库app_saas_v1的数据表t_pro_supplier中根据条件查询数据列表
  	 */
    public List<SearchSupplier> getSupplierList(String distName,List<String> supplierIdList,
    		Integer startNum,Integer endNum);
    
    /**
  	 * 从数据库app_saas_v1的数据表app_t_edu_calendar中根据条件查询数据列表
  	 */
    public List<EduPackage> getPackageList(List<String> listYearMonth, String startDate, String endDateAddOne,String schName,
    		String distName,List<String> schoolIdList);
    
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_dish中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduMaterialDishD> getAppTEduMaterialDishDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialDishD inputObj,
    		Integer startNum,Integer endNum);

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_dish中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduMaterialDishDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialDishD inputObj);

}
