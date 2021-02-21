package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchIdNameDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchOptModeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchOwnershipDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSuperviseUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;

public interface Db1Service {
	 //从数据库test_edu的数据表t_edu_district中查找id和区域名称
    List<TEduDistrictDo> getListByDs1IdName();
    
    //从数据库test_edu的数据表t_edu_school中查找level以id
    SchOptModeDo getSchOptModeByDs1Id(String id);
    
    //从数据库test_edu的数据表t_edu_school中查找property以id
    SchOwnershipDo getSchOwnByDs1Id(String id);
    
    //从数据库test_edu的数据表t_edu_school中查找level以id
    SchTypeDo getSchTypeByDs1Id(String id);
    
    //从数据库test_edu的数据表t_edu_school中查找所有id
    List<SchIdNameDo> getSchIdListByDs1(String distId);
    
    //从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）和输出字段方法
    List<TEduSchoolDo> getTEduSchoolDoListByDs1(String distId,Integer stat,Integer reviewed, int outMethod);
    
    //从数据源ds1的数据表t_edu_school中查找学校信息以学校id
    TEduSchoolDo getTEduSchoolDoBySchId(String SchId, int outMethod);
    
    //从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）和输出字段方法
    List<TEduSchoolDo> getTEduSchoolDoListByDs1(List<Object> distIdList,Integer stat,Integer reviewed);
    
    //从数据源ds1的数据表t_edu_school中查找所有总校id以区域ID（空时在查询所有）
    List<TEduSchoolDo> getGenSchIdNameListByDs1(String distId);
    
    //从数据源ds1的数据表t_edu_supervise_user中查找用户名和密码（sha1字符串）以用户名（账号）
    TEduSuperviseUserDo getUserNamePassByUserName(String userName);
    
    //更新生成的token到数据源ds1的数据表t_edu_supervise_user表中
    boolean updateUserTokenToTEduSuperviseUser(String userName, String password, String token);
    
    //从数据源ds1的数据表t_edu_supervise_user中查找授权码以当前授权码
    String getAuthCodeByCurAuthCode(String token);
    
    
    /**
     * 从mysql数据库saas_v1的数据表t_pro_license中根据条件查询数据条数
     */
    public List<SearchLicense> getLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum);
    public List<SearchLicense> getEmployeeLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,List<String> schoolSupplierIdList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum);
    
    /**
    * 从数据库app_saas_v1的数据表t_edu_bd_department中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<DepartmentObj> getDepartmentObjList(DepartmentObj inputObj,
    		List<Object> departmentIdList,
    		Integer startNum,Integer endNum);
    
    /**
     * 修改学校管理部门
     */
    public boolean updateSchoolDepartMent(String id, String departmentId); 
}
