package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;

@Mapper
public interface TEduSchoolSupplierDoMapper {
	//从表t_edu_school_supplier中获取供应商id和学校名称
    List<TEduSchoolSupplierDo> getSupplierIdSchoolName();
    
    //从表t_edu_school_supplier中获取团餐公司id和学校名称
    List<TEduSchoolSupplierDo> getRmcIdSchoolName();
    
    //从表t_edu_school_supplier中获取供应商id以学校id
    TEduSchoolSupplierDo getSupplierIdSchoolId(@Param(value = "schoolId")String schoolId);
    
    //从表t_edu_school_supplier中获取团餐公司id以学校id
    TEduSchoolSupplierDo getRmcIdSchoolId(@Param(value = "schoolId")String schoolId);
    
    //从表t_edu_school_supplier中获取学校id和供应商id
    List<TEduSchoolSupplierDo> getAllSupplierIdSchoolId();
        
    //从表t_edu_school_supplier中获取主键id、学校id和供应商id
    List<TEduSchoolSupplierDo> getAllIdSupplierIdSchoolId(@Param(value = "stat")Integer stat);
}
